import os, time, json
from groq import Groq
from dotenv import load_dotenv

load_dotenv()

client = Groq(api_key=os.getenv("GROQ_API_KEY"))

def call_groq(prompt: str, temperature: float = 0.3,
              max_tokens: int = 1000, retries: int = 3) -> dict | None:
    for attempt in range(retries):
        try:
            res = client.chat.completions.create(
                model="llama-3.3-70b-versatile",
                messages=[{"role": "user", "content": prompt}],
                temperature=temperature,
                max_tokens=max_tokens,
            )
            text = res.choices[0].message.content.strip()
            return json.loads(text)
        except json.JSONDecodeError:
            return {"raw": text}
        except Exception as e:
            if attempt < retries - 1:
                time.sleep(2 ** attempt)
            else:
                print(f"Groq error: {e}")
                return None