# Pricing Service Load Tests

## Test

The pricing endpoint is tested using Grafana k6.

### Load profile

- 1 → 10 VUs
- 10 → 25 VUs
- 25 → 50 VUs
- 50 → 100 VUs
- Graceful ramp down

## Metrics

The tests measure:

- Requests per second
- HTTP error rate
- p95 latency
- p99 latency
- Cache hit rate
- Cache miss rate
- Distributed lock acquisition
- Distributed lock contention
- Negative cache hits

## Run

```bash
BASE_URL=http://localhost:8080 \
PRODUCT_ID=1 \
TOKEN="$JWT_TOKEN" \
k6 run k6/pricing-cache-test.js

