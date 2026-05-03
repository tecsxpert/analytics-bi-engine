from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
import os

recommend_bp = Blueprint("recommend", __name__)

PROMPT = open(
    os.path.join(os.path.dirname(__file__), "../prompts/recommend.txt")
).read()

@recommend_bp.post("/recommend")
def recommend():
    body = request.get_json(silent=True)
    if not body or not body.get("context"):
        return jsonify({"error": "field 'context' is required"}), 400

    context = str(body["context"]).strip()
    prompt = PROMPT.replace("{context}", context)
    result = call_groq(prompt, temperature=0.4)

    if result is None:
        return jsonify({
            "is_fallback": True,
            "recommendations": [
                {"action_type": "monitor", "description": "Review data manually — AI unavailable.", "priority": "high"}
            ]
        }), 503

    if isinstance(result, list):
        return jsonify({"recommendations": result}), 200
    return jsonify({"recommendations": result.get("recommendations", result)}), 200