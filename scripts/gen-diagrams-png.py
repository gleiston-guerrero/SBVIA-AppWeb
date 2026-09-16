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
    scores = []
    with open('docs/mediciones/sus/sus-raw-data.csv', 'r') as f:
        reader = csv.DictReader(f)
        for row in reader:
            scores.append(float(row['sus_score']))
    
    mean_score = sum(scores) / len(scores)
    median = sorted(scores)[len(scores)//2]
    min_val = min(scores)
    max_val = max(scores)

    fig, ax = plt.subplots(figsize=(8, 5))
    
    # Draw boxplot horizontally for similar look to original SVG if desired, 
    # but the SVG was custom. We'll use ax.boxplot
    bp = ax.boxplot(scores, vert=False, patch_artist=True, widths=0.4,
                    boxprops=dict(facecolor=COLOR_SKY_BLUE, color='black', linewidth=1.5),
                    medianprops=dict(color=COLOR_VERMILLION, linewidth=2),
                    whiskerprops=dict(linewidth=1.5, linestyle='--'),
                    capprops=dict(linewidth=1.5))

    ax.set_title("SUS Score Distribution (N = 15 Participants)", fontsize=14, fontweight='bold')
    ax.set_xlabel("SUS Score", fontsize=12)
    ax.set_yticks([])
    
    # Adding the text annotations exactly as requested
    ax.text(mean_score, 1.35, f"Mean: {mean_score:.2f} (Grade B+ - Good+)", 
            horizontalalignment='center', color='black', fontsize=11, fontweight='bold')
            
    ax.text(min_val, 0.7, f"Min: {min_val:.1f}", horizontalalignment='center')
    ax.text(median, 0.7, f"Median: {median:.1f}", horizontalalignment='center')
    ax.text(max_val, 0.7, f"Max: {max_val:.1f}", horizontalalignment='center')
    
    ax.set_xlim(60, 100)
    fig.tight_layout()
    out_path = os.path.join(OUT_DIR, "sus-boxplot.png")
    fig.savefig(out_path, dpi=120)
    print(f"Generated {out_path} with mean {mean_score:.2f}, min {min_val}, max {max_val}")


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
