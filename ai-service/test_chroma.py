from services.chroma_client import embed_and_store, query_similar, doc_count

embed_and_store("test-1", "Revenue grew 12% in APAC due to new enterprise clients.")
embed_and_store("test-2", "Customer churn dropped after loyalty programme launch.")
embed_and_store("test-3", "Inventory turnover improved with demand forecasting model.")

results = query_similar("revenue growth Asia")
print("Query results:", results)
print("Total docs:", doc_count())
assert len(results) > 0, "ChromaDB query returned nothing!"
print("ChromaDB test PASSED")