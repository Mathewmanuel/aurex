import os
from typing import List, Dict, Any, Optional

from fastapi import FastAPI, HTTPException, Query
from fastapi.middleware.cors import CORSMiddleware
from neo4j import GraphDatabase, Driver
from pydantic import BaseModel

from lemmatizer import get_tamil_lemma, normalize_phonetic_key


# ============================================================
# Neo4j Configuration
# ============================================================

NEO4J_URI = os.getenv(
    "NEO4J_URI",
    "neo4j+ssc://79ac8e4f.databases.neo4j.io"
)

NEO4J_USER = os.getenv(
    "NEO4J_USER",
    "neo4j"
)

NEO4J_PASSWORD = os.getenv(
    "NEO4J_PASSWORD",
    "ky5GglCEHCyCTqxjs6eCAq_htEzGBecq3ijn5Xe_kxo"
)

NEO4J_DATABASE = os.getenv(
    "NEO4J_DATABASE",
    "neo4j"
)

AURA_INSTANCEID = "79ac8e4f"
AURA_INSTANCENAME = "Instance01"


# ============================================================
# FastAPI Application
# ============================================================

app = FastAPI(
    title="Tamil Literature NLP & Sense Disambiguation API",
    description=(
        "FastAPI service connecting React frontend to Neo4j AuraDB "
        "for Tamil WordNet graph analysis."
    ),
    version="2.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ============================================================
# Neo4j Driver
# ============================================================

driver: Optional[Driver] = None


@app.on_event("startup")
def startup():
    global driver

    driver = GraphDatabase.driver(
        NEO4J_URI,
        auth=(NEO4J_USER, NEO4J_PASSWORD)
    )

    driver.verify_connectivity()

    print("Connected to Neo4j AuraDB")


@app.on_event("shutdown")
def shutdown():
    global driver

    if driver:
        driver.close()
        print("Closed Neo4j connection")


# ============================================================
# Response Schemas
# ============================================================

class NodeModel(BaseModel):
    id: str
    label: str
    properties: Dict[str, Any]


class EdgeModel(BaseModel):
    source: str
    target: str
    type: str


class GraphResponse(BaseModel):
    nodes: List[NodeModel]
    edges: List[EdgeModel]


# ============================================================
# WordNet Relationship Types
# ============================================================

SEMANTIC_RELATIONSHIPS = [
    "SYNONYMOF",
    "HYPERNYMOF",
    "HYPONYMOF",
    "MERONYMOF",
    "HOLONYMOF",
    "TROPONYMOF",
    "RELATEDFORM",
    "ANTONYMOF",
]


# ============================================================
# Helper: Find Semantic WordNet Nodes
# ============================================================

def find_wordnet_words(
    session,
    input_word: str,
    canonical_key: str
):
    query = """
    MATCH (w:Word)
    WHERE
        w.name = $input_word
        OR w.label = $input_word
        OR w.normalized_label = $canonical_key

    WITH DISTINCT w

    OPTIONAL MATCH (semantic:Word)-[:HASNODE]->(w)

    WITH
        w,
        collect(DISTINCT semantic) AS semantic_nodes

    UNWIND
        CASE
            WHEN size(semantic_nodes) > 0
            THEN semantic_nodes
            ELSE [w]
        END AS candidate

    WITH DISTINCT candidate

    RETURN candidate

    ORDER BY
        CASE
            WHEN candidate.name = $input_word THEN 0
            WHEN candidate.partOfSpeech IS NOT NULL THEN 1
            WHEN candidate.frequency IS NOT NULL THEN 2
            ELSE 3
        END,
        candidate.name
    """

    result = session.run(
        query,
        input_word=input_word,
        canonical_key=canonical_key
    )

    nodes = [
        record["candidate"]
        for record in result
    ]

    semantic_nodes = []

    for node in nodes:
        properties = dict(node)

        if (
            "partOfSpeech" in properties
            or "frequency" in properties
        ):
            semantic_nodes.append(node)

    if semantic_nodes:
        return semantic_nodes

    return nodes


# ============================================================
# Helper: Get Semantic Relationships
# ============================================================

def get_word_relationships(
    session,
    word_name: str
):
    query = """
    MATCH (w:Word {name: $word_name})-[r]->(neighbor:Word)

    WHERE type(r) IN [
        "SYNONYMOF",
        "HYPERNYMOF",
        "HYPONYMOF",
        "MERONYMOF",
        "HOLONYMOF",
        "TROPONYMOF",
        "RELATEDFORM",
        "ANTONYMOF"
    ]

    RETURN
        type(r) AS relationship,
        properties(neighbor) AS target

    ORDER BY
        relationship,
        target.name
    """

    result = session.run(
        query,
        word_name=word_name
    )

    relationships = {}

    for record in result:
        relationship = record["relationship"]
        target = record["target"]

        if relationship not in relationships:
            relationships[relationship] = []

        relationships[relationship].append(target)

    return relationships


# ============================================================
# Helper: Get HASNODE Entries
# ============================================================

def get_linked_nodes(
    session,
    word_name: str
):
    query = """
    MATCH (w:Word {name: $word_name})-[:HASNODE]->(n:Word)

    RETURN properties(n) AS node

    ORDER BY n.name
    """

    result = session.run(
        query,
        word_name=word_name
    )

    return [
        record["node"]
        for record in result
    ]


# ============================================================
# Helper: Convert Neo4j Node
# ============================================================

def node_to_model(node):
    properties = dict(node)

    node_id = str(
        properties.get("name", "")
    )

    label = str(
        properties.get(
            "label",
            properties.get("name", "")
        )
    )

    return {
        "id": node_id,
        "label": label,
        "properties": properties
    }


# ============================================================
# Root Endpoint
# ============================================================

@app.get("/")
def read_root():
    return {
        "status": "online",
        "message": "Tamil WordNet NLP Backend Running"
    }


# ============================================================
# Word Details
# ============================================================

@app.get(
    "/api/word/{word_name}",
    response_model=Dict[str, Any]
)
def get_word_details(word_name: str):

    canonical_key = get_tamil_lemma(word_name)

    if not canonical_key:
        raise HTTPException(
            status_code=400,
            detail="Unable to normalize the supplied word."
        )

    try:

        with driver.session(
            database=NEO4J_DATABASE
        ) as session:

            words = find_wordnet_words(
                session,
                word_name,
                canonical_key
            )

            if not words:
                raise HTTPException(
                    status_code=404,
                    detail=(
                        f"Word '{word_name}' "
                        f"(Canonical key: '{canonical_key}') "
                        "not found in WordNet."
                    )
                )

            entries = []

            for word_node in words:

                properties = dict(word_node)

                semantic_name = properties.get("name")

                relationships = get_word_relationships(
                    session,
                    semantic_name
                )

                linked_nodes = get_linked_nodes(
                    session,
                    semantic_name
                )

                entries.append({
                    "properties": properties,
                    "relationships": relationships,
                    "linked_nodes": linked_nodes
                })

            return {
                "input_word": word_name,
                "canonical_key": canonical_key,
                "match_count": len(entries),
                "entries": entries
            }

    except HTTPException:
        raise

    except Exception as exc:

        print(
            "WORD ENDPOINT ERROR:",
            type(exc).__name__,
            repr(str(exc))
        )

        raise HTTPException(
            status_code=500,
            detail=(
                f"Word lookup failed: "
                f"{type(exc).__name__}: {str(exc)}"
            )
        )


# ============================================================
# WordNet Graph Subgraph
# ============================================================

@app.get("/api/graph/subgraph")
def get_word_subgraph(
    word_name: str = Query(
        ...,
        description="Target Tamil or WordNet word"
    ),
    depth: int = Query(
        1,
        ge=1,
        le=3,
        description="Semantic traversal depth (1 to 3)"
    )
):

    try:

        # ----------------------------------------------------
        # Normalize input
        # ----------------------------------------------------

        canonical_key = get_tamil_lemma(word_name)

        if not canonical_key:
            raise HTTPException(
                status_code=400,
                detail="Unable to normalize the supplied word."
            )

        # ----------------------------------------------------
        # Open Neo4j session
        # ----------------------------------------------------

        with driver.session(
            database=NEO4J_DATABASE
        ) as session:

            # ------------------------------------------------
            # Find semantic WordNet nodes
            # ------------------------------------------------

            matching_words = find_wordnet_words(
                session,
                word_name,
                canonical_key
            )

            if not matching_words:
                raise HTTPException(
                    status_code=404,
                    detail=(
                        f"Word '{word_name}' "
                        f"(Canonical key: '{canonical_key}') "
                        "not found in graph."
                    )
                )

            matching_ids = [
                str(node["name"])
                for node in matching_words
            ]

            # ------------------------------------------------
            # GRAPH QUERY
            # ------------------------------------------------

            if depth == 1:

                query = """
                MATCH (start:Word)-[rel]->(neighbor:Word)

                WHERE start.name IN $matching_ids
                  AND type(rel) IN $semantic_relationships

                RETURN
                    start,
                    rel,
                    neighbor
                """

                result = session.run(
                    query,
                    matching_ids=matching_ids,
                    semantic_relationships=SEMANTIC_RELATIONSHIPS
                )

                nodes = {}
                edges = {}

                for record in result:

                    start = record["start"]
                    relationship = record["rel"]
                    neighbor = record["neighbor"]

                    start_properties = dict(start)
                    neighbor_properties = dict(neighbor)

                    start_id = str(
                        start_properties.get(
                            "name",
                            ""
                        )
                    )

                    neighbor_id = str(
                        neighbor_properties.get(
                            "name",
                            ""
                        )
                    )

                    # ----------------------------------------
                    # Add start node
                    # ----------------------------------------

                    if start_id:

                        nodes[start_id] = {
                            "id": start_id,
                            "label": str(
                                start_properties.get(
                                    "label",
                                    start_id
                                )
                            ),
                            "properties": start_properties
                        }

                    # ----------------------------------------
                    # Add neighbor node
                    # ----------------------------------------

                    if neighbor_id:

                        nodes[neighbor_id] = {
                            "id": neighbor_id,
                            "label": str(
                                neighbor_properties.get(
                                    "label",
                                    neighbor_id
                                )
                            ),
                            "properties": neighbor_properties
                        }

                    # ----------------------------------------
                    # Add relationship
                    # ----------------------------------------

                    if start_id and neighbor_id:

                        edge_type = str(
                            relationship.type
                        )

                        edge_key = (
                            start_id,
                            edge_type,
                            neighbor_id
                        )

                        edges[edge_key] = {
                            "source": start_id,
                            "target": neighbor_id,
                            "type": edge_type
                        }

            else:

                # --------------------------------------------
                # Depth 2 / 3
                # --------------------------------------------

                if depth == 2:

                    query = """
                    MATCH path =
                        (start:Word)-[rel1]->(middle:Word)
                        -[rel2]->(neighbor:Word)

                    WHERE start.name IN $matching_ids
                      AND type(rel1) IN $semantic_relationships
                      AND type(rel2) IN $semantic_relationships

                    RETURN path
                    """

                else:

                    query = """
                    MATCH path =
                        (start:Word)-[rel1]->(middle1:Word)
                        -[rel2]->(middle2:Word)
                        -[rel3]->(neighbor:Word)

                    WHERE start.name IN $matching_ids
                      AND type(rel1) IN $semantic_relationships
                      AND type(rel2) IN $semantic_relationships
                      AND type(rel3) IN $semantic_relationships

                    RETURN path
                    """

                result = session.run(
                    query,
                    matching_ids=matching_ids,
                    semantic_relationships=SEMANTIC_RELATIONSHIPS
                )

                nodes = {}
                edges = {}

                for record in result:

                    path = record["path"]

                    # ----------------------------------------
                    # Nodes in path
                    # ----------------------------------------

                    for node in path.nodes:

                        properties = dict(node)

                        node_id = str(
                            properties.get(
                                "name",
                                ""
                            )
                        )

                        if node_id:

                            nodes[node_id] = {
                                "id": node_id,
                                "label": str(
                                    properties.get(
                                        "label",
                                        node_id
                                    )
                                ),
                                "properties": properties
                            }

                    # ----------------------------------------
                    # Relationships in path
                    # ----------------------------------------

                    for relationship in path.relationships:

                        source_properties = dict(
                            relationship.start_node
                        )

                        target_properties = dict(
                            relationship.end_node
                        )

                        source_id = str(
                            source_properties.get(
                                "name",
                                ""
                            )
                        )

                        target_id = str(
                            target_properties.get(
                                "name",
                                ""
                            )
                        )

                        if not source_id or not target_id:
                            continue

                        edge_type = str(
                            relationship.type
                        )

                        edge_key = (
                            source_id,
                            edge_type,
                            target_id
                        )

                        edges[edge_key] = {
                            "source": source_id,
                            "target": target_id,
                            "type": edge_type
                        }

            # ------------------------------------------------
            # Always include the searched semantic node
            # ------------------------------------------------

            for node in matching_words:

                properties = dict(node)

                node_id = str(
                    properties.get(
                        "name",
                        ""
                    )
                )

                if node_id:

                    nodes[node_id] = {
                        "id": node_id,
                        "label": str(
                            properties.get(
                                "label",
                                node_id
                            )
                        ),
                        "properties": properties
                    }

            # ------------------------------------------------
            # Final response
            # ------------------------------------------------

            return {
                "nodes": list(nodes.values()),
                "edges": list(edges.values())
            }

    except HTTPException:
        raise

    except Exception as exc:

        print(
            "GRAPH ENDPOINT ERROR:",
            type(exc).__name__,
            repr(str(exc))
        )

        raise HTTPException(
            status_code=500,
            detail=(
                f"Graph endpoint failed: "
                f"{type(exc).__name__}: {str(exc)}"
            )
        )


# ============================================================
# Sense Disambiguation
# ============================================================

@app.get(
    "/api/disambiguate",
    response_model=Dict[str, Any]
)
def disambiguate_sense(
    word: str = Query(
        ...,
        description="Target word to disambiguate"
    ),
    context_words: List[str] = Query(
        ...,
        description="Context words present in sentence"
    )
):

    target_key = get_tamil_lemma(word)

    if not target_key:
        raise HTTPException(
            status_code=400,
            detail="Unable to normalize target word."
        )

    context_pairs = []

    for context_word in context_words:

        context_key = get_tamil_lemma(
            context_word
        )

        if context_key:

            context_pairs.append(
                (
                    context_word,
                    context_key
                )
            )

    context_keys = [
        key
        for _, key in context_pairs
    ]

    if not context_pairs:

        return {
            "input_target": word,
            "canonical_target_key": target_key,
            "context_analyzed": context_words,
            "context_canonical_keys": [],
            "semantic_paths": []
        }

    with driver.session(
        database=NEO4J_DATABASE
    ) as session:

        target_words = find_wordnet_words(
            session,
            word,
            target_key
        )

        if not target_words:

            raise HTTPException(
                status_code=404,
                detail=(
                    f"Target word '{word}' "
                    f"(Canonical key: '{target_key}') "
                    "not found in WordNet."
                )
            )

        target_ids = [
            node["name"]
            for node in target_words
        ]

        context_ids = []
        context_matches = {}

        for context_word, context_key in context_pairs:

            nodes = find_wordnet_words(
                session,
                context_word,
                context_key
            )

            for node in nodes:

                node_id = node["name"]

                if node_id not in context_ids:
                    context_ids.append(node_id)

                context_matches[
                    node_id
                ] = context_word

        if not context_ids:

            return {
                "input_target": word,
                "canonical_target_key": target_key,
                "context_analyzed": context_words,
                "context_canonical_keys": context_keys,
                "semantic_paths": [],
                "message": (
                    "No context words were found "
                    "in WordNet."
                )
            }

        # ----------------------------------------------------
        # Semantic shortest-path query
        # ----------------------------------------------------

        query = """
        MATCH (target:Word)
        WHERE target.name IN $target_ids

        MATCH (context:Word)
        WHERE context.name IN $context_ids

        MATCH path = shortestPath(
            (target)-[rel*1..4]-(context)
        )

        WHERE ALL(
            relationship IN relationships(path)
            WHERE type(relationship) IN $semantic_relationships
        )

        RETURN
            target.name AS target_id,

            coalesce(
                target.label,
                target.name
            ) AS target_word,

            target.partOfSpeech
                AS target_part_of_speech,

            target.frequency
                AS target_frequency,

            context.name AS context_id,

            coalesce(
                context.label,
                context.name
            ) AS context_word,

            context.partOfSpeech
                AS context_part_of_speech,

            length(path) AS distance,

            [
                i IN range(
                    0,
                    length(path) - 1
                ) |
                {
                    source:
                        nodes(path)[i].name,

                    relationship:
                        type(
                            relationships(path)[i]
                        ),

                    target:
                        nodes(path)[i + 1].name
                }
            ] AS semantic_edges,

            [
                n IN nodes(path) |
                coalesce(
                    n.label,
                    n.name
                )
            ] AS path_labels,

            [
                n IN nodes(path) |
                n.name
            ] AS path_ids

        ORDER BY distance ASC

        LIMIT 20
        """

        try:

            records = session.run(
                query,
                target_ids=target_ids,
                context_ids=context_ids,
                semantic_relationships=SEMANTIC_RELATIONSHIPS
            )

            paths = [
                dict(record)
                for record in records
            ]

        except Exception as exc:

            print(
                "DISAMBIGUATION ENDPOINT ERROR:",
                type(exc).__name__,
                repr(str(exc))
            )

            raise HTTPException(
                status_code=500,
                detail=(
                    "Neo4j disambiguation query failed: "
                    f"{type(exc).__name__}: {str(exc)}"
                )
            )

        return {
            "input_target": word,
            "canonical_target_key": target_key,
            "context_analyzed": context_words,
            "context_canonical_keys": context_keys,
            "semantic_paths": paths
        }


# ============================================================
# Unified Search
#
# WordNet and Literary Corpus remain separate in Neo4j.
# FastAPI combines their results for the frontend.
# ============================================================

@app.get("/api/search/{query}")
def unified_search(query: str):

    # --------------------------------------------------------
    # Normalize input
    # --------------------------------------------------------

    tamil_lemma = get_tamil_lemma(query)

    if not tamil_lemma:
        raise HTTPException(
            status_code=400,
            detail="Unable to normalize the supplied word."
        )

    canonical_key = normalize_phonetic_key(
        tamil_lemma
    )

    try:

        with driver.session(
            database=NEO4J_DATABASE
        ) as session:

            # =================================================
            # 1. WORDNET SEARCH
            # =================================================

            wordnet_result = session.run(
                """
                MATCH (w:Word)
                WHERE
                    w.name = $search_query
                    OR w.label = $search_query
                    OR w.normalized_label = $canonical_key

                OPTIONAL MATCH (semantic:Word)-[:HASNODE]->(w)

                WITH DISTINCT
                    CASE
                        WHEN semantic IS NOT NULL
                        THEN semantic
                        ELSE w
                    END AS word

                OPTIONAL MATCH (word)-[r]->(related:Word)

                WHERE type(r) IN [
                    "SYNONYMOF",
                    "HYPERNYMOF",
                    "HYPONYMOF",
                    "MERONYMOF",
                    "HOLONYMOF",
                    "TROPONYMOF",
                    "RELATEDFORM",
                    "ANTONYMOF"
                ]

                RETURN
                    word,
                    type(r) AS relationship,
                    related
                """,
                search_query=query,
                canonical_key=canonical_key
            )

            wordnet_entries = {}

            for record in wordnet_result:

                word = record["word"]
                relationship = record["relationship"]
                related = record["related"]

                if word is None:
                    continue

                word_name = word.get("name")

                if not word_name:
                    continue

                if word_name not in wordnet_entries:

                    wordnet_entries[word_name] = {
                        "properties": dict(word),
                        "relationships": {}
                    }

                if relationship and related:

                    wordnet_entries[
                        word_name
                    ][
                        "relationships"
                    ].setdefault(
                        relationship,
                        []
                    )

                    wordnet_entries[
                        word_name
                    ][
                        "relationships"
                    ][
                        relationship
                    ].append(
                        dict(related)
                    )

            # =================================================
            # 2. LITERARY CORPUS SEARCH
            # =================================================

            corpus_result = session.run(
                """
                MATCH (wf:WordForm)

                WHERE
                    wf.canonical_key = $canonical_key
                    OR wf.sample = $search_query

                MATCH (wf)-[:APPEARS_IN]->(v:Verse)

                RETURN
                    wf.canonical_key AS canonical_key,
                    wf.sample AS tamil_word,
                    v.number AS verse_number,
                    v.book AS book,
                    v.section AS section,
                    v.text AS text

                ORDER BY
                    v.book,
                    v.number
                """,
                search_query=query,
                canonical_key=canonical_key
            )

            occurrences = []

            corpus_canonical_key = None
            corpus_tamil_word = None

            for record in corpus_result:

                corpus_canonical_key = (
                    record["canonical_key"]
                )

                corpus_tamil_word = (
                    record["tamil_word"]
                )

                occurrences.append({
                    "book": record["book"],
                    "section": record["section"],
                    "verse_number": record["verse_number"],
                    "text": record["text"]
                })

        # =====================================================
        # 3. COMBINED RESPONSE
        # =====================================================

        return {
            "query": query,

            "canonical_key": canonical_key,

            "wordnet": {
                "found": len(wordnet_entries) > 0,
                "entries": list(
                    wordnet_entries.values()
                )
            },

            "literary_corpus": {
                "found": len(occurrences) > 0,
                "word": corpus_tamil_word,
                "canonical_key": corpus_canonical_key,
                "occurrences": occurrences
            }
        }

    except HTTPException:
        raise

    except Exception as exc:

        print(
            "UNIFIED SEARCH ERROR:",
            type(exc).__name__,
            repr(str(exc))
        )

        raise HTTPException(
            status_code=500,
            detail=(
                "Unified search failed: "
                f"{type(exc).__name__}: {str(exc)}"
            )
        )