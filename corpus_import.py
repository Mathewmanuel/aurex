"""
corpus_import.py

Pushes the literary corpus (Thirukkural + Purananuru JSON files) into
Neo4j AuraDB and builds a word-occurrence index.

Usage:
    python corpus_import.py thirukural.json purananuru.json
"""

import sys
import json
import re

from neo4j import GraphDatabase
from lemmatizer import get_tamil_lemma


# ============================================================
# Neo4j AuraDB configuration
# ============================================================

# +ssc = encrypted connection, accepts self-signed certificates
NEO4J_URI = "neo4j+ssc://79ac8e4f.databases.neo4j.io"

NEO4J_USER = "neo4j"
NEO4J_PASSWORD = "ky5GglCEHCyCTqxjs6eCAq_htEzGBecq3ijn5Xe_kxo"
NEO4J_DATABASE = "neo4j"


# Any run of Tamil-script codepoints is treated as one word token.
TAMIL_WORD_RE = re.compile(r"[\u0B80-\u0BFF]+")


def tokenize(text: str):
    """Extract Tamil word tokens from a verse/poem's raw text."""
    if not text:
        return []

    return TAMIL_WORD_RE.findall(text)


def _link_words(session, text: str, match_cypher: str, match_params: dict):
    """
    Tokenizes text, computes each word's canonical key,
    and links every distinct word to the corresponding Verse or Poem.
    """

    seen = {}

    for word in set(tokenize(text)):
        key = get_tamil_lemma(word)

        if key and key not in seen:
            seen[key] = word

    if not seen:
        return

    pairs = [
        {"key": key, "word": word}
        for key, word in seen.items()
    ]

    query = f"""
    {match_cypher}
    WITH item
    UNWIND $pairs AS pair

    MERGE (wf:WordForm {{canonical_key: pair.key}})
      ON CREATE SET wf.sample = pair.word

    MERGE (wf)-[:APPEARS_IN]->(item)
    """

    session.run(
        query,
        pairs=pairs,
        **match_params
    )


# ============================================================
# Thirukkural
# ============================================================

def import_thirukkural(session, path: str):

    with open(path, encoding="utf-8") as f:
        data = json.load(f)

    count = 0

    for rec in data:

        if not rec.get("text"):
            continue

        session.run(
            """
            MERGE (w:Work {name:$book})

            MERGE (s:Section {
                name:$section,
                work:$book
            })

            MERGE (w)-[:HAS_SECTION]->(s)

            MERGE (v:Verse {
                book:$book,
                number:$number
            })

            SET v.text = $text,
                v.section = $section

            MERGE (s)-[:HAS_VERSE]->(v)
            """,

            book=rec["book"],
            section=rec["section"],
            number=rec["number"],
            text=rec["text"],
        )

        _link_words(
            session,
            rec["text"],
            match_cypher="""
            MATCH (item:Verse {
                book:$book,
                number:$number
            })
            """,
            match_params={
                "book": rec["book"],
                "number": rec["number"]
            }
        )

        count += 1

        if count % 10 == 0:
            print(f"Thirukkural: processed {count} verses...")


    print(f"Thirukkural: imported {count} verses.")


# ============================================================
# Purananuru
# ============================================================

def import_purananuru(session, path: str):

    with open(path, encoding="utf-8") as f:
        data = json.load(f)

    count = 0
    skipped = 0

    for rec in data:

        if not rec.get("available", True) or not rec.get("text"):
            skipped += 1
            continue

        session.run(
            """
            MERGE (w:Work {name:$book})

            MERGE (p:Poem {
                book:$book,
                number:$number
            })

            SET p.title = $title,
                p.text = $text,
                p.thinai = $thinai,
                p.thurai = $thurai

            MERGE (w)-[:HAS_POEM]->(p)
            """,

            book=rec["book"],
            number=rec["poem_number"],
            title=rec.get("title"),
            text=rec["text"],
            thinai=rec.get("thinai"),
            thurai=rec.get("thurai"),
        )

        if rec.get("poet"):

            session.run(
                """
                MATCH (p:Poem {
                    book:$book,
                    number:$number
                })

                MERGE (a:Author {
                    name:$poet
                })

                MERGE (p)-[:WRITTEN_BY]->(a)
                """,

                book=rec["book"],
                number=rec["poem_number"],
                poet=rec["poet"],
            )

        if rec.get("sung_for"):

            session.run(
                """
                MATCH (p:Poem {
                    book:$book,
                    number:$number
                })

                MERGE (person:Person {
                    name:$sung_for
                })

                MERGE (p)-[:SUNG_FOR]->(person)
                """,

                book=rec["book"],
                number=rec["poem_number"],
                sung_for=rec["sung_for"],
            )

        _link_words(
            session,
            rec["text"],
            match_cypher="""
            MATCH (item:Poem {
                book:$book,
                number:$number
            })
            """,
            match_params={
                "book": rec["book"],
                "number": rec["poem_number"]
            }
        )

        count += 1

        if count % 10 == 0:
            print(f"Purananuru: processed {count} poems...")

    print(
        f"Purananuru: imported {count} poems, "
        f"skipped {skipped} (no text / unavailable)."
    )


# ============================================================
# Main
# ============================================================

def main():

    if len(sys.argv) != 3:
        print(
            "Usage: python corpus_import.py "
            "<thirukkural.json> <purananuru.json>"
        )
        sys.exit(1)

    kural_path = sys.argv[1]
    puram_path = sys.argv[2]

    print("Connecting to Neo4j AuraDB...")
    print(f"URI: {NEO4J_URI}")
    print(f"Database: {NEO4J_DATABASE}")

    driver = GraphDatabase.driver(
        NEO4J_URI,
        auth=(NEO4J_USER, NEO4J_PASSWORD)
    )

    try:

        print("Testing Neo4j connection...")
        driver.verify_connectivity()

        print("Neo4j connection OK.")
        print("Starting corpus import...")

        with driver.session(
            database=NEO4J_DATABASE
        ) as session:

            import_thirukkural(
                session,
                kural_path
            )

            import_purananuru(
                session,
                puram_path
            )

            print("Creating indexes...")

            session.run(
                """
                CREATE INDEX wordform_key IF NOT EXISTS
                FOR (wf:WordForm)
                ON (wf.canonical_key)
                """
            )

            session.run(
                """
                CREATE INDEX verse_key IF NOT EXISTS
                FOR (v:Verse)
                ON (v.book, v.number)
                """
            )

            session.run(
                """
                CREATE INDEX poem_key IF NOT EXISTS
                FOR (p:Poem)
                ON (p.book, p.number)
                """
            )

        print("Import complete.")

    finally:
        driver.close()


if __name__ == "__main__":
    main()