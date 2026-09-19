# Simulador de Comportamiento Vial con Inteligencia Artificial (SBVIA)

[![CI Pipeline](https://github.com/gleiston-guerrero/SBVIA-AppWeb/actions/workflows/main.yml/badge.svg)](https://github.com/gleiston-guerrero/SBVIA-AppWeb/actions/workflows/main.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![DOI Software](https://zenodo.org/badge/DOI/10.5281/zenodo.22740480.svg)](https://doi.org/10.5281/zenodo.22740480)
[![DOI Dataset](https://zenodo.org/badge/DOI/10.5281/zenodo.22785358.svg)](https://doi.org/10.5281/zenodo.22785358)

## 📌 Descripción del Proyecto
El sistema **Simulador de Comportamiento Vial con Inteligencia Artificial (SBVIA)** proporciona un entorno interactivo y reproducible de entrenamiento y evaluación para conductores en formación. Esta versión final (`v1.1.0`) integra autenticación segura con JWT en cookies `HttpOnly + Secure + SameSite=Strict`, CRUD optimizado sobre Spring Boot 3.2.x y PostgreSQL 16, estrategia híbrida de acceso a datos con Procedimientos Almacenados, caché distribuida con Redis 7 y frontend reactivo en Angular 17+.

---

## 🚀 Despliegue y Acceso Público
- **Frontend Web (HTTPS):** [https://sbvia-frontend.onrender.com](https://sbvia-frontend.onrender.com) (Despliegue activo y auditado)
- **API Backend / Actuator Health:** [https://sbvia-appweb.onrender.com/actuator/health](https://sbvia-appweb.onrender.com/actuator/health)
- **Documentación Swagger UI:** [https://sbvia-appweb.onrender.com/api/swagger-ui.html](https://sbvia-appweb.onrender.com/api/swagger-ui.html)

### 👤 Cuenta de Demostración para Tribunal / Evaluación:
- **Correo:** `conductor@sbvia.com`
- **Contraseña:** `password123`
- **Rol:** Conductor (`ROLE_USER`)

---

## 🐳 Artefactos Docker
- **Imagen Docker Backend:** `ghcr.io/keithdrox/sbvia-backend:v1.2.0`
- **Imagen Docker Frontend:** `ghcr.io/keithdrox/sbvia-frontend:v1.2.0`

> Los digests SHA-256 exactos se obtienen al publicar las imágenes con `docker buildx build --push`.
> Consultar el registro: [GitHub Container Registry](https://github.com/gleiston-guerrero/SBVIA-AppWeb/pkgs/container/sbvia-backend)

## Publicación y preservación

El **DOI del Software** ya se encuentra registrado y disponible a través de Zenodo en `10.5281/zenodo.22740480`. 

El **DOI del Dataset** de validación empírica se encuentra publicado independientemente en Zenodo bajo el DOI `10.5281/zenodo.22785358`. El sistema SBVIA es accesible públicamente a través de HTTPS según los requisitos del tribunal.

---

## ⚙️ Ejecución Reproducible Local (`make all`)

El sistema completo está orquestado mediante Docker Compose. Sigue estos pasos para arrancar el entorno completo:

```bash
# 1. Clonar el repositorio
git clone https://github.com/gleiston-guerrero/SBVIA-AppWeb.git
cd SBVIA-AppWeb

# 2. Configurar variables de entorno
cp .env.example .env

# 3. Compilar, verificar y levantar todo en un solo comando
make all
```

*Nota para Windows sin `make`:* Puedes ejecutar secuencialmente:
```powershell
Copy-Item .env.example .env
docker compose build
docker compose up -d --wait
```

### Servicios Locales:
- **Frontend Angular:** [http://localhost:4200](http://localhost:4200)
- **API Backend Swagger:** [http://localhost:8080/api/swagger-ui.html](http://localhost:8080/api/swagger-ui.html)
- **Health Check Actuator:** [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

---

## 🧪 Pruebas, Auditorías y Verificaciones
- **Verificación Completa:** `make verify`
- **Pruebas de Carga k6 (50 VUs / 30s):** `make bench`
- **Auditoría Lighthouse & OWASP:** `make audit`
- **Auditoría de SQL Dinámico:** `./scripts/audit-sql-dynamic.sh`

---

## 📄 Generación del Informe PDF (`make pdf`)

El informe académico en LaTeX se compila con tres pasadas (pdflatex → bibtex → pdflatex × 2):

```bash
make pdf
# Genera: docs/informe-final.pdf
```

*Requisitos: `pdflatex` y `bibtex` instalados localmente (TeX Live / MiKTeX).*

Alternativa con Docker (sin instalación local):
```bash
docker run --rm -v "%cd%\docs:/work" -w /work \
  texlive/texlive:latest \
  sh -c "pdflatex -interaction=nonstopmode informe-final.tex && bibtex informe-final && pdflatex -interaction=nonstopmode informe-final.tex && pdflatex -interaction=nonstopmode informe-final.tex"
```

---

## 🗄️ Semillas y Determinismo
- **Semilla Aleatoria Global (PRNG):** `SEED=42`
- **Dataset de Evaluación SUS (estudio retirado):** $N = 15$ respuestas crudas en `docs/mediciones/sus/sus-raw-data.csv`. El estudio de usabilidad se retira y **no** se reporta media, desviación típica ni intervalo de confianza: la fecha declarada de aplicación (2026-07-28/29) es anterior a la incorporación al repositorio de la funcionalidad de simulación evaluada (`55201f5`, 2026-09-03; `5e852c6`, 2026-09-04). Véase `docs/mediciones/sus/sus-analysis.md`.

---

## 🏛️ Flujo MVC y Arquitectura de Acceso a Datos
El sistema utiliza una **estrategia híbrida** (ADR-006):
1. **CRUDs elementales:** Gestionados a través de Spring Data JPA / Hibernate.
2. **Operaciones analíticas, masivas y agregadas:** Optimizadas y encapsuladas en 6 procedimientos almacenados en PostgreSQL (`db/procs/`), invocados mediante `@Procedure` y `@NamedStoredProcedureQuery`.
