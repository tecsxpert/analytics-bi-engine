from flask import Blueprint, request, jsonify
from services.rag_pipeline import retrieve_context
from services.groq_client import call_groq
import os

query_bp = Blueprint("query", __name__)

PROMPT = open(
    os.path.join(os.path.dirname(__file__), "../prompts/rag_query.txt")
).read()

@query_bp.post("/query")
def rag_query():
    body = request.get_json(silent=True)
    if not body or not body.get("question"):
        return jsonify({"error": "field 'question' is required"}), 400

    question = str(body["question"]).strip()
    chunks = retrieve_context(question, n=3)

    if not chunks:
        return jsonify({
            "answer": "No relevant data found.",
            "sources_used": False
        }), 200

    context = "\n\n".join(chunks)
    prompt = PROMPT.replace("{context}", context).replace("{question}", question)
    result = call_groq(prompt, temperature=0.3)

    if result is None:
        return jsonify({
            "error": "AI unavailable",
            "is_fallback": True
        }), 503

    result["retrieved_chunks"] = len(chunks)
    return jsonify(result), 200