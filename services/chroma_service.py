import logging
from typing import Optional
from chromadb import PersistentClient
from chromadb.config import Settings

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(levelname)s - %(message)s"
)
logger = logging.getLogger(__name__)


class ChromaService:
    def __init__(self, collection_name: str = "default", persist_directory: str = "./chroma_data"):
        self.client = PersistentClient(path=persist_directory, settings=Settings(anonymized_telemetry=False))
        self.collection = self.client.get_or_create_collection(name=collection_name)
        logger.info(f"ChromaDB collection '{collection_name}' initialized at {persist_directory}")

    def add_documents(self, documents: list[str], ids: list[str], metadata: Optional[list[dict]] = None):
        if metadata is None:
            metadata = [{"source": "default"}] * len(documents)

        self.collection.upsert(
            documents=documents,
            ids=ids,
            metadatas=metadata
        )
        logger.info(f"Added {len(documents)} documents to collection")

    def query(self, query_text: str, n_results: int = 3) -> dict:
        results = self.collection.query(
            query_texts=[query_text],
            n_results=n_results
        )
        logger.info(f"Query '{query_text}' returned {len(results.get('ids', [[]])[0])} results")
        return results

    def get_all(self) -> dict:
        return self.collection.get()

    def clear(self):
        self.client.delete_collection(name=self.collection.name)
        self.collection = self.client.get_or_create_collection(name=self.collection.name)
        logger.info(f"Collection '{self.collection.name}' cleared")