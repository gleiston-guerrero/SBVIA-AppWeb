# Estrategia de Versionado Semántico (SemVer)

Este proyecto adopta de forma estricta [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html) desde la Tercera Entrega.

## Regla de Versionado
Dado un número de versión `MAJOR.MINOR.PATCH`, se incrementa:

1. `MAJOR` cuando se realizan cambios incompatibles en la API.
2. `MINOR` cuando se añade funcionalidad de manera compatible hacia atrás.
3. `PATCH` cuando se realizan correcciones de errores compatibles hacia atrás.

Las etiquetas adicionales (ej. `-rc`, `-alpha`) están disponibles como metadatos de pre-lanzamiento.

## Entrega actual

La versión vigente es `v1.2.0`, etiquetada de forma anotada sobre el commit que resuelve las observaciones X1–X12 del informe de revisión del docente-director. La `v1.1.0` se conserva como referencia histórica en el commit `a5938b8` y **no se reescribe**.

**Política de etiquetas:** una etiqueta publicada es inmutable. Cada ronda de correcciones se publica sobre una etiqueta **nueva** (`v1.3.0`, `v1.4.0`, ...), nunca moviendo ni forzando una etiqueta existente. Esta política atiende la indicación escrita del docente-director en la revisión de la v1.2.0 (observaciones X1 y X2): una etiqueta de versión que se reescribe deja de ser una línea base.

La versión `v0.9.0` se conserva como referencia de la tercera entrega. Los commits siguen Conventional Commits para mantener un historial legible y facilitar la generación del changelog.
