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

- **Descripción:** Se configuraron y ejecutaron auditorías Lighthouse reales utilizando `@lhci/cli` apuntando a la URL pública. Los informes JSON y HTML resultantes se versionaron correctamente.
- **Orden exacta:** `ls docs/mediciones/lighthouse/participante | grep "manifest.json"`
- **Salida:** `manifest.json`
- **Ruta del archivo que la respalda:** `docs/mediciones/lighthouse/participante/manifest.json`.

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

- **Descripción:** Se auditaron los 46 DOIs de `docs/refs.bib` contra Crossref uno por uno (título citado vs título resuelto): 18 correctos, 10 corregidos y 18 retirados. El detalle completo está en `docs/doi_check.log`.
- **Orden exacta:** `grep -c "Veredicto:" docs/doi_check.log`
- **Salida:** `46`
- **Ruta del archivo que la respalda:** `docs/refs.bib` y `docs/doi_check.log`.

## P4 — Zenodo (Depósito y DOI)

- **Descripción:** El software (licencia MIT) y el dataset de validación (CC BY 4.0) están depositados en Zenodo y sus DOIs resuelven correctamente.
- **Orden exacta:** `curl -s -o /dev/null -w "%{http_code}\n" https://doi.org/api/handles/10.5281/zenodo.22740480 https://doi.org/api/handles/10.5281/zenodo.22785358`
- **Salida:**
```
200
200
```
- **Ruta del archivo que la respalda:** `CITATION.cff` y `docs/informe-final.tex` (DOI del software y del dataset).

## P8 — GQM y preguntas de investigación

- **Descripción:** El enfoque Goal-Question-Metric se aplicó con 6 objetivos (G1–G6), cada uno con pregunta y métrica, y las preguntas RQ1–RQ3 están planteadas y respondidas en el informe.
- **Orden exacta:** `grep -c "textbf{G[0-9]" docs/informe-final.tex`
- **Salida:** `6`
- **Ruta del archivo que la respalda:** `docs/informe-final.tex` (Tabla GQM y respuestas RQ1–RQ3).

## P2 — Javadoc

- **Estado:** Pendiente. Persisten bloques `Método público.` sin traducir y la cobertura de Javadoc no alcanza el umbral del 90%. Se corrige en la fase de código (maven-javadoc-plugin).

## P9 — SRS firmado

- **Estado:** Resuelto por **confirmación directa del docente-director**, no por un cambio técnico. El docente-director confirmó el 18-09-2026, por WhatsApp, que su firma en el informe de revisión (`docs/requisitos/Revision_SRS_SBVIA_v1.1.0.pdf`) constituye validación suficiente del SRS, sin requerir firma adicional dentro del propio SRS. En consecuencia, el control del documento marca la v1.1.0 como **Aprobada — validada mediante firma del docente-director**; la v1.0.0 permanece como "Sometido a revisión con observaciones, no aprobado".
