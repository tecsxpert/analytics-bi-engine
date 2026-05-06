import json
import hashlib
import logging
from redis import Redis
from redis.exceptions import ConnectionError

logger = logging.getLogger(__name__)

class RedisCache:
    def __init__(self, host='localhost', port=6379, db=0):
        self.client = Redis(host=host, port=port, db=db, decode_responses=True)
        self.stats = {"hits": 0, "misses": 0}
        
        # Test connection gracefully
        try:
            self.client.ping()
            self.enabled = True
        except ConnectionError:
            logger.warning("Redis server not available. Caching is disabled.")
            self.enabled = False

    def generate_key(self, prefix: str, data: dict) -> str:
        data_str = json.dumps(data, sort_keys=True)
        key_hash = hashlib.sha256(data_str.encode('utf-8')).hexdigest()
        return f"{prefix}:{key_hash}"

    def get(self, key: str):
        if not self.enabled:
            return None
            
        try:
            data = self.client.get(key)
            if data:
                self.stats["hits"] += 1
                return json.loads(data)
            else:
                self.stats["misses"] += 1
                return None
        except Exception as e:
            logger.error(f"Redis get error: {e}")
            return None

    def set(self, key: str, value: dict, ttl_seconds: int = 900):
        if not self.enabled:
            return
            
        try:
            self.client.setex(key, ttl_seconds, json.dumps(value))
        except Exception as e:
            logger.error(f"Redis set error: {e}")

    def get_stats(self):
        return self.stats
