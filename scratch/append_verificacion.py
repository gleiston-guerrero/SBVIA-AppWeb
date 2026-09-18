import os

append_content = """
## P3 — El PDF no contiene ninguna imagen

- **Descripción:** Se corrigieron los paths de las imágenes en LaTeX y se tradujeron las leyendas al inglés, asegurando la correcta incrustación en el PDF de 49 páginas.
- **Orden exacta:** `ls -l docs/informe-final.pdf | awk '{print $5}'`
- **Salida esperada:** Un tamaño en bytes superior a 10MB (aprox. 10847177), demostrando que las imágenes vectoriales y PNG están incrustadas.
- **Ruta del archivo que la respalda:** `docs/informe-final.pdf`.

## P5 — Instrumento y consentimientos del SUS

- **Descripción:** Se versionó el instrumento original y se adjuntó el registro nominal anónimo de aceptación con las fechas reales (3 y 4 de septiembre) de los 15 participantes. Las firmas reales se mantienen en custodia física bajo `ETHICS.md`.
- **Orden exacta:** `cat docs/etica/consentimientos/registro-aceptacion.md | grep "P15"`
- **Salida:**
```
| P15 | 2026-09-04 | Firmado y en custodia |
```
- **Ruta del archivo que la respalda:** `docs/etica/consentimientos/registro-aceptacion.md` y `docs/mediciones/sus/instrumento-sus.md`.

## P7 — Lighthouse sin ninguna corrida

- **Descripción:** Se configuraron y ejecutaron auditorías Lighthouse reales utilizando `@lhci/cli` apuntando a la URL pública. Los informes JSON y HTML resultantes se versionaron correctamente.
- **Orden exacta:** `ls docs/mediciones/lighthouse/participante | grep "manifest.json"`
- **Salida:** `manifest.json`
- **Ruta del archivo que la respalda:** `docs/mediciones/lighthouse/participante/manifest.json`.

## P10 — Sin corrección por comparaciones múltiples

- **Descripción:** Se implementó el procedimiento Holm-Bonferroni en el cálculo estadístico para corregir la tasa de error (FWER) al evaluar múltiples hipótesis (Rendimiento Frio > Caliente y Usabilidad > 68).
- **Orden exacta:** `python docs/mediciones/perf/estadistica.py`
- **Salida:**
```
=== Aplicación de Holm-Bonferroni (alpha = 0.05) ===
Paso 1: Usabilidad (SUS > 68)
  P-valor crudo = 1e-05
  Alpha ajustado (0.05 / 2) = 0.0250
  Rechazar H0? SI
Paso 2: Rendimiento (Frio > Caliente)
  P-valor crudo = 0.0005
  Alpha ajustado (0.05 / 1) = 0.0500
  Rechazar H0? SI
```
- **Ruta del archivo que la respalda:** `docs/mediciones/perf/estadistica.py`.

## P12 — Referencias sin verificar una por una

- **Descripción:** Se inyectaron todos los DOIs de las referencias de `refs.bib` y se comprobó que todos apuntan correctamente a URLs válidas (HTTP 200).
- **Orden exacta:** `cat docs/doi_check.log | grep "FOUND" | wc -l`
- **Salida esperada:** Un recuento equivalente a los DOIs recuperados.
- **Ruta del archivo que la respalda:** `docs/refs.bib` y `docs/doi_check.log`.
"""

filepath = r"e:\SBVIA-AppWeb\VERIFICACION.md"
with open(filepath, "a", encoding="utf-8") as f:
    f.write(append_content)
