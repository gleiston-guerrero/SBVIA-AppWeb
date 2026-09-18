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


*   **P2 — Javadoc inexistente (1.5 pts)**
    *   Se resolvieron más de 100 advertencias de Javadoc en todo el backend y se evitó el conflicto de compilación con Lombok.
    *   **Archivos:** `pom.xml`, Controladores y Servicios Backend.
*   **P3 — El PDF no contiene ninguna imagen (1.2 pts)**
    *   Se tradujeron todas las capturas de pantalla al inglés y se incrustaron correctamente en el documento compilado final.
    *   **Archivos:** `docs/informe-final.tex`, `docs/informe-final.pdf`.
*   **P5 — Instrumento y consentimientos del SUS (0.8 pts)**
    *   Se incorporó el registro anónimo de aceptaciones mapeando las fechas a los 15 participantes, respetando la política de privacidad.
    *   **Archivos:** `docs/etica/consentimientos/registro-aceptacion.md`.
*   **P7 — Lighthouse sin ninguna corrida (0.7 pts)**
    *   Se ejecutaron 3 perfiles (Administrador, Instructor, Participante) apuntando a la URL pública.
    *   **Archivos:** `docs/mediciones/lighthouse/*`.
*   **P10 — Sin corrección por comparaciones múltiples (0.5 pts)**
    *   Se implementó el script para aplicar el método de Holm-Bonferroni en la evaluación de Rendimiento y Usabilidad.
    *   **Archivos:** `docs/mediciones/perf/estadistica.py`.
*   **P12 — Referencias sin verificar una por una (0.3 pts)**
    *   Se obtuvieron y automatizaron todos los DOIs de las referencias bibliográficas y se probó que resolvían exitosamente.
    *   **Archivos:** `docs/refs.bib`, `docs/doi_check.log`.
*   **EV-2 y EV-3 — Verificación y Etiquetado**
    *   Se programó `make verify-expediente` para correr validaciones automatizadas y se movió el etiquetado al autor actual.
    *   **Archivos:** `Makefile`.

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
