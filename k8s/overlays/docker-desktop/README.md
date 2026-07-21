# Docker Desktop deployment

This overlay runs the complete platform on Docker Desktop's single Kubernetes
node without the surge replicas used by the production-oriented base.

Build the application and local images before applying the overlay:

```bash
./mvnw clean package -DskipTests

docker build -f docker/api-gateway/Dockerfile -t event-platform/api-gateway:swagger-test .
docker build -f docker/auth-service/Dockerfile -t event-platform/auth-service:swagger-test .
docker build -f docker/order-service/Dockerfile -t event-platform/order-service:swagger-test .
docker build -f docker/inventory-service/Dockerfile -t event-platform/inventory-service:swagger-test .
docker build -f docker/pricing-service/Dockerfile -t event-platform/pricing-service:swagger-test .
docker build -f docker/delivery-service/Dockerfile -t event-platform/delivery-service:swagger-test .
```

Deploy and test the gateway:

```bash
kubectl apply -k k8s/overlays/docker-desktop
kubectl rollout status deployment/app-api-gateway -n event-platform --timeout=10m
kubectl port-forward -n event-platform service/app-api-gateway 8080:8080
```

Swagger UI is then available at <http://localhost:8080/swagger-ui.html>.
