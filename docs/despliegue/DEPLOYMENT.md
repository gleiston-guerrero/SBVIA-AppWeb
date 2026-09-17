# Guía de Despliegue en Producción (DEPLOYMENT.md)

Este documento describe la topología, los recursos computacionales, las variables de entorno de producción y el procedimiento paso a paso para reproducir el despliegue del sistema SBVIA.

---

## 1. Topología de Red y Arquitectura de Despliegue

```
               [ Cliente Web / Tribunal ]
                           │
                    HTTPS (Puerto 443)
                    Certificado Let's Encrypt
                           ▼
                 ┌───────────────────┐
                 │   Render Proxy    │ (Proxy Inverso & TLS Termination)
                 │                   │
                 └─────────┬─────────┘
          ┌────────────────┴────────────────┐
          ▼                                 ▼
┌───────────────────┐             ┌───────────────────┐
│   sbvia-frontend  │             │   sbvia-backend   │
│   (Static Site)   │             │ (Web Service Java)│
└───────────────────┘             └─────────┬─────────┘
                                            │
                       ┌────────────────────┴────────────────────┐
                       ▼                                         ▼
             ┌───────────────────┐                     ┌───────────────────┐
             │  sbvia-postgres   │                     │    sbvia-redis    │
             │  (PostgreSQL 16)  │                     │   (Valkey/Redis 8)│
             └───────────────────┘                     └───────────────────┘
```

---

## 2. Recursos Computacionales Estimados

| Componente | CPU Mínima | RAM Mínima | Almacenamiento |
|:---|:---:|:---:|:---:|
| **Frontend (Nginx / Angular)** | 0.25 vCPU | 256 MB | 100 MB |
| **Backend (Spring Boot JVM)** | 0.75 vCPU | 1024 MB | 500 MB |
| **PostgreSQL 16** | 0.50 vCPU | 512 MB | 2 GB (SSD) |
| **Redis 7** | 0.25 vCPU | 256 MB | 200 MB |
| **Total Recomendado** | **2 vCPU** | **2048 MB (2 GB)** | **10 GB** |

---

## 3. Variables de Entorno de Producción (Sin Secretos)

```bash
# Configuración de Base de Datos y Caché provista por Render
DB_URL=jdbc:postgresql://<internal_render_host>/sbvia_db
DB_USER=sbvia_db_user
DB_PASSWORD=<PROD_SECRET_PASSWORD>
REDIS_HOST=<internal_redis_host>

# Configuración JWT y Seguridad
JWT_SECRET=<SECRETO_CRIPTO_64_CHARS_RANDOM>
COOKIE_SECURE=true
CORS_ALLOWED_ORIGINS=https://sbvia-frontend.onrender.com
```

---

## 4. Procedimiento de Despliegue Paso a Paso

1. **Aprovisionar Bases de Datos (Render):**
   - Crear servicio PostgreSQL (versión 16).
   - Crear servicio Redis (Valkey).
   - Obtener credenciales y conexiones internas (Internal URL).

2. **Despliegue del Backend (Web Service):**
   - Crear un **Web Service** conectado al repositorio.
   - Definir `Root Directory`: `backend`.
   - Definir `Environment Variables` usando las credenciales obtenidas de BD, `JWT_SECRET`, y `COOKIE_SECURE=true`.

3. **Despliegue del Frontend (Static Site):**
   - Crear un **Static Site** conectado al repositorio.
   - `Root Directory`: `frontend`.
   - `Build Command`: `npm install && npm run build`.
   - `Publish Directory`: `dist/frontend/browser`.
   - **Reglas de Reescritura (Rewrites):**
     1. Source: `/api/*` -> Destination: `https://sbvia-appweb.onrender.com/api/*` (Rewrite)
     2. Source: `/*` -> Destination: `/index.html` (Rewrite)

4. **Verificación de Salud:**
   - Navegar a la URL del Frontend para validar acceso seguro y redirecciones.
   - Verificar endpoint Actuator: `https://sbvia-appweb.onrender.com/actuator/health`.
