import os
from dotenv import load_dotenv

load_dotenv()

from groq import Groq

client = Groq(api_key=os.environ.get("GROQ_API_KEY"))

chat_completion = client.chat.completions.create(
    model="llama-3.3-70b-versatile",
    messages=[{"role": "user", "content": "Hello! Just testing the Groq API."}],
    temperature=0.7,
)

print(f"Success! Response: {chat_completion.choices[0].message.content}")