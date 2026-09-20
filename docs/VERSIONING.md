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

El propio docente-director autoriza expresamente mover `v1.1.0`. Consta **por escrito** en su evaluación del **2026-09-20**, apartado 2 (*"Cómo dejar la etiqueta donde ustedes quieran"*), que incluye las órdenes concretas:

> Su v1.1.0 quedó cuatro commits por detrás porque, al existir ya en el remoto, un envío normal se rechaza. Estas son las órdenes:
>
>     git tag -f -a v1.1.0 -m "Entrega del examen suspenso" <commit>
>     git push --force origin refs/tags/v1.1.0

En consecuencia, `v1.1.0` se sigue actualizando (reetiquetando) con cada ronda de correcciones del examen suspenso, por instrucción escrita del evaluador. Esto sustituye, **únicamente para `v1.1.0` como línea base del examen**, la recomendación general de X1 (revisión del SRS v1.2.0) de no reescribir etiquetas publicadas.

Quedan **fuera** de esta excepción la `v1.2.0` y cualquier etiqueta futura de versión del SRS, que se rigen por la política general de inmutabilidad.

**Nota sobre la cita.** El documento del 2026-09-20 es una evaluación del docente-director conservada por el equipo y **no está versionado en este repositorio**; se cita con su fecha y su texto literal, conforme a la regla de atribución de [`docs/etica/ETHICS.md`](etica/ETHICS.md) §v.

La versión `v0.9.0` se conserva como referencia de la tercera entrega. Los commits siguen Conventional Commits para mantener un historial legible y facilitar la generación del changelog.
