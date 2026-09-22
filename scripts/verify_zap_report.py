#!/usr/bin/env python3
"""Verifica el informe ZAP versionado sin depender de Docker.

Reproducir el escaneo completo exige el demonio de Docker, descargar la imagen
de ZAP y que el objetivo responda: es la receta de reproduccion, no una
comprobacion que deba correr en cada verificacion. Aqui se valida el artefacto
que si esta versionado, de modo que el expediente pueda comprobarse en cualquier
entorno.

Uso:  python scripts/verify_zap_report.py
Sale con codigo 0 solo si el informe es valido.
"""
from __future__ import annotations

import sys
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent
INFORME = RAIZ / "docs" / "mediciones" / "sec" / "zap" / "zap-report.html"
OBJETIVO = "sbvia-appweb.onrender.com"
MINIMO = 20_000


def main() -> int:
    if not INFORME.exists():
        print("FALLA: no existe el informe ZAP versionado", file=sys.stderr)
        return 1

    tam = INFORME.stat().st_size
    texto = INFORME.read_text(encoding="utf-8", errors="replace")

    fallos = []
    if tam < MINIMO:
        fallos.append(f"el informe pesa {tam} bytes, menos de {MINIMO}")
    if OBJETIVO not in texto:
        fallos.append(f"el informe no menciona el objetivo {OBJETIVO}")
    if "ZAP" not in texto:
        fallos.append("el informe no parece generado por ZAP")

    print(f"Informe ZAP: {INFORME.name} ({tam} bytes)")
    print(f"  objetivo declarado: {'si' if OBJETIVO in texto else 'no'}")
    print("=" * 60)
    if fallos:
        print(f"ZAP FALLIDO: {len(fallos)} comprobaciones")
        for f in fallos:
            print("  -", f)
        return 1
    print("ZAP OK: informe versionado valido")
    return 0


if __name__ == "__main__":
    sys.exit(main())
