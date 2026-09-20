#!/usr/bin/env python3
"""Calculo y verificacion de la escala SUS — Proyecto SBVIA.

Uso:
    python calcular_sus.py --ronda 2026-09     # estudio vigente (septiembre 2026)
    python calcular_sus.py --ronda retirado    # estudio de julio 2026 (retirado)

El script NO confia en la columna `sus_score` del CSV: recalcula cada puntuacion
desde las respuestas crudas con la formula de Brooke (1996) y falla si alguna fila
no la reproduce.

Formula:
    contribucion impar (Q1,Q3,Q5,Q7,Q9) = valor - 1
    contribucion par   (Q2,Q4,Q6,Q8,Q10) = 5 - valor
    SUS_individual = (suma de las diez contribuciones) * 2.5
"""
from __future__ import annotations

import argparse
import csv
import statistics as st
import sys
from pathlib import Path

AQUI = Path(__file__).resolve().parent

RONDAS = {
    "2026-09": {
        "csv": "sus-raw-data-2026-09.csv",
        "titulo": "Estudio de septiembre de 2026 (vigente)",
        "reporta": True,
    },
    "retirado": {
        "csv": "sus-raw-data.csv",
        "titulo": "Estudio de julio de 2026 (RETIRADO — no usar como evidencia)",
        "reporta": False,
    },
}

T95 = {13: 2.160, 14: 2.145, 15: 2.131, 16: 2.120}  # t de Student, 95%, dos colas


def puntuacion(respuestas: list[int]) -> tuple[int, int, float]:
    impares = sum(respuestas[i] - 1 for i in (0, 2, 4, 6, 8))
    pares = sum(5 - respuestas[i] for i in (1, 3, 5, 7, 9))
    return impares, pares, (impares + pares) * 2.5


def main() -> int:
    ap = argparse.ArgumentParser(description="Calculo SUS — SBVIA")
    ap.add_argument("--ronda", default="2026-09", choices=sorted(RONDAS),
                    help="ronda a procesar (por defecto: 2026-09)")
    args = ap.parse_args()

    cfg = RONDAS[args.ronda]
    ruta = AQUI / cfg["csv"]
    if not ruta.exists():
        print(f"ERROR: no se encuentra {ruta}", file=sys.stderr)
        return 2

    filas, discrepancias = [], []
    with ruta.open(encoding="utf-8", newline="") as f:
        for fila in csv.DictReader(f):
            pid = fila["participant_id"]
            try:
                resp = [int(fila[f"Q{i}"]) for i in range(1, 11)]
            except (KeyError, ValueError) as e:
                print(f"ERROR: datos invalidos en {pid}: {e}", file=sys.stderr)
                return 1
            if any(v < 1 or v > 5 for v in resp):
                print(f"ERROR: {pid} tiene valores fuera de la escala 1-5", file=sys.stderr)
                return 1

            imp, par, calc = puntuacion(resp)
            declarado = float(fila["sus_score"])
            if abs(calc - declarado) > 0.01:
                discrepancias.append((pid, declarado, calc))
            filas.append((pid, resp, calc, imp, par,
                          fila.get("fecha_sesion", ""), fila.get("duracion_min", ""),
                          fila.get("dispositivo", "")))

    print("=" * 78)
    print(f"  {cfg['titulo']}")
    print(f"  Archivo: {cfg['csv']}")
    print("=" * 78)
    print(f"{'cod':5} {'respuestas':24} {'imp':>4} {'par':>4} {'SUS':>6}  {'fecha':11} {'min':>4}  dispositivo")
    print("-" * 78)
    for pid, resp, calc, imp, par, fecha, dur, dev in filas:
        print(f"{pid:5} {','.join(map(str, resp)):24} {imp:4} {par:4} {calc:6.1f}  {fecha:11} {dur:>4}  {dev}")

    if discrepancias:
        print("\n*** DISCREPANCIAS entre el CSV y la formula SUS ***", file=sys.stderr)
        for pid, declarado, calc in discrepancias:
            print(f"  [{pid}] CSV={declarado} formula={calc}", file=sys.stderr)
        print("El CSV no reproduce la formula. Corregir antes de usar los datos.", file=sys.stderr)
        return 1
    print(f"\nVerificacion: las {len(filas)} filas reproducen la formula SUS. OK")

    if not cfg["reporta"]:
        print("\n" + "=" * 78)
        print("  ESTUDIO RETIRADO — NO SE REPORTAN RESULTADOS")
        print("=" * 78)
        print("  Este estudio se retiro porque su fecha declarada de aplicacion")
        print("  (2026-07-28/29) es anterior a los commits 55201f5 (2026-09-03) y")
        print("  5e852c6 (2026-09-04) que introducen la funcionalidad de simulacion")
        print("  evaluada. No debe citarse el promedio como resultado de usabilidad.")
        print("  Vease docs/mediciones/sus/sus-analysis.md y docs/RETRACTIONS.md (R-07).")
        return 0

    vals = [f[2] for f in filas]
    n = len(vals)
    media, de = st.mean(vals), st.stdev(vals)
    ee = de / n ** 0.5
    t = T95.get(n - 1, 2.145)
    print("\n" + "=" * 78)
    print(f"N                                  : {n}")
    print(f"Media SUS                          : {media:.2f}")
    print(f"Desviacion estandar (muestral)     : {de:.2f}")
    print(f"Error estandar                     : {ee:.2f}")
    print(f"IC 95% (t={t}, gl={n-1})            : [{media - t*ee:.2f} , {media + t*ee:.2f}]")
    print(f"Mediana / Min / Max                : {st.median(vals):.1f} / {min(vals):.1f} / {max(vals):.1f}")
    print("-" * 78)
    cumple_n, cumple_m = n >= 15, media >= 68
    print(f"RNF-06 exige >= 15 participantes y media >= 68")
    print(f"  muestra : {n} participantes -> {'CUMPLE' if cumple_n else 'NO CUMPLE'}")
    print(f"  media   : {media:.2f} -> {'CUMPLE' if cumple_m else 'NO CUMPLE'} (margen {media-68:+.2f})")
    print(f"  RESULTADO DEL CRITERIO: {'CUMPLE' if (cumple_n and cumple_m) else 'NO CUMPLE'}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
