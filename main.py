from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from dotenv import load_dotenv
from services.groq_client import GroqClient
from services.chroma_service import ChromaService

load_dotenv()

app = FastAPI()

groq_client = GroqClient()
chroma_service = ChromaService()

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


@app.post("/categorise")
def categorise(request: CategoriseRequest):
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

    result = groq_client.call(prompt)

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

    category = result["category"]
    if category not in CATEGORIES:
        category = "unknown"

    return {
        "category": category,
        "confidence": float(result.get("confidence", 0.0)),
        "reasoning": result.get("reasoning", "")
    }


@app.post("/query")
def query(request: QueryRequest):
    results = chroma_service.query(request.question, n_results=3)
    
    docs = results.get("documents") or [[]]
    chunks = docs[0] if docs else []
    
    if not chunks:
        return {
            "answer": "No relevant context found.",
            "sources": []
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
    
    result = groq_client.call(prompt, temperature=0.3)
    answer = result.get("content", result) if isinstance(result, dict) else result
    
    return {"answer": answer, "sources": sources}


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="127.0.0.1", port=8000, reload=True)