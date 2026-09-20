# Declaración de Ética y Responsabilidad de los Datos

Este documento declara explícitamente los aspectos éticos del uso de datos y de participantes en el proyecto **Simulador de Comportamiento Vial con IA (SBVIA)**, conforme a las directrices de la Tercera Entrega (Bloque F).

## i. Fuentes de datos y su licencia
Los datos utilizados para entrenar o validar los modelos de IA provienen de fuentes públicas y conjuntos de datos abiertos para investigación (por ejemplo, [A multi-class driver behavior dataset](https://doi.org/10.1016/J.DIB.2025.111529)). Estos datos se utilizan exclusivamente bajo los términos de sus licencias originales orientadas a la academia e investigación (generalmente CC BY 4.0 o similares).

## ii. Tratamiento de datos personales
El sistema SBVIA registra datos de cuenta y métricas de comportamiento, como tiempos de reacción e infracciones. Las contraseñas se conservan mediante derivación BCrypt y la comunicación productiva debe utilizar HTTPS. Esto no equivale a afirmar que todos los campos de la base estén cifrados. Los datos no deben compartirse con terceros fuera de las finalidades informadas y autorizadas.

## iii. Consentimiento y aprobación institucional

Las pruebas de usabilidad con personas requieren consentimiento informado individual. Este apartado describe **la secuencia real** de este proyecto: **no hubo aprobación previa**. Las sesiones se realizaron el 19 y el 20 de septiembre de 2026 y la convalidación por la autoridad competente es **posterior** a su aplicación (código `APPWEB-SBVIA-2026-01`, de fecha 2026-09-20). El formulario vigente es [Consentimiento-Informado-SBVIA.pdf](consentimientos/Consentimiento-Informado-SBVIA.pdf) (LaTeX: `Consentimiento-Informado-SBVIA.tex`), versión 1.0 de septiembre de 2026.

### iii.a Estudio de julio de 2026 — RETIRADO
La cronología declarada de ese estudio es **incompatible con el historial de desarrollo del repositorio**: la fecha de aplicación declarada (2026-07-28/29) es **anterior a la incorporación de la funcionalidad de simulación que el instrumento dice haber evaluado** —la "práctica vial interactiva" entra el 2026-09-03 (commit `55201f5`) y el "simulador de conducción 2D" el 2026-09-04 (commit `5e852c6`)—. Por ese motivo el estudio SUS **se retira** como evidencia empírica y no se presenta como estudio institucionalmente aprobado ni como resultado de usabilidad. No se propone ninguna explicación alternativa para esa incompatibilidad. La presentación de documentos por canal privado no restablece la validez de ese estudio, porque la incompatibilidad de fechas es independiente de la custodia documental.

### iii.b Estudio de septiembre de 2026 — VIGENTE
**Cronología verificable.** Las sesiones se realizaron el **19 y el 20 de septiembre de 2026** sobre el sistema desplegado en `https://sbvia-frontend.onrender.com`. Los diez ítems del SUS aplicados están versionados en el repositorio desde el commit `786e042` (2026-09-18), **anterior a la primera sesión**; el formulario imprimible se incorpora en `docs/mediciones/sus/`. El consentimiento se generó el 2026-09-19, antes de las sesiones de ese día. No hay incompatibilidad de cronología en este estudio.

**Consentimiento.** Cada participante firmó el formulario antes de su sesión. Los formularios firmados contienen nombre y firma, por lo que **no se publican**; se conservan bajo custodia de los investigadores, en acceso restringido y separados de las respuestas. El enlace al almacenamiento se facilita al docente-director **por canal privado**, nunca desde este repositorio público.

**Naturaleza de los datos.** Con el formulario firmado en custodia y un código asignado en el mismo documento, los datos son **confidenciales y seudonimizados**, no anónimos. El repositorio público solo contiene los códigos `P01`…`P15` y sus respuestas, sin ningún identificador directo.

**Autorización y convalidación.** Las sesiones se realizaron **antes** de contar con una aprobación formal previa, y el expediente lo declara así en lugar de simular el orden. La autoridad competente es el **docente-director de la asignatura Aplicaciones Web**, dentro del alcance de la práctica docente —no es un dictamen de comité de ética ni lo sustituye—, que convalidó la aplicación con fecha **2026-09-20** bajo el código **`APPWEB-SBVIA-2026-01`**. La naturaleza de esa convalidación es **posterior a la aplicación**, no una aprobación previa. El texto íntegro del mensaje de convalidación se conserva en [`convalidacion-APPWEB-SBVIA-2026-01.md`](convalidacion-APPWEB-SBVIA-2026-01.md).

La indicación metodológica previa del docente-director sobre reclutar familiares, compañeros y amigos **no constituye aprobación ética** y no se registra como tal.

## iv. Ausencia de datos identificables en el repositorio
Se certifica que en este repositorio público **no se incluyen datos personales identificables** (PII), ni de los usuarios del sistema, ni de los participantes en las pruebas empíricas. Los datos crudos reportados en `docs/mediciones/` se identifican mediante códigos (`P01`, `P02`…). Esa codificación es **seudonimización, no anonimización**: la correspondencia entre código y persona existe y está custodiada fuera del repositorio (véase §iii.b).

## v. Evidencia que debe presentar el equipo por vía privada

Para el estudio de septiembre de 2026 (véase §iii.b):

- **Convalidación de la autoridad**, con código, fecha y alcance: `APPWEB-SBVIA-2026-01`, 2026-09-20 — [`convalidacion-APPWEB-SBVIA-2026-01.md`](convalidacion-APPWEB-SBVIA-2026-01.md). Naturaleza: posterior a la aplicación.
- **Un consentimiento firmado por cada participante**, con finalidad, conservación, acceso y retiro. Custodiado fuera del repositorio.
- **Registro de sesión** con código, fecha, hora, duración y modalidad.
- **Consistencia de las 15 entradas**: código, fecha de sesión y fecha de firma deben coincidir entre el registro versionado, los cuestionarios y los originales firmados.
- **Enlace al almacenamiento restringido**, compartido con el docente-director en **solo lectura** y por canal privado; nunca desde el repositorio.

### Regla de atribución

Toda afirmación atribuida al docente-director se cita **con su texto escrito y su fecha**, y no se registra como "confirmado por el docente". Si no existe texto que la autoridad pueda releer, no se cita.
