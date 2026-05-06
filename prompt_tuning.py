import asyncio
import os
import json
from dotenv import load_dotenv
from services.groq_client import GroqClient

load_dotenv()
client = GroqClient()

# Test cases for Categorise
CATEGORISE_TESTS = [
    "The new iPhone 15 features a titanium body and USB-C port.",
    "The Lakers won the championship after a stunning fourth quarter comeback.",
    "Eating a balanced diet and regular exercise is key to longevity.",
    "The stock market saw a 5% drop amid inflation fears.",
    "The latest Marvel movie broke box office records.",
    "The senate passed the new infrastructure bill today.",
    "Scientists discovered a new exoplanet with water vapor.",
    "Online learning platforms have seen a surge in users.",
    "A guide to the best beaches in Bali for digital nomads.",
    "How to make the perfect sourdough bread at home."
]
EXPECTED_CATEGORIES = [
    "technology", "sports", "health", "finance", "entertainment",
    "politics", "science", "education", "travel", "food"
]

def test_categorise():
    print("--- Running Prompt Tuning for Categorise ---")
    score = 0
    for i, text in enumerate(CATEGORISE_TESTS):
        prompt = [
            {
                "role": "system",
                "content": f"""You are a text classifier. Classify the input text into exactly one of these categories: technology, sports, health, finance, entertainment, politics, science, education, travel, food.
Respond with ONLY a JSON object in this exact format (no other text):
{{"category": "category_name", "confidence": 0.9, "reasoning": "brief explanation"}}"""
            },
            {
                "role": "user",
                "content": text
            }
        ]
        
        try:
            res = client.call(prompt, temperature=0.0)
            data = res.get("data", res)
            category = data.get("category", "")
            
            is_correct = category.lower() == EXPECTED_CATEGORIES[i].lower()
            if is_correct:
                score += 1
                print(f"Pass: {EXPECTED_CATEGORIES[i]} -> {category}")
            else:
                print(f"Fail: expected {EXPECTED_CATEGORIES[i]}, got {category}")
                print(f"Response: {data}")
        except Exception as e:
            print(f"Error on {text}: {e}")
            
    print(f"Categorise Score: {score}/10\n")
    return score

def test_query():
    print("--- Running Prompt Tuning for Query ---")
    QUERY_TESTS = [
        {"context": "The Eiffel Tower is located in Paris, France.", "question": "Where is the Eiffel Tower?", "expected": "paris"},
        {"context": "Water boils at 100 degrees Celsius at 1 atmosphere of pressure.", "question": "At what temperature does water boil?", "expected": "100"},
        {"context": "Apple was founded by Steve Jobs, Steve Wozniak, and Ronald Wayne.", "question": "Who founded Apple?", "expected": "steve jobs"},
        {"context": "The capital of Japan is Tokyo.", "question": "What is Japan's capital?", "expected": "tokyo"},
        {"context": "Mount Everest is the highest mountain on Earth above sea level.", "question": "What is the highest mountain?", "expected": "everest"},
        {"context": "The speed of light is approximately 299,792 kilometers per second.", "question": "How fast is light?", "expected": "299"},
        {"context": "Jupiter is the largest planet in the Solar System.", "question": "Which planet is the largest?", "expected": "jupiter"},
        {"context": "Shakespeare wrote Romeo and Juliet.", "question": "Who wrote Romeo and Juliet?", "expected": "shakespeare"},
        {"context": "The human body has 206 bones.", "question": "How many bones are in the human body?", "expected": "206"},
        {"context": "Oxygen makes up about 21% of the Earth's atmosphere.", "question": "What percentage of the atmosphere is Oxygen?", "expected": "21"}
    ]
    
    score = 0
    for i, test in enumerate(QUERY_TESTS):
        prompt = [
            {
                "role": "system",
                "content": "You are a helpful assistant. Use the provided context to answer the user's question. If the context doesn't contain relevant information, say so."
            },
            {
                "role": "user",
                "content": f"Context:\n{test['context']}\n\nQuestion: {test['question']}"
            }
        ]
        
        try:
            res = client.call(prompt, temperature=0.3)
            data = res.get("data", res)
            answer = data.get("content", str(data)).lower()
            
            is_correct = test['expected'] in answer
            if is_correct:
                score += 1
                print(f"Pass: {test['question']} -> {test['expected']}")
            else:
                print(f"Fail: expected {test['expected']}, got {answer}")
        except Exception as e:
            print(f"Error on {test['question']}: {e}")
            
    print(f"Query Score: {score}/10\n")
    return score

if __name__ == "__main__":
    test_categorise()
    test_query()
