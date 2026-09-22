#!/usr/bin/env python3
"""Genera la figura del estudio de usabilidad de septiembre de 2026.

La figura que usaba el informe era la del estudio de JULIO, retirado: su propio
titulo lo decia y sus valores iban de 75 a 95, mientras el pie de figura afirmaba
que era el estudio de septiembre (45 a 85, media 69,00). El generador existente,
scripts/gen-sus-dist.py, lee sus-raw-data.csv, que es el conjunto retirado.

Esta figura se construye desde sus-raw-data-2026-09.csv y rotula todo en ingles,
que es lo que exige el criterio para las figuras.
"""
from __future__ import annotations

import csv
import sys
from pathlib import Path

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

RAIZ = Path(__file__).resolve().parent.parent
CSV = RAIZ / "docs" / "mediciones" / "sus" / "sus-raw-data-2026-09.csv"
DESTINOS = [
    RAIZ / "docs" / "mediciones" / "sus" / "sus-distribution-2026-09.png",
    RAIZ / "docs" / "diagramas" / "sus-distribution.png",
]


def main() -> int:
    puntajes = []
    with CSV.open(encoding="utf-8") as f:
        for fila in csv.DictReader(f):
            puntajes.append(float(fila["sus_score"]))

    media = sum(puntajes) / len(puntajes)
    print(f"  participantes: {len(puntajes)}")
    print(f"  rango        : {min(puntajes):.1f} - {max(puntajes):.1f}")
    print(f"  media        : {media:.2f}")

    fig, ax = plt.subplots(figsize=(8, 5))
    ax.hist(puntajes, bins=8, range=(45, 85), color="#009E73", edgecolor="black", alpha=0.85)
    ax.axvline(68, color="#D55E00", linestyle="--", linewidth=1.6, label="Acceptance threshold (68)")
    ax.axvline(media, color="#0072B2", linestyle="-", linewidth=1.8, label=f"Observed mean ({media:.2f})")
    ax.set_title("Individual SUS scores - September 2026 study (n = 15)")
    ax.set_xlabel("SUS score")
    ax.set_ylabel("Frequency")
    ax.legend(loc="upper left", fontsize=9)
    fig.tight_layout()

    for destino in DESTINOS:
        destino.parent.mkdir(parents=True, exist_ok=True)
        fig.savefig(destino, dpi=120)
        print(f"  guardada: {destino.relative_to(RAIZ)}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
