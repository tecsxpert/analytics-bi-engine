import time
import requests
import numpy as np
import concurrent.futures

BASE_URL = "http://127.0.0.1:8000"

def benchmark_endpoint(endpoint, method, payload, num_requests=50):
    url = f"{BASE_URL}{endpoint}"
    times = []
    
    # We will run them sequentially to measure individual latency accurately
    for _ in range(num_requests):
        start = time.time()
        try:
            if method == "POST":
                res = requests.post(url, json=payload)
            else:
                res = requests.get(url)
            res.raise_for_status()
            times.append((time.time() - start) * 1000)
        except Exception as e:
            print(f"Error on request: {e}")
            
    if not times:
        return
        
    p50 = np.percentile(times, 50)
    p95 = np.percentile(times, 95)
    p99 = np.percentile(times, 99)
    avg = np.mean(times)
    
    print(f"--- Benchmark for {endpoint} ({num_requests} requests) ---")
    print(f"Average: {avg:.2f} ms")
    print(f"p50: {p50:.2f} ms")
    print(f"p95: {p95:.2f} ms")
    print(f"p99: {p99:.2f} ms")
    print("-" * 40)

def run_benchmarks():
    print("Wait for the server to be up...")
    try:
        requests.get(f"{BASE_URL}/health")
    except:
        print("Server is not running. Please start it with 'uvicorn main:app' before benchmarking.")
        return

    benchmark_endpoint("/categorise", "POST", {"text": "The new MacBook Pro is amazing!"})
    benchmark_endpoint("/query", "POST", {"question": "What is Python?"})
    benchmark_endpoint("/health", "GET", None)

if __name__ == "__main__":
    run_benchmarks()
