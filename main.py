from fastapi import FastAPI, HTTPException, BackgroundTasks
from pydantic import BaseModel
from dotenv import load_dotenv
import time
import uuid
import urllib.request
import json

from services.groq_client import GroqClient
from services.chroma_service import ChromaService
from services.redis_service import RedisCache

load_dotenv()

app = FastAPI()

START_TIME = time.time()
response_times = []
jobs = {}

groq_client = GroqClient()
chroma_service = ChromaService()
redis_cache = RedisCache()

CATEGORIES = [
    "technology",
    "sports",
    "health",
    "finance",
    "entertainment",
    "politics",
    "science",
    "education",
    "travel",
    "food",
]



class CategoriseRequest(BaseModel):
    text: str


class QueryRequest(BaseModel):
    question: str

class ReportRequest(BaseModel):
    topic: str
    webhook_url: str = None

@app.post("/categorise")
def categorise(request: CategoriseRequest):
    cache_key = redis_cache.generate_key("categorise", {"text": request.text})
    cached_result = redis_cache.get(cache_key)
    if cached_result:
        cached_result["meta"]["cached"] = True
        return cached_result

    categories_str = ", ".join(CATEGORIES)
    
    prompt = [
        {
            "role": "system",
            "content": f"""You are a text classifier. Classify the input text into exactly one of these categories: {categories_str}.
Respond with ONLY a JSON object in this exact format (no other text):
{{"category": "category_name", "confidence": 0.9, "reasoning": "brief explanation"}}"""
        },
        {
            "role": "user",
            "content": request.text
        }
    ]

    result_raw = groq_client.call(prompt)
    result = result_raw["data"]
    meta = result_raw["metadata"]

    # If the response contains markdown wrapped JSON, try to extract it
    if "category" not in result and "content" in result:
        import json, re
        match = re.search(r"\{.*\}", result["content"], re.DOTALL)
        if match:
            try:
                parsed = json.loads(match.group(0))
                if "category" in parsed:
                    result = parsed
            except:
                pass

    if "category" not in result:
        raise HTTPException(status_code=500, detail="Invalid response from API")

    category = result.get("category", "unknown")
    if category not in CATEGORIES and category != "unknown":
        category = "unknown"
        
    confidence = float(result.get("confidence", 0.0))
    meta["confidence"] = confidence
    meta["cached"] = False
    meta["is_fallback"] = meta.get("is_fallback", False)

    response = {
        "category": category,
        "confidence": confidence,
        "reasoning": result.get("reasoning", ""),
        "meta": meta
    }
    
    response_times.append(meta["response_time_ms"])
    if len(response_times) > 10:
        response_times.pop(0)
        
    redis_cache.set(cache_key, response, ttl_seconds=900)
    return response


@app.post("/query")
def query(request: QueryRequest):
    cache_key = redis_cache.generate_key("query", {"question": request.question})
    cached_result = redis_cache.get(cache_key)
    if cached_result:
        cached_result["meta"]["cached"] = True
        return cached_result

    results = chroma_service.query(request.question, n_results=3)
    
    docs = results.get("documents") or [[]]
    chunks = docs[0] if docs else []
    
    if not chunks:
        return {
            "answer": "No relevant context found.",
            "sources": [],
            "meta": {"cached": False, "confidence": 0.0, "model_used": "none", "tokens_used": 0, "response_time_ms": 0}
        }
    
    context = "\n\n".join([f"Source {i+1}: {chunk}" for i, chunk in enumerate(chunks)])
    
    ids = results.get("ids") or [[]]
    metadatas = results.get("metadatas") or [[]]
    
    ids_list = ids[0] if ids else []
    metas_list = metadatas[0] if metadatas and metadatas[0] is not None else [{}] * len(ids_list)
    
    sources = [
        {"id": id_, "metadata": meta}
        for id_, meta in zip(ids_list, metas_list)
    ]
    
    prompt = [
        {
            "role": "system",
            "content": "You are a helpful assistant. Use the provided context to answer the user's question. If the context doesn't contain relevant information, say so."
        },
        {
            "role": "user",
            "content": f"Context:\n{context}\n\nQuestion: {request.question}"
        }
    ]
    
    result_raw = groq_client.call(prompt, temperature=0.3)
    result = result_raw["data"]
    meta = result_raw["metadata"]
    
    answer = result.get("content", result) if isinstance(result, dict) else result
    
    meta["cached"] = False
    meta["is_fallback"] = meta.get("is_fallback", False)
    meta["confidence"] = 0.95 if not meta.get("is_fallback") else 0.0
    
    response = {
        "answer": answer, 
        "sources": sources,
        "meta": meta
    }
    
    response_times.append(meta["response_time_ms"])
    if len(response_times) > 10:
        response_times.pop(0)
        
    redis_cache.set(cache_key, response, ttl_seconds=900)
    return response

@app.get("/health")
def health():
    uptime = time.time() - START_TIME
    avg_response_time = sum(response_times) / len(response_times) if response_times else 0.0
    doc_count = 0
    try:
        col = chroma_service.collection.get()
        doc_count = len(col['ids']) if col and 'ids' in col else 0
    except Exception:
        pass
        
    return {
        "status": "ok",
        "uptime_seconds": int(uptime),
        "model_name": groq_client.model,
        "avg_response_time_ms": avg_response_time,
        "chromadb_doc_count": doc_count,
        "cache_stats": redis_cache.get_stats()
    }

def process_report(job_id: str, topic: str, webhook_url: str):
    try:
        prompt = [
            {"role": "system", "content": "You are a report generator. Generate a comprehensive report on the given topic."},
            {"role": "user", "content": f"Topic: {topic}"}
        ]
        
        result_raw = groq_client.call(prompt, temperature=0.5)
        result = result_raw["data"]
        meta = result_raw["metadata"]
        
        answer = result.get("content", result) if isinstance(result, dict) else result
        meta["cached"] = False
        meta["is_fallback"] = meta.get("is_fallback", False)
        
        jobs[job_id] = {
            "status": "completed",
            "report": answer,
            "meta": meta
        }
        
        if webhook_url:
            try:
                req = urllib.request.Request(
                    webhook_url, 
                    data=json.dumps(jobs[job_id]).encode('utf-8'), 
                    headers={'Content-Type': 'application/json'}
                )
                urllib.request.urlopen(req, timeout=5)
            except Exception as e:
                print(f"Webhook failed: {e}")
                
    except Exception as e:
        jobs[job_id] = {
            "status": "failed",
            "error": str(e)
        }

@app.post("/generate-report")
def generate_report(request: ReportRequest, background_tasks: BackgroundTasks):
    job_id = str(uuid.uuid4())
    jobs[job_id] = {"status": "processing"}
    background_tasks.add_task(process_report, job_id, request.topic, request.webhook_url)
    return {"job_id": job_id, "status": "processing"}

@app.get("/report/{job_id}")
def get_report(job_id: str):
    if job_id not in jobs:
        raise HTTPException(status_code=404, detail="Job not found")
    return jobs[job_id]


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="127.0.0.1", port=8000, reload=True)