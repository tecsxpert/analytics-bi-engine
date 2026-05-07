from services.rag_pipeline import ingest_document

documents = [
    {
        "id": "doc-01",
        "text": """
        Revenue Analysis Best Practices: Revenue growth should be measured year-over-year (YoY) 
        and quarter-over-quarter (QoQ). Key metrics include total revenue, revenue by region, 
        revenue by product line, and revenue per customer. A healthy growth rate for SaaS 
        companies is typically 15-25% YoY. APAC region has shown consistent growth potential 
        with enterprise client acquisition driving revenue expansion.
        """,
        "metadata": {"type": "knowledge", "topic": "revenue"}
    },
    {
        "id": "doc-02",
        "text": """
        Customer Churn Analysis: Churn rate measures the percentage of customers who stop 
        using a product in a given period. Industry average churn for B2B SaaS is 5-7% annually. 
        A churn rate below 5% is considered excellent. Key drivers of churn include poor 
        onboarding, lack of product adoption, competitive alternatives, and poor customer support. 
        Loyalty programmes and proactive customer success reduce churn significantly.
        """,
        "metadata": {"type": "knowledge", "topic": "churn"}
    },
    {
        "id": "doc-03",
        "text": """
        Customer Satisfaction (CSAT) Benchmarks: CSAT scores measure customer satisfaction 
        on a scale of 1-100. A score above 80% is considered good, above 90% is excellent. 
        CSAT is measured through post-interaction surveys. Key factors affecting CSAT include 
        response time, resolution quality, agent knowledge, and ease of contact. 
        Regular CSAT monitoring helps identify service gaps before they impact retention.
        """,
        "metadata": {"type": "knowledge", "topic": "csat"}
    },
    {
        "id": "doc-04",
        "text": """
        Support Ticket Management: Average ticket resolution time should be under 24 hours 
        for standard issues and under 4 hours for critical issues. Key metrics include 
        first response time, average resolution time, ticket volume, and escalation rate. 
        High ticket volume with long resolution times indicates understaffing or process 
        inefficiencies. Automation and self-service portals reduce ticket volume by 30-40%.
        """,
        "metadata": {"type": "knowledge", "topic": "support"}
    },
    {
        "id": "doc-05",
        "text": """
        APAC Market Analysis: The Asia-Pacific region represents the fastest growing market 
        for enterprise software. Key markets include Singapore, Tokyo, Sydney, and Mumbai. 
        Enterprise client acquisition in APAC typically involves longer sales cycles of 
        6-12 months but higher contract values. Cultural factors, local partnerships, and 
        regulatory compliance are critical success factors for APAC expansion.
        """,
        "metadata": {"type": "knowledge", "topic": "apac"}
    },
    {
        "id": "doc-06",
        "text": """
        Business Intelligence KPIs: Key performance indicators for BI dashboards include 
        revenue growth rate, customer acquisition cost (CAC), customer lifetime value (CLV), 
        net promoter score (NPS), monthly active users (MAU), and gross margin. 
        CAC should be less than one third of CLV for sustainable growth. 
        Dashboards should update in real-time and provide drill-down capability by region, 
        product, and time period.
        """,
        "metadata": {"type": "knowledge", "topic": "kpis"}
    },
    {
        "id": "doc-07",
        "text": """
        Inventory Management Metrics: Inventory turnover ratio measures how many times 
        inventory is sold and replaced in a period. A ratio of 6-12 is considered healthy 
        for most industries. Stockout rate should be below 2% to avoid lost sales. 
        Overstock ties up capital and increases holding costs. Demand forecasting models 
        using machine learning can improve inventory accuracy by 20-30%.
        """,
        "metadata": {"type": "knowledge", "topic": "inventory"}
    },
    {
        "id": "doc-08",
        "text": """
        Enterprise Sales Strategy: Enterprise client acquisition requires a multi-stakeholder 
        approach involving economic buyers, technical evaluators, and end users. 
        Average enterprise deal size ranges from 50k to 500k annually. 
        Key success factors include proof of concept delivery, security compliance documentation, 
        SLA guarantees, and dedicated customer success management. 
        Referrals from existing enterprise clients reduce sales cycle by 40%.
        """,
        "metadata": {"type": "knowledge", "topic": "sales"}
    },
    {
        "id": "doc-09",
        "text": """
        Data Quality Standards: High quality data must meet six dimensions — accuracy, 
        completeness, consistency, timeliness, validity, and uniqueness. 
        Data quality issues cost businesses an average of 15% of revenue annually. 
        Key data quality metrics include error rate, duplicate rate, null rate, 
        and freshness. Automated data validation pipelines catch 80% of quality 
        issues before they reach dashboards and reports.
        """,
        "metadata": {"type": "knowledge", "topic": "data_quality"}
    },
    {
        "id": "doc-10",
        "text": """
        Financial Performance Analysis: Quarterly financial reports should include 
        revenue, gross profit, operating expenses, EBITDA, and net income. 
        Gross margin above 70% is typical for SaaS businesses. 
        Operating expense ratio should decrease as revenue scales. 
        Year-over-year comparisons are more meaningful than sequential quarters 
        due to seasonality effects. Cash flow from operations is the most 
        important indicator of business health for growth stage companies.
        """,
        "metadata": {"type": "knowledge", "topic": "finance"}
    },
]

print("Seeding ChromaDB with 10 domain knowledge documents...")
for doc in documents:
    count = ingest_document(doc["id"], doc["text"], doc["metadata"])
    print(f"Ingested {doc['id']} — topic: {doc['metadata']['topic']} ({count} chunks)")

print("\nSeeding complete!")
print("Testing retrieval...")

from services.rag_pipeline import retrieve_context
test_queries = [
    "What is a good churn rate?",
    "APAC market growth",
    "How to improve CSAT scores?"
]

for query in test_queries:
    results = retrieve_context(query, n=2)
    print(f"\nQuery: '{query}'")
    print(f"Retrieved: {results[0][:100]}..." if results else "No results")

print("\nAll done — ChromaDB seeded and verified!")