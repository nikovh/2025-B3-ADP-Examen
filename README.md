# Examen - Pipeline de Deployment con Blue/Green, Acceptance y Rollback

Este repositorio contiene una aplicación Java simple, pruebas (unitarias e integración) con Maven y un pipeline de CI/CD que realiza:

- Build + pruebas (Surefire + Failsafe + JaCoCo)
- Construcción de imagen Docker
- Puesta en marcha de entorno de pruebas con Blue/Green y router NGINX
- Acceptance tests automatizados sobre endpoints /health, / y /version
- Switch Blue/Green con validación post-switch y rollback automático si falla
- Publicación de evidencias como artefactos del pipeline

## Estructura
- examen/
  - src/main/java/com/develop/nvh/App.java (servidor HTTP)
  - src/test/java/com/develop/nvh/* (tests)
  - Dockerfile
  - pom.xml
- deploy/
  - docker-compose.yml (app-green, app-blue y router)
  - nginx/
    - nginx.conf
    - conf.d/default.conf
- scripts/
  - acceptance.sh (pruebas de aceptación)
  - blue_green_deploy.sh (cambio de enrutamiento y reload NGINX)
- .github/workflows/ci-cd.yml (pipeline)

## Cómo funciona el Blue/Green
- GREEN: imagen/tag estable (latest) expuesta como 18080 y roteada en 8080 por NGINX.
- BLUE: nueva versión (tag SHA) expuesta como 18081, sin tráfico inicialmente.
- Switch: el script reescribe el upstream de NGINX a app-blue y recarga el router.
- Rollback: si las pruebas de aceptación fallan tras el switch, se vuelve a green y se registra evidencia.

## Ejecutar localmente
Requisitos: Docker y Docker Compose.

bash
# Construir imagen
docker build -t examen:latest -f examen/Dockerfile examen

# Levantar entorno GREEN + router
IMAGE_NAME=examen GREEN_TAG=latest GREEN_VERSION=green-stable \
  docker compose -f deploy/docker-compose.yml up -d router app-green

# Pruebas de aceptación contra GREEN
./scripts/acceptance.sh http://localhost:8080 green-stable

# Levantar BLUE y probar pre-switch
IMAGE_NAME=examen BLUE_TAG=latest BLUE_VERSION=candidate \
  docker compose -f deploy/docker-compose.yml --profile blue up -d app-blue
./scripts/acceptance.sh http://localhost:18081 candidate

# Cambiar a BLUE
./scripts/blue_green_deploy.sh switch blue

# Validar y, si falla, hacer rollback
./scripts/acceptance.sh http://localhost:8080 candidate || \
  (./scripts/blue_green_deploy.sh switch green && ./scripts/acceptance.sh http://localhost:8080 green-stable)

## Evidencia
El pipeline sube artefactos en el job "Upload evidence artifacts" en Actions con logs:
- evidence/acceptance-green.log
- evidence/acceptance-blue.log
- evidence/after-switch.log
- evidence/rollback.log (si aplica)
