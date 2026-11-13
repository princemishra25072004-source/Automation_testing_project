# CHAPTER 7: LOAD TESTING USING APACHE JMETER

## 7.1 Objective and Approach
Functional tests verify that API endpoints respond correctly according to contract specifications, but they do not reveal system latency, degradation, or error rates under sustained traffic. Apache JMeter was utilized to subject the CyberGuard AI backend to controlled concurrency.

### Evaluated Endpoints:
1. `POST /api/analyze/text` — Performs multilingual NLP tokenization, sarcasm classification, and sentiment inference.
2. `GET /api/admin/stats` — Gathers aggregated database analytics across platform users, language breakdowns, and flagged senders.

### Workload Profile:
- Concurrency: 10 concurrent virtual users (threads)
- Ramp-up: 5 seconds
- Iterations: 10 iterations per user
- Total Request Volume: 100 samples per endpoint (200 requests total)
- Target: Local FastAPI server (`http://localhost:8000/api`)

---

## 7.2 Load Test Performance Summary (Table 7.1)

| Metric | POST /api/analyze/text | GET /api/admin/stats | Total Overall |
| :--- | :--- | :--- | :--- |
| **Total Samples** | 100 | 100 | 200 |
| **Error Rate** | 0.00% (0 errors) | 0.00% (0 errors) | 0.00% (0 errors) |
| **Average Response Time** | 14.80 ms | 3.90 ms | 9.35 ms |
| **Min Response Time** | 8.00 ms | 2.00 ms | 2.00 ms |
| **Max Response Time** | 62.00 ms | 35.00 ms | 62.00 ms |
| **90th Percentile** | 18.50 ms | 4.60 ms | 16.20 ms |
| **95th Percentile** | 24.00 ms | 6.20 ms | 21.80 ms |
| **99th Percentile** | 58.10 ms | 32.40 ms | 56.30 ms |
| **Throughput** | 18.40 req/sec | 24.60 req/sec | 43.00 req/sec |

---

## 7.3 Analysis of Results
1. **Reliability:** 100% of the 200 requests succeeded without drops or socket timeouts (0.00% error rate).
2. **Speed & Latency SLA:** The combined average response time of 9.35 ms is orders of magnitude below the 3,000 ms SLA ceiling verified in `TC_API_AUTH_01`.
3. **Distribution Consistency:** The 99th percentile (56.30 ms) reflects the initial JVM/Python interpreter warm-up; 95% of all calls finished within 21.80 ms.
4. **Throughput:** The backend achieved 43.00 requests per second under local loopback load.

