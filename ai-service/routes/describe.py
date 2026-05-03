from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
from datetime import datetime, timezone
import os

describe_bp = Blueprint("describe", __name__)

PROMPT = open(
    os.path.join(os.path.dirname(__file__), "../prompts/describe.txt")
).read()

@describe_bp.post("/describe")
def describe():
    body = request.get_json(silent=True)
    if not body or not body.get("item"):
        return jsonify({"error": "field 'item' is required"}), 400

    item = str(body["item"]).strip()
    if len(item) < 10:
        return jsonify({"error": "item too short (min 10 chars)"}), 400
    if len(item) > 2000:
        return jsonify({"error": "item too long (max 2000 chars)"}), 400

    prompt = PROMPT.replace("{item}", item)
    result = call_groq(prompt)

    if result is None:
        return jsonify({
            "error": "AI service unavailable",
            "is_fallback": True,
            "summary": "Description temporarily unavailable. Please retry.",
            "generated_at": datetime.now(timezone.utc).isoformat()
        }), 503

    result["generated_at"] = datetime.now(timezone.utc).isoformat()
    return jsonify(result), 200