# Registro de Aceptación de Consentimientos Informados

Este archivo reúne **dos estudios distintos**. Se mantienen separados a propósito: el primero se
retiró como evidencia y no debe confundirse con el segundo.

---

## 1. Estudio de julio de 2026 — **RETIRADO**

> **Estado: RETIRADO. No debe citarse como evidencia de usabilidad.**
>
> El estudio SUS se retiró porque su fecha declarada de aplicación (2026-07-28 / 2026-07-29) es
> **anterior a la incorporación de la funcionalidad de simulación** que el instrumento dice haber
> evaluado: la "práctica vial interactiva" entra el 2026-09-03 (commit `55201f5`) y el "simulador
> de conducción 2D" el 2026-09-04 (commit `5e852c6`). Véanse
> [`sus-analysis.md`](../../mediciones/sus/sus-analysis.md) y
> [`RETRACTIONS.md`](../../RETRACTIONS.md) (R-07).

La fecha declarada permanece **sin alteración** desde la creación de `sus-analysis.md`
(commit `bd17051`, 2026-08-08), según el historial de git. El repositorio público **no contiene
evidencia verificable** de que se obtuviera consentimiento informado firmado de estos
participantes en la fecha declarada, por lo que la columna correspondiente dice "No verificado".

Los códigos P01–P15 de esta tabla corresponden a las 15 filas de
`docs/mediciones/sus/sus-raw-data.csv` (registro del estudio retirado).

| Código | Fecha declarada de aplicación | Consentimiento firmado |
| :---: | :---: | :---: |
| P01 | 2026-07-28 / 2026-07-29 | No verificado |
| P02 | 2026-07-28 / 2026-07-29 | No verificado |
| P03 | 2026-07-28 / 2026-07-29 | No verificado |
| P04 | 2026-07-28 / 2026-07-29 | No verificado |
| P05 | 2026-07-28 / 2026-07-29 | No verificado |
| P06 | 2026-07-28 / 2026-07-29 | No verificado |
| P07 | 2026-07-28 / 2026-07-29 | No verificado |
| P08 | 2026-07-28 / 2026-07-29 | No verificado |
| P09 | 2026-07-28 / 2026-07-29 | No verificado |
| P10 | 2026-07-28 / 2026-07-29 | No verificado |
| P11 | 2026-07-28 / 2026-07-29 | No verificado |
| P12 | 2026-07-28 / 2026-07-29 | No verificado |
| P13 | 2026-07-28 / 2026-07-29 | No verificado |
| P14 | 2026-07-28 / 2026-07-29 | No verificado |
| P15 | 2026-07-28 / 2026-07-29 | No verificado |

---

## 2. Estudio de septiembre de 2026 — **VIGENTE**

**Diseño:** sesiones presenciales individuales, realizadas en persona, sobre el sistema desplegado
en `https://sbvia-frontend.onrender.com`.
**Periodo de aplicación:** sábado 19 y domingo 20 de septiembre de 2026.
**Instrumento:** cuestionario SUS de 10 ítems (Brooke, 1996) —
[`Cuestionario-SUS-Participante.pdf`](../../mediciones/sus/Cuestionario-SUS-Participante.pdf).
**Consentimiento:** [`Consentimiento-Informado-SBVIA.pdf`](Consentimiento-Informado-SBVIA.pdf),
firmado por cada participante **antes** de su sesión.

### Orden de los códigos

Los códigos `P01`…`P15` se asignaron **antes** de las sesiones, no en el orden en que estas se
realizaron. Por eso el orden de los códigos **no coincide** con el orden cronológico: el 19 de
septiembre se realizaron las sesiones de los códigos P01, P03, P04, P06 y P08, y el 20 de
septiembre las de P02, P05, P07 y P09 a P15.

### Corrección de una inconsistencia detectada (2026-09-20)

La versión anterior de este archivo listaba los códigos `P01`…`P15` **únicamente** con las fechas
del estudio de julio (2026-07-28 / 2026-07-29), sin indicar a qué estudio pertenecían. El estudio de
septiembre **reutiliza esos mismos códigos para personas distintas**, de modo que esa versión hacía
parecer que las sesiones de septiembre estaban fechadas en julio. Se corrige aquí, dejando
constancia del cambio:

- Los dos estudios quedan separados en dos secciones y rotulados.
- Los códigos `P01`…`P15` del estudio de septiembre corresponden a **personas distintas** de las del
  estudio retirado. La coincidencia de códigos se declara en lugar de ocultarse.
- Las fechas de sesión del estudio vigente son las del 19 y 20 de septiembre de 2026, no las de
  julio.

Si al revisar los originales firmados apareciera alguna entrada mal fechada, se corregirá dejando
constancia del cambio, sin reescribirla en silencio.

**Convalidación de la autoridad:** `APPWEB-SBVIA-2026-01`, de fecha 2026-09-20 — texto íntegro en
[`convalidacion-APPWEB-SBVIA-2026-01.md`](../convalidacion-APPWEB-SBVIA-2026-01.md).

### Registro

Las **fechas de sesión, duraciones y dispositivos** provienen de los cuestionarios SUS versionados
([`sus-raw-data-2026-09.csv`](../../mediciones/sus/sus-raw-data-2026-09.csv)). El **código y la fecha
de firma** provienen de cada formulario de consentimiento firmado.

| Código | Fecha de sesión | Duración | Dispositivo | Fecha de firma | Consentimiento | Custodia |
| :---: | :---: | :---: | :---: | :---: | :---: | :--- |
| P01 | 2026-09-19 | 67 min | Laptop | por verificar | Sí | Custodia externa de los investigadores |
| P02 | 2026-09-20 | 15 min | Laptop | por verificar | Sí | Custodia externa de los investigadores |
| P03 | 2026-09-19 | 13 min | Escritorio | por verificar | Sí | Custodia externa de los investigadores |
| P04 | 2026-09-19 | 15 min | Escritorio | por verificar | Sí | Custodia externa de los investigadores |
| P05 | 2026-09-20 | 12 min | Escritorio | por verificar | Sí | Custodia externa de los investigadores |
| P06 | 2026-09-19 | 15 min | Escritorio | por verificar | Sí | Custodia externa de los investigadores |
| P07 | 2026-09-20 | 15 min | Laptop | por verificar | Sí | Custodia externa de los investigadores |
| P08 | 2026-09-19 | 17 min | Escritorio | por verificar | Sí | Custodia externa de los investigadores |
| P09 | 2026-09-20 | 10 min | Laptop | por verificar | Sí | Custodia externa de los investigadores |
| P10 | 2026-09-20 | 14 min | Laptop | por verificar | Sí | Custodia externa de los investigadores |
| P11 | 2026-09-20 | 15 min | Laptop | por verificar | Sí | Custodia externa de los investigadores |
| P12 | 2026-09-20 | 18 min | Laptop | por verificar | Sí | Custodia externa de los investigadores |
| P13 | 2026-09-20 | 17 min | Laptop | por verificar | Sí | Custodia externa de los investigadores |
| P14 | 2026-09-20 | 13 min | Laptop | por verificar | Sí | Custodia externa de los investigadores |
| P15 | 2026-09-20 | 15 min | Laptop | por verificar | Sí | Custodia externa de los investigadores |

**Resumen:** 15 participantes — 5 sesiones el 2026-09-19 y 10 sesiones el 2026-09-20.

> **Pendiente de verificar: la fecha de firma de cada formulario.** No se rellena aquí porque exigiría
> deducirla, y el docente-director pidió expresamente que las tres fechas —código, sesión y firma—
> sean consistentes entre el registro, los cuestionarios y los **originales firmados**. La fecha de
> firma debe leerse de los originales en papel (custodia externa), no suponerse igual a la de sesión.
>
> **Inconsistencia conocida en los archivos digitalizados de consentimiento.** Los PDF de
> consentimiento recibidos no están nombrados de forma fiable por código: el archivo nombrado `P08`
> contiene el consentimiento del código **P03** (fecha 2026-09-19). Antes de dar por buena cualquier
> correspondencia archivo ↔ código, debe leerse el código **dentro** de cada formulario. Se deja
> constancia aquí en lugar de corregirlo en silencio.

### Custodia y acceso

Los formularios firmados contienen **nombre y firma**, por lo que **no se publican**. Se conservan
bajo custodia de los investigadores en un almacenamiento de acceso restringido, **separados de las
respuestas**, y se muestran al docente-director por canal privado cuando los solicite.

El enlace a ese almacenamiento **no se incluye en este repositorio público**, porque daría acceso
a datos personales de los participantes. Se facilita al docente por el canal privado indicado en
[`ETHICS.md`](../ETHICS.md) §iii.

### Nota sobre la cronología del instrumento

Los diez ítems del SUS aplicados son los del cuestionario estándar de Brooke. El archivo se incorporó
en el commit `786e042` (2026-09-18), pero **su redacción en español se revisó el 2026-09-20 a las
18:05** (`b9cac65`), de modo que las sesiones del **19 de septiembre** usaron la redacción anterior a
esa revisión. **No se sostiene, por tanto, que el instrumento sea anterior a la primera sesión.** El
formulario imprimible
[`Cuestionario-SUS-Participante.tex`](../../mediciones/sus/Cuestionario-SUS-Participante.tex)
reproduce los ítems en su redacción vigente, que no tiene por qué ser la aplicada el 19. La fecha de
compilación del PDF corresponde al artefacto, no a la fecha de aplicación del instrumento; la
referencia válida del instrumento es el fuente versionado.

**Cronología del consentimiento.** Se declara con fecha de generación del **2026-09-19**, y su PDF
lleva fecha interna de compilación del **2026-09-20 a las 13:03** (hora de Ecuador), posterior al
inicio de las sesiones del 19. **Ambas fechas se declaran sin conciliarlas.** Las quince fechas de
firma figuran como **por verificar**.
