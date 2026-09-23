import os
import re
from typing import List, Dict, Any, Optional
from fastapi import FastAPI, HTTPException, Query
from fastapi.middleware.cors import CORSMiddleware
from neo4j import GraphDatabase, Driver
from pydantic import BaseModel
from lemmatizer import get_tamil_lemma, normalize_phonetic_key

# --- Neo4j Credentials ---
NEO4J_URI = os.getenv("NEO4J_URI", "neo4j+ssc://79ac8e4f.databases.neo4j.io")
NEO4J_USER = os.getenv("NEO4J_USER", "neo4j")
NEO4J_PASSWORD = os.getenv("NEO4J_PASSWORD", "ky5GglCEHCyCTqxjs6eCAq_htEzGBecq3ijn5Xe_kxo")

app = FastAPI(
    title="Tamil Literature NLP & Sense Disambiguation API",
    description="FastAPI service connecting React frontend to Neo4j AuraDB for Tamil WordNet graph analysis.",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

driver: Optional[Driver] = None

@app.on_event("startup")
def startup():
    global driver
    driver = GraphDatabase.driver(NEO4J_URI, auth=(NEO4J_USER, NEO4J_PASSWORD))
    print("✅ Connected to Neo4j AuraDB")

@app.on_event("shutdown")
def shutdown():
    global driver
    if driver:
        driver.close()
        print("🛑 Closed Neo4j connection")


# --- Response Schemas ---
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


# --- Shared Cypher Clauses ---
# Exact match clause (preserves word distinctions like sattai vs sathai)
CYPHER_EXACT_CLAUSE = """
toLower(coalesce(node.label, '')) = $raw_clean 
OR toLower(coalesce(node.name, '')) = $raw_clean
"""

# Aligned with lemmatizer.py soft dental logic ('th' -> 'd')
CYPHER_FUZZY_CLAUSE = """
replace(replace(replace(replace(replace(toLower(coalesce(node.label, '')), 'th', 'd'), 'tt', 't'), 'kk', 'k'), 'pp', 'p'), 'cc', 'c') CONTAINS $canonical_key
"""


@app.get("/")
def read_root():
    return {"status": "online", "message": "Tamil WordNet NLP Backend Running"}


@app.get("/api/word/{word_name}", response_model=Dict[str, Any])
def get_word_details(word_name: str):
    canonical_key = get_tamil_lemma(word_name)
    raw_clean = word_name.lower().strip()
    
    # Tier 1: Try exact match first
    query_exact = f"""
    MATCH (w:Word)
    WHERE {CYPHER_EXACT_CLAUSE.replace('node', 'w')}
    OPTIONAL MATCH (w)-[r]->(neighbor:Word)
    RETURN properties(w) AS word_properties, 
           collect(DISTINCT {{rel: type(r), target_id: neighbor.name, target_label: coalesce(neighbor.label, neighbor.name)}}) AS relationships
    LIMIT 1
    """
    
    # Tier 2: Fallback to fuzzy canonical match
    query_fuzzy = f"""
    MATCH (w:Word)
    WHERE {CYPHER_FUZZY_CLAUSE.replace('node', 'w')}
    OPTIONAL MATCH (w)-[r]->(neighbor:Word)
    RETURN properties(w) AS word_properties, 
           collect(DISTINCT {{rel: type(r), target_id: neighbor.name, target_label: coalesce(neighbor.label, neighbor.name)}}) AS relationships
    LIMIT 1
    """
    
    with driver.session() as session:
        result = session.run(query_exact, raw_clean=raw_clean).single()
        
        if not result or not result["word_properties"]:
            result = session.run(query_fuzzy, canonical_key=canonical_key).single()
        
        if not result or not result["word_properties"]:
            raise HTTPException(
                status_code=404, 
                detail=f"Word '{word_name}' (Canonical key: '{canonical_key}') not found in WordNet"
            )
        
        return {
            "input_word": word_name,
            "canonical_key": canonical_key,
            "properties": result["word_properties"],
            "relationships": result["relationships"]
        }


@app.get("/api/graph/subgraph", response_model=GraphResponse)
def get_word_subgraph(
    word_name: str = Query(..., description="Target Tamil word"),
    depth: int = Query(1, ge=1, le=3, description="Traversal depth (1 to 3)")
):
    canonical_key = get_tamil_lemma(word_name)
    raw_clean = word_name.lower().strip()
    
    # Query 1: Exact match traversal
    query_exact = f"""
    MATCH path = (w:Word)-[*1..{depth}]-(neighbor:Word)
    WHERE {CYPHER_EXACT_CLAUSE.replace('node', 'w')}
    WITH nodes(path) AS ns, relationships(path) AS rs
    UNWIND ns AS n
    UNWIND rs AS r
    RETURN collect(DISTINCT n) AS nodes, collect(DISTINCT r) AS rels
    LIMIT 100
    """
    
    # Query 2: Fuzzy canonical traversal
    query_fuzzy = f"""
    MATCH path = (w:Word)-[*1..{depth}]-(neighbor:Word)
    WHERE {CYPHER_FUZZY_CLAUSE.replace('node', 'w')}
    WITH nodes(path) AS ns, relationships(path) AS rs
    UNWIND ns AS n
    UNWIND rs AS r
    RETURN collect(DISTINCT n) AS nodes, collect(DISTINCT r) AS rels
    LIMIT 100
    """
    
    with driver.session() as session:
        # Step 1: Run Exact Traversal
        result = session.run(query_exact, raw_clean=raw_clean).single()
        
        # Step 2: Fallback to Fuzzy Traversal
        if not result or not result["nodes"]:
            result = session.run(query_fuzzy, canonical_key=canonical_key).single()
            
        # Step 3: Handle single isolated nodes (no edges)
        if not result or not result["nodes"]:
            fallback_query = f"""
            MATCH (w:Word)
            WHERE {CYPHER_EXACT_CLAUSE.replace('node', 'w')} OR {CYPHER_FUZZY_CLAUSE.replace('node', 'w')}
            RETURN w LIMIT 1
            """
            single_node = session.run(fallback_query, raw_clean=raw_clean, canonical_key=canonical_key).single()
            
            if not single_node:
                raise HTTPException(
                    status_code=404, 
                    detail=f"Word '{word_name}' (Canonical key: '{canonical_key}') not found in graph."
                )
            
            w_node = single_node["w"]
            display_label = w_node.get("label", w_node["name"])
            return GraphResponse(
                nodes=[NodeModel(
                    id=w_node["name"], 
                    label=display_label, 
                    properties=dict(w_node)
                )],
                edges=[]
            )

        nodes_list = [
            NodeModel(
                id=node["name"],
                label=node.get("label", node.get("name")),
                properties=dict(node)
            )
            for node in result["nodes"]
        ]

        edges_list = [
            EdgeModel(
                source=rel.start_node["name"], 
                target=rel.end_node["name"], 
                type=rel.type
            )
            for rel in result["rels"]
        ]

        return GraphResponse(nodes=nodes_list, edges=edges_list)


@app.get("/api/disambiguate", response_model=Dict[str, Any])
def disambiguate_sense(
    word: str = Query(..., description="Target word to disambiguate"),
    context_words: List[str] = Query(..., description="Context words present in sentence")
):
    target_key = get_tamil_lemma(word)
    raw_target = word.lower().strip()
    
    context_keys = [get_tamil_lemma(cw) for cw in context_words]
    raw_contexts = [cw.lower().strip() for cw in context_words]
    
    query = f"""
    MATCH (target:Word)
    WHERE {CYPHER_EXACT_CLAUSE.replace('node', 'target')} OR {CYPHER_FUZZY_CLAUSE.replace('node', 'target')}
    MATCH (context:Word) 
    WHERE {CYPHER_EXACT_CLAUSE.replace('node', 'context')} OR {CYPHER_FUZZY_CLAUSE.replace('node', 'context')}
    MATCH path = shortestPath((target)-[*..4]-(context))
    RETURN coalesce(target.label, target.name) AS target_word,
           coalesce(context.label, context.name) AS context_word, 
           length(path) AS distance, 
           [n IN nodes(path) | coalesce(n.label, n.name)] AS path_labels,
           [n IN nodes(path) | n.name] AS path_ids
    ORDER BY distance ASC
    LIMIT 10
    """
    
    with driver.session() as session:
        records = session.run(
            query, 
            raw_clean=raw_target,
            canonical_key=target_key, 
            raw_target=raw_target, 
            target_key=target_key, 
            raw_contexts=raw_contexts, 
            context_keys=context_keys
        )
        paths = [dict(record) for record in records]
        
        return {
            "input_target": word,
            "canonical_target_key": target_key,
            "context_analyzed": context_words,
            "semantic_paths": paths
        }