# Event Stream Platform Architecture

This project is organized as a Maven multi-module microservice system.

## Modules

- `common`: shared DTOs, events, exceptions, and reusable utility code.
- `gateway`: single external entry point for clients.
- `order-service`: accepts and owns order creation workflows.
- `inventory-service`: owns product stock checks and reservations.
- `payment-service`: owns payment authorization and capture workflows.
- `notification-service`: sends email, SMS, push, or webhook notifications.
- `tracking-service`: exposes live order tracking state.
- `docker`: local infrastructure and image build helpers.
- `k8s`: Kubernetes manifests.
- `docs`: architecture and API documentation.

## Event Flow

1. Clients call `gateway`.
2. `gateway` routes API calls to the owning service.
3. Services publish domain events to Kafka.
4. Interested services consume events and update their own data stores.
5. `tracking-service` keeps fast moving order state in Redis for live reads.
