import pymysql
from rdflib import Graph, Namespace, Literal, RDF, RDFS, URIRef
from rdflib.namespace import XSD

# ============================================================
# CONFIG
# ============================================================
MYSQL_CONFIG = {
    "host": "localhost",
    "user": "root",
    "password": "maddiebaddie@18",   # <-- your actual MySQL root password
    "database": "wordnet",
    "charset": "utf8mb4",
}

TWN = Namespace("http://tamilwordnet.org/onto#")
LEX = Namespace("http://tamilwordnet.org/lex#")

# ============================================================
# RELATION CODE MAPPING — CONFIRMED from PDF + data inspection
# ============================================================
RELATION_CODE_MAP = {
    "0": "hypernymOf",
    "1": "meronymOf",
    "2": "holonymOf",
    "3": "hyponymOf",          # overridden to troponymOf for verbs below
    "4": "synonymOf",
    "5": "associatedWith",
    "7": "relatedForm",
    "8": "relatedForm",
    "9": "relatedForm",
    "10": "relatedForm",
    "11": "relatedForm",
}

INVERSE_MAP = {
    "hypernymOf": "hyponymOf",
    "hyponymOf": "hypernymOf",
    "meronymOf": "holonymOf",
    "holonymOf": "meronymOf",
    "troponymOf": "hypernymOf",   # a troponym is still narrower-than its hypernym
    "causes": "causedBy",
    "causedBy": "causes",
    "hasAttribute": "attributeOf",
    "attributeOf": "hasAttribute",
    "synonymOf": "synonymOf",
    "antonymOf": "antonymOf",
    "associatedWith": "associatedWith",
    "coOccursWith": "coOccursWith",
    "relatedForm": "relatedForm",
}

ALL_RELATION_PROPS = [
    "synonymOf", "antonymOf", "hypernymOf", "hyponymOf",
    "meronymOf", "holonymOf", "troponymOf", "entails",
    "causes", "causedBy", "associatedWith", "coOccursWith",
    "hasAttribute", "attributeOf", "relatedForm",
]

# ============================================================
# HELPERS
# ============================================================
def clean_uri_part(text):
    if text is None:
        return "unknown"
    return str(text).strip().replace(" ", "_").replace(",", "-")

def word_uri(label):
    return LEX[f"word/{clean_uri_part(label)}"]

def node_uri(nodeindex):
    return TWN[f"node/{clean_uri_part(nodeindex)}"]

def parent_nodeindex(nodeindex):
    parts = nodeindex.split(",")
    if len(parts) <= 1:
        return None
    return ",".join(parts[:-1])

def resolve_relation_type(rel_code, pos):
    """Apply the code map, with a verb-specific override for code 3."""
    if rel_code == "3" and pos == "Verb":
        return "troponymOf"
    return RELATION_CODE_MAP.get(rel_code)

# ============================================================
# MAIN EXTRACTION
# ============================================================
def main():
    conn = pymysql.connect(**MYSQL_CONFIG)
    cursor = conn.cursor(pymysql.cursors.DictCursor)

    g = Graph()
    g.bind("twn", TWN)
    g.bind("lex", LEX)

    for prop in ALL_RELATION_PROPS:
        g.add((TWN[prop], RDF.type, RDF.Property))

    # --------------------------------------------------------
    # 1. Load twn table
    # --------------------------------------------------------
    print("Loading twn table...")
    cursor.execute("SELECT nodeindex, label, gloss, example, relation, pos, english FROM twn")
    twn_rows = cursor.fetchall()

    nodeindex_to_label = {row["nodeindex"]: row["label"] for row in twn_rows if row["label"]}

    unmapped_codes_seen = set()

    for row in twn_rows:
        label = row["label"]
        if not label:
            continue

        w_uri = word_uri(label)
        n_uri = node_uri(row["nodeindex"])

        g.add((w_uri, RDF.type, LEX.LexicalEntry))
        g.add((w_uri, LEX.hasNode, n_uri))
        g.add((n_uri, LEX.label, Literal(label)))

        if row["pos"]:
            g.add((w_uri, LEX.partOfSpeech, Literal(row["pos"])))
        if row["gloss"]:
            g.add((n_uri, LEX.gloss, Literal(row["gloss"])))
        if row["example"]:
            g.add((n_uri, LEX.example, Literal(row["example"])))
        if row["english"]:
            g.add((w_uri, LEX.englishGloss, Literal(row["english"])))

        parent_idx = parent_nodeindex(row["nodeindex"])
        if parent_idx and parent_idx in nodeindex_to_label:
            parent_label = nodeindex_to_label[parent_idx]
            if parent_label and parent_label != label:
                rel_code = str(row["relation"]) if row["relation"] is not None else None
                rel_type = resolve_relation_type(rel_code, row["pos"])

                if rel_type is None:
                    rel_type = "associatedWith"
                    if rel_code not in unmapped_codes_seen:
                        print(f"WARNING: unmapped relation code '{rel_code}' — defaulting to associatedWith")
                        unmapped_codes_seen.add(rel_code)

                g.add((w_uri, TWN[rel_type], word_uri(parent_label)))
                inverse = INVERSE_MAP.get(rel_type)
                if inverse:
                    g.add((word_uri(parent_label), TWN[inverse], w_uri))

    # --------------------------------------------------------
    # 2. Load sense table — direct hypernym cross-references
    # --------------------------------------------------------
    print("Loading sense table...")
    cursor.execute("SELECT label, pos, hypernym FROM sense")
    sense_rows = cursor.fetchall()

    for row in sense_rows:
        label = row["label"]
        hypernym_idx = row["hypernym"]
        if not label or not hypernym_idx:
            continue

        broader_label = nodeindex_to_label.get(hypernym_idx)
        if not broader_label:
            continue

        w_uri = word_uri(label)
        b_uri = word_uri(broader_label)

        g.add((w_uri, TWN.hypernymOf, b_uri))
        g.add((b_uri, TWN.hyponymOf, w_uri))

        if row["pos"]:
            g.add((w_uri, LEX.partOfSpeech, Literal(row["pos"])))

    # --------------------------------------------------------
    # 3. Load morphtable — root word mapping
    # --------------------------------------------------------
    print("Loading morphtable...")
    cursor.execute("SELECT inflated_word, root_word FROM morphtable")
    morph_rows = cursor.fetchall()

    for row in morph_rows:
        inflected = row["inflated_word"]
        root = row["root_word"]
        if not inflected or not root:
            continue
        g.add((word_uri(inflected), LEX.hasRootForm, word_uri(root)))
        g.add((word_uri(root), LEX.rootFormOf, word_uri(inflected)))

    # --------------------------------------------------------
    # 4. Load frequency table
    # --------------------------------------------------------
    print("Loading frequency table...")
    cursor.execute("SELECT word, freq FROM frequency")
    freq_rows = cursor.fetchall()

    for row in freq_rows:
        word = row["word"]
        freq = row["freq"]
        if not word or freq is None:
            continue
        g.add((word_uri(word), LEX.frequency, Literal(freq, datatype=XSD.integer)))

    cursor.close()
    conn.close()

    print(f"Total triples: {len(g)}")
    g.serialize(destination="tamil_wordnet.ttl", format="turtle")
    print("Done. Wrote tamil_wordnet.ttl")


if __name__ == "__main__":
    main()