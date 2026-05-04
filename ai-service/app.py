from flask import Flask, jsonify
from dotenv import load_dotenv
from routes.describe import describe_bp
from routes.recommend import recommend_bp
from routes.query import query_bp
from routes.generate_report import generate_report_bp
from routes.analyse_document import analyse_document_bp

from routes.batch_process import batch_process_bp
import time

load_dotenv()
app = Flask(__name__)

app.register_blueprint(describe_bp)
app.register_blueprint(recommend_bp)
app.register_blueprint(query_bp)
app.register_blueprint(generate_report_bp)
app.register_blueprint(analyse_document_bp)
app.register_blueprint(batch_process_bp)

START_TIME = time.time()
response_times = []

@app.get("/health")
def health():
    from services.chroma_client import doc_count
    uptime_seconds = int(time.time() - START_TIME)
    avg_response = round(sum(response_times[-10:]) / len(response_times[-10:]), 2) if response_times else 0
    return jsonify({
        "status": "ok",
        "model": "llama-3.3-70b-versatile",
        "avg_response_time_ms": avg_response,
        "chroma_doc_count": doc_count(),
        "uptime_seconds": uptime_seconds,
        "endpoints": [
            "/describe",
            "/recommend",
            "/query",
            "/generate-report",
            "/analyse-document",
            "/batch-process"
        ]
    }), 200

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)