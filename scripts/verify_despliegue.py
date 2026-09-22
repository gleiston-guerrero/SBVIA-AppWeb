#!/usr/bin/env python3
"""Comprueba el despliegue publico, con reintentos.

`curl -sI ... | grep HTTP` falla si la instancia esta suspendida o tarda en
despertar, que es lo normal en el plan gratuito del proveedor. Eso hacia que el
expediente fallara por una causa ajena al repositorio. Esta comprobacion
reintenta y ademas verifica que las cabeceras de seguridad declaradas esten
realmente presentes.

Uso:  python scripts/verify_despliegue.py
Sale con codigo 0 solo si los dos servicios responden y la API trae sus cabeceras.
"""
from __future__ import annotations

import sys
import time
import urllib.error
import urllib.request

API = "https://sbvia-appweb.onrender.com"
WEB = "https://sbvia-frontend.onrender.com"
SALUD = API + "/actuator/health"
INTENTOS = 5
ESPERA = 20
TIMEOUT = 90

# Headers the criterion requires from the API.
CABECERAS_API = (
    "Content-Security-Policy",
    "X-Frame-Options",
    "X-Content-Type-Options",
    "Strict-Transport-Security",
    "X-XSS-Protection",
)


def pide(url: str):
    """Devuelve (codigo, cabeceras) reintentando ante fallos transitorios."""
    ultimo = None
    for intento in range(INTENTOS):
        try:
            req = urllib.request.Request(url, headers={"User-Agent": "SBVIA-despliegue/1.0"})
            with urllib.request.urlopen(req, timeout=TIMEOUT) as r:
                return r.status, r.headers
        except urllib.error.HTTPError as e:
            return e.code, e.headers
        except Exception as e:
            ultimo = e
            if intento < INTENTOS - 1:
                time.sleep(ESPERA)
    raise ultimo if ultimo else RuntimeError("sin respuesta")


def main() -> int:
    fallos = []

    try:
        codigo, cabeceras = pide(SALUD)
        print(f"API  {SALUD} -> {codigo}")
        if codigo != 200:
            fallos.append(f"la API respondio {codigo}")
    except Exception as e:
        print(f"API  sin respuesta: {type(e).__name__}", file=sys.stderr)
        fallos.append("la API no respondio")
        cabeceras = None

    if cabeceras is not None:
        for c in CABECERAS_API:
            presente = bool(cabeceras.get(c))
            print(f"  {c:28} {'presente' if presente else 'AUSENTE'}")
            if not presente:
                fallos.append(f"falta la cabecera {c}")

    try:
        codigo, _ = pide(WEB + "/")
        print(f"WEB  {WEB}/ -> {codigo}")
        if codigo != 200:
            fallos.append(f"el sitio respondio {codigo}")
    except Exception as e:
        print(f"WEB  sin respuesta: {type(e).__name__}", file=sys.stderr)
        fallos.append("el sitio no respondio")

    print("=" * 62)
    if fallos:
        print(f"DESPLIEGUE FALLIDO: {len(fallos)} comprobaciones")
        for f in fallos:
            print("  -", f)
        return 1
    print("DESPLIEGUE OK: API y sitio responden con sus cabeceras")
    return 0


if __name__ == "__main__":
    sys.exit(main())
