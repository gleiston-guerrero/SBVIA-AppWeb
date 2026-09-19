#!/usr/bin/env python3
"""
Script de generación de figuras vectoriales (SVG/PDF) para el documento final de SBVIA.
Utiliza paletas accesibles para personas con daltonismo (Okabe-Ito / Viridis).
Semilla fija: SEED = 42
"""

import os
import numpy as np

# Configuración de directorio de salida
OUT_DIR = "docs/diagramas"
os.makedirs(OUT_DIR, exist_ok=True)

# Paleta Okabe-Ito accesible
COLOR_SKY_BLUE = "#56B4E9"
COLOR_ORANGE = "#E69F00"
COLOR_BLUISH_GREEN = "#009E73"
COLOR_VERMILLION = "#D55E00"

def generar_svg_rendimiento():
    """Genera gráfico SVG de barras comparando p95 en frío vs caliente."""
    svg_content = f"""<svg xmlns="http://www.w3.org/2000/svg" width="600" height="350" viewBox="0 0 600 350">
        <style>
            .axis {{ stroke: #333; stroke-width: 1.5; }}
            .text {{ font-family: sans-serif; font-size: 13px; fill: #333; }}
            .title {{ font-family: sans-serif; font-size: 15px; font-weight: bold; fill: #111; }}
        </style>
        <text x="300" y="30" text-anchor="middle" class="title">Latencia p95 k6: Caché Frío vs Caché Caliente (Redis 7)</text>
        <line x1="80" y1="280" x2="520" y2="280" class="axis" />
        <line x1="80" y1="60" x2="80" y2="280" class="axis" />
        
        <!-- Barra Caché Frío (416 ms) -->
        <rect x="150" y="90" width="100" height="190" fill="{COLOR_VERMILLION}" rx="4" />
        <text x="200" y="80" text-anchor="middle" class="text">416.0 ms (p95)</text>
        <text x="200" y="305" text-anchor="middle" class="text">Caché Frío (PostgreSQL)</text>
        
        <!-- Barra Caché Caliente (76 ms) -->
        <rect x="350" y="245" width="100" height="35" fill="{COLOR_BLUISH_GREEN}" rx="4" />
        <text x="400" y="235" text-anchor="middle" class="text">76.4 ms (p95)</text>
        <text x="400" y="305" text-anchor="middle" class="text">Caché Caliente (Redis)</text>
    </svg>"""
    with open(os.path.join(OUT_DIR, "k6-latencia-comparativa.svg"), "w", encoding="utf-8") as f:
        f.write(svg_content)
    print("Figura k6 generada con éxito.")

def generar_svg_sus():
    """Registro SVG de las respuestas crudas SUS.

    El estudio SUS esta RETIRADO (fecha declarada 2026-07-28/29 anterior a los
    commits 55201f5 y 5e852c6 que introducen la funcionalidad de simulacion
    evaluada). Por eso esta figura NO muestra media, mediana, minimo, maximo ni
    clasificacion: solo las respuestas individuales registradas, en ingles.
    """
    respuestas = [75.0, 92.5, 75.0, 92.5, 85.0, 75.0, 95.0, 75.0, 92.5, 77.5,
                  95.0, 80.0, 75.0, 95.0, 82.5]
    puntos = "".join(
        f'<circle cx="{80 + (v - 60) * 8.8:.1f}" cy="150" r="6" '
        f'fill="{COLOR_SKY_BLUE}" stroke="#333" stroke-width="1.5" />'
        for v in respuestas
    )
    svg_content = f"""<svg xmlns="http://www.w3.org/2000/svg" width="600" height="300" viewBox="0 0 600 300">
        <style>
            .axis {{ stroke: #333; stroke-width: 1.5; }}
            .text {{ font-family: sans-serif; font-size: 13px; fill: #333; }}
            .title {{ font-family: sans-serif; font-size: 15px; font-weight: bold; fill: #111; }}
        </style>
        <text x="300" y="30" text-anchor="middle" class="title">Individual SUS responses (study retracted - raw material only)</text>
        <line x1="80" y1="200" x2="520" y2="200" class="axis" />
        {puntos}
        <text x="80" y="225" text-anchor="middle" class="text">60</text>
        <text x="300" y="225" text-anchor="middle" class="text">85</text>
        <text x="520" y="225" text-anchor="middle" class="text">100</text>
        <text x="300" y="260" text-anchor="middle" class="text">SUS Score (raw responses, no aggregated statistics reported)</text>
    </svg>"""
    with open(os.path.join(OUT_DIR, "sus-boxplot.svg"), "w", encoding="utf-8") as f:
        f.write(svg_content)
    print("Figura SUS generada con éxito (datos crudos, sin estadísticos).")

if __name__ == "__main__":
    generar_svg_rendimiento()
    generar_svg_sus()
