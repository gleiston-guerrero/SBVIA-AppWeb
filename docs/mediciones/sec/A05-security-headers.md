# A05 — Security Misconfiguration (cabeceras HTTP y CORS)

## Control implementado

Cabeceras de seguridad configuradas explícitamente en
`backend/src/main/java/com/sbvia/backend/security/SecurityConfig.java`,
incluyendo HSTS condicionado a HTTPS.

## Cabeceras verificadas

| Cabecera | Valor |
|---|---|
| `X-Content-Type-Options` | `nosniff` |
| `X-Frame-Options` | `DENY` |
| `Content-Security-Policy` | `default-src 'self'; frame-ancestors 'none';` |
| `X-XSS-Protection` | `1; mode=block` |
| `Strict-Transport-Security` | solo sobre HTTPS (configurado en `SecurityConfig`) |

## Comportamiento HTTP frente a HTTPS

Spring Security omite la cabecera HSTS sobre HTTP por diseño (el navegador
la ignoraría si fuese enviada sin TLS). Verificado contra la API real en
`http://localhost:8080`:

```text
Strict-Transport-Security: ""
X-Content-Type-Options: "nosniff"
X-Frame-Options: "DENY"
Content-Security-Policy: "default-src 'self'; frame-ancestors 'none';"
X-XSS-Protection: "1; mode=block"
```

## Afirmaciones verificadas

| Afirmación | Evidencia |
|---|---|
| `X-Content-Type-Options: nosniff` | `raw/owasp-evidence.md` (HTTP real) |
| `X-Frame-Options: DENY` | `raw/owasp-evidence.md` |
| CSP `frame-ancestors 'none'` | `raw/owasp-evidence.md` |
| HSTS se emite solo sobre HTTPS | comportamiento Spring Security + config |

## Reproducción

```bash
docker compose up -d --wait
curl -sI http://localhost:8080/api/escenarios
```

## Limitaciones

- `Permissions-Policy` y cabeceras `Cross-Origin-*` (COOP/COEP/CORP) no
  están configuradas explícitamente; no se documentan para no inventar
  evidencia.

---

## Comprobación de cabeceras en el sistema DESPLEGADO (P1/P11)

Comprobación ejecutada el 2026-09-21 contra los dos servicios en producción, con
un cliente TLS independiente del navegador. Resultado: **ninguno de los dos envía
cabeceras de seguridad**.

| Cabecera | Frontend (`sbvia-frontend.onrender.com`) | API (`sbvia-appweb.onrender.com`) |
| :--- | :--- | :--- |
| `Content-Security-Policy` | ausente | ausente |
| `X-Frame-Options` | ausente | ausente |
| `X-Content-Type-Options` | ausente | ausente |
| `Strict-Transport-Security` | ausente | ausente |
| `X-XSS-Protection` | ausente | ausente |

En la API se probaron tres rutas: `/actuator/health` (200), `/api/simulations/mis-practicas`
sin token (401) y `/api/auth/login` con credenciales inválidas (401). Las dos últimas
atraviesan la cadena de filtros de Spring Security y **tampoco** traen las cabeceras.

### Diagnóstico

El código **sí** configura las cabeceras: `SecurityConfig.securityFilterChain` incluye
`contentTypeOptions`, `frameOptions(deny)`, `httpStrictTransportSecurity`,
`xssProtection` y `contentSecurityPolicy("default-src 'self'; frame-ancestors 'none';")`.
Ese archivo no se ha modificado desde el commit `b03f8e3` (2026-09-18), anterior a la
revisión. Que el servicio desplegado no las aplique indica que **la instancia en
ejecución no corresponde a ese código**: procede **redesplegar el servicio de la API**
en Render y volver a comprobar con la misma orden.

### Cabeceras del sitio estático

El frontend es un sitio estático, y Render no las toma de ningún archivo del
repositorio: se definen en el panel del servicio
(https://render.com/docs/static-site-headers.md). Valores recomendados, para pegar
como reglas de cabecera sobre la ruta `/*`:

| Nombre | Valor |
| :--- | :--- |
| `Content-Security-Policy` | `default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:; font-src 'self' data:; connect-src 'self' https://sbvia-appweb.onrender.com; frame-ancestors 'none'; base-uri 'self'; form-action 'self'` |
| `X-Frame-Options` | `DENY` |
| `X-Content-Type-Options` | `nosniff` |
| `Referrer-Policy` | `strict-origin-when-cross-origin` |
| `Permissions-Policy` | `geolocation=(), microphone=(), camera=()` |
| `Strict-Transport-Security` | `max-age=31536000; includeSubDomains` |

La `connect-src` debe incluir el origen de la API porque el frontend la invoca
desde el navegador.
