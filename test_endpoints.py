import pytest
from unittest.mock import patch, MagicMock
from app import app

@pytest.fixture
def client():
    app.config["TESTING"] = True
    with app.test_client() as client:
        yield client

# /describe tests
def test_describe_missing_item(client):
    res = client.post("/describe", json={})
    assert res.status_code == 400

def test_describe_item_too_short(client):
    res = client.post("/describe", json={"item": "hi"})
    assert res.status_code == 400

def test_describe_item_too_long(client):
    res = client.post("/describe", json={"item": "x" * 2001})
    assert res.status_code == 400

@patch("routes.describe.call_groq")
def test_describe_success(mock_groq, client):
    mock_groq.return_value = {
        "summary": "Test summary",
        "key_metrics": ["m1", "m2"],
        "data_quality": "high",
        "insights": "Test insight"
    }
    res = client.post("/describe", json={"item": "Q1 2026 revenue grew 12% in APAC region"})
    assert res.status_code == 200
    data = res.get_json()
    assert "summary" in data
    assert "generated_at" in data

@patch("routes.describe.call_groq")
def test_describe_groq_failure(mock_groq, client):
    mock_groq.return_value = None
    res = client.post("/describe", json={"item": "Q1 2026 revenue grew 12% in APAC region"})
    assert res.status_code == 503
    assert res.get_json()["is_fallback"] == True

# /recommend tests
def test_recommend_missing_context(client):
    res = client.post("/recommend", json={})
    assert res.status_code == 400

@patch("routes.recommend.call_groq")
def test_recommend_success(mock_groq, client):
    mock_groq.return_value = [
        {"action_type": "investigate", "description": "Test", "priority": "high"},
        {"action_type": "optimise", "description": "Test", "priority": "medium"},
        {"action_type": "monitor", "description": "Test", "priority": "low"}
    ]
    res = client.post("/recommend", json={"context": "Q1 revenue data for APAC region"})
    assert res.status_code == 200
    assert "recommendations" in res.get_json()

@patch("routes.recommend.call_groq")
def test_recommend_groq_failure(mock_groq, client):
    mock_groq.return_value = None
    res = client.post("/recommend", json={"context": "Q1 revenue data for APAC region"})
    assert res.status_code == 503

# /analyse-document tests
def test_analyse_missing_text(client):
    res = client.post("/analyse-document", json={})
    assert res.status_code == 400

@patch("routes.analyse_document.call_groq")
def test_analyse_success(mock_groq, client):
    mock_groq.return_value = {
        "findings": [{"type": "insight", "title": "Test", "description": "Test desc", "severity": "low"}],
        "overall_sentiment": "positive",
        "confidence": 0.9,
        "word_count": 10
    }
    res = client.post("/analyse-document", json={"text": "Q1 2026 revenue grew 12% in APAC showing strong results"})
    assert res.status_code == 200
    data = res.get_json()
    assert "findings" in data
    assert "generated_at" in data

# /health test
def test_health(client):
    res = client.get("/health")
    assert res.status_code == 200
    data = res.get_json()
    assert data["status"] == "ok"
    assert "model" in data