import asyncio
import os
import json
from dotenv import load_dotenv
from services.groq_client import GroqClient

load_dotenv()
client = GroqClient()

def qa_endpoint(name, tests, system_prompt_builder, user_prompt_builder):
    print(f"--- Running Final QA for {name} ({len(tests)} records) ---")
    score = 0
    for i, test in enumerate(tests):
        prompt = [
            {"role": "system", "content": system_prompt_builder(test)},
            {"role": "user", "content": user_prompt_builder(test)}
        ]
        try:
            res = client.call(prompt, temperature=0.3)
            data = res.get("data", res)
            answer = data.get("content", str(data)).lower() if isinstance(data, dict) else str(data).lower()
            
            # Simple check if expected keyword is in the response (or exact match for category)
            if 'expected' in test and test['expected'].lower() in answer:
                score += 1
            elif 'expected_category' in test and test['expected_category'].lower() in str(data.get('category', '')).lower():
                score += 1
            else:
                print(f"[{name}] Fail on record {i+1}: expected {test.get('expected', test.get('expected_category'))}, got {answer}")
        except Exception as e:
            print(f"Error on record {i+1}: {e}")
            
    print(f"{name} QA Score: {score}/{len(tests)}\n")
    return score

if __name__ == "__main__":
    # We will simulate 30 demo records here
    categorise_tests = [{"text": f"News about topic {i}", "expected_category": "technology"} for i in range(15)] + \
                       [{"text": f"Cooking show episode {i}", "expected_category": "food"} for i in range(15)]
                       
    query_tests = [{"context": f"Fact {i} is that water is wet.", "question": f"Is water wet in fact {i}?", "expected": "wet"} for i in range(30)]
    
    qa_endpoint("Categorise", categorise_tests, 
                lambda t: "You are a text classifier. Classify the input text into: technology, sports, health, finance, entertainment, politics, science, education, travel, food. Respond with JSON.",
                lambda t: t['text'])
                
    qa_endpoint("Query", query_tests,
                lambda t: "You are a helpful assistant. Use the provided context to answer the user's question.",
                lambda t: f"Context:\n{t['context']}\n\nQuestion: {t['question']}")
