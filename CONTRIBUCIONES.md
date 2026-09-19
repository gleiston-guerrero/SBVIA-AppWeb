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
*   **P3 — El PDF no contiene ninguna imagen (1.2 pts)**
    *   Se tradujeron al inglés los pies de figura (las 12 figuras del documento) y se referenciaron desde el texto las 4 capturas de interfaz que no estaban citadas. Las imágenes incrustadas son capturas reales de la aplicación; **no** se tradujeron las capturas de pantalla de la interfaz.
    *   **Commit:** `9afd075`.
    *   **Archivos:** `docs/informe-final.tex`, `docs/informe-final.pdf`.
*   **P4 — Sin depósito propio en Zenodo (1.0 pts)**
    *   Se añadió a `CITATION.cff` la referencia al dataset de validación con su DOI real (`10.5281/zenodo.22785358`), además del DOI del software (`10.5281/zenodo.22740480`).
    *   **Commit:** `12297ee`.
    *   **Archivos:** `CITATION.cff`.
*   **P5 — Instrumento y consentimientos del SUS (0.8 pts) — NO RESUELTO**
    *   **Estado:** sin avance (0%). El estudio SUS se retira de todo el expediente. Su fecha declarada de aplicación (2026-07-28/29) es anterior a la incorporación al repositorio de la funcionalidad de simulación que el instrumento dice haber evaluado: la "práctica vial interactiva" entra el 2026-09-03 (commit `55201f5`) y el "simulador de conducción 2D" el 2026-09-04 (commit `5e852c6`). No se propone ninguna explicación alternativa para esa inconsistencia, y la custodia de los consentimientos permanece no verificada.
    *   **Referencias del historial (no se presentan como logro):** las respuestas crudas y el análisis se incorporaron en `bd17051` (2026-08-08); el registro de aceptación se creó en `0107364` y se corrigió en `ec56c20` (ambos de 2026-09-18) para declarar únicamente hechos verificables. La retirada del estudio no aporta puntaje.
    *   **Archivos:** `docs/mediciones/sus/*`, `docs/etica/consentimientos/registro-aceptacion.md`, `docs/etica/ETHICS.md`, `VERIFICACION.md`.
*   **P6 — Nombres en español en el código (Contrato REST roto) (1.0 pts)**
    *   Se restauraron los contratos REST y, en la ronda final, se alinearon los modelos del frontend (Backup y AuditLog) con el JSON real del backend, campo por campo, verificados con una prueba end-to-end contra producción.
    *   **Commits:** `21edc80`, `a191fb3`.
    *   **Archivos:** `BackupController.java`, `AuditController.java`, `frontend/src/app/features/backups/*`, `frontend/src/app/features/audit/*`.
*   **P7 — Lighthouse sin ninguna corrida (0.7 pts)**
    *   Se eliminaron los tres JSON fabricados y se ejecutaron auditorías reales (perfil desktop y móvil) contra la URL pública; los reportes resultantes pesan cientos de kB.
    *   **Commit:** `fc4c701`.
    *   **Archivos:** `docs/mediciones/perf/lighthouse/*`.
*   **P9 — SRS firmado (0.6 pts)**
    *   Se corrigió el control del documento para que la versión 1.0.0 figure como *"sometido a revisión con observaciones, no aprobado"*, en los mismos términos que declaró el informe de revisión del docente, y la v1.1.0 quedó marcada como aprobada.
    *   **Resolución de la firma:** por **confirmación directa del docente-director** el 18-09-2026, por WhatsApp, quien indicó que su firma en el informe de revisión (`docs/requisitos/Revision_SRS_SBVIA_v1.1.0.pdf`) constituye validación suficiente del SRS, sin requerir firma adicional dentro del propio documento. **Esto NO se resolvió por un cambio técnico**, sino por la aclaración del docente.
    *   **Commit del estado de la v1.0.0:** `01237e6`.
    *   **Archivos:** `docs/requisitos/SRS-v1.1.0.tex`, `docs/requisitos/SRS-v1.1.0.pdf`.
*   **P10 — Sin corrección por comparaciones múltiples (0.5 pts)**
    *   Se corrigió `estadistica.py` para **calcular** los p-valores reales desde el estadístico t (en lugar de fijarlos a mano) antes de aplicar Holm-Bonferroni.
    *   **Commit:** `7b3cfbe`.
    *   **Archivos:** `docs/mediciones/perf/estadistica.py`.
*   **P11 — Cookie de sesión sin el atributo Secure (0.4 pts)**
    *   Se documentó el cableado real de `security.cookie.secure` (código + `application-prod.yml`) y se sustituyó la salida de expediente que no reproducía su propia orden.
    *   **Commit:** `e7a1e34`.
    *   **Archivos:** `VERIFICACION.md`, `AuthController.java`, `application-prod.yml`.
*   **P12 — Referencias sin verificar una por una (0.3 pts)**
    *   Se auditaron los 46 DOIs de `refs.bib` uno por uno contra Crossref (18 correctos, 10 corregidos, 18 retirados), con log auditable.
    *   **Commit:** `94fdd61`.
    *   **Archivos:** `docs/refs.bib`, `docs/doi_check.log`.
*   **EV-1 — Expediente de verificación**
    *   Se completó `VERIFICACION.md` con los puntos P2, P4, P8 y P9 y se corrigieron las salidas obsoletas.
    *   **Commit:** `6f450de`.
    *   **Archivos:** `VERIFICACION.md`.
*   **EV-2 — Verificador del expediente**
    *   Se eliminó la lista blanca de `verify_expediente.py`: cualquier orden con código distinto de cero hace fallar el verificador, y se compara la salida real contra la esperada.
    *   **Commit:** `7a691d0`.
    *   **Archivos:** `scripts/verify_expediente.py`, `Makefile`.
*   **EV-3 — Etiquetado y despliegue**
    *   La etiqueta se publica de forma anotada sobre el commit final, sin re-apuntar etiquetas existentes.
    *   **Archivos:** `README.md`, `Makefile`.

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
