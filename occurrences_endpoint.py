# --- Add this to main.py -----------------------------------------------
# Paste this route alongside your other endpoints (e.g. right after
# get_word_details). It reuses the same driver / get_tamil_lemma already
# set up in main.py, so no extra imports are needed there.

@app.get("/api/word/{word_name}/occurrences", response_model=Dict[str, Any])
def get_word_occurrences(word_name: str):
    """
    Returns every place `word_name` appears in the literary corpus,
    grouped by work (book), each with its author and the list of specific
    parts (Thirukkural: section + verse number; Purananuru: poem number
    + title), so the frontend can list the works first and, on click,
    show where in that work the word occurs.
    """
    canonical_key = get_tamil_lemma(word_name)

    query = """
    MATCH (wf:WordForm {canonical_key: $key})-[:APPEARS_IN]->(item)
    OPTIONAL MATCH (item)<-[:HAS_VERSE]-(section:Section)<-[:HAS_SECTION]-(kwork:Work)
    OPTIONAL MATCH (item)<-[:HAS_POEM]-(pwork:Work)
    OPTIONAL MATCH (item)-[:WRITTEN_BY]->(author:Author)
    OPTIONAL MATCH (item)-[:SUNG_FOR]->(person:Person)
    RETURN
        coalesce(kwork.name, pwork.name)      AS book,
        author.name                            AS author,
        CASE WHEN item:Verse THEN 'குறள்' ELSE 'பாடல்' END AS unit_type,
        item.number                            AS number,
        CASE WHEN item:Verse THEN section.name ELSE item.thinai END AS part_label,
        item.title                             AS title,
        item.text                              AS text,
        person.name                            AS sung_for
    ORDER BY book, number
    """

    with driver.session() as session:
        rows = [dict(r) for r in session.run(query, key=canonical_key)]

    if not rows:
        raise HTTPException(
            status_code=404,
            detail=f"Word '{word_name}' (canonical key: '{canonical_key}') not found in literary corpus"
        )

    works: Dict[str, Any] = {}
    for r in rows:
        book = r["book"]
        entry = works.setdefault(book, {"author": r["author"], "occurrences": []})
        entry["occurrences"].append({
            "unit_type": r["unit_type"],       # 'குறள்' or 'பாடல்'
            "number": r["number"],             # kural number, or poem_number
            "part": r["part_label"],           # section name (Kural) or thinai (Puram)
            "title": r["title"],               # only set for Purananuru
            "sung_for": r["sung_for"],
            "text": r["text"],
        })

    return {
        "input_word": word_name,
        "canonical_key": canonical_key,
        "works": works
    }
# -------------------------------------------------------------------------
