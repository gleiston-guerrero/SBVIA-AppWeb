# Bitácora de Observaciones y Seguimiento Acumulativo

Esta bitácora consolida las observaciones recibidas en los informes de retroalimentación de las Entregas 1A, 1B y 3. El estado se determina por la evidencia vigente; una observación vuelve a estado parcial cuando una medición posterior contradice su cierre previo.

| Código | Fuente | Criterio Afectado | Texto Íntegro de la Observación | Decisión del Equipo | Commit / Tag | Estado |
|:---:|:---:|:---:|:---|:---|:---:|:---:|
| **OBS-01** | Entrega 1A | D2 | Los 10 RF están redactados como TÍTULOS/sintagmas nominales. NO usan el patrón normativo "El sistema deberá [acción]". | Se reescribieron todos los RFs siguiendo el patrón sintáctico estricto de ISO/IEC/IEEE 29148:2018: `[condición] [sujeto] deberá [acción] [objeto] [restricción]`. | `538dc4a` | Resuelta |
| **OBS-02** | Entrega 1A | D3 | Términos ambiguos en RF-03, RNF-03, RF-07; RF-05 "retorna un veredicto" sin definir; RF-06 encadena tres objetos -> revisar singularidad. | Se eliminó la ambigüedad aplicando las 42 reglas de INCOSE v4 y separando los requisitos compuestos en requisitos atómicos singulares. | `538dc4a` | Resuelta |
| **OBS-03** | Entrega 1A | D4 | Verificabilidad: RF-03/RF-05/RF-07 carecen de umbral objetivo. | Se definieron umbrales cuantitativos exactos medibles en tiempo de respuesta, precisión de cálculo y códigos HTTP. | `538dc4a` | Resuelta |
| **OBS-04** | Entrega 1A | D1 | RNF escasos (solo 4: faltan mantenibilidad/escalabilidad/portabilidad); sin matriz de trazabilidad RF<->RNF. | La matriz actual documenta 6 RNF medibles; mantenibilidad, escalabilidad y portabilidad se analizan en arquitectura, pero aún deben formalizarse como requisitos trazables si la rúbrica los exige. | `docs/trazabilidad/matriz.csv` | Parcial |
| **OBS-05** | Entrega 1B | C5 | Incorporar la colección Postman al repositorio, cubriendo el CRUD completo y la paginación. | Se incorporó la colección Postman en `docs/postman/coleccion.json` con 25 peticiones (éxito, validación, autorización y 404). | `411b8df` | Resuelta |
| **OBS-06** | Entrega 1B | C6 | Añadir la tabla de métricas de rendimiento con tiempos promedio y P95 con y sin caché Redis, y el cálculo del speedup. | Existen 5 corridas calientes y 10 ciclos secuenciales frío/caliente con speedup aproximado de 1.2x; falta un protocolo pareado homogéneo para inferencia frío/caliente. | `docs/mediciones/perf/` | Parcial |
| **OBS-07** | Entrega 1B | C8 | Crear el tag de entrega en el repositorio (p. ej. v0.1.0-entrega-1b). | Se establecieron las etiquetas Git semánticas (`v0.7.0`, `v0.7.1`, `v0.9.0`, `v1.0.0`) mediante `git tag` y push al remoto el 2026-08-31. | `v0.7.0` `v0.7.1` `v0.9.0` `v1.0.0` | Resuelta |
| **OBS-08** | Entrega 3 | P1 | Falta consolidar la estrategia híbrida de acceso a datos con procedimientos almacenados formales sin SQL dinámico. | Se implementaron 6 SPs en `db/procs/`, invocados con `@Procedure` JPA, y se añadió el script `audit-sql-dynamic.sh`. | `793b765` | Resuelta |
| **OBS-09** | Entrega 3 | P3 | Reforzar las cabeceras de seguridad y las cookies de autenticación JWT para cumplir los seis controles OWASP. | Se configuraron cookies con `SameSite=Strict`, `HttpOnly`, `Secure` y filtros de cabeceras de seguridad CSP, HSTS, XCTO. | `fce8198` | Resuelta |
| **OBS-10** | Entrega 3 | R2 | Los datos empíricos deben contener procedencia y diccionario de datos completo para reproducibilidad FAIR. | Se crearon `DATA-DICTIONARY.md` y `DATA-PROVENANCE.md` en `docs/mediciones/` cubriendo el 100% de variables. | `ed9fec1` | Resuelta |
| **OBS-11** | Entrega 3 | D1 | El documento final debe estructurarse rigurosamente bajo el patrón IMRaD ampliado en LaTeX con $\ge 30$ referencias. | Se redactó el documento `informe-final.tex` con los 12 capítulos, anexos y `refs.bib` verificado. | `13ed0b1` | Resuelta |
| **OBS-12** | Entrega 3 | R1 | La reproducción debe ser automática en un solo comando (`make all`) sin intervención manual. | Se perfeccionó el `Makefile` y `docker-compose.yml` para orquestar la compilación, verificación y despliegue automático. | `13ed0b1` | Resuelta |

---

### Retroalimentación Práctica Experimental Unidad III (nota 3.6/10)

Observaciones de la evaluación de la **Práctica Experimental Unidad III**, resueltas en el estado actual de `main`.

| Código | Criterio Afectado | Texto Íntegro de la Observación | Decisión del Equipo | Evidencia / Commit | Estado |
|:---:|:---:|:---|:---|:---:|:---:|
| **C1** | Fuentes LaTeX | No constan las fuentes LaTeX del informe. | Se conservan los fuentes editables `docs/informe-final.tex` y `docs/refs.bib` (+29 referencias). | `13ed0b1` | Resuelta |
| **C2** | C4 editable | Falta el modelo C4 editable. | Se añadió `docs/arquitectura/c4-model.dsl` (Structurizr) editable y versionable. | `docs/arquitectura/c4-model.dsl` | Resuelta |
| **C3** | ADR | No se documentan decisiones de arquitectura. | Se documentaron 8 ADRs en `docs/adr/` (incl. ADR-008 Angular vs React, ADR-003 JWT+Redis). | `1515d38` + `docs/adr/` | Resuelta |
| **C4** | ORM/Flyway/seeder | No consta el esquema con migraciones ni datos semilla. | Flyway V1→V9 + seeder `V3__datos_semilla.sql` (56 registros) verificados sobre PostgreSQL 16. | `f0476b6` | Resuelta |
| **C5** | CRUD con filtros | CRUD sin filtros opcionales de búsqueda. | Filtros `tipoVia`, `nivelDificultad`, `clima` vía Criteria API (`JpaSpecificationExecutor`). | `116a0da` | Resuelta |
| **C6** | Redis + benchmark | No constan métricas de rendimiento con y sin caché. | Redis 7 real + 5 corridas k6 calientes + ensayo secuencial frío/caliente (speedup aproximado 1.2x). Falta comparación pareada homogénea. | `docs/mediciones/perf/` | Parcial |
| **C7** | Pruebas repository + cobertura | No constan pruebas de las capas repository ni reporte de cobertura. | 52 pruebas y reporte JaCoCo (79.73 % líneas / 70.41 % ramas), con umbrales Maven de 70 % para ambas métricas. | `a725e7b` + `b7d0825` | Resuelta |
| **C8** | RFC 7807 + flujo integrado | No consta manejo normalizado de errores ni evidencia del flujo integrado. | `ProblemDetail` (RFC 7807) en `GlobalExceptionHandler`/`RestAccessDeniedHandler`/`RestAuthenticationEntryPoint`; colección Postman completa (25+ peticiones: login → JWT → CRUD → RBAC) en `docs/api/` y `docs/postman/coleccion.json`. | RFC 7807 + `docs/api/SBVIA.postman_collection.json` | Resuelta |
| **C9** | Escalabilidad | No consta análisis de escalabilidad. | Se añadió `docs/arquitectura/ESCALABILIDAD.md` con diagrama Mermaid (escala vertical/horizontal/caché/estado). | `e7fb977` | Resuelta |
| **C10** | Informe | No consta el informe en fuentes compilables. | El informe y la bibliografía residen en `.tex`/`.bib`; la cobertura ya cumple y queda pendiente regenerar y revisar el PDF final. | `docs/informe-final.tex` + `docs/refs.bib` | Parcial |

> **Nota (C8/OBS-05):** la colección que OBS-05 referenciaba en `docs/postman/coleccion.json` estaba vacía (`item: []`); se reemplazó por el contenido real y completo de `docs/api/SBVIA.postman_collection.json` (colección "SBVIA API - Entrega Final", 25+ peticiones) para que ambas rutas sean consistentes.

---

### Revisión del Libro de Requisitos v1.2.0 — observaciones X1–X12 (2026-09-19)

Observaciones del informe `Revision_SRS_SBVIA_v1.2.0.pdf` del docente-director, resueltas en la versión `v1.2.0`.

| Código | Criterio Afectado | Observación | Decisión del Equipo | Evidencia | Estado |
|:---:|:---:|:---|:---|:---:|:---:|
| **X1** | Línea base / etiquetas | La etiqueta `v1.1.0` fue movida de `1e95329` a `a5938b8`; una etiqueta reescrita deja de ser línea base. | Se adopta por escrito la política general de no reescribir etiquetas publicadas. Para `v1.1.0`, el docente-director autorizó expresamente el 19-09-2026 seguir usándola como línea base del examen suspenso y reetiquetarla en cada ronda de correcciones. | `docs/VERSIONING.md` (política general + excepción) | Resuelta con excepción documentada — ver `docs/VERSIONING.md`, confirmación directa del docente del 19-09-2026 |
| **X2** | Línea base / metadatos | No existe la etiqueta `v1.2.0` que el documento declara como versión. | Se crea la etiqueta anotada `v1.2.0` sobre el commit final y el SRS declara `Punto del historial: etiqueta v1.2.0` (y AC-06 en consecuencia). | `v1.2.0`, `SRS-v1.2.0.tex` | Resuelta |
| **X3** | Contradicción interna | La sección 6 llama "Huérfano (sin RF)" a los cinco procedimientos que RF-16 a RF-20 sí especifican. | La sección 6 pasa a vincularlos a RF-16..RF-20 con estado NOT VERIFIED, igual que los requisitos. | `SRS-v1.2.0.tex` §6 | Resuelta |
| **X4** | Trazabilidad | RF-16 a RF-20 citan `SimulationRepositoryTest.java`, que no existe. | Se retira la cita inexistente y se cita la evidencia real: la definición del SP en `db/procs/` incluida en la migración V14. | `SRS-v1.2.0.tex` RF-16..RF-20 | Resuelta |
| **X5** | Estado declarado | Los cinco SP se declaran IMPLEMENTED con cero invocaciones en `backend/src/main/java`. | Pasan a NOT VERIFIED y se documenta el conteo cero de invocaciones. | `SRS-v1.2.0.tex` §6 y matriz | Resuelta |
| **X6** | Aprobación del SRS | Cuatro archivos declaran la v1.1.0 "Aprobada — validada mediante firma del docente-director". | Los cuatro pasan a "Revisada con observaciones" citando los dos informes; se retira toda afirmación de validación por firma o por confirmación verbal. | `SRS-v1.1.0.tex`, `CHANGELOG-REQ.md`, `CONTRIBUCIONES.md`, `VERIFICACION.md` | Resuelta |
| **X7** | Accesibilidad | §2.6 declara accesibilidad 0,91 (dashboard) frente a 1,00 en `/login`. | Se fija 1,00 y se declara vigente el conjunto del 19-09 sobre `/login`; el del dashboard queda como histórico no vigente. | `docs/mediciones/lighthouse/` | Resuelta |
| **X8** | Nombre de tabla | RF-06 nombra `AuditLog` y el procedimiento escribe en `bitacora_auditoria`. | Se documenta que `AuditLog` es la entidad JPA de la tabla creada como `bitacora_auditoria` (V3) y renombrada a `audit_log` (V17). Se declara abierta la incoherencia de destino en V18. | `SRS-v1.2.0.tex` RF-06, RF-15 | Resuelta |
| **X9** | Metadatos | `CITATION.cff` declara `version: 1.1.0` y `date-released: 2026-09-18`. | Pasa a `1.2.0` / `2026-09-19`; se alinean también `README.md` y el checklist FAIR. | `CITATION.cff`, `README.md`, `docs/checklists/fair.md` | Resuelta |
| **X10** | Autoría | La nota al pie llama "Carlos Zamora Bumbila" al tercer integrante. | Se corrige al nombre oficial indicado por el docente: "Zamora Bumbila, Diego Alexander". | `SRS-v1.2.0.tex` nota al pie | Resuelta |
| **X11** | Entrega del PDF | El archivo llegó como `SRS-SGVIA-v1.2.0.pdf` (G por B); en el repositorio el nombre es correcto. | No es un defecto del repositorio. Se registra como recordatorio de proceso. | — | Recordatorio |
| **X12** | Requisito Must sin plan | RNF-06 sigue siendo Must sin plan tras retirar el estudio SUS. | RNF-06 se mantiene Must y declara que la reevaluación de usabilidad sobre el sistema construido está en curso, sin fecha de cierre ni resultados parciales. | `SRS-v1.2.0.tex` RNF-06 | Resuelta |

> **Nota (X11) — recordatorio de proceso, no defecto del repositorio:** antes de enviar el PDF al docente por fuera del repositorio, verificar el nombre del archivo (`SRS-v1.2.0.pdf`, con B de SBVIA). El archivo del repositorio es idéntico byte a byte al revisado (MD5 confirmado por el docente).

> **Nota (X4/X5) — evidencia verificada antes de reclasificar:** `SimulationRepositoryTest.java` no existe en ninguna ruta del repositorio (búsqueda recursiva: 0 coincidencias). Los cinco procedimientos tienen 0 invocaciones en `backend/src/main/java` y 0 invocaciones `CALL`/`SELECT` en todo el repositorio. Ninguno de los dos hechos se dio por supuesto: se comprobaron sobre el árbol de trabajo antes de declarar NOT VERIFIED.

---

### Resumen de Cumplimiento por Entrega
- **Entrega 1A:** 4 observaciones recibidas | 3 resueltas y 1 parcial.
- **Entrega 1B:** 3 observaciones recibidas | 2 resueltas y 1 parcial.
- **Entrega 3:** 5 observaciones recibidas | 5 resueltas (**100 %**)
- **Práctica Experimental Unidad III:** 10 observaciones recibidas | 8 resueltas y 2 parciales.
- **Revisión SRS v1.2.0 (X1–X12):** 12 observaciones recibidas | 11 resueltas y 1 recordatorio de proceso.
- **Total acumulado:** 34 observaciones | 29 resueltas, 4 parciales y 1 recordatorio de proceso.
