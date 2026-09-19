import os
import csv
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt

OUT_DIR = "docs/diagramas"
os.makedirs(OUT_DIR, exist_ok=True)

# Paletas accesibles
COLOR_SKY_BLUE = "#56B4E9"
COLOR_BLUISH_GREEN = "#009E73"
COLOR_VERMILLION = "#D55E00"

def generar_sus():
    """Registro grafico de las respuestas crudas SUS.

    El estudio SUS esta RETIRADO: la fecha declarada de aplicacion
    (2026-07-28/29) es anterior a los commits 55201f5 (2026-09-03) y 5e852c6
    (2026-09-04) que introducen la funcionalidad de simulacion evaluada. Por eso
    esta figura NO muestra media, mediana, minimo, maximo ni clasificacion
    alguna: solo las respuestas individuales registradas.
    """
    scores = []
    with open('docs/mediciones/sus/sus-raw-data.csv', 'r') as f:
        reader = csv.DictReader(f)
        for row in reader:
            scores.append(float(row['sus_score']))

    fig, ax = plt.subplots(figsize=(8, 5))

    ax.plot(scores, [1] * len(scores), 'o', color=COLOR_SKY_BLUE,
            markeredgecolor='black', markeredgewidth=1.2, markersize=10)

    ax.set_title("Individual SUS responses (study retracted - raw material only)",
                 fontsize=13, fontweight='bold')
    ax.set_xlabel("SUS Score", fontsize=12)
    ax.set_yticks([])
    ax.set_xlim(60, 100)
    fig.tight_layout()
    out_path = os.path.join(OUT_DIR, "sus-boxplot.png")
    fig.savefig(out_path, dpi=120)
    print(f"Generated {out_path} (retracted study: raw responses only, no statistics)")


def generar_k6():
    fig, ax = plt.subplots(figsize=(8, 5))
    
    # Data from docs/mediciones/perf/SPEEDUP-CACHE.md
    labels = ["Cold Cache (PostgreSQL)", "Hot Cache (Redis)"]
    p95_values = [71.0, 60.7] # Actual measured values, not the fake 416
    colors = [COLOR_VERMILLION, COLOR_BLUISH_GREEN]
    
    bars = ax.bar(labels, p95_values, color=colors, width=0.5, edgecolor='black', linewidth=1)
    
    ax.set_title("k6 p95 Latency: Cold Cache vs Hot Cache (Redis 7)", fontsize=14, fontweight='bold')
    ax.set_ylabel("Latency p95 (ms)", fontsize=12)
    
    for bar in bars:
        yval = bar.get_height()
        ax.text(bar.get_x() + bar.get_width()/2, yval + 2, f"{yval} ms", 
                ha='center', va='bottom', fontsize=11, fontweight='bold')
                
    ax.set_ylim(0, max(p95_values) * 1.3)
    fig.tight_layout()
    out_path = os.path.join(OUT_DIR, "k6-latencia-comparativa.png")
    fig.savefig(out_path, dpi=120)
    print(f"Generated {out_path} with values {p95_values}")

if __name__ == "__main__":
    generar_sus()
    generar_k6()
