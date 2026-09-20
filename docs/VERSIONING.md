# Estrategia de Versionado Semántico (SemVer)

Este proyecto adopta de forma estricta [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html) desde la Tercera Entrega.

## Regla de Versionado
Dado un número de versión `MAJOR.MINOR.PATCH`, se incrementa:

1. `MAJOR` cuando se realizan cambios incompatibles en la API.
2. `MINOR` cuando se añade funcionalidad de manera compatible hacia atrás.
3. `PATCH` cuando se realizan correcciones de errores compatibles hacia atrás.

Las etiquetas adicionales (ej. `-rc`, `-alpha`) están disponibles como metadatos de pre-lanzamiento.

## Entrega actual

La versión vigente es `v1.2.0`, etiquetada de forma anotada sobre el commit que resuelve las observaciones X1–X12 del informe de revisión del docente-director. La `v1.1.0` es la **línea base del examen suspenso** y se rige por la excepción documentada más abajo.

**Política de etiquetas:** con carácter general, una etiqueta publicada es inmutable. Cada ronda de correcciones se publica sobre una etiqueta **nueva** (`v1.3.0`, `v1.4.0`, ...), nunca moviendo ni forzando una etiqueta existente. Esta política atiende la indicación escrita del docente-director en la revisión de la v1.2.0 (observaciones X1 y X2): una etiqueta de versión que se reescribe deja de ser una línea base.

## Excepción a la política de no reescribir etiquetas

Confirmado directamente por el docente-director (Ing. Gleiston Cíceron Guerrero Ulloa) el 19-09-2026, respondiendo a la consulta explícita del equipo sobre si usar v1.1.0 de forma continua o etiquetas nuevas por versión: "como quieras si es con la anterior, no hay problema (parece que sería mejor)".

En consecuencia, v1.1.0 se sigue actualizando (reetiquetando) con cada ronda de correcciones del examen suspenso, por instrucción explícita y verificable del evaluador. Esto sustituye, únicamente para v1.1.0 como línea base del examen, la recomendación general de X1 (revisión del SRS v1.2.0) de no reescribir etiquetas publicadas. El respaldo documental de la excepción es el propio mensaje del docente-director, conservado por el equipo.

Quedan **fuera** de esta excepción la `v1.2.0` y cualquier etiqueta futura de versión del SRS, que se rigen por la política general de inmutabilidad.

La versión `v0.9.0` se conserva como referencia de la tercera entrega. Los commits siguen Conventional Commits para mantener un historial legible y facilitar la generación del changelog.
