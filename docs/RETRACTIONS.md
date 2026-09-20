# Historial de integridad de datos y evidencia retractada

**Proyecto:** SBVIA — Sistema de Simulación de Comportamiento Vial con Inteligencia Artificial
**Repositorio:** https://github.com/gleiston-guerrero/SBVIA-AppWeb
**Etiqueta de referencia:** `v1.2.0`
**Última actualización:** 19-09-2026

---

## 1. Por qué existe este documento

El informe de revisión del Libro de Requisitos (`docs/requisitos/Revision_SRS_SBVIA_v1.2.0.pdf`), en su
apartado *"Observación sobre el expediente"*, señaló que una serie de commits del 18 y 19 de
septiembre corrigen evidencia del informe final, y que **la magnitud de lo corregido obliga a
una lectura cuidadosa del informe**, por lo que conviene que el propio informe declare qué
evidencia fue retractada y por qué.

Este documento atiende esa observación. No es un registro de logros: es un registro de
**errores de integridad de datos detectados y corregidos**, con el commit exacto que aplicó
cada corrección. Reúne también las retractaciones que el equipo decidió por iniciativa propia
antes de que el revisor las señalara.

Criterio de inclusión: toda evidencia que en algún momento se presentó como medición, salida
de comando o resultado empírico y que **no resistió una verificación contra el repositorio, la
herramienta o el entorno declarado**. Las correcciones puramente de estilo, traducción o
formato no se listan.

Todos los hashes de esta tabla son verificables con `git show <hash>`.

---

## 2. Retractaciones registradas

### R-01 — JSON de Lighthouse escritos a mano (rendimiento)

| Campo | Detalle |
|:---|:---|
| **Qué se retractó** | Tres archivos JSON de Lighthouse redactados manualmente, presentados como corridas de auditoría: `docs/mediciones/perf/lighthouse/run1-mobile.json` (682 bytes), `run2-desktop.json` (683 bytes) y `run3-desktop.json` (701 bytes). |
| **Commit de corrección** | `fc4c701` — 18-09-2026 — *fix(perf): replace fabricated Lighthouse JSON reports with real desktop and mobile runs (P7)* |
| **Qué se hizo** | Se eliminaron los tres archivos y se sustituyeron por corridas reales de Lighthouse con su `.report.html` y `.report.json` completos (miles de líneas cada uno). |
| **Por qué** | Los JSON no provenían de una ejecución de Lighthouse: eran documentos cortos escritos a mano. Una auditoría de rendimiento solo es evidencia si proviene de la herramienta declarada. |

### R-02 — `report.json` fabricado y cifras de una corrida local presentadas como producción

| Campo | Detalle |
|:---|:---|
| **Qué se retractó** | `docs/mediciones/lighthouse/report.json` (30 líneas, 781 bytes), escrito a mano. Además, la cifra de rendimiento del 86 % procedía de una corrida **local** sobre `localhost:4201` (`report-realtime.json`), no del despliegue público. |
| **Commits de corrección** | `2ad52ff` — 19-09-2026 — *fix(lighthouse): remove fabricated report.json and add real mobile/desktop runs (P1/P7)*<br>`5fa54d6` — 19-09-2026 — *docs(report): cite real production Lighthouse runs and retract local-run figures (P7)* |
| **Qué se hizo** | Se eliminó el `report.json` fabricado. Se ejecutaron tres corridas móvil y tres de escritorio contra `https://sbvia-frontend.onrender.com/login` (Lighthouse 12.1.0, 19-09-2026), con 337–611 kB por JSON y marcas de tiempo irregulares propias de una ejecución real. Se añadió `scripts/lighthouse-collect.js` para que la recolección sea reproducible. En el informe se retiró la cifra del 86 % y se citaron las corridas de producción. |
| **Por qué** | El archivo no era salida de Lighthouse, y una medición hecha contra `localhost` no describe el comportamiento del sistema desplegado. Presentarla como resultado de producción atribuía al entorno equivocado una cifra que sí era real, lo que induce a error igualmente. |

### R-03 — Salida de `grep` falsa en el expediente de verificación (P11)

| Campo | Detalle |
|:---|:---|
| **Qué se retractó** | En `VERIFICACION.md` se pegaba `200 / 200` como salida de una orden que en realidad imprime `200` seguido del cuerpo JSON de la segunda URL: la opción `-o /dev/null` de `curl` solo afecta a la primera URL de la orden. |
| **Commit de corrección** | `e7a1e34` — 18-09-2026 — *fix(docs): replace false grep output in verification record (P11)* |
| **Qué se hizo** | Se sustituyó la salida no reproducible por la salida literal de la orden realmente ejecutada. |
| **Por qué** | Un expediente de verificación vale exactamente lo que vale su reproducibilidad. Una salida pegada que su propia orden no produce invalida el punto que pretendía demostrar. |

### R-04 — p-valores fijados a mano (P10)

| Campo | Detalle |
|:---|:---|
| **Qué se retractó** | En `docs/mediciones/perf/estadistica.py` los p-valores estaban escritos como constantes en lugar de calcularse a partir del estadístico. |
| **Commit de corrección** | `7b3cfbe` — 18-09-2026 — *fix(stats): compute real p-values instead of hardcoding them (P10)* |
| **Qué se hizo** | El script ahora calcula el p-valor desde el estadístico t y luego aplica la corrección de Holm–Bonferroni. |
| **Por qué** | Un p-valor escrito a mano no es un resultado estadístico: es una afirmación. Con el cálculo real, el valor obtenido (t = 4,571; p = 6,73 × 10⁻⁴) coincide con `scipy`, lo que permite verificar el procedimiento. |

### R-05 — DOIs apuntando a obras distintas de las citadas (P12)

| Campo | Detalle |
|:---|:---|
| **Qué se retractó** | Entradas de `docs/refs.bib` cuyo DOI resolvía a un trabajo diferente del citado, o que no correspondía a una obra existente. |
| **Commit de corrección** | `94fdd61` — 18-09-2026 — *fix(refs): correct and retract DOIs pointing to wrong works (P12)* |
| **Qué se hizo** | Se auditaron los 46 DOI uno por uno contra Crossref y se dejó registro auditable en `docs/doi_check.log`. Resultado: 10 DOIs corregidos, 18 retirados y 18 que ya eran correctos. |
| **Por qué** | Un identificador persistente que apunta a otra obra no es un error tipográfico: es una cita falsa verificable por cualquiera. |

### R-06 — Lista blanca de fallos en el verificador del expediente (EV-2)

| Campo | Detalle |
|:---|:---|
| **Qué se retractó** | `scripts/verify_expediente.py` ignoraba el fallo de determinadas órdenes mediante una lista blanca, de modo que el verificador podía terminar en éxito aunque una orden fallara. |
| **Commit de corrección** | `7a691d0` — 18-09-2026 — *fix(verify): remove failure whitelist and compare expected vs actual output (EV-2)* |
| **Qué se hizo** | Se eliminó la lista blanca: cualquier orden con código de salida distinto de cero hace fallar al verificador, y la salida real se compara contra la esperada. |
| **Por qué** | Un verificador que puede ignorar selectivamente sus propios fallos no verifica nada; su resultado positivo deja de ser informativo. |

### R-07 — Estudio de usabilidad SUS retirado por incompatibilidad de fechas (P5)

| Campo | Detalle |
|:---|:---|
| **Qué se retractó** | El estudio SUS completo: puntuación media de 84,17, intervalo de confianza y clasificación "B+" presentados como resultado empírico de 15 participantes, junto con su registro de consentimientos. |
| **Commits de corrección** | `ec56c20` — 18-09-2026 — *fix(ethics): correct SUS consent registry dates and duration to verifiable facts (P5)*<br>`93ab5ce` — 19-09-2026 — *docs(ethics): retract unverifiable SUS study claims; timeline incompatible with simulator development history (P5)* |
| **Qué se hizo** | Se corrigieron primero las fechas del registro de consentimientos a hechos verificables y se marcaron como "No verificado". Después se retiró el estudio en 36 archivos: `RNF-06` pasa a `NOT VERIFIED` y el artefacto lleva un encabezado que prohíbe citarlo. La figura con las puntuaciones individuales se conserva marcada como *"study retracted; shown as raw material only"*. El cuaderno de análisis quedó sin salidas agregadas. |
| **Por qué** | La fecha declarada de aplicación (2026-07-28/29) es **anterior a la existencia de la funcionalidad que el instrumento dice haber evaluado**: la práctica vial interactiva entra el 2026-09-03 (commit `55201f5`) y el simulador de conducción 2D el 2026-09-04 (commit `5e852c6`). La cronología es incompatible con el historial del repositorio, por lo que el resultado no puede sostenerse. Retirar evidencia propia que no se sostiene es preferible a defenderla. |

### R-08 — Corrección N4 escrita sobre una migración ya aplicada (V14)

| Campo | Detalle |
|:---|:---|
| **Qué se retractó** | La corrección N4 (`ultimo_acceso` en lugar de `fecha_registro`) se había escrito directamente sobre `V14__procedimientos_almacenados.sql`, una migración ya aplicada. |
| **Commit de corrección** | `deda187` — 18-09-2026 — *fix(db): revert V14 to original state and move N4 fix to new V18 migration to resolve Flyway checksum mismatch in production* |
| **Qué se hizo** | Se revirtió `V14` a su estado original y la corrección se movió a la migración nueva `V18__conectar_procedimientos_sp.sql`, restaurando la coherencia de *checksums* de Flyway con la base desplegada. |
| **Por qué** | Reescribir una migración ya aplicada rompe la reproducibilidad del esquema: el mismo archivo produciría bases distintas según cuándo se ejecutara. |

### R-09 — Atribución al docente sin texto que la respalde ("MD5 confirmado por el docente")

| Campo | Detalle |
|:---|:---|
| **Qué se retractó** | En `docs/observaciones/OBSERVACIONES.md`, la nota del punto X11 afirmaba que el PDF del repositorio era "idéntico byte a byte al revisado (MD5 confirmado por el docente)", sin citar ningún texto del docente ni indicar cómo comprobarlo. |
| **Commit de corrección** | `5c02c22` — 20-09-2026 — *docs(integrity): fix an unsupported attribution and a citation to a non-existent report* |
| **Qué se hizo** | Se sustituye por el hecho comprobable: el archivo `docs/requisitos/SRS-v1.2.0.pdf` en el commit `a5938b8` tiene MD5 `79d62386e6c4afb634147ff7f6c8384c`, que es el que consigna el informe de revisión, y se aclara que el PDF se recompiló después, por lo que su hash actual es distinto. |
| **Por qué** | Es el mismo patrón que la atribución verbal retirada en la ronda X6: atribuir al docente una confirmación sin texto que la respalde. Lo detectó el propio docente-director en su evaluación del 2026-09-20, que lo cuenta entre las dos afirmaciones graves que mantienen el Piso 3 en riesgo. Se corrige con una comprobación reproducible en lugar de con otra afirmación. |

### R-10 — Cuatro citas a un informe de revisión que no existía en el repositorio

| Campo | Detalle |
|:---|:---|
| **Qué se retractó** | Cuatro archivos citaban `Revision_SRS_SBVIA_v1.2.0.pdf` como informe firmado del docente-director: `docs/observaciones/OBSERVACIONES.md`, `docs/requisitos/CHANGELOG-REQ.md`, `docs/RETRACTIONS.md` y `VERIFICACION.md`. Ese archivo **no existía ni había existido nunca** en el historial (`git log --all` sobre esa ruta no devuelve nada), mientras que los informes de las versiones 1.0.0 y 1.1.0 sí estaban versionados. |
| **Commit de corrección** | `5c02c22` — 20-09-2026 — *docs(integrity): fix an unsupported attribution and a citation to a non-existent report* |
| **Qué se hizo** | Se incorpora `docs/requisitos/Revision_SRS_SBVIA_v1.2.0.pdf` al repositorio, junto a los informes de las otras dos versiones, de modo que las cuatro citas pasan a ser verdaderas y verificables. Dos de ellas se completan con la ruta `docs/requisitos/`. |
| **Por qué** | Citar como prueba un archivo inexistente es del mismo tipo que pegar una salida que la orden no produce: la afirmación no resiste su propia comprobación. El origen del error fue dar por versionado un documento que solo se había recibido como adjunto, sin comprobarlo con `git log` antes de citarlo. |

---

## 3. Resumen

| # | Evidencia retractada | Commit | Fecha |
|:---:|:---|:---|:---|
| R-01 | JSON de Lighthouse escritos a mano (682/683/701 bytes) | `fc4c701` | 18-09-2026 |
| R-02 | `report.json` fabricado (781 bytes) y cifra de corrida local como producción | `2ad52ff`, `5fa54d6` | 19-09-2026 |
| R-03 | Salida de `grep` falsa en el expediente (P11) | `e7a1e34` | 18-09-2026 |
| R-04 | p-valores fijados a mano (P10) | `7b3cfbe` | 18-09-2026 |
| R-05 | DOIs apuntando a obras distintas de las citadas (P12) | `94fdd61` | 18-09-2026 |
| R-06 | Lista blanca de fallos en el verificador (EV-2) | `7a691d0` | 18-09-2026 |
| R-07 | Estudio SUS retirado por incompatibilidad de fechas (P5) | `ec56c20`, `93ab5ce` | 18–19-09-2026 |
| R-08 | Migración V14 ya aplicada reescrita (N4) | `deda187` | 18-09-2026 |
| R-09 | Atribución al docente sin texto que la respalde ("MD5 confirmado por el docente") | `5c02c22` | 20-09-2026 |
| R-10 | Cuatro citas a un informe de revisión que no existía en el repositorio | `5c02c22` | 20-09-2026 |

---

## 4. Nota sobre lo que este documento no hace

Las retractaciones anteriores corrigen evidencia del expediente; **no corrigen por sí solas el
puntaje de los puntos afectados**. Varios de esos puntos siguen sin cumplir su criterio por
otras razones, que se declaran en sus propios requisitos y en el informe de evaluación:

- **RNF-06 (SUS)** sigue siendo `NOT VERIFIED` y con prioridad *Must*; se está ejecutando una
  nueva evaluación de usabilidad sobre el sistema ya construido (véase la nota X12 en el SRS).
- **RNF-02 (latencia en frío)** declara su umbral incumplido con la medición real.
- **RF-16 a RF-20** pasan a `NOT VERIFIED` porque los procedimientos no se invocan desde la
  aplicación, aunque su definición exista y esté versionada.

Este historial tampoco sustituye a la verificación: cada corrección es comprobable con
`git show <hash>` y con la re-ejecución de la orden correspondiente.
