from services.rag_pipeline import ingest_document, retrieve_context

ingest_document("report-q1", """
Q1 2026 APAC revenue reached 2.3M, a 12% year-over-year increase.
Growth was driven by three new enterprise clients in Singapore and Tokyo.
Customer churn improved to 4.2% from 6.1% last quarter following the
loyalty programme launch. Support CSAT remained strong at 87%.
""", metadata={"type": "quarterly_report", "period": "Q1-2026"})

chunks = retrieve_context("APAC revenue growth")
print("Retrieved chunks:", chunks)
assert len(chunks) > 0, "RAG retrieval failed!"
print("RAG pipeline PASSED")