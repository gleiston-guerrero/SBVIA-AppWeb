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
