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

Comprobación ejecutada el 2026-09-21 contra los dos servicios en producción, con un
cliente TLS independiente del navegador.

> **Corrección de una comprobación previa.** Una primera versión de esta comprobación
> concluyó que **ninguno** de los dos servicios enviaba cabeceras. Era falso, y el error
> estaba en la propia comprobación: convertía las cabeceras a un diccionario y las buscaba
> con mayúsculas iniciales, pero las cabeceras HTTP son insensibles a mayúsculas y ambos
> servicios responden en minúsculas. La comprobación correcta las consulta sin distinguir
> mayúsculas. El resultado válido es el de esta sección. Se conserva el error anotado
> porque explica por qué la conclusión anterior no debía darse por buena.

### API (`sbvia-appweb.onrender.com`)

| Cabecera | Valor |
| :--- | :--- |
| `Content-Security-Policy` | `default-src 'self'; frame-ancestors 'none';` |
| `X-Frame-Options` | `DENY` |
| `X-Content-Type-Options` | `nosniff` |
| `Strict-Transport-Security` | `max-age=31536000 ; includeSubDomains` |
| `X-XSS-Protection` | `1; mode=block` |
| `Referrer-Policy` | no se envía |
| `Permissions-Policy` | no se envía |

Comprobado en tres rutas: `/actuator/health` (200), `/api/simulations/mis-practicas` sin
token (401) y `/api/auth/login` con credenciales inválidas (401). **Las tres traen las
cinco cabeceras**, de modo que la configuración de `SecurityConfig.securityFilterChain`
—`contentSecurityPolicy`, `frameOptions(deny)`, `contentTypeOptions`,
`httpStrictTransportSecurity` y `xssProtection`— **se aplica correctamente en producción**.
Las cinco están alineadas con OWASP A05. `Referrer-Policy` y `Permissions-Policy` no las
exige el criterio; quedan anotadas como mejora opcional.

### Frontend (`sbvia-frontend.onrender.com`)

El proveedor añade por su cuenta `X-Content-Type-Options: nosniff` y
`Strict-Transport-Security`, pero **no** CSP ni `X-Frame-Options`. Se añadieron las cuatro
cabeceras que faltaban desde el panel del servicio, en la sección **Headers**, sobre la
ruta `/*` (Render no lee reglas de cabecera de ningún archivo del repositorio:
https://render.com/docs/static-site-headers.md).

Estado verificado tras aplicarlas:

| Cabecera | Valor |
| :--- | :--- |
| `Content-Security-Policy` | `default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:; font-src 'self' data:; connect-src 'self' https://sbvia-appweb.onrender.com; frame-ancestors 'none'; base-uri 'self'; form-action 'self'` |
| `X-Frame-Options` | `DENY` |
| `Referrer-Policy` | `strict-origin-when-cross-origin` |
| `Permissions-Policy` | `geolocation=(), microphone=(), camera=()` |
| `X-Content-Type-Options` | `nosniff` (del proveedor) |
| `Strict-Transport-Security` | `max-age=315360000; includeSubdomains; preload` (del proveedor) |

Se comprobó que la regla cubre todas las rutas y no solo la raíz: `/`, `/login`,
`/dashboard`, `/favicon.ico` y los recursos reales del build (`/styles-*.css`,
`/chunk-*.js`) devuelven las cabeceras.

**Control de no regresión:** con la CSP activa se cargó el sitio con un navegador real y se
completó un inicio de sesión contra la API, que respondió `200` y redirigió a `/dashboard`.
También cargaron la lista de escenarios y el informe de IA. La directiva `connect-src`
incluye el origen de la API, que es la única que podía romper la aplicación si faltara.

> **Nota de procedimiento.** Estas cabeceras se intentaron primero en la página
> `Redirects/Rewrites`, que **no** es la correcta: allí cada fila tiene *Source,
> Destination* y *Action*, y una cabecera introducida ahí se convierte en una redirección.
> Las reglas se descartaron sin guardar y no llegaron a aplicarse. La sección válida es
> **Headers**, con columnas *Request Path, Header Name, Header Value*. La distinción útil:
> si la interfaz pide una *Action*, es la página de redirecciones.
