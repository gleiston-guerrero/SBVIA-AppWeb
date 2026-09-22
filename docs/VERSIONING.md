# Estrategia de Versionado Semántico (SemVer)

Este proyecto adopta de forma estricta [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html) desde la Tercera Entrega.

## Regla de Versionado
Dado un número de versión `MAJOR.MINOR.PATCH`, se incrementa:

1. `MAJOR` cuando se realizan cambios incompatibles en la API.
2. `MINOR` cuando se añade funcionalidad de manera compatible hacia atrás.
3. `PATCH` cuando se realizan correcciones de errores compatibles hacia atrás.

Las etiquetas adicionales (ej. `-rc`, `-alpha`) están disponibles como metadatos de pre-lanzamiento.

## Entrega actual

Hay **dos etiquetas con papeles distintos**, y conviene no confundirlas:

| Etiqueta | Papel | Dónde apunta |
| :--- | :--- | :--- |
| `v1.1.0` | **Línea base del examen suspenso.** Se mueve con cada ronda de correcciones, por la excepción documentada más abajo. | Al commit evaluado (el último de `main`) |
| `v1.2.0` | **Etiqueta de la especificación SRS v1.2.0.** No se mueve: identifica el documento revisado por el docente-director. | Al commit de la especificación v1.2.0 (`0302db6`) |
| `v1.3.0` | **Etiqueta del SRS v1.3.0 y del depósito de software.** Se movió después de crear el depósito, contra la política de esta página; la consecuencia se declara abajo. | Al commit final |
| `v1.3.1` | **Depósito de software que archiva el commit evaluado.** Etiqueta nueva, no movida: se creó sobre el commit final y no se ha reetiquetado. Sustituye a `v1.3.0` como referencia del DOI en `CITATION.cff`. | Al commit final (`c54d9a8`) |

El chequeo de *"etiqueta anotada sobre HEAD"* lo satisface `v1.1.0`, que es la etiqueta de entrega del examen. `v1.2.0` **no se reescribe**: hacerlo dejaría la etiqueta sin correspondencia con el contenido del documento `SRS-v1.2.0`, que es exactamente el problema que el docente-director señaló en X2. La próxima versión de la especificación recibirá su propia etiqueta nueva.

**Etiqueta `v1.3.0` — incumplimiento declarado.** Se creó sobre `5561569` para publicar el *release* que Zenodo convierte en depósito, y después se movió hasta el commit final. El depósito `10.5281/zenodo.22906357`, creado el 2026-09-21 a las 01:51, archivó el estado del repositorio **en ese momento**, no el evaluado: su `isSupplementTo` apunta a `/tree/v1.3.0`, que es una referencia móvil. Esto incumple la política de esta misma página, que se escribió precisamente para evitarlo. La discrepancia se resuelve con un **depósito nuevo desde el commit final** (`v1.3.1`, DOI `10.5281/zenodo.22906357`), que **no se ha reetiquetado** y por tanto sí archiva el código evaluado. `CITATION.cff` declara ya ese identificador.

**Alcance exacto del depósito.** El depósito archiva el commit `c54d9a8`, al que apunta `v1.3.1`. El commit inmediatamente posterior solo actualiza la referencia al DOI en los metadatos, de modo que el árbol evaluado y el archivado difieren en esa única línea. Es inevitable: un depósito no puede contener su propio identificador antes de que exista. Se declara aquí para que la diferencia sea comprobable y no se lea como un desfase oculto.

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
