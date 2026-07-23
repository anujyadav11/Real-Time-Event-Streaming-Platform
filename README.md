# Real-Time-Event-Streaming-Platform

![CI](https://github.com/anujyadav11/Real-Time-Event-Streaming-Platform/actions/workflows/ci.yml/badge.svg)

## CI/CD

Pull requests to `main` build and test the complete Maven project. Pushes to
`main` additionally build and publish all service images to GitHub Container
Registry, then deploy the immutable commit image set to Kubernetes.

Production cluster and GHCR setup is documented in
[`k8s/overlays/production/README.md`](k8s/overlays/production/README.md).
