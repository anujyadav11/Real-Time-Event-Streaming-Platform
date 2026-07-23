# Production deployment

The GitHub Actions workflow deploys this overlay after the Docker matrix has
successfully pushed all service images from `main`.

Create a GitHub environment named `production`, then add these environment
secrets:

- `KUBECONFIG_B64`: Base64-encoded kubeconfig for a service account with access
  to the `event-platform` namespace. For example:
  `base64 < ~/.kube/config | tr -d '\n'`.
- `GHCR_USERNAME`: GitHub user or bot account allowed to pull the packages.
- `GHCR_PULL_TOKEN`: A GitHub classic personal access token with `read:packages`
  (and permission to read this repository's packages).

The workflow creates or updates the `ghcr-pull-secret`, renders the complete
Kustomize overlay with every service image set to the full commit-SHA tag,
applies it, and waits for every rollout. Pull requests only build and test;
they never deploy.

For a first-time cluster setup, replace the example values in
`k8s/base/secrets/platform-secret.yaml` with externally managed production
credentials before deploying. Do not commit real production secrets.
