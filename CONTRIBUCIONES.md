# Declaración de Aportes (EV-4)

Este documento detalla quién resolvió cada uno de los puntos pendientes de la evaluación, con evidencia contrastable en el historial del repositorio. Todo el trabajo reciente ha sido realizado por el primer integrante, tal como lo demuestra el registro de commits.

## Resoluciones por Integrante

### Justyn K. Cruz Pérez (jcruzp@uteq.edu.ec)
**Usuario Git:** `JustynCruz04` / `keithdrox`

Puntos cerrados de manera comprobable (el hash es el commit que aplicó la corrección):

*   **P1 — Sin despliegue público (1.3 pts)**
    *   **Archivos:** `README.md`, `frontend/proxy.conf.json`, configuración de Render.
    *   **Commits:** `bd17051`, `a1e5714` y los ajustes de documentación del despliegue `a89a29d`, `15f4b29`.
*   **Piso 3 (ZAP Falso) -> Escaneo ZAP Real**
    *   Se eliminó el reporte ZAP falsificado a mano y se generó un nuevo reporte real apuntando al backend productivo `https://sbvia-appweb.onrender.com`.
    *   **Commit:** `21edc80`.
    *   **Archivos:** `docs/mediciones/sec/zap/zap-report.html` (reporte real de 40 kB, verificado).
*   **P2 — Javadoc inexistente (1.5 pts)**
    *   Se resolvieron las advertencias de Javadoc del backend (0 de tipo `no comment` y 0 de tipo `no @param`), se tradujeron los bloques `Método público.` y se movieron los bloques que quedaban entre anotación y método. Se añadió `maven-javadoc-plugin` con `failOnError`.
    *   **Commits:** `b03f8e3`, `735dc81`.
    *   **Archivos:** `backend/pom.xml`, controladores y servicios del backend.
    *   **Segunda pasada — los 80 javadoc plantilla (`25450d1`).** Ochenta de los 102 tipos públicos conservaban un javadoc cuyo único contenido era el nombre del tipo: `<p>Decision class.</p>`. Ahora cada uno describe para qué sirve, redactado desde su rol y su paquete: las entidades qué persisten, los repositorios a qué entidad sirven, los DTO qué carga llevan, los controladores qué endpoints exponen, los servicios qué caso de uso sostienen y el paquete de retroalimentación qué estrategia implementa cada proveedor. Quedan **0 plantillas** y **0 referencias `{@link}` sin resolver** —importa porque `maven-javadoc-plugin` corre con `failOnError` y `doclint`—. Solo se tocaron comentarios: 80 archivos, 80 líneas.
*   **P3 — El PDF no contiene ninguna imagen (1.2 pts)**
    *   Se referenciaron desde el texto las 4 capturas de interfaz que no estaban citadas y se empezaron a traducir al inglés los pies de figura. La primera pasada (`9afd075`) tradujo 12 de 20 y dejó 8 en español, que es la inconsistencia que recoge la evaluación. Después **se completó la traducción: los 20 pies de figura del informe están en inglés** (`6fc74d3`), y sus etiquetas automáticas también: `Figure 3.1` y `Table 5.1` en lugar de «Figura» y «Tabla» (`0455786`).
    *   **Capturas de la interfaz en inglés.** El sistema es para conductores en formación ecuatorianos y su interfaz está en español; traducir el producto habría perjudicado a sus usuarios. La solución fue **internacionalización con español por defecto** (`ccee94f`): la aplicación sigue sirviendo en español y puede mostrarse en inglés desde un selector ES | EN. Con él se **recapturaron las 4 pantallas contra el sistema desplegado** (`9e7cc01`), en el mismo tamaño que las originales (1599×770).
    *   **Se declara lo que sigue en español y por qué:** los nombres de escenario, los tipos de vía y el texto del informe de IA los genera el servidor o están en la base de datos. Son **contenido**, no interfaz, igual que los nombres de escenario son topónimos ecuatorianos. La interfaz de las cuatro capturas está íntegramente en inglés.
    *   **Commits:** `9afd075`, `6fc74d3`, `0455786`, `ccee94f`, `d5f7841`, `9e7cc01`.
    *   **Archivos:** `docs/informe-final.tex`, `docs/informe-final.pdf`, `docs/diagramas/pantalla-*.png`, `frontend/src/app/i18n/`.
*   **P4 — Sin depósito propio en Zenodo (1.0 pts)**
    *   Se añadió a `CITATION.cff` la referencia al dataset de validación con su DOI real (`10.5281/zenodo.22785358`).
    *   **Commit:** `12297ee`.
    *   **Corrección del DOI del software (`84a8ae0`, `6f4a9e1`).** `CITATION.cff` declaraba la versión 1.2.0 con el DOI `10.5281/zenodo.22740480`, que **resuelve a la v1.0.1**, un depósito del 14-sep que además lista al tercer integrante entre sus creadores: quien siguiera ese identificador descargaba código que no era el evaluado. Se sustituye por el depósito de la **v1.3.0** (`10.5281/zenodo.22866363`), que Zenodo generó automáticamente al publicar el *release* de `v1.3.0`. Se comprueba contra DataCite: título, versión, tipo Software y `IsSupplementTo` apuntando a `github.com/gleiston-guerrero/SBVIA-AppWeb/tree/v1.3.0`. El DOI de concepto (`10.5281/zenodo.22740479`) queda documentado como la alternativa que no caduca. **Excepción declarada:** el reemplazo global que actualizó el DOI cambió también la primera línea, `cff-version`, que es la versión del *esquema* y no la del software, dejando el archivo inválido. Está corregida y registrada como `R-14`.
    *   **El dataset no se modifica:** su depósito contiene datos crudos, diccionarios, análisis estático, escaneo de vulnerabilidades y mediciones k6, y **no incluye el estudio de usabilidad**, de modo que no le afecta ni la retirada de julio ni el estudio de septiembre.
    *   **Commits:** `12297ee`, `84a8ae0`, `6f4a9e1`.
    *   **Archivos:** `CITATION.cff`, `README.md`, `docs/checklists/fair.md`, `docs/requisitos/SRS-v1.3.0.tex`, `scripts/verify_dois.py`.
*   **P5 — Instrumento y consentimientos del SUS (0.8 pts) — PARCIAL, con la limitación declarada**
    *   **Estudio de septiembre de 2026 (`186f64e`, `b9cac65`, `b6c37dc`).** 15 participantes en 15 sesiones presenciales el 19 y 20 de septiembre sobre el sistema desplegado. Media **69,00** (DE 9,90; IC 95 % [63,52; 74,48]; mediana 70,0): `RNF-06` **cumple** con un margen de +1,00, y así figura como `VERIFIED` en el SRS v1.3.0. Incluye nueve limitaciones declaradas y la trazabilidad del estudio en ocho eslabones comprobables. El instrumento y el consentimiento están versionados y son compilables.
    *   **Limitación declarada y no disimulada:** no se alcanzaron los 16 participantes previstos, y con la media observada un participante más habría dejado la puntuación por debajo del umbral. La redacción del instrumento se revisó el 2026-09-20 a las 18:05, de modo que las sesiones del 19 usaron la redacción anterior (véase `R-12`).
    *   **Estado anterior, retirado:** el estudio SUS de julio se retira de todo el expediente. Su fecha declarada de aplicación (2026-07-28/29) es anterior a la incorporación al repositorio de la funcionalidad de simulación que el instrumento dice haber evaluado: la "práctica vial interactiva" entra el 2026-09-03 (commit `55201f5`) y el "simulador de conducción 2D" el 2026-09-04 (commit `5e852c6`). No se propone ninguna explicación alternativa para esa inconsistencia, y la custodia de los consentimientos permanece no verificada.
    *   **Referencias del historial (no se presentan como logro):** las respuestas crudas y el análisis se incorporaron en `bd17051` (2026-08-08); el registro de aceptación se creó en `0107364` y se corrigió en `ec56c20` (ambos de 2026-09-18) para declarar únicamente hechos verificables. La retirada del estudio no aporta puntaje.
    *   **Archivos:** `docs/mediciones/sus/*`, `docs/etica/consentimientos/registro-aceptacion.md`, `docs/etica/ETHICS.md`, `VERIFICACION.md`.
*   **P6 — Nombres en español en el código (Contrato REST roto) (1.0 pts)**
    *   Se restauraron los contratos REST y, en la ronda final, se alinearon los modelos del frontend (Backup y AuditLog) con el JSON real del backend, campo por campo, verificados con una prueba end-to-end contra producción.
    *   **Commits:** `21edc80`, `a191fb3`.
    *   **Archivos:** `BackupController.java`, `AuditController.java`, `frontend/src/app/features/backups/*`, `frontend/src/app/features/audit/*`.
    *   **Renombrado del backend (`cd30632`).** Once métodos con nombre en español pasan a inglés —`listar`, `listarActivos`, `iniciar`, `iniciarSimulacion`, `finalizar`, `finalizarSimulacion`, `finalizarConduccion`, `cambiarRol`, `inactivarUsuariosInactivos`, `guardarMetrica`, `guardarInfraccion`—, con 43 puntos de llamada actualizados. **Sin cambios de contrato REST:** las rutas son explícitas (`@PostMapping("/iniciar/{scenarioId}")`), así que Spring no deriva ninguna ruta del nombre del método, y no se tocó ningún campo ni propiedad JSON.
    *   **Renombrado del frontend y de los métodos que faltaban (esta ronda).** El renombrado anterior solo miró `backend/src`, con un patrón que exigía `public`/`private`/`protected` al inicio de la línea: dejó fuera los métodos de paquete, las llamadas con `objeto.metodo(` y **todo el frontend**. Esta ronda cubre `.java`, `.ts` y `.html`: 6 métodos de repositorio y servicio en el backend y 25 en el frontend, con sus llamadas en plantilla.
    *   **Cifra real, medida y declarada.** El mensaje del commit `cd30632` afirmaba *"0 of 172 methods in main and 0 of 25 in test carry a Spanish name"*. **Era falso**: un recuento independiente encuentra 96 nombres en español en `src/test` y, en `src/main` y en el frontend, únicamente falsos positivos del detector (`findById`, `catchError`, `getErrorMessage`, por las palabras «id» y «error») y **nombres impuestos por el esquema o el contrato**: `findByCodigo` y `findFirstByActivoTrueOrderByIdVehiculoAsc` son *derived queries* sobre campos de entidad en español, y `getFechaProgramada`/`getComentario` son los métodos de acceso cuyo nombre Jackson usa como propiedad JSON. Renombrarlos rompería el contrato, que es exactamente el defecto que se corrigió en la ronda anterior. **El renombrado de los nombres de prueba se completa en esta ronda:** 116 identificadores (119 ocurrencias) en `src/test`, verificados con `mvn test`, que ejecuta **113 pruebas con 0 fallos y 0 errores**. **Cifra final:** en `src/main` y en el frontend no queda ningún nombre en español que no sea un falso positivo del detector (`findById`, `catchError`, `getErrorMessage`, por las palabras «id» y «error») o un nombre impuesto por el esquema o el contrato JSON; en `src/test` quedan **3**, los de `RepositoryIntegrationTest`, que reflejan a propósito los métodos de repositorio que prueban. El defecto se registra como `R-13`.
    *   **Commits:** `21edc80`, `a191fb3`, `cd30632`, y el de esta ronda.
*   **P7 — Lighthouse sin ninguna corrida (0.7 pts)**
    *   Se eliminaron los tres JSON fabricados y se ejecutaron auditorías reales (perfil desktop y móvil) contra la URL pública; los reportes resultantes pesan cientos de kB.
    *   **Commit:** `fc4c701`.
    *   **Archivos:** `docs/mediciones/perf/lighthouse/*`.
*   **P9 — SRS firmado (0.6 pts)**
    *   El control del documento quedó con la versión 1.0.0 y la 1.1.0 marcadas como *revisadas con observaciones*, en los mismos términos que declararon los informes de revisión del docente.
    *   **Corrección de la declaración de aprobación (observación X6):** se retiran de `SRS-v1.1.0.tex`, `CHANGELOG-REQ.md`, `CONTRIBUCIONES.md` y `VERIFICACION.md` las frases que afirmaban que la v1.1.0 estaba "Aprobada — validada mediante firma del docente-director" y que la firma del informe "constituye validación suficiente del SRS". El docente-director escribió lo contrario en dos informes distintos: su firma *"no asigna calificación y no constituye aprobación del SRS"*. La firma acredita revisión, no aprobación; ninguna versión figura por tanto como aprobada.
    *   **Archivos:** `docs/requisitos/SRS-v1.1.0.tex`, `docs/requisitos/SRS-v1.1.0.pdf`, `docs/requisitos/CHANGELOG-REQ.md`, `CONTRIBUCIONES.md`, `VERIFICACION.md`.
*   **P10 — Sin corrección por comparaciones múltiples (0.5 pts)**
    *   Se corrigió `estadistica.py` para **calcular** los p-valores reales desde el estadístico t (en lugar de fijarlos a mano) antes de aplicar Holm-Bonferroni.
    *   **Commit:** `7b3cfbe`.
    *   **Archivos:** `docs/mediciones/perf/estadistica.py`.
*   **P11 — Cookie de sesión sin el atributo Secure (0.4 pts)**
    *   Se documentó el cableado real de `security.cookie.secure` (código + `application-prod.yml`) y se sustituyó la salida de expediente que no reproducía su propia orden.
    *   **Commit:** `e7a1e34`.
    *   **Archivos:** `VERIFICACION.md`, `AuthController.java`, `application-prod.yml`.
*   **P12 — Referencias sin verificar una por una (0.3 pts)**
    *   Se auditaron las **46 referencias** de `refs.bib` una por una contra Crossref, DataCite y OpenLibrary. La primera pasada (`94fdd61`) dio 18 correctos, 10 corregidos y 18 retirados; una **segunda auditoría completa** (`833925c`) dejó el resultado en **30 con DOI resuelto y verificado**, **16 sin DOI registrado pero con ISBN o URL oficial verificados** y **0 sin ningún identificador** (antes eran 16 las que no tenían DOI, URL ni ISBN). El registro es auditable y reproducible con `python scripts/check_dois.py`.
    *   **Commits:** `94fdd61`, `833925c`.
    *   **Archivos:** `docs/refs.bib`, `docs/doi_check.log`, `scripts/check_dois.py`.
*   **EV-1 — Expediente de verificación**
    *   Se completó `VERIFICACION.md` con los puntos P2, P4, P8 y P9 y se corrigieron las salidas obsoletas.
    *   **Commit:** `6f450de`.
    *   **Corrección de la orden de P4 (`988bb6a`).** La orden no podía producir su propia salida: en `curl`, `-o` se aplica a la URL siguiente, de modo que la segunda imprimía su cuerpo JSON. Comprobado contra un servidor propio: devolvía 45 líneas empezando por `200` y siguiendo con el HTML. Encadenar dos invocaciones con `&&` tampoco sirve en Windows (`curl.exe` no acepta `/dev/null` de forma portable y termina con código 23). Ahora usa `scripts/verify_dois.py`, que **lee los DOI del propio `CITATION.cff`**, reintenta ante errores de red transitorios y falla si alguno no resuelve.
    *   **Corrección del recuento de P12.** La entrada esperaba el recuento de la auditoría anterior; ahora cuenta las entradas del registro regenerado.
    *   **Commit:** `988bb6a`, `84a8ae0`.
    *   **Archivos:** `VERIFICACION.md`, `scripts/verify_dois.py`.
*   **EV-2 — Verificador del expediente**
    *   Se eliminó la lista blanca de `verify_expediente.py`: cualquier orden con código distinto de cero hace fallar el verificador, y se compara la salida real contra la esperada.
    *   **Commit:** `7a691d0`.
    *   **Integridad de las mediciones (`97e0819`).** La evaluación señaló que ocho de doce mutaciones sobrevivían, por una causa estructural: la comprobación de evidencia leía el **índice** de git y no el **árbol de trabajo**, y ninguna orden inspeccionaba el **contenido** de las mediciones. Se añade `scripts/verify_integridad_mediciones.py` con **36 comprobaciones** de contenido y árbol: las seis corridas de Lighthouse apuntan al despliegue público y ninguna a `localhost`, con accesibilidad 1,00 y cero auditorías fallidas; los manifiestos coinciden con sus informes; las cifras de cobertura del informe reproducen el resumen de JaCoCo (y el CSV contra el XML); no hay `@CrossOrigin("*")`; el PDF del informe no es un archivo de prueba; y los contratos Backup (11 campos) y AuditLog (8 campos) coinciden campo por campo entre backend y frontend.
    *   **Pruebas de mutación (`97e0819`).** `scripts/test_mutaciones_integridad.py` aplica nueve manipulaciones —incluidas las que sobrevivían— y exige que el verificador falle en todas: **9 de 9 detectadas, 0 supervivientes**. Restaura el árbol y confirma que vuelve a estar en verde.
    *   **Commits:** `7a691d0`, `97e0819`.
    *   **Archivos:** `scripts/verify_expediente.py`, `scripts/verify_integridad_mediciones.py`, `scripts/test_mutaciones_integridad.py`, `Makefile`.
*   **EV-3 — Etiquetado y despliegue**
    *   La etiqueta se publica de forma anotada sobre el commit final. `v1.2.0` permanece anclada al documento que nombra. **Corrección: `v1.3.0` también se ha movido**, lo contrario de lo que afirmaba esta entrada en la ronda anterior. Se creó en `5561569` para disparar el depósito de Zenodo y hoy apunta al commit final, de modo que **el depósito no archiva el código evaluado**. La discrepancia se resuelve publicando un depósito nuevo desde el commit final, y la afirmación falsa queda registrada. `v1.1.0` se mueve en cada ronda, porque el docente-director lo autorizó **por escrito en su evaluación del 2026-09-20, apartado 2**; no el 19, como decía una nota anterior (véase `R-11`).
    *   **Cabeceras de seguridad (P1/P11).** La API envía `Content-Security-Policy`, `X-Frame-Options: DENY`, `X-Content-Type-Options`, HSTS y `X-XSS-Protection` en todas las rutas probadas. Al sitio estático se le añadieron CSP, `X-Frame-Options`, `Referrer-Policy` y `Permissions-Policy` desde el panel del proveedor sobre la ruta `/*`, y se comprobó que cubre todas las rutas y los recursos reales del build. **Control de no regresión:** con la política activa se cargó el sitio con un navegador real y se completó un inicio de sesión contra la API (`200`, redirección a `/dashboard`).
    *   **Autocorrección declarada.** Una versión anterior de la tabla de cabeceras concluía que la API no enviaba ninguna; era **falso y el error estaba en la comprobación**, que convertía las cabeceras a un diccionario y las buscaba con mayúsculas iniciales, cuando las cabeceras HTTP son insensibles a mayúsculas. Se corrigió en contra del propio interés y el error se conserva anotado en `docs/mediciones/sec/A05-security-headers.md`.
    *   **Archivos:** `README.md`, `Makefile`, `docs/VERSIONING.md`, `docs/mediciones/sec/A05-security-headers.md`.

### Jefferson M. Umaginga Arévalo (jumagingaa@uteq.edu.ec)
*   De acuerdo con el historial del repositorio (git log), no se registran commits posteriores a la guía inicial por parte de este integrante que puedan ser validados para el cierre de los puntos P1-P12.

---

## Firmas

Declaramos que la información presentada en este documento y respaldada por el historial de Git es cierta y verificable.

**Identidad de las cuentas empleadas.** Los 140 commits de Jefferson M. Umaginga Arévalo están firmados con su correo institucional `jumagingaa@uteq.edu.ec`. Los de Justyn K. Cruz Pérez están firmados con la dirección personal `justyncruzperez@gmail.com`, en sus dos cuentas de GitHub (`JustynCruz04` y `keithdrox`, cuyas cifras exactas se leen con `git shortlog -sne`); `jcruzp@uteq.edu.ec` es su correo institucional de contacto y no aparece en ninguno de ellos.

**Firma 1:** Justyn Keith Cruz Pérez
**Nombre:** Justyn Keith Cruz Pérez
**Correo institucional:** jcruzp@uteq.edu.ec
**Fecha:** 2026-09-20

**Firma 2:** *No firma.*
**Nombre:** Jefferson Manuel Umaginga Arévalo
**Motivo:** no registra actividad en el repositorio desde el **2026-09-04** (commit `34f9bf8`), anterior al trabajo que declara este documento. Sus aportes previos constan en la sección correspondiente.

> La línea de firma 1 reproduce el nombre y la fecha del declarante y **no es una firma manuscrita**. Si el evaluador requiere firma manuscrita, el equipo imprimirá el documento, lo firmará y aportará el escaneo.
>
> La firma 2 se deja explícitamente sin firmar y no en blanco: firmar por una persona que no participó en el trabajo declarado no es admisible, y dejar la línea vacía sin explicación fue el defecto que señaló la evaluación.
