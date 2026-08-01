# Gateway Dashboard

`gateway-dashboard.json` visualizes rate limiting at the API Gateway using the
Prometheus metric `gateway_rate_limit_rejected_total`.

Panels include:

- Total rejected requests
- Rejected requests by route and HTTP method
- Rejections over time and per second
- Top throttled routes

## Import

In Grafana, select **Dashboards → New → Import**, upload
`gateway-dashboard.json`, and select the Prometheus data source when prompted.
The dashboard uses a data-source variable, so the same file works in local,
staging, and production environments.

## Test it locally

Generate more requests than the configured bucket allows:

```bash
for i in {1..100}; do
  curl -s -o /dev/null -w '%{http_code}\n' http://localhost:8080/auth/rate-limit-test
done
```

For a Kubernetes deployment, send the same requests through its ingress URL.
The rejected-request metric, time series, and top-routes panel will update as
Prometheus scrapes the gateway actuator endpoint.

## Why this file is in Git

Dashboards are production configuration. Keeping them in version control makes
changes reviewable, reversible, and reusable across environments.
