import pytest
from services.chroma_service import ChromaService


def test_embed_and_query_returns_correct_result():
    collection_name = "test_collection"
    service = ChromaService(collection_name=collection_name)
    service.clear()

    docs = [
        "Python is a high-level programming language.",
        "JavaScript is used for web development.",
        "Docker is a containerization platform.",
    ]
    service.add_documents(docs, ["py", "js", "dock"])

    query = "What is Python?"
    results = service.query(query, n_results=1)

    assert results is not None
    assert len(results["ids"]) > 0
    assert "py" in results["ids"][0]

    service.clear()