#!/usr/bin/env python3
"""Comprueba las banderas de las cookies de sesion contra el despliegue (P11).

El criterio pide la cookie con Secure y HttpOnly, comprobada contra el
despliegue. Hasta ahora solo se comprobaba la configuracion del repositorio, no lo
que el servidor emite de verdad.

Uso:  python scripts/verify_cookie.py
Sale con codigo 0 solo si las tres cookies llevan Secure y las de sesion HttpOnly.
"""
from __future__ import annotations

import json
import sys
import urllib.error
import urllib.request

API = "https://sbvia-appweb.onrender.com"
CUENTA = {"email": "conductor@sbvia.com", "password": "password123"}
TIMEOUT = 90

# nombre de cookie -> banderas exigidas
EXIGIDAS = {
    "XSRF-TOKEN": {"Secure"},
    "accessToken": {"Secure", "HttpOnly"},
    "refreshToken": {"Secure", "HttpOnly"},
}


def main() -> int:
    req = urllib.request.Request(
        API + "/api/auth/login", method="POST",
        data=json.dumps(CUENTA).encode(),
        headers={"Content-Type": "application/json", "User-Agent": "SBVIA-cookie/1.0"})

    try:
        with urllib.request.urlopen(req, timeout=TIMEOUT) as r:
            crudo = r.headers.get_all("Set-Cookie") or []
    except urllib.error.HTTPError as e:
        print(f"FALLA: el despliegue respondio {e.code} al iniciar sesion", file=sys.stderr)
        return 1
    except Exception as e:
        print(f"FALLA: sin respuesta del despliegue ({type(e).__name__})", file=sys.stderr)
        return 1

    cookies = {}
    for linea in crudo:
        nombre = linea.split("=", 1)[0].strip()
        cookies[nombre] = linea

    print(f"Cookies emitidas por {API}: {', '.join(cookies) or 'ninguna'}")
    fallos = []
    for nombre, exigidas in EXIGIDAS.items():
        linea = cookies.get(nombre)
        if linea is None:
            print(f"  {nombre:14} AUSENTE")
            fallos.append(f"no se emitio la cookie {nombre}")
            continue
        presentes = {b for b in exigidas if f"; {b}" in linea or linea.rstrip().endswith(b)}
        faltan = exigidas - presentes
        print(f"  {nombre:14} {'OK' if not faltan else 'FALTA ' + ', '.join(sorted(faltan))}")
        if faltan:
            fallos.append(f"{nombre} sin {' y '.join(sorted(faltan))}")

    print("=" * 60)
    if fallos:
        print(f"COOKIES FALLIDAS: {len(fallos)}")
        for f in fallos:
            print("  -", f)
        return 1
    print("COOKIES OK: Secure en las tres, HttpOnly en las de sesion")
    return 0


if __name__ == "__main__":
    sys.exit(main())
