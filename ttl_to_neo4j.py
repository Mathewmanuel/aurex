import re
from rdflib import Graph, URIRef, Literal
from neo4j import GraphDatabase

# --- Neo4j Configuration ---
NEO4J_URI = "neo4j+ssc://79ac8e4f.databases.neo4j.io"
NEO4J_USER = "neo4j"
NEO4J_PASSWORD = "ky5GglCEHCyCTqxjs6eCAq_htEzGBecq3ijn5Xe_kxo"

TTL_FILE = "tamil_wordnet.ttl"
BATCH_SIZE = 5000

def extract_word_name(uri_or_str):
    s = str(uri_or_str)
    if "#word/" in s:
        return s.split("#word/")[-1]
    return s.split("/")[-1]

def process_literal_batch(tx, batch, prop_name):
    query = f"""
    UNWIND $batch AS item
    MERGE (w:Word {{name: item.name}})
    SET w.{prop_name} = item.val
    """
    tx.run(query, batch=batch)

def process_rel_batch(tx, batch, rel_type):
    query = f"""
    UNWIND $batch AS item
    MERGE (w1:Word {{name: item.subj}})
    MERGE (w2:Word {{name: item.obj}})
    MERGE (w1)-[:{rel_type}]->(w2)
    """
    tx.run(query, batch=batch)

def load_ttl_to_neo4j():
    print(f"Parsing {TTL_FILE} into memory (this may take 15-30s for 1.2M triples)...")
    g = Graph()
    g.parse(TTL_FILE, format="turtle")
    print(f"Graph loaded! Total triples to ingest: {len(g)}")

    driver = GraphDatabase.driver(NEO4J_URI, auth=(NEO4J_USER, NEO4J_PASSWORD))

    with driver.session() as session:
        print("Creating constraints for fast indexing...")
        session.run("CREATE CONSTRAINT IF NOT EXISTS FOR (w:Word) REQUIRE w.name IS UNIQUE;")

        # Group data into relationship and property buckets
        rel_buckets = {}      # rel_type -> list of {subj, obj}
        prop_buckets = {}     # prop_name -> list of {name, val}

        print("Grouping triples for batch ingestion...")
        for s, p, o in g:
            p_str = str(p)
            if "rdf-syntax-ns#type" in p_str and "rdf-syntax-ns#Property" in str(o):
                continue

            subj_word = extract_word_name(s)

            if isinstance(o, Literal):
                prop_name = p_str.split("#")[-1].split("/")[-1]
                prop_name = re.sub(r'[^a-zA-Z0-9_]', '_', prop_name)
                if prop_name not in prop_buckets:
                    prop_buckets[prop_name] = []
                prop_buckets[prop_name].append({"name": subj_word, "val": o.toPython()})

            elif isinstance(o, URIRef):
                obj_word = extract_word_name(o)
                rel_type = p_str.split("#")[-1].split("/")[-1].upper()
                rel_type = re.sub(r'[^A-Z0-9_]', '_', rel_type)
                if rel_type not in rel_buckets:
                    rel_buckets[rel_type] = []
                rel_buckets[rel_type].append({"subj": subj_word, "obj": obj_word})

        # Ingest Literal Properties
        for prop_name, items in prop_buckets.items():
            print(f"Ingesting property '{prop_name}' ({len(items)} items)...")
            for i in range(0, len(items), BATCH_SIZE):
                chunk = items[i:i + BATCH_SIZE]
                session.execute_write(process_literal_batch, chunk, prop_name)

        # Ingest Relationships
        for rel_type, items in rel_buckets.items():
            print(f"Ingesting relationship ':{rel_type}' ({len(items)} items)...")
            for i in range(0, len(items), BATCH_SIZE):
                chunk = items[i:i + BATCH_SIZE]
                session.execute_write(process_rel_batch, chunk, rel_type)

    driver.close()
    print("\n✅ Success! All 1.2M triples successfully loaded into Neo4j Aura.")

if __name__ == "__main__":
    load_ttl_to_neo4j()
