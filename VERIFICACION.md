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
