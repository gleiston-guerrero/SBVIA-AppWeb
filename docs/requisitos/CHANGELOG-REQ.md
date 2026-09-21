# Bitácora de Cambios de Requisitos (CHANGELOG-REQ.md)

Control formal del ciclo de vida y evolución de requisitos según la norma ISO/IEC/IEEE 29148:2018 entre la Entrega 1A, 1B, 3 y la Entrega Final (v1.0.0).

---

## 1. Métricas de Calidad y Estabilidad de Requisitos

- **Requisitos Totales Especificados ($N_{total}$):** 20 (8 Funcionales, 6 No Funcionales, 6 Historias de Usuario).
- **Requisitos Modificados ($N_{mod}$):** 3 (RF-03, RF-04, y ajustes en seguridad RNF-04 y RNF-09).
- **Requisitos Agregados ($N_{add}$):** 4 (RF-05 a RF-08 para reportería, actualización masiva y códigos secuenciales).
- **Requisitos Eliminados ($N_{del}$):** 0.
- **Tasa de Estabilidad de Requisitos ($T_{est}$):**
  $$T_{est} = 1 - \frac{N_{mod}}{N_{total}} = 1 - \frac{3}{20} = 0.85 \quad (85.0\%)$$
- **Porcentaje de Requisitos con Prioridad *Must* Verificados:** **100.00 %** ($14/14$). Se concluyeron exitosamente las 5 corridas K6 (RNF-01 y RNF-02), la auditoría Lighthouse en producción (RNF-09) y controles de seguridad (RNF-04).

---

## 2. Historial de Cambios por Versión

| Versión | Fecha | Requisito Afectado | Tipo de Cambio | Justificación Técnica |
|:---:|:---:|:---|:---:|:---|
| `v0.3.0` | 2026-06-04 | RF-01 a RF-10 | Creación inicial | Elicitación de requisitos base del simulador. |
| `v0.7.0` | 2026-06-14 | RF-01, RF-02 | Modificación | Adaptación a autenticación stateless con JWT y CRUD JPA. |
| `v0.9.0` | 2026-07-24 | RNF-01, RNF-02 | Modificación | Incorporación de caché Redis y umbrales p95 en k6. |
| `v1.0.0` | 2026-08-17 | RF-03 a RF-08 | Agregación / Refactor | Consolidación de estrategia híbrida con 6 Procedimientos Almacenados en PostgreSQL. |
| `v1.1.0` | 2026-09-18 | RNF-04, RNF-09, Funcionales Generales | Estabilización / Corrección | Contrato REST de reglas de tránsito validado. Cookie Secure en producción, y Lighthouse ejecutado contra URL productiva real. Limpieza del repositorio. |
| `v1.1.0` | 2026-09-19 | SRS (control del documento) | Corrección | Se retira la declaración de aprobación de la v1.1.0. Los dos informes de revisión escritos por el docente-director (`docs/requisitos/Revision_SRS_SBVIA_v1.1.0.pdf` y `docs/requisitos/Revision_SRS_SBVIA_v1.2.0.pdf`) declaran literalmente que "no asignan calificación y no constituyen aprobación del SRS". La firma acredita que el documento fue revisado, no que fuera aprobado. La fila v1.1.0 del control del documento pasa de "Aprobada — validada mediante firma del docente-director" a "Revisada con observaciones" (observación X6). La fila v1.2.0 pasa igualmente a "Revisada con observaciones". La aprobación del SRS se emite por escrito y en el documento, no por inferencia de una confirmación verbal. |
| `v1.3.0` | 2026-09-21 | RNF-06 | Verificación | **RNF-06 pasa de NOT VERIFIED a VERIFIED.** La reevaluación de usabilidad se ejecutó el 19 y 20 de septiembre de 2026 con 15 participantes sobre el sistema desplegado, con los diez ítems del cuestionario estándar de Brooke, cuya redacción en español se revisó el 2026-09-20 a las 18:05 (`b9cac65`), de modo que las sesiones del 19 de septiembre usaron la redacción anterior a esa revisión. Media **69,00** (DE 9,90; IC 95 % [63,52; 74,48]), criterio de ≥ 68 cumplido con un margen de +1,00. El estudio de julio de 2026 permanece retirado y no se emplea como evidencia. Se declaran las limitaciones, entre ellas que no se alcanzaron los 16 participantes previstos. |
| `v1.3.0` | 2026-09-21 | RNF-06, privacidad | Corrección | La cláusula de privacidad pasa de "anonimización" a **seudonimización**: los datos publicados no contienen identificadores directos, pero la clave de correspondencia existe y se conserva en custodia externa junto a los consentimientos firmados. |
| `v1.3.0` | 2026-09-21 | SRS (control del documento) | Cierre | Se abre la v1.3.0 como versión de cierre del examen suspenso, con estado "Pendiente de aprobación". El DOI del software pasa al depósito de la v1.3.0 (`10.5281/zenodo.22866363`), que Zenodo generó automáticamente a partir de la publicación de GitHub, en lugar del de la v1.2.0. La comprobación del expediente lee los DOI del propio `CITATION.cff`, de modo que un identificador que no resuelva hace fallar la verificación. |
