#!/usr/bin/env bash
set -euo pipefail

URL="${1:-http://localhost:8080}"
EXPECTED_VERSION="${2:-}"

retry() {
  local attempts=${1:-30}
  local delay=${2:-2}
  shift 2
  local n=1
  until "$@"; do
    if (( n >= attempts )); then
      echo "Command failed after ${attempts} attempts: $*" >&2
      return 1
    fi
    ((n++))
    sleep "${delay}"
  done
}

echo "Waiting for service at ${URL} to become healthy..."
retry 60 2 curl -fsS "${URL}/health" >/dev/null

echo "Hitting ${URL}/health ..."
if ! curl -fsS "${URL}/health" | grep -qi "ok"; then
  echo "Health check failed at ${URL}/health" >&2
  exit 1
fi

echo "Hitting ${URL}/ ..."
if ! curl -fsS "${URL}/" | grep -qi "ok"; then
  echo "Root endpoint failed at ${URL}/" >&2
  exit 1
fi

echo "Checking version at ${URL}/version ..."
VERSION=$(curl -fsS "${URL}/version")
echo "Version response: ${VERSION}"
if [[ -n "${EXPECTED_VERSION}" ]]; then
  if [[ "${VERSION}" != "${EXPECTED_VERSION}" ]]; then
    echo "Version mismatch. Expected '${EXPECTED_VERSION}', got '${VERSION}'" >&2
    exit 1
  fi
fi

echo "Acceptance passed for ${URL}"
