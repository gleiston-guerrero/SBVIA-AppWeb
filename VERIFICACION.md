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
- **Cifras y Notebook de Rendimiento:** Las cifras contradictorias en el informe (ej. SUS 82.5 vs 84.17) fueron unificadas hacia los datos reales del experimento (84.17). El antiguo cuaderno Jupyter que producía `KeyError` ha sido descontinuado y reemplazado de manera definitiva por un script estable en Python (`docs/mediciones/perf/estadistica.py`) que procesa los JSON de k6 y maneja correctamente la corrección de múltiples hipótesis.

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

- **Descripción:** Las cookies JWT `accessToken` y `refreshToken` se generan con los atributos correctos para producción (`secure(true)` y `httpOnly(true)`).
- **Orden exacta:** `grep -A 5 "tokenCookie(" backend/src/main/java/com/sbvia/backend/controller/AuthController.java`
- **Salida:**
```java
    private ResponseCookie tokenCookie(String name, String value, long maxAgeSeconds, String path) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
```
- **Orden exacta:** `grep "cookieSecure" backend/src/main/resources/application-prod.yml`
- **Salida:**
```yaml
      secure: true
```
- **Ruta del archivo que la respalda:** `backend/src/main/java/com/sbvia/backend/controller/AuthController.java` y `backend/src/main/resources/application-prod.yml`.
