from services.chroma_client import embed_and_store, query_similar

def chunk_text(text: str, size: int = 500, overlap: int = 50) -> list:
    chunks = []
    start = 0
    while start < len(text):
        end = start + size
        chunks.append(text[start:end])
        start += size - overlap
    return chunks

def ingest_document(doc_id: str, text: str, metadata: dict = None):
    chunks = chunk_text(text)
    for i, chunk in enumerate(chunks):
        meta = {"source": doc_id, "chunk_index": i}
        if metadata:
            meta.update(metadata)
        embed_and_store(
            doc_id=f"{doc_id}-chunk-{i}",
            text=chunk,
            metadata=meta,
        )
    return len(chunks)

def retrieve_context(query: str, n: int = 3) -> list:
    return query_similar(query, n_results=n)