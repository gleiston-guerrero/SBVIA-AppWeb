# ADR-007: Estrategia de Despliegue en Producción y Orquestación

## Estado
Aceptado

## Contexto
Para la defensa final y la evaluación externa, el sistema SBVIA requiere una infraestructura de despliegue accesible públicamente mediante HTTPS con certificado TLS válido de autoridad reconocida (Let's Encrypt), con alta disponibilidad y reproducibilidad completa. Se evaluaron alternativas como:
1. Despliegue en Servidor Dedicado / VPS propio.
2. Plataformas PaaS para contenedores (Render, Railway, Fly.io, Vercel).
3. Cloud Providers con capa gratuita (Oracle Cloud Free Tier / AWS EC2).

## Decisión
Se adopta una arquitectura de servicios gestionados (PaaS) orquestados mediante la plataforma **Render**, garantizando alta disponibilidad, aprovisionamiento automático de certificados TLS (Let's Encrypt) y configuración de proxies internos:
1. **Frontend (Static Site):** Servido a través de la infraestructura CDN estática de Render para SPA Angular 17. Se configura un Rewrite Proxy (`/api/*`) apuntando al Web Service backend, eliminando cualquier conflicto de CORS y asegurando la integridad de las cookies seguras.
2. **Backend (Web Service):** Contenedor Java 21 Spring Boot 3.2 ejecutándose en Render Web Services con despliegue automático desde GitHub.
3. **Persistencia & Caché:** PostgreSQL 16 y Redis (Valkey) aprovisionados como servicios nativos dentro de la red privada de Render, inaccesibles desde el exterior.
4. **Despliegue secundario / Entorno Local:** Despliegue 100% reproducible en local mediante `docker-compose.yml`.

## Consecuencias
- **Positivas:**
  - Despliegue en la nube automatizado y 100% verificado (CI/CD mediante GitHub).
  - Proxy Inverso sin código: CORS resuelto nativamente a través del enrutador de Render.
  - Certificado SSL/TLS válido automático sin necesidad de scripts Certbot manuales.
  - Endpoint `/actuator/health` público y verificable por el tribunal en tiempo real.
- **Negativas / Compromisos:**
  - Requiere configuración manual de variables de entorno (secretos) en el panel web del proveedor de nube.
