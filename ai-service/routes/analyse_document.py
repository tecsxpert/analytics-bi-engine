from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
from datetime import datetime, timezone
import os

analyse_document_bp = Blueprint("analyse_document", __name__)

PROMPT = open(
    os.path.join(os.path.dirname(__file__), "../prompts/analyse_document.txt")
).read()

@analyse_document_bp.post("/analyse-document")
def analyse_document():
    body = request.get_json(silent=True)
    if not body or not body.get("text"):
        return jsonify({"error": "field 'text' is required"}), 400

    text = str(body["text"]).strip()
    if len(text) < 20:
        return jsonify({"error": "text too short (min 20 chars)"}), 400
    if len(text) > 5000:
        return jsonify({"error": "text too long (max 5000 chars)"}), 400

    word_count = len(text.split())
    prompt = PROMPT.replace("{text}", text)
    result = call_groq(prompt, temperature=0.3)

    if result is None:
        return jsonify({
            "is_fallback": True,
            "findings": [],
            "overall_sentiment": "neutral",
            "confidence": 0.0,
            "word_count": word_count,
            "generated_at": datetime.now(timezone.utc).isoformat()
        }), 503

    result["word_count"] = word_count
    result["generated_at"] = datetime.now(timezone.utc).isoformat()
    return jsonify(result), 200