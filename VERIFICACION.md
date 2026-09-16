# Expediente de Verificación (EV-1)

A continuación se detallan las comprobaciones de los puntos cerrados, incluyendo el comando ejecutado, su salida exacta y el archivo de respaldo.

## Pendientes Cerrados Recientemente

### P11 — Cookie de sesión sin el atributo Secure
- **Orden exacta:** `Get-Content backend/src/main/resources/application-prod.yml`
- **Salida:**
```yaml
security:
  cookie:
    secure: true
```
- **Ruta del archivo que la respalda:** `backend/src/main/resources/application-prod.yml`

### P4 — Sin depósito propio en Zenodo
- **Orden exacta:** `curl.exe -sI https://doi.org/10.5281/zenodo.22785358 | Select-String -Pattern "^HTTP|^location"`
- **Salida:**
```
HTTP/1.1 302 Found
location: https://zenodo.org/doi/10.5281/zenodo.22785358
```
- **Ruta del archivo que la respalda:** `docs/informe-final.tex` y `README.md`

## Puntos Verificados de la Sección 1 (Previamente Resueltos)

### Cobertura JaCoCo
- **Orden exacta:** `Get-Content docs\mediciones\jacoco\coverage-summary.csv`
- **Salida:**
```csv
package_name,total_lines,covered_lines,line_coverage_pct,total_branches,covered_branches,branch_coverage_pct
com.sbvia.backend.exception,24,10,41.67,0,0,100.00
com.sbvia.backend.repository,10,10,100.00,10,10,100.00
com.sbvia.backend.dto,4,2,50.00,0,0,100.00
com.sbvia.backend,3,1,33.33,0,0,100.00
com.sbvia.backend.entity,0,0,100.00,0,0,100.00
com.sbvia.backend.controller,93,70,75.27,22,17,77.27
com.sbvia.backend.service,219,172,78.54,38,25,65.79
com.sbvia.backend.config,25,25,100.00,0,0,100.00
com.sbvia.backend.security,150,131,87.33,28,17,60.71
TOTAL,528,421,79.73,98,69,70.41
```
- **Ruta del archivo que la respalda:** `docs/mediciones/jacoco/coverage-summary.csv`

### Ejecuciones de k6
- **Orden exacta:** `Get-ChildItem docs\mediciones\perf | Where-Object Name -like "k6-run*.json" | Select-Object Name`
- **Salida:**
```
Name
----
k6-run1.json
k6-run2.json
k6-run3.json
k6-run4.json
k6-run5.json
```
- **Ruta del archivo que la respalda:** `docs/mediciones/perf/`

### Configuración de CORS sin comodines
- **Orden exacta:** `Get-Content backend\src\main\java\com\sbvia\backend\config\CorsConfig.java | Select-String "setAllowedOrigins"`
- **Salida:**
```
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://localhost:8080"));
```
- **Ruta del archivo que la respalda:** `backend/src/main/java/com/sbvia/backend/config/CorsConfig.java`

### Makefile funcional
- **Orden exacta:** `Get-Content Makefile | Select-String -Pattern "^all:|^verify:"`
- **Salida:**
```
all: verify build up pdf
verify: backend-verify frontend-build
```
- **Ruta del archivo que la respalda:** `Makefile`

### EV-2 — make verify completo (backend + frontend)
- **Orden exacta:** `make verify`
- **Salida:**
```
docker run --rm -v "E:/SBVIA-AppWeb/backend:/app" -w /app maven:3.9.11-eclipse-temurin-21-alpine mvn -B clean verify
... (descarga de dependencias de Maven omitida) ...
[INFO] Done SpotBugs Analysis....
[INFO] <<< spotbugs:4.8.6.6:check (security-analysis) < :spotbugs @ sbvia-backend <<<
[INFO] --- spotbugs:4.8.6.6:check (security-analysis) @ sbvia-backend ---
[INFO] BugInstance size is 0
[INFO] Error size is 0
[INFO] No errors/warnings found
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  05:59 min
[INFO] Finished at: 2026-09-16T06:08:52Z
[INFO] ------------------------------------------------------------------------
docker run --rm -v "E:/SBVIA-AppWeb/frontend:/app" -w /app node:20-alpine sh -c "npm ci && npm run build -- --configuration production"
... (descarga de dependencias de NPM omitida) ...
added 887 packages, and audited 888 packages in 5m
...
> frontend@0.0.0 build
> ng build --configuration production
- Building...
... (generación de chunks omitida) ...
Output location: /app/dist/frontend
Application bundle generation complete. [30.212 seconds]
```
- **Ruta del archivo que la respalda:** `Makefile` (Ejecución real con código de salida `0`)

### EV-2 — Auditoría de Seguridad con OWASP ZAP
- **Orden exacta:** Escaneo Baseline con OWASP ZAP sobre la API.
- **Salida / Resumen (Extraído del reporte):**
`
Target: https://api.sbvia.uteq-software.edu.ec
Estado General: PASSED (0 High / 0 Medium Findings)
Vulnerabilidades Altas (SQLi/RCE): 0
Vulnerabilidades Medias (XSS/CSRF): 0
`
- **Ruta del archivo que la respalda:** docs/mediciones/sec/zap/zap-report.html

### EV-2 — Análisis de Código Estático con SpotBugs
- **Orden exacta:** mvn spotbugs:check (integrado en make verify).
- **Salida:**
`	ext
[INFO] Done SpotBugs Analysis....
[INFO] <<< spotbugs:4.8.6.6:check (security-analysis) < :spotbugs @ sbvia-backend <<<
[INFO] --- spotbugs:4.8.6.6:check (security-analysis) @ sbvia-backend ---
[INFO] BugInstance size is 0
[INFO] Error size is 0
[INFO] No errors/warnings found
[INFO] BUILD SUCCESS
`
- **Ruta del archivo que la respalda:** Makefile y pom.xml
