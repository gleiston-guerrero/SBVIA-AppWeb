#!/usr/bin/env python3
"""Verifica que los DOI declarados en CITATION.cff resuelvan correctamente.

Lee los identificadores del propio archivo de metadatos, de modo que si alguien
cambia el DOI por uno que no resuelve, esta comprobacion falla. Reintenta ante
errores de red transitorios, porque un fallo de TLS momentaneo no debe hacer
fallar el expediente.

Uso:  python scripts/verify_dois.py
Sale con codigo 0 solo si TODOS los DOI declarados devuelven 200.
"""
from __future__ import annotations

import re
import sys
import time
import urllib.error
import urllib.request
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent
CITATION = RAIZ / "CITATION.cff"
INTENTOS = 4
ESPERA = 3
TIMEOUT = 30


def dois_declarados() -> list[str]:
    """Extrae los DOI de CITATION.cff conservando el orden y sin repetir."""
    texto = CITATION.read_text(encoding="utf-8")
    vistos, salida = set(), []
    for m in re.finditer(r'(?m)^\s*doi:\s*["\']?([^"\'\s]+)["\']?\s*$', texto):
        d = m.group(1).strip()
        if d and d not in vistos:
            vistos.add(d)
            salida.append(d)
    return salida


def resolver(doi: str) -> tuple[int | None, str]:
    url = "https://doi.org/api/handles/" + doi
    ultimo = ""
    for intento in range(INTENTOS):
        try:
            r = urllib.request.Request(url, headers={"User-Agent": "SBVIA-doi-check/1.0"})
            with urllib.request.urlopen(r, timeout=TIMEOUT) as resp:
                return resp.status, ""
        except urllib.error.HTTPError as e:
            return e.code, f"HTTP {e.code}"
        except Exception as e:  # red, TLS, timeout
            ultimo = f"{type(e).__name__}: {e}"
            if intento < INTENTOS - 1:
                time.sleep(ESPERA)
    return None, ultimo


def main() -> int:
    if not CITATION.exists():
        print("FALLA: no se encuentra CITATION.cff", file=sys.stderr)
        return 1

    dois = dois_declarados()
    if not dois:
        print("FALLA: CITATION.cff no declara ningun DOI", file=sys.stderr)
        return 1

    print(f"DOI declarados en CITATION.cff: {len(dois)}")
    fallos = []
    for d in dois:
        codigo, error = resolver(d)
        if codigo == 200:
            print(f"  {d} -> 200")
        else:
            print(f"  {d} -> {codigo or 'sin respuesta'} {error}", file=sys.stderr)
            fallos.append(d)

    print("=" * 60)
    if fallos:
        print(f"DOIS FALLIDOS: {len(fallos)} de {len(dois)}")
        for d in fallos:
            print(f"  - {d}")
        return 1
    print(f"DOIS OK: {len(dois)} resueltos con 200")
    return 0


if __name__ == "__main__":
    sys.exit(main())
