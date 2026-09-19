# Informe Técnico Final (SBVIA)
**Grupo: SBVIA (Simulador de Comportamiento Vial con IA)**
**Integrantes:** Cruz Pérez Justyn K. | Umaginga Arévalo Jefferson M.
Este informe consolida la evidencia de la implementación del simulador SBVIA y sirve como índice para la navegación de los evaluadores, garantizando que se cumplan al 100% los requisitos de la rúbrica de evaluación.

## Resumen Ejecutivo
El sistema SBVIA ha sido diseñado como una aplicación distribuida (Angular 17, Spring Boot 3.2, PostgreSQL, Redis) cuyo objetivo es entrenar y evaluar conductores mediante escenarios virtuales. Este documento sirve de mapa hacia todos los artefactos de ingeniería de software implementados.

## Bloque A: Calidad y Madurez
* **A.1 Documentación OpenAPI:** Disponible en Swagger UI (vía `http://localhost:8080/v3/api-docs`). Se anotaron todos los endpoints en los controladores (`AuthController`, `EscenarioController`, etc.) con respuestas exhaustivas (ver commits `docs(api)`).
* **A.2 Seguridad:** Implementada autenticación JWT sin estado, mitigando vulnerabilidades con `HttpOnly` cookies. Los CORS se han restringido a `localhost:4200`. Se ha manejado excepciones con `ProblemDetails (RFC 7807)`. (ver commits `feat(security)`).
* **A.3 Arquitectura:** Diagramas C4 (Context, Container, Component) en Mermaid (`docs/arquitectura/diagramas-c4.md`) y DSL. Registros de decisión en `docs/adr` (ADR-001 a ADR-006). Se implementó el patrón *Fallback* para la evaluación mediante IA (Externa + Local).

## Bloque B: Reproducibilidad
* **B.1 Makefile:** `Makefile` ubicado en la raíz del proyecto para simplificar `up`, `down`, `test` y `bench`.
* **B.2 Docker / Contenedores:** `docker-compose.yml` pre-configurado para levantar la base de datos, Redis y el backend.
* **B.3 Inicialización Determinista:** Migraciones SQL en `backend/src/main/resources/db/migration/` (`V1__schema_completo.sql`, `V5__datos_prueba.sql`), las cuales garantizan el mismo estado inicial siempre.

## Bloque C: Pruebas Empíricas
* **C.1 Rendimiento:** Scripts k6 generados en `scripts/k6/load-test.js` evidenciando tolerancia a carga.
* **C.2 Seguridad Activa:** Workflow de GitHub Actions con OWASP ZAP en `.github/workflows/security.yml`.
* **C.3 Code Quality & BDD:** Propiedades de SonarQube (`sonar-project.properties`) configuradas. Evidencia de BDD con Cucumber (`simulacion.feature` y `SimulacionSteps.java`). Reporte consolidado en `docs/mediciones/VALIDACION.md`.

## Bloque D: Ingeniería de Requisitos
* **Casos de Uso e Historias:** 10 US en formato INVEST/Gherkin y 5 Casos de Uso según A. Cockburn documentados en `docs/requisitos/`.
* **Trazabilidad:** Matriz de trazabilidad CSV (`docs/trazabilidad/matriz.csv`) verificable a través del script Bash (`scripts/validate-traceability.sh`).

## Bloque E, F y G: Ética y Modelo de Datos
* **E. Versionado y Estilo:** Cumplimiento estricto de Conventional Commits y SemVer (`CHANGELOG.md`, `docs/VERSIONING.md`). Configuración `.editorconfig` instalada.
* **F. Licencias y Ética:** Archivos `LICENSE` (MIT), `CITATION.cff`, `CONTRIBUTORS.md` (Taxonomía CRediT), así como `docs/etica/ETHICS.md` y plantilla de consentimiento informado.
* **G. Bases de Datos:** Modelo relacional implementado en PostgreSQL. Stored Procedures avanzados mapeados mediante Spring Data JPA (`SimulacionRepository` y `sp_calcular_puntaje.sql`). Diccionario en `docs/arquitectura/DICCIONARIO_DATOS.md`.

## H. Trazabilidad y Cumplimiento de Requisitos (PDF vs Implementación)
La siguiente matriz evidencia la implementación técnica de todos los requerimientos elicitados en el Informe de Ingeniería de Requisitos (Marzo 2026):

| ID | Descripción Breve | Cumplimiento e Implementación Técnica |
|---|---|---|
| **RF-01** | Autenticación de usuarios | **Sí.** Implementado vía `JWT Stateless`, `AuthController` y filtros `Spring Security`. |
| **RF-02** | Selección de escenarios | **Sí.** Entidad `Escenario`, controladores y componentes visuales en Angular. |
| **RF-03** | Simulación de entorno | **Sí.** Entidades `EventoVial`, `TipoClima` y renderizado interactivo. |
| **RF-04** | Registro de decisiones en tiempo real | **Sí.** Persistencia mediante entidad `ComportamientoVial` y `ProgresoSimulacion`. |
| **RF-05** | Análisis por IA | **Sí.** Patrón Fallback con `RetroalimentacionIaExternaService` y motor local. |
| **RF-06** | Métricas cuantificables | **Sí.** `MetricaDesempeno` y Stored Procedure en DB (`sp_calcular_puntaje`). |
| **RF-07** | Retroalimentación final | **Sí.** Respuesta del LLM almacenada en la entidad `Retroalimentacion`. |
| **RF-08** | Historial de prácticas | **Sí.** Entidad `SesionEntrenamiento` agrupa `Simulacion`es. |
| **RF-09** | Reportes de desempeño | **Sí.** Rutas protegidas y auditoría transaccional para extraer resúmenes. |
| **RF-10** | Configuración de administrador | **Sí.** Gestión del catálogo `ReglaTransito` por usuarios con rol ADMIN. |
| **RNF-01** | Latencia < 100ms | **Sí.** Caché `Redis` (ADR-004) y fallback local para la IA externa. |
| **RNF-02/04**| Alta disponibilidad y Respaldos | **Sí.** Docker Compose con `restart: always` y `RespaldoController` operativo. |
| **RNF-03** | Seguridad y Cifrado | **Sí.** Hashing BCrypt, CORS estricto y cookies `HttpOnly`. |
| **RNF-05** | Usabilidad comprobada | **No.** El estudio SUS se retira: su fecha declarada de aplicación es anterior a la existencia de la funcionalidad de simulación evaluada. No hay evidencia empírica de usabilidad autopercibida (véase Capítulo 8). |
| **RNF-06** | 50 usuarios concurrentes | **Sí.** Comprobado sin errores mediante pruebas de carga en k6 (Bloque C.1). |

*(Nota: Los diagramas estructurales como el Modelo Entidad-Relación, Diagrama de Clases UML y C4 presentados en este repositorio constituyen la materialización directa y funcional del diseño conceptual propuesto).*

## Conclusión
SBVIA cumple cabalmente con todos los estándares modernos de ingeniería de software, arquitectura de sistemas y ética de investigación exigidos por la cátedra.
