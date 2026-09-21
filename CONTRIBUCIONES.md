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
    *   Se referenciaron desde el texto las 4 capturas de interfaz que no estaban citadas y se empezaron a traducir al inglés los pies de figura. La primera pasada (`9afd075`) tradujo 12 de 20 y dejó 8 en español, que es la inconsistencia que recoge la evaluación. Después **se completó la traducción: los 20 pies de figura del informe están en inglés** (`6fc74d3`), y sus etiquetas automáticas también: `Figure 3.1` y `Table 5.1` en lugar de «Figura» y «Tabla» (`0455786`).
    *   **Capturas de la interfaz en inglés.** El sistema es para conductores en formación ecuatorianos y su interfaz está en español; traducir el producto habría perjudicado a sus usuarios. La solución fue **internacionalización con español por defecto** (`ccee94f`): la aplicación sigue sirviendo en español y puede mostrarse en inglés desde un selector ES | EN. Con él se **recapturaron las 4 pantallas contra el sistema desplegado** (`9e7cc01`), en el mismo tamaño que las originales (1599×770).
    *   **Se declara lo que sigue en español y por qué:** los nombres de escenario, los tipos de vía y el texto del informe de IA los genera el servidor o están en la base de datos. Son **contenido**, no interfaz, igual que los nombres de escenario son topónimos ecuatorianos. La interfaz de las cuatro capturas está íntegramente en inglés.
    *   **Commits:** `9afd075`, `6fc74d3`, `0455786`, `ccee94f`, `d5f7841`, `9e7cc01`.
    *   **Archivos:** `docs/informe-final.tex`, `docs/informe-final.pdf`, `docs/diagramas/pantalla-*.png`, `frontend/src/app/i18n/`.
*   **P4 — Sin depósito propio en Zenodo (1.0 pts)**
    *   Se añadió a `CITATION.cff` la referencia al dataset de validación con su DOI real (`10.5281/zenodo.22785358`), además del DOI del software (`10.5281/zenodo.22840356`).
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
    *   El control del documento quedó con la versión 1.0.0 y la 1.1.0 marcadas como *revisadas con observaciones*, en los mismos términos que declararon los informes de revisión del docente.
    *   **Corrección de la declaración de aprobación (observación X6):** se retiran de `SRS-v1.1.0.tex`, `CHANGELOG-REQ.md`, `CONTRIBUCIONES.md` y `VERIFICACION.md` las frases que afirmaban que la v1.1.0 estaba "Aprobada — validada mediante firma del docente-director" y que la firma del informe "constituye validación suficiente del SRS". El docente-director escribió lo contrario en dos informes distintos: su firma *"no asigna calificación y no constituye aprobación del SRS"*. La firma acredita revisión, no aprobación; ninguna versión figura por tanto como aprobada.
    *   **Archivos:** `docs/requisitos/SRS-v1.1.0.tex`, `docs/requisitos/SRS-v1.1.0.pdf`, `docs/requisitos/CHANGELOG-REQ.md`, `CONTRIBUCIONES.md`, `VERIFICACION.md`.
*   **P10 — Sin corrección por comparaciones múltiples (0.5 pts)**
    *   Se corrigió `estadistica.py` para **calcular** los p-valores reales desde el estadístico t (en lugar de fijarlos a mano) antes de aplicar Holm-Bonferroni.
    *   **Commit:** `7b3cfbe`.
    *   **Archivos:** `docs/mediciones/perf/estadistica.py`.
*   **P11 — Cookie de sesión sin el atributo Secure (0.4 pts)**
    *   Se documentó el cableado real de `security.cookie.secure` (código + `application-prod.yml`) y se sustituyó la salida de expediente que no reproducía su propia orden.
    *   **Commit:** `e7a1e34`.
    *   **Archivos:** `VERIFICACION.md`, `AuthController.java`, `application-prod.yml`.
*   **P12 — Referencias sin verificar una por una (0.3 pts)**
    *   Se auditaron las **46 referencias** de `refs.bib` una por una contra Crossref, DataCite y OpenLibrary. La primera pasada (`94fdd61`) dio 18 correctos, 10 corregidos y 18 retirados; una **segunda auditoría completa** (`833925c`) dejó el resultado en **30 con DOI resuelto y verificado**, **16 sin DOI registrado pero con ISBN o URL oficial verificados** y **0 sin ningún identificador** (antes eran 16 las que no tenían DOI, URL ni ISBN). El registro es auditable y reproducible con `python scripts/check_dois.py`.
    *   **Commits:** `94fdd61`, `833925c`.
    *   **Archivos:** `docs/refs.bib`, `docs/doi_check.log`, `scripts/check_dois.py`.
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

Declaramos que la información presentada en este documento y respaldada por el historial de Git es cierta y verificable.

**Identidad de las cuentas empleadas.** Los 140 commits de Jefferson M. Umaginga Arévalo están firmados con su correo institucional `jumagingaa@uteq.edu.ec`. Los de Justyn K. Cruz Pérez están firmados con la dirección personal `justyncruzperez@gmail.com`, en sus dos cuentas de GitHub (`JustynCruz04`, 175 commits, y `keithdrox`, 41); `jcruzp@uteq.edu.ec` es su correo institucional de contacto y no aparece en ninguno de ellos.

**Firma 1:** Justyn Keith Cruz Pérez
**Nombre:** Justyn Keith Cruz Pérez
**Correo institucional:** jcruzp@uteq.edu.ec
**Fecha:** 2026-09-20

**Firma 2:** *No firma.*
**Nombre:** Jefferson Manuel Umaginga Arévalo
**Motivo:** no registra actividad en el repositorio desde el **2026-09-04** (commit `34f9bf8`), anterior al trabajo que declara este documento. Sus aportes previos constan en la sección correspondiente.

> La línea de firma 1 reproduce el nombre y la fecha del declarante y **no es una firma manuscrita**. Si el evaluador requiere firma manuscrita, el equipo imprimirá el documento, lo firmará y aportará el escaneo.
>
> La firma 2 se deja explícitamente sin firmar y no en blanco: firmar por una persona que no participó en el trabajo declarado no es admisible, y dejar la línea vacía sin explicación fue el defecto que señaló la evaluación.
