#!/usr/bin/env bash
set -euo pipefail

CFG_GREEN="/etc/nginx/conf.d/default.conf"
CFG_BLUE="/etc/nginx/conf.d/default-blue.conf"
TARGET="deploy/nginx/conf.d"

switch_to_green() {
  cat > "${TARGET}/default.conf" <<'NGINX'
upstream backend {
    server app-green:8080 weight=10;
}
server {
    listen 80;
    location / {
        proxy_pass http://backend;
    }
}
NGINX
}

switch_to_blue() {
  cat > "${TARGET}/default.conf" <<'NGINX'
upstream backend {
    server app-blue:8080 weight=10;
}
server {
    listen 80;
    location / {
        proxy_pass http://backend;
    }
}
NGINX
}

case "${1:-}" in
  switch)
    color="${2:-green}"
    if [[ "$color" == "green" ]]; then
      switch_to_green
    elif [[ "$color" == "blue" ]]; then
      switch_to_blue
    else
      echo "Unknown color: $color" >&2
      exit 1
    fi
    docker exec router nginx -s reload || true
    ;;
  *)
    echo "Usage: $0 switch [green|blue]" >&2
    exit 1
    ;;
 esac
