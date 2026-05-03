# AI Service — Tool-78 Analytics and BI Engine

Flask-based AI microservice powering the Analytics and BI Engine.
Runs on port 5000 and exposes 7 endpoints using Groq LLaMA-3.3-70b and ChromaDB RAG pipeline.

---

## Prerequisites

- Python 3.11+
- Groq API key (free at console.groq.com — no credit card)
- pip

---

## Setup

### 1. Clone the repository
```bash
git clone https://github.com/tecsxpert/analytics-bi-engine.git
cd ai-service
```

### 2. Install dependencies
```bash
pip install -r requirements.txt
```

### 3. Create `.env` file
```
GROQ_API_KEY=gsk_your_actual_key_here
```

### 4. Run the service
```bash
python app.py
```

Service starts at: `http://localhost:5000`

---

## Environment Variables

| Variable | Required | Description |
|----------|----------|-------------|
| `GROQ_API_KEY` | Yes | API key from console.groq.com |

---

## Tech Stack

| Technology | Purpose |
|------------|---------|
| Python 3.11 | Language |
| Flask 3.0.3 | Web framework |
| Groq API (LLaMA-3.3-70b) | AI model |
| ChromaDB | Vector database for RAG |
| sentence-transformers | Text embeddings |
| flask-limiter | Rate limiting (30 req/min) |

---

## API Reference

### GET /health
Returns service status, model info, ChromaDB doc count and uptime.

**Response:**
```json
{
  "status": "ok",
  "model": "llama-3.3-70b-versatile",
  "avg_response_time_ms": 0,
  "chroma_doc_count": 4,
  "uptime_seconds": 120,
  "endpoints": ["/describe", "/recommend", "/query", "/generate-report", "/analyse-document", "/batch-process"]
}
```

---

### POST /describe
Generates a professional description for a data item.

**Request:**
```json
{
  "item": "Q1 2026 revenue $2.3M up 12% YoY APAC region"
}
```

**Response:**
```json
{
  "summary": "Professional 2-3 sentence description",
  "key_metrics": ["Revenue", "YoY Growth", "Regional Performance"],
  "data_quality": "high",
  "insights": "Actionable business insight",
  "generated_at": "2026-05-03T10:00:00+00:00"
}
```

**Errors:**
- `400` — missing or invalid item
- `503` — AI unavailable (returns is_fallback: true)

---

### POST /recommend
Returns 3 actionable recommendations based on analytics context.

**Request:**
```json
{
  "context": "Q1 2026 revenue $2.3M, churn 4.2%, CSAT 87%"
}
```

**Response:**
```json
{
  "recommendations": [
    {"action_type": "investigate", "description": "...", "priority": "high"},
    {"action_type": "optimise", "description": "...", "priority": "medium"},
    {"action_type": "monitor", "description": "...", "priority": "low"}
  ]
}
```

---

### POST /query
RAG-powered Q&A — retrieves relevant chunks from ChromaDB and answers using Groq.

**Request:**
```json
{
  "question": "What was the APAC revenue in Q1 2026?"
}
```

**Response:**
```json
{
  "answer": "2.3M",
  "sources_used": true,
  "confidence": 0.85,
  "retrieved_chunks": 3
}
```

---

### POST /generate-report
Generates a full BI report. Supports SSE streaming with `?stream=true`.

**Request:**
```json
{
  "data": "Q1 2026 revenue 2.3M up 12% YoY, churn 4.2%, CSAT 87%"
}
```

**Response:**
```json
{
  "title": "Q1 2026 Business Performance Report",
  "executive_summary": "...",
  "overview": "...",
  "top_items": [
    {"rank": 1, "item": "Revenue Growth", "insight": "..."},
    {"rank": 2, "item": "Churn Rate", "insight": "..."},
    {"rank": 3, "item": "Customer Satisfaction", "insight": "..."}
  ],
  "recommendations": [
    {"priority": "high", "action": "..."},
    {"priority": "medium", "action": "..."},
    {"priority": "low", "action": "..."}
  ],
  "generated_at": "2026-05-03T10:00:00+00:00"
}
```

**Streaming mode:**
```
GET /generate-report?stream=true
data: {"token": "Q1"}
data: {"token": " 2026"}
...
data: {"done": true, "generated_at": "..."}
```

---

### POST /analyse-document
Analyses text and returns key insights, risks and opportunities.

**Request:**
```json
{
  "text": "Q1 2026 revenue grew 12%. Churn rate increased to 4.2%..."
}
```

**Response:**
```json
{
  "findings": [
    {"type": "insight", "title": "Revenue Growth", "description": "...", "severity": "low"},
    {"type": "risk", "title": "Churn Rate", "description": "...", "severity": "medium"}
  ],
  "overall_sentiment": "neutral",
  "confidence": 0.85,
  "word_count": 36,
  "generated_at": "2026-05-03T10:00:00+00:00"
}
```

---

### POST /batch-process
Processes up to 20 items in one request with 100ms delay between each.

**Request:**
```json
{
  "items": [
    "Q1 revenue 2.3M up 12%",
    "Churn rate 4.2% down from 6.1%",
    "CSAT score 87% stable"
  ]
}
```

**Response:**
```json
{
  "total": 3,
  "processed": 3,
  "results": [
    {"index": 0, "item": "...", "summary": "...", "category": "revenue", "score": 8.0, "flag": "normal"},
    {"index": 1, "item": "...", "summary": "...", "category": "customer", "score": 7.5, "flag": "normal"},
    {"index": 2, "item": "...", "summary": "...", "category": "customer", "score": 8.5, "flag": "normal"}
  ],
  "generated_at": "2026-05-03T10:00:00+00:00"
}
```

---

## Running Tests

```bash
pytest test_endpoints.py -v
```

Expected: 11 tests passing

---

## Folder Structure

```
ai-service/
├── routes/          # Flask blueprints for each endpoint
├── services/        # groq_client, chroma_client, rag_pipeline
├── prompts/         # Prompt templates for each endpoint
├── app.py           # Flask entry point
├── requirements.txt # Python dependencies
├── Dockerfile       # Container setup
└── README.md        # This file
```