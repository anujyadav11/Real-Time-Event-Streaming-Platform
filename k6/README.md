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
```

## Kafka Scalability Benchmark

The event streaming pipeline was tested using k6 under increasing
concurrent load.

### Consumer concurrency

| Concurrency | Throughput | p95 | p99 | Error Rate |
|---:|---:|---:|---:|---:|
| 1 | TBD | TBD | TBD | TBD |
| 3 | TBD | TBD | TBD | TBD |
| 6 | TBD | TBD | TBD | TBD |

### Kafka design

High-volume event topics use multiple partitions.

`orderId` is used as the Kafka message key to preserve ordering
for events belonging to the same order while allowing different
orders to be processed concurrently.

### Load test

```bash
BASE_URL=http://localhost:8080 \
TOKEN="$JWT_TOKEN" \
k6 run k6/order-load-test.js
```
