from neo4j import GraphDatabase
from lemmatizer import get_tamil_lemma

NEO4J_URI = "neo4j+ssc://79ac8e4f.databases.neo4j.io"
NEO4J_USER = "neo4j"
NEO4J_PASSWORD = "ky5GglCEHCyCTqxjs6eCAq_htEzGBecq3ijn5Xe_kxo"

BATCH_SIZE = 2000

driver = GraphDatabase.driver(
    NEO4J_URI,
    auth=(NEO4J_USER, NEO4J_PASSWORD)
)


def update_batch(session, rows):
    session.run(
        """
        UNWIND $rows AS row
        MATCH (w:Word {name: row.name})
        SET w.normalized_label = row.normalized
        """,
        rows=rows
    ).consume()


def normalize_wordnet_words():

    with driver.session(database="neo4j") as read_session:

        result = read_session.run("""
            MATCH (w:Word)
            RETURN w.name AS name, w.label AS label
        """)

        batch = []
        count = 0

        with driver.session(database="neo4j") as write_session:

            for record in result:

                name = record["name"]
                label = record["label"]

                if not label:
                    continue

                normalized = get_tamil_lemma(label)

                batch.append({
                    "name": name,
                    "normalized": normalized
                })

                if len(batch) >= BATCH_SIZE:

                    update_batch(write_session, batch)

                    count += len(batch)

                    print(f"Normalized {count} Word nodes...")

                    batch.clear()

            # Remaining nodes
            if batch:
                update_batch(write_session, batch)
                count += len(batch)
                print(f"Normalized {count} Word nodes...")

    print(f"\nDone. Normalized {count} Word nodes.")


if __name__ == "__main__":

    try:
        print("Connecting to Neo4j...")
        driver.verify_connectivity()
        print("Connected.")

        normalize_wordnet_words()

    finally:
        driver.close()