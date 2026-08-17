# Scalability and Throughput Benchmarks

This directory contains repeatable k6 benchmarks for the three scalability
areas in this project: Redis-backed pricing, Kafka-backed order processing,
and horizontally scaled HTTP services.

## What is verified in this repository

| Area | Implementation evidence | Automated verification | Live benchmark status |
|---|---|---|---|
| Redis cache and TTL | `PricingCacheService`, `RedisCacheConfig` | Pricing service unit tests | Requires pricing service + Redis |
| Distributed lock ownership and TTL | `DistributedLockService` uses `SET NX` and a compare-and-delete Lua script | Code inspection | Requires concurrent live requests |
| Kafka partitions and keyed ordering | Topic beans use explicit partitions; producers use aggregate/order IDs as keys | Consumer/unit tests | Requires broker and active services |
| Consumer concurrency | `@KafkaListener` concurrency is configured where high-volume consumers need it | Startup/unit tests | Requires multi-instance run |
| Producer reliability | `acks=all`, idempotence, batching, LZ4, and polling settings are supplied through Spring Kafka properties/configuration | Configuration inspection | Requires broker metrics |
| Horizontal service scaling | Gateway, Eureka, shared PostgreSQL/Redis/Kafka, Kubernetes replicas and HPAs | Manifest validation | Requires Kubernetes or multiple local instances |

The Docker Compose stack currently provides **one Redis node and one Kafka
broker**. It can validate application behavior and throughput, but it cannot
prove Redis Cluster failover or Kafka broker replication. Those require a
multi-node deployment.

## Prerequisites

Start infrastructure and the services being tested. `docker compose ps` should
show `api-gateway`, `pricing-service`, and/or `order-service` as healthy before
running a benchmark.

```bash
docker compose -f docker/docker-compose.yaml up -d
```

Obtain a JWT for protected order endpoints and set it in your shell:

```bash
export TOKEN='<access-token>'
export BASE_URL='http://localhost:8080'
```

## Redis pricing throughput

Warm the cache with one request, then execute the benchmark and retain the JSON
result. Do not report cache-hit throughput until Redis is reachable and the
pricing endpoint returns `200`.

```bash
PRODUCT_ID=1 k6 run --summary-export results/pricing-cache.json \
  k6/pricing-cache-test.js
```

Use a non-existent product for the negative-cache scenario:

```bash
PRODUCT_ID=999999 k6 run --summary-export results/pricing-negative-cache.json \
  k6/pricing-negative-cache-test.js
```

Inspect Prometheus for cache and lock behavior during the run:

```promql
sum(rate(pricing_cache_hit_total[5m]))
sum(rate(pricing_cache_miss_total[5m]))
sum(rate(pricing_cache_lock_acquired_total[5m]))
sum(rate(pricing_cache_lock_wait_total[5m]))
```

## Kafka/order throughput

This test creates orders through the gateway. Its HTTP request rate is an
ingress measure; confirm asynchronous throughput separately in Kafka consumer
metrics and topic lag.

```bash
TOKEN="$TOKEN" k6 run --summary-export results/order-load.json \
  k6/order-load-test.js
```

For each concurrency level, keep the same traffic profile and record:

| Consumer concurrency | k6 req/s | p95 | p99 | HTTP error rate | Consumer lag |
|---:|---:|---:|---:|---:|---:|
| 1 | | | | | |
| 3 | | | | | |
| 6 | | | | | |

Kafka order is preserved per key/aggregate, not globally. Scale consumer
instances only up to the partition count for the relevant topic.

## Horizontal scaling test

Use Kubernetes for the meaningful version of this test because the manifests
define replicas, HPAs, readiness probes, and service discovery.

1. Record a baseline with one replica.
2. Scale the gateway and target service to two replicas.
3. Repeat the identical k6 script.
4. Stop one target pod during the run and record failed requests and recovery.

```bash
kubectl -n event-stream scale deployment/pricing-service --replicas=2
kubectl -n event-stream get pods -w
```

Store the exported k6 JSON and the following table with the commit being
benchmarked. Never replace empty values with estimates.

| Instances | req/s | p95 | p99 | Error rate | Scaling efficiency |
|---:|---:|---:|---:|---:|---:|
| 1 | | | | | |
| 2 | | | | | |

## Current test result

On the latest local verification, Docker infrastructure (Redis, Kafka,
PostgreSQL, Prometheus, Grafana, Config Server, and Discovery Server) was
running, but application containers were not. Therefore **no live k6 result is
recorded**. The scripts are installed and ready; start the relevant services
before running them.
