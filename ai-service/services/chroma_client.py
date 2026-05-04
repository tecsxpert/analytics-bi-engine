import chromadb
from sentence_transformers import SentenceTransformer

_client = chromadb.PersistentClient(path="./chroma_data")
_model = SentenceTransformer("all-MiniLM-L6-v2")
_collection = _client.get_or_create_collection("bi_knowledge")

def embed_and_store(doc_id: str, text: str, metadata: dict = None):
    embedding = _model.encode(text).tolist()
    if metadata is None or len(metadata) == 0:
        metadata = {"source": doc_id}
    _collection.upsert(
        ids=[doc_id],
        embeddings=[embedding],
        documents=[text],
        metadatas=[metadata],
    )

def query_similar(query: str, n_results: int = 3) -> list:
    embedding = _model.encode(query).tolist()
    results = _collection.query(
        query_embeddings=[embedding],
        n_results=n_results,
    )
    return results["documents"][0] if results["documents"] else []

def doc_count() -> int:
    return _collection.count()