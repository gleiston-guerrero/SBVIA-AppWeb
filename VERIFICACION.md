# Expediente de Verificación (EV-1)

A continuación se detallan las comprobaciones de los puntos pendientes que se han resuelto de manera definitiva. Para cada punto se provee el comando exacto de comprobación, su salida esperada y el archivo/ruta que lo sustenta.

## Piso 3 — Escaneo ZAP Real

- **Descripción:** Se ha eliminado el escaneo falso (`docs/mediciones/sec/zap/zap-report.html` de 1.8kB) y se ha generado un escaneo automatizado contra la instancia de producción en Render.
- **Orden exacta:** `docker run --rm -v "${PWD}/docs/mediciones/sec/zap:/zap/wrk/:rw" -t ghcr.io/zaproxy/zaproxy:stable zap-baseline.py -t https://sbvia-appweb.onrender.com -r zap-report.html`
- **Salida / Resumen:**
```
PASS: Vulnerable JS Library (Powered by Retire.js) [10003]
PASS: In Page Banner Information Leak [10009]
...
FAIL-NEW: 0	FAIL-INPROG: 0	WARN-NEW: 4	WARN-INPROG: 0	INFO: 0	IGNORE: 0	PASS: 63
Automation plan warnings:
	Job spider error accessing URL https://sbvia-appweb.onrender.com status code returned : 401 expected 200
```
- **Ruta del archivo que la respalda:** `docs/mediciones/sec/zap/zap-report.html` (Verificado: tamaño 40kB).
- **Cifras y Notebook de Rendimiento:** Las cifras de rendimiento provienen del script estable en Python (`docs/mediciones/perf/estadistica.py`), que procesa los JSON de k6 y aplica la corrección por comparaciones múltiples; el antiguo cuaderno Jupyter que producía `KeyError` fue descontinuado. Las cifras de usabilidad (SUS 82.5 y 84.17) **no se unifican hacia ninguna de las dos**: el estudio SUS se retira por completo (véase P5) y no se reporta media, desviación típica ni intervalo de confianza.

## P1 — Sin despliegue público

- **Descripción:** Se logró completar el despliegue del sistema tanto del Frontend (estático o web service) como del Backend utilizando Render y sus variables de entorno configuradas (`application-prod.yml`).
- **Orden exacta:** `curl -sI https://sbvia-appweb.onrender.com/actuator/health | grep HTTP`
- **Salida:** `HTTP/1.1 200 OK`
- **Ruta del archivo que la respalda:** `README.md` (URLs actualizadas) y `.github/workflows/main.yml`.

## P6 — Nombres en español en el código (Contratos REST)

- **Descripción:** Se resolvieron los desajustes causados por scripts de traducción mecánica entre el JSON emitido por el frontend y lo esperado por el backend. Por ejemplo, en Auditorías, `startDate` y `endDate`, y en Tránsito, `nombre` y `descripcion`.
- **Orden exacta:** `grep -r "@RequestParam" backend/src/main/java/com/sbvia/backend/controller/AuditController.java`
- **Salida parcial:**
```java
@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
```
- **Ruta del archivo que la respalda:** `backend/src/main/java/com/sbvia/backend/controller/AuditController.java` y `TrafficRuleDTO.java`.

## P11 — Cookie de sesión sin el atributo Secure

- **Descripción:** Las cookies JWT `accessToken` y `refreshToken` se generan con los atributos correctos para producción (`secure(true)`, `httpOnly(true)` y `SameSite=Strict`).
- **Orden exacta:** `grep -A 5 "private ResponseCookie tokenCookie(" backend/src/main/java/com/sbvia/backend/controller/AuthController.java`
- **Salida:**
```java
    private ResponseCookie tokenCookie(String name, String value, long maxAgeSeconds, String path) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path(path)
```
- **Orden exacta:** `grep -n "secure" backend/src/main/resources/application-prod.yml`
- **Salida:**
```
3:    secure: true
```
- **Resolución de `security.cookie.secure`:**
  - `AuthController.java:43` inyecta `@Value("${security.cookie.secure:false}")` (por defecto `false`).
  - `application-prod.yml` fija `security.cookie.secure: true` (literal, sin variable de entorno).
  - `application.yml:38` (perfil base, no `prod`) resuelve `secure: ${COOKIE_SECURE:false}`, leyendo la variable de entorno `COOKIE_SECURE` (por defecto `false`).
  - **No verificable desde el repositorio:** si Render definiera la variable de entorno `SECURITY_COOKIE_SECURE` (enlace relajado de Spring Boot) o `COOKIE_SECURE`, esta tendría precedencia sobre `application-prod.yml`. El valor en producción ya fue comprobado en vivo en la evaluación (cookie `Secure; HttpOnly; SameSite=Strict`).
- **Ruta del archivo que la respalda:** `backend/src/main/java/com/sbvia/backend/controller/AuthController.java` y `backend/src/main/resources/application-prod.yml`.

## P3 — El PDF no contiene ninguna imagen

- **Descripción:** Se corrigieron los paths de las imágenes en LaTeX, asegurando la correcta incrustación en el PDF de 49 páginas. Las leyendas de las figuras permanecen en español (la traducción al inglés está pendiente).
- **Orden exacta:** `ls -l docs/informe-final.pdf | awk '{print $5}'`
- **Salida esperada:** Un tamaño en bytes superior a 10MB (aprox. 10847177), demostrando que las imágenes vectoriales y PNG están incrustadas.
- **Ruta del archivo que la respalda:** `docs/informe-final.pdf`.

## P5 — Instrumento y consentimientos del SUS

- **Estado:** **Sin avance (0%).** El punto no se resuelve. El estudio SUS se retira de todo el expediente en lugar de sostenerse con documentación añadida.
- **Motivo de la retirada:** La fecha declarada de aplicación del instrumento (2026-07-28/29, en `sus-analysis.md` y en `registro-aceptacion.md`, sin alteración desde el commit `bd17051` del 8-ago-2026) es **anterior a la incorporación al repositorio de la funcionalidad de simulación que el instrumento dice haber evaluado**. No se propone ninguna explicación alternativa para esa inconsistencia: los datos crudos se conservan como material de registro, sin garantía de validez metodológica.
- **Orden exacta:** `git log --no-walk --date=short --pretty=format:"%h %ad" 55201f5 5e852c6`
- **Salida:**
```
5e852c6 2026-09-04
55201f5 2026-09-03
```
- **Además:** la custodia de los 15 consentimientos informados permanece **NO verificada** (`ETHICS.md` §iii) y no se reporta puntuación SUS agregada en ningún documento del repositorio.
- **Ruta del archivo que la respalda:** `docs/etica/consentimientos/registro-aceptacion.md`, `docs/mediciones/sus/sus-analysis.md` y `docs/etica/ETHICS.md`.

## P7 — Lighthouse sin ninguna corrida

- **Descripción:** Se ejecutaron auditorías Lighthouse **reales** contra la URL pública `https://sbvia-frontend.onrender.com/login` (punto de entrada declarado en RNF-09) en los **dos perfiles de dispositivo**: `mobile` y `desktop`, con **3 corridas por perfil**. Se guardan **todas** las corridas (JSON + HTML) y un `manifest.json` por perfil, no solo la representativa.
  - Motivo: la revisión del 18-sep observó que las corridas anteriores eran **todas de escritorio y ninguna móvil**, y que el equipo confundía «perfil» con rol de usuario. Estas corridas cubren los dos perfiles de dispositivo que pide la guía.
- **Orden exacta:** `git ls-files docs/mediciones/lighthouse/mobile docs/mediciones/lighthouse/desktop`
- **Salida:**
```
docs/mediciones/lighthouse/desktop/manifest.json
docs/mediciones/lighthouse/desktop/sbvia_frontend_onrender_com_login-20260919_153756-desktop-run1.report.html
docs/mediciones/lighthouse/desktop/sbvia_frontend_onrender_com_login-20260919_153756-desktop-run1.report.json
docs/mediciones/lighthouse/desktop/sbvia_frontend_onrender_com_login-20260919_153803-desktop-run2.report.html
docs/mediciones/lighthouse/desktop/sbvia_frontend_onrender_com_login-20260919_153803-desktop-run2.report.json
docs/mediciones/lighthouse/desktop/sbvia_frontend_onrender_com_login-20260919_153812-desktop-run3.report.html
docs/mediciones/lighthouse/desktop/sbvia_frontend_onrender_com_login-20260919_153812-desktop-run3.report.json
docs/mediciones/lighthouse/mobile/manifest.json
docs/mediciones/lighthouse/mobile/sbvia_frontend_onrender_com_login-20260919_153708-mobile-run1.report.html
docs/mediciones/lighthouse/mobile/sbvia_frontend_onrender_com_login-20260919_153708-mobile-run1.report.json
docs/mediciones/lighthouse/mobile/sbvia_frontend_onrender_com_login-20260919_153718-mobile-run2.report.html
docs/mediciones/lighthouse/mobile/sbvia_frontend_onrender_com_login-20260919_153718-mobile-run2.report.json
docs/mediciones/lighthouse/mobile/sbvia_frontend_onrender_com_login-20260919_153732-mobile-run3.report.html
docs/mediciones/lighthouse/mobile/sbvia_frontend_onrender_com_login-20260919_153732-mobile-run3.report.json
```
- **Resultado real** (Lighthouse `12.1.0`, 155 auditorías por informe):
  - `mobile`: performance 0.98 / 0.86 / 0.99 (media 0.94), accessibility 1, best-practices 1, seo 1.
  - `desktop`: 1 en las cuatro categorías en las tres corridas.
- **Ruta del archivo que la respalda:** `docs/mediciones/lighthouse/mobile/manifest.json` y `docs/mediciones/lighthouse/desktop/manifest.json`.
- **Generación:** `node scripts/lighthouse-collect.js <url> <mobile|desktop> <corridas> <dirSalida>` (Lighthouse conectado a un Chrome propio con puerto de depuración; véase el encabezado del script).

## P10 — Sin corrección por comparaciones múltiples

- **Descripción:** Se implementó el procedimiento Holm-Bonferroni en el cálculo estadístico para corregir la tasa de error (FWER) cuando se evalúan múltiples hipótesis. La familia declarada originalmente incluía rendimiento (frío > caliente) y usabilidad (SUS > 68); al retirarse el estudio SUS, la única hipótesis efectivamente evaluada es la de rendimiento. El script conserva el procedimiento para el caso en que se añadan nuevas pruebas.
- **Orden exacta:** `python docs/mediciones/perf/estadistica.py`
- **Salida:**
```
=== Resultados de Evaluación Estadística ===
Estudio SUS: RETIRADO. Respuestas crudas conservadas: N=15, sin análisis estadístico.
Muestra Rendimiento (N=10): Media Diferencia = 9.00ms
  t = 4.5708, p-valor (una cola, df=9) = 6.727e-04

=== Aplicación de Holm-Bonferroni (alpha = 0.05) ===
Paso 1: Rendimiento (Frio > Caliente)
  P-valor crudo = 6.727e-04
  Alpha ajustado (0.05 / 1) = 0.0500
  Rechazar H0? SI
```
- **Ruta del archivo que la respalda:** `docs/mediciones/perf/estadistica.py`.

## P12 — Referencias verificadas una por una

- **Descripción:** Se auditaron las 46 referencias de `docs/refs.bib` una por una contra Crossref, DataCite y OpenLibrary: **30 con DOI resuelto y verificado**, **16 sin DOI registrado pero con ISBN o URL oficial verificados** y **0 sin ningún identificador**. El detalle completo está en `docs/doi_check.log`, que lista una entrada por referencia.
- **Orden exacta:** `grep -c "^\[" docs/doi_check.log`
- **Salida:** `46`
- **Ruta del archivo que la respalda:** `docs/refs.bib` y `docs/doi_check.log`.

## P4 — Zenodo (Depósito y DOI)

- **Descripción:** El software (licencia MIT) y el dataset de validación (CC BY 4.0) están depositados en Zenodo y sus DOIs resuelven correctamente.
- **Orden exacta:** `python -c "import urllib.request as u; print(u.urlopen('https://doi.org/api/handles/10.5281/zenodo.22740480').status); print(u.urlopen('https://doi.org/api/handles/10.5281/zenodo.22785358').status)"`
- **Salida:**
```
200
200
```
- **Nota de obtención y corrección (EV-2).** La orden anterior de este expediente era `curl -s -o /dev/null -w "%{http_code}\n" URL1 URL2` y **no podía producir la salida de arriba**: en `curl`, `-o` se aplica a la URL siguiente, de modo que la primera iba a `/dev/null` pero **la segunda imprimía su cuerpo JSON**. Comprobado en local contra un servidor propio: esa orden devuelve 45 líneas empezando por `200` y siguiendo con el HTML. El verificador del expediente fallaba por esta causa en dos corridas limpias, porque su salida real jamás contenía `200 200`.
  - Encadenar dos invocaciones con `&&` **tampoco sirve en Windows**: `curl.exe` no acepta `/dev/null` de forma portable y la segunda invocación termina con código **23** (`Failed writing body`). Por eso la orden usa ahora la biblioteca estándar de **Python**, que ya es dependencia del propio verificador: negocia TLS sin depender del cliente del sistema, imprime un `200` por DOI y **falla con excepción si alguno no resuelve**, lo que hace que el verificador devuelva un código distinto de cero.
  - Salida verificada con esta misma orden desde un entorno con Python: `200` y `200`. El integrante del equipo la había obtenido el **2026-09-19** invocando cada URL por separado con `curl.exe` en PowerShell; ambas devolvieron `200`.
  - Comprobación independiente con otro cliente TLS (Python `urllib`) contra el mismo endpoint: `HTTP 200` en ambos handles.
- **Ruta del archivo que la respalda:** `CITATION.cff` y `docs/informe-final.tex` (DOI del software y del dataset).

## P8 — GQM y preguntas de investigación

- **Descripción:** El enfoque Goal-Question-Metric se aplicó con 6 objetivos (G1–G6), cada uno con pregunta y métrica, y las preguntas RQ1–RQ3 están planteadas y respondidas en el informe.
- **Orden exacta:** `grep -c "textbf{G[0-9]" docs/informe-final.tex`
- **Salida:** `6`
- **Ruta del archivo que la respalda:** `docs/informe-final.tex` (Tabla GQM y respuestas RQ1–RQ3).

## P2 — Javadoc público sin documentar

- **Estado:** **Resuelto.** No queda ningún bloque placeholder `Método público.` en `backend/src/main/java`, y `maven-javadoc-plugin` 3.10.1 está configurado con `failOnError=true` y `doclint=all` en `backend/pom.xml`.
- **Orden exacta:** `git grep -n "todo p" -- backend/src/main/java || echo 0 coincidencias`
- **Salida:**
```
0 coincidencias
```
- **Sobre el patrón:** `todo p` es la subcadena ASCII de `Método público.` (el acento cae entre la `M` y `todo`), de modo que la orden no depende de la codificación con la que se copie.
- **Control — la misma orden sí detecta el texto cuando existe:** `git grep -n "todo p" 786e042 -- backend/src/main/java` devuelve **89** líneas en `786e042` (commit que introdujo el backend) y 0 en el estado actual. Es decir, el resultado vacío de arriba es un negativo real, no un fallo de búsqueda.
- **Ruta del archivo que la respalda:** `backend/pom.xml` y `backend/src/main/java/**/*.java`.

## P9 — SRS firmado

- **Estado:** **Rectificado (observación X6).** Se retira la declaración de aprobación. Los dos informes de revisión firmados por el docente-director (`docs/requisitos/Revision_SRS_SBVIA_v1.1.0.pdf` y `docs/requisitos/Revision_SRS_SBVIA_v1.2.0.pdf`) declaran por escrito, ambos, que *"no asignan calificación y no constituyen aprobación del SRS"*. La firma acredita que el documento fue revisado, no que fuera aprobado. En consecuencia, el control del documento marca la v1.0.0, la v1.1.0 y la v1.2.0 como **Revisada con observaciones**, y se elimina de este expediente toda afirmación de que la firma del informe "constituye validación suficiente del SRS".
- **Comprobación:** la frase "Aprobada — validada mediante firma del docente-director" ya no aparece en el repositorio; los cuatro archivos que la contenían (`SRS-v1.1.0.tex`, `CHANGELOG-REQ.md`, `CONTRIBUCIONES.md` y este expediente) declaran el mismo estado. La aprobación del SRS se emite por escrito y en el propio documento, no por inferencia de una confirmación verbal.

## Integridad de las mediciones — contenido y árbol de trabajo (EV-2)

- **Descripción:** El verificador anterior comprobaba la **existencia** de la evidencia con `git ls-files`, que lee el **índice** y no el **árbol de trabajo** —de modo que un archivo borrado del disco seguía contando— y ninguna orden inspeccionaba el **contenido** de las mediciones. Esta comprobación cubre ambas cosas: las seis corridas de Lighthouse apuntan al despliegue público (ninguna a `localhost`), su accesibilidad es 1,00 y no tienen auditorías binarias fallidas; los manifiestos coinciden con sus informes; las cifras de cobertura que cita el informe reproducen el resumen de JaCoCo (y el CSV coincide con el XML); no hay `@CrossOrigin("*")`; `application-prod.yml` fija `secure: true`; el PDF del informe no es un archivo de prueba; y los contratos Backup (11 campos) y AuditLog (8 campos) coinciden campo por campo entre backend y frontend.
- **Orden exacta:** `python scripts/verify_integridad_mediciones.py`
- **Salida:** `INTEGRIDAD OK: 36 comprobaciones superadas`
- **Ruta del archivo que la respalda:** `docs/mediciones/`, `docs/informe-final.pdf`, `backend/src/main/java/`, `frontend/src/app/features/`.

## Resistencia a mutaciones del verificador de integridad (EV-2)

- **Descripción:** Aplica **nueve** manipulaciones sobre el árbol de trabajo —apuntar Lighthouse a localhost, falsear la accesibilidad, marcar una auditoría como fallida, falsear un manifiesto, borrar una corrida, alterar la cifra de cobertura del informe, añadir `@CrossOrigin("*")`, romper un campo del contrato y sustituir el PDF por un archivo de un byte— y exige que el verificador de integridad **falle en todas**. Restaura el árbol al terminar y comprueba que vuelve a estar en verde. Es la comprobación que faltaba: antes sobrevivían ocho de doce mutaciones.
- **Orden exacta:** `python scripts/test_mutaciones_integridad.py`
- **Salida:** `Sobrevivientes : 0`
- **Ruta del archivo que la respalda:** `scripts/test_mutaciones_integridad.py`.
