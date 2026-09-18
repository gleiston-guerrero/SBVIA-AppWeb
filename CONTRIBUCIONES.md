# Declaración de Aportes (EV-4)

Este documento detalla quién resolvió cada uno de los puntos pendientes de la evaluación, con evidencia contrastable en el historial del repositorio. Todo el trabajo reciente ha sido realizado por el primer integrante, tal como lo demuestra el registro de commits.

## Resoluciones por Integrante

### Justyn K. Cruz Pérez (jcruzp@uteq.edu.ec)
**Usuario Git:** `JustynCruz04` / `keithdrox`

Puntos cerrados de manera comprobable:

*   **P1 — Sin despliegue público (1.3 pts)**
    *   **Archivos:** `README.md`, `frontend/proxy.conf.json`, configuración de Render.
    *   **Commits:** `bd17051`, `a1e5714` y recientes arreglos de la etiqueta.
*   **Piso 3 (ZAP Falso) -> Escaneo ZAP Real**
    *   Se eliminó el reporte ZAP falsificado a mano (`docs/mediciones/sec/zap/zap-report.html`) y se generó un nuevo reporte real apuntando al backend productivo `https://sbvia-appweb.onrender.com`.
    *   **Archivos:** `docs/mediciones/sec/zap/zap-report.html` (Nuevo archivo real de 40kB).
*   **P6 — Nombres en español en el código (Contrato REST roto) (1.0 pts)**
    *   Se restauraron y corrigieron los contratos REST (DTOs y Controladores) que habían sido traducidos al inglés mecánicamente, devolviendo consistencia a las peticiones del frontend (ej: error 400 en Reglas de Tránsito y parámetros de Auditoría).
    *   **Archivos:** `AuditController.java`, `TrafficRuleDTO.java`, `TrafficRule.java`, `TrafficRuleService.java`.
*   **P11 — Cookie de sesión sin el atributo Secure (0.4 pts)**
    *   Se aplicó la configuración correcta para la cookie mediante `cookieSecure` para que contenga `.secure(true)` y `.httpOnly(true)` en producción.
    *   **Archivos:** `AuthController.java`, `application.yml`, `application-prod.yml`.
*   **Resto de la base de código (Historial previo)**
    *   **Frontend, k6, Lighthouse, Backend:** El 100% de los 68+ commits posteriores a la primera guía llevan el alias de este autor.

### Jefferson M. Umaginga Arévalo (jumagingaa@uteq.edu.ec)
*   De acuerdo con el historial del repositorio (git log), no se registran commits posteriores a la guía inicial por parte de este integrante que puedan ser validados para el cierre de los puntos P1-P12.

---

## Firmas

Declaramos bajo nuestro correo institucional que la información presentada en este documento y respaldada por el historial de Git es cierta y verificable.

**Firma 1:** ___________________________  
**Nombre:** Justyn Keith Cruz Pérez  
**Correo:** jcruzp@uteq.edu.ec  

**Firma 2:** ___________________________  
**Nombre:** Jefferson Manuel Umaginga Arévalo  
**Correo:** jumagingaa@uteq.edu.ec  
