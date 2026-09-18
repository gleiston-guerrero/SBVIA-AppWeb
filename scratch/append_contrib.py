import os

append_contrib = """
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
"""

filepath = r"e:\SBVIA-AppWeb\CONTRIBUCIONES.md"
with open(filepath, "r", encoding="utf-8") as f:
    content = f.read()

# Insert before "### Jefferson M. Umaginga Arévalo"
insert_idx = content.find("### Jefferson M. Umaginga Arévalo")
new_content = content[:insert_idx] + append_contrib + "\n" + content[insert_idx:]

with open(filepath, "w", encoding="utf-8") as f:
    f.write(new_content)
