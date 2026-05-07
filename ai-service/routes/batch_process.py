from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
from datetime import datetime, timezone
import os
import time

batch_process_bp = Blueprint("batch_process", __name__)

PROMPT = open(
    os.path.join(os.path.dirname(__file__), "../prompts/batch_process.txt")
).read()

@batch_process_bp.post("/batch-process")
def batch_process():
    body = request.get_json(silent=True)
    if not body or not body.get("items"):
        return jsonify({"error": "field 'items' is required"}), 400

    items = body["items"]
    if not isinstance(items, list):
        return jsonify({"error": "'items' must be an array"}), 400
    if len(items) == 0:
        return jsonify({"error": "items array is empty"}), 400
    if len(items) > 20:
        return jsonify({"error": "max 20 items allowed"}), 400

    results = []
    for i, item in enumerate(items):
        item_str = str(item).strip()
        if not item_str:
            results.append({
                "index": i,
                "item": item_str,
                "error": "empty item skipped"
            })
            continue

        prompt = PROMPT.replace("{item}", item_str)
        result = call_groq(prompt, temperature=0.3)

        if result is None:
            results.append({
                "index": i,
                "item": item_str,
                "is_fallback": True,
                "summary": "Processing unavailable",
                "flag": "review"
            })
        else:
            result["index"] = i
            result["item"] = item_str
            results.append(result)

        time.sleep(0.1)

    return jsonify({
        "total": len(items),
        "processed": len(results),
        "results": results,
        "generated_at": datetime.now(timezone.utc).isoformat()
    }), 200