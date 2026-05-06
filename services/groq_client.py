import logging
import os
import time
import json
from typing import Any

from groq import Groq

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(levelname)s - %(message)s"
)
logger = logging.getLogger(__name__)


class GroqClient:
    def __init__(self, api_key: str = None, model: str = "llama-3.3-70b-versatile"):
        if api_key is None:
            api_key = os.environ.get("GROQ_API_KEY")
        if not api_key:
            raise ValueError("API key is required. Provide GROQ_API_KEY env var or pass it explicitly.")
        self.client = Groq(api_key=api_key)
        self.model = model

    def call(self, messages: list[dict], temperature: float = 0.7, max_retries: int = 3) -> dict:
        backoff = 1.0
        last_exception = None

        for attempt in range(max_retries):
            try:
                start_time = time.time()
                response = self.client.chat.completions.create(
                    model=self.model,
                    messages=messages,
                    temperature=temperature,
                )
                end_time = time.time()
                
                content = response.choices[0].message.content
                tokens_used = response.usage.total_tokens if response.usage else 0
                response_time_ms = int((end_time - start_time) * 1000)
                
                metadata = {
                    "tokens_used": tokens_used,
                    "response_time_ms": response_time_ms,
                    "model_used": self.model
                }
                
                try:
                    parsed = json.loads(content)
                    logger.info(f"API call successful on attempt {attempt + 1}")
                    return {"data": parsed, "metadata": metadata}
                except json.JSONDecodeError:
                    logger.warning("Response is not valid JSON, returning raw content")
                    return {"data": {"content": content}, "metadata": metadata}

            except Exception as e:
                last_exception = e
                logger.error(f"API call failed (attempt {attempt + 1}/{max_retries}): {e}")
                if attempt < max_retries - 1:
                    logger.info(f"Retrying in {backoff} seconds...")
                    time.sleep(backoff)
                    backoff *= 2

        raise last_exception

    def call_with_json_response(
        self,
        messages: list[dict],
        temperature: float = 0.7,
        max_retries: int = 3
    ) -> Any:
        return self.call(messages, temperature, max_retries)