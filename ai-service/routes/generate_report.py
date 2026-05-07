from flask import Blueprint, request, jsonify, Response, stream_with_context
from services.groq_client import call_groq
from datetime import datetime, timezone
import os
import json

generate_report_bp = Blueprint("generate_report", __name__)

PROMPT = open(
    os.path.join(os.path.dirname(__file__), "../prompts/generate_report.txt")
).read()

FALLBACK = {
    "is_fallback": True,
    "title": "Report Temporarily Unavailable",
    "executive_summary": "AI service is currently unavailable. Please retry.",
    "overview": "N/A",
    "top_items": [],
    "recommendations": []
}

def stream_report(data: str):
    from groq import Groq
    from dotenv import load_dotenv
    load_dotenv()

    client = Groq(api_key=os.getenv("GROQ_API_KEY"))
    prompt = PROMPT.replace("{data}", data)

    try:
        stream = client.chat.completions.create(
            model="llama-3.3-70b-versatile",
            messages=[{"role": "user", "content": prompt}],
            temperature=0.5,
            max_tokens=1000,
            stream=True,
        )
        for chunk in stream:
            token = chunk.choices[0].delta.content
            if token:
                yield f"data: {json.dumps({'token': token})}\n\n"

        yield f"data: {json.dumps({'done': True, 'generated_at': datetime.now(timezone.utc).isoformat()})}\n\n"

    except Exception as e:
        yield f"data: {json.dumps({'error': str(e), 'is_fallback': True})}\n\n"


@generate_report_bp.post("/generate-report")
def generate_report():
    body = request.get_json(silent=True)
    if not body or not body.get("data"):
        return jsonify({"error": "field 'data' is required"}), 400

    data = str(body["data"]).strip()
    if len(data) < 20:
        return jsonify({"error": "data too short (min 20 chars)"}), 400

    stream = request.args.get("stream", "false").lower() == "true"

    if stream:
        return Response(
            stream_with_context(stream_report(data)),
            mimetype="text/event-stream",
            headers={
                "Cache-Control": "no-cache",
                "X-Accel-Buffering": "no",
            }
        )

    result = call_groq(PROMPT.replace("{data}", data), temperature=0.5, max_tokens=1000)
    if result is None:
        return jsonify(FALLBACK), 503

    result["generated_at"] = datetime.now(timezone.utc).isoformat()
    return jsonify(result), 200