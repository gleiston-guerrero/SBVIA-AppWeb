# Análisis de Usabilidad — System Usability Scale (SUS)

> **ESTUDIO RETIRADO — evidencia no sostenible.**
> Este documento se conserva únicamente como constancia del material existente y del
> motivo de su retirada. No debe citarse como resultado de usabilidad del sistema.

**Proyecto:** SBVIA — Simulador de Comportamiento Vial con IA  
**Fecha de aplicación declarada:** 2026-07-28 / 2026-07-29 (**no verificable**)  
**N participantes:** 15 filas en `sus-raw-data.csv` (caracterización de los participantes: no verificable)  
**Instrumento:** Cuestionario SUS estándar de 10 ítems (escala Likert 1-5)

## Motivo de la retirada

El origen y las condiciones exactas de aplicación de este instrumento **no pueden
verificarse contra el historial del repositorio**: la fecha declarada de aplicación
(2026-07-28/29) es **anterior a la existencia de la funcionalidad de simulación** que el
instrumento dice haber evaluado.

Evidencia en el historial de git:

| Commit | Fecha | Aporte |
|---|---|---|
| `55201f5` | 2026-09-03 | `feat: agrega la práctica vial interactiva` |
| `5e852c6` | 2026-09-04 | `feat: simulador de conduccion 2D con retroalimentacion IA` |

La funcionalidad de simulación de conducción se incorpora al repositorio el **3-4 de
septiembre de 2026**, con posterioridad a la fecha declarada de aplicación del
instrumento. La sesión de simulación que el documento declaraba como contexto de la
prueba **no puede confirmarse contra el historial del sistema**, y no se propone ninguna
explicación alternativa para esa incompatibilidad.

En consecuencia, los datos de `docs/mediciones/sus/sus-raw-data.csv` se presentan **sin
garantía de validez metodológica** y **no deben interpretarse como evidencia empírica
confiable del sistema**.

## Metodología

La versión anterior de este documento declaraba que se aplicó el cuestionario SUS de
Brooke (1996) a 15 participantes inmediatamente después de que cada uno completara una
sesión de simulación. **Esa declaración se retira**: no es sostenible por lo expuesto
arriba.

- **Población:** estudiantes de conducción (no verificable).
- **Consentimiento informado:** **no verificado.** El repositorio no contiene evidencia
  de consentimiento informado firmado; véase
  `docs/etica/consentimientos/registro-aceptacion.md` y `ETHICS.md` §iii.
- **Datos crudos:** `sus-raw-data.csv` en este mismo directorio.

## Fórmula de Cálculo (Brooke, 1996)

Se conserva únicamente como referencia metodológica; **no constituye un resultado de este
proyecto**.

```
SUS_individual = (Σcontribuciones_impares + Σcontribuciones_pares) × 2.5

donde:
  contribución_impar(Qi) = valor_Qi - 1     (para Q1, Q3, Q5, Q7, Q9)
  contribución_par(Qi)   = 5 - valor_Qi     (para Q2, Q4, Q6, Q8, Q10)
```

Rango teórico: 0–100 (no es porcentaje, es escala SUS).

## Datos crudos por participante (sin interpretación)

Inventario de los valores contenidos en `sus-raw-data.csv`. Se listan **únicamente como
registro del material**; no constituyen un resultado de usabilidad del sistema y no se
interpretan frente a ninguna escala de calificación.

| ID  | sus_score |
|-----|-----------|
| P01 | 75.0      |
| P02 | 92.5      |
| P03 | 75.0      |
| P04 | 92.5      |
| P05 | 85.0      |
| P06 | 75.0      |
| P07 | 95.0      |
| P08 | 75.0      |
| P09 | 92.5      |
| P10 | 77.5      |
| P11 | 95.0      |
| P12 | 80.0      |
| P13 | 75.0      |
| P14 | 95.0      |
| P15 | 82.5      |

## Resumen estadístico e interpretación — RETIRADOS

**No se reportan** media, desviación estándar ni intervalo de confianza como resultados
válidos, ni se clasifica al sistema según escalas de interpretación del SUS. Cualquier
cálculo que pudiera hacerse sobre `sus-raw-data.csv` corresponde a datos cuya aplicación
no puede sostenerse y **no debe presentarse como resultado de usabilidad del sistema**.

## Referencia

- Brooke, J. (1996). SUS: A "quick and dirty" usability scale. *Usability Evaluation in Industry*, 189(194), 4-7.
- Bangor, A., Kortum, P., & Miller, J. (2008). An empirical evaluation of the System Usability Scale. *Int. J. Human–Computer Interaction*, 24(6), 574-594.
- Sauro, J., & Lewis, J. R. (2016). *Quantifying the user experience* (2nd ed.). Morgan Kaufmann.
