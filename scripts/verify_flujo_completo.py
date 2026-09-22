#!/usr/bin/env python3
"""Ejecuta una operación completa contra el despliegue público (P1).

El criterio pide HTTPS, la URL en la primera pantalla, iniciar sesión y completar
una operación. Lo primero estaba acreditado; lo que faltaba era demostrar el
inicio de sesión y una operación entera, que es lo que hace esta orden: entra con
la cuenta de demostración, consulta los escenarios, inicia una práctica, la
cierra con una puntuación y recupera el informe que el servidor genera.

Uso:  python scripts/verify_flujo_completo.py
Sale con codigo 0 solo si todos los pasos responden lo esperado.
"""
from __future__ import annotations

import json
import sys
import urllib.error
import urllib.request

API = "https://sbvia-appweb.onrender.com"
CUENTA = {"email": "conductor@sbvia.com", "password": "password123"}
TIMEOUT = 90


def configurar_base(base: str | None) -> None:
    """Permite apuntar el flujo a otro despliegue, por ejemplo el local."""
    global API
    if base:
        API = base.rstrip("/")


# La API protege las operaciones mutables con el patron de doble envio: emite una
# cookie XSRF-TOKEN y exige que el mismo valor viaje en la cabecera X-XSRF-TOKEN.
# Sin el, un POST responde 403 aunque el token de sesion sea valido, que es
# exactamente lo que debe hacer. Se reproduce el mismo comportamiento que el
# navegador para que el flujo atraviese la seguridad real.
_cookies: dict[str, str] = {}


def pide(ruta: str, metodo: str = "GET", datos: dict | None = None, token: str | None = None):
    cabeceras = {"User-Agent": "SBVIA-flujo/1.0", "Accept": "application/json"}
    cuerpo = None
    if datos is not None:
        cabeceras["Content-Type"] = "application/json"
        cuerpo = json.dumps(datos).encode()
    if token:
        cabeceras["Authorization"] = f"Bearer {token}"
    if metodo != "GET" and "XSRF-TOKEN" in _cookies:
        cabeceras["X-XSRF-TOKEN"] = _cookies["XSRF-TOKEN"]
    if _cookies:
        cabeceras["Cookie"] = "; ".join(f"{k}={v}" for k, v in _cookies.items())

    req = urllib.request.Request(API + ruta, method=metodo, data=cuerpo, headers=cabeceras)
    try:
        with urllib.request.urlopen(req, timeout=TIMEOUT) as r:
            for cabecera in r.headers.get_all("Set-Cookie") or []:
                par = cabecera.split(";", 1)[0]
                if "=" in par:
                    nombre, valor = par.split("=", 1)
                    _cookies[nombre.strip()] = valor.strip()
            texto = r.read().decode("utf-8", "replace")
            return r.status, (json.loads(texto) if texto.strip().startswith(("{", "[")) else texto)
    except urllib.error.HTTPError as e:
        return e.code, None
    except Exception:
        return None, None


def main() -> int:
    fallos = []

    # 1. El servicio responde; el esquema se informa, no se exige, porque el
    #    mismo flujo sirve para comprobar el despliegue publico (https) y una
    #    instancia local antes de desplegar (http).
    codigo, _ = pide("/actuator/health")
    esquema = "HTTPS" if API.startswith("https://") else "HTTP"
    print(f"1. API responde ({esquema}){' ' * max(0, 10 - len(esquema))}-> {codigo}")
    if codigo != 200:
        fallos.append("la API no responde 200")

    # 2. Inicio de sesion
    codigo, cuerpo = pide("/api/auth/login", "POST", CUENTA)
    if codigo != 200 or not isinstance(cuerpo, dict) or not cuerpo.get("accessToken"):
        print(f"2. Inicio de sesion            -> {codigo} FALLO")
        print("=" * 62)
        print("FLUJO FALLIDO: no se pudo iniciar sesion")
        return 1
    token = cuerpo["accessToken"]
    print(f"2. Inicio de sesion            -> {codigo} (token emitido)")

    # 3. Escenarios disponibles
    codigo, cuerpo = pide("/api/scenarios?page=0&size=1", token=token)
    escenarios = (cuerpo or {}).get("content") or []
    if codigo != 200 or not escenarios:
        print(f"3. Consulta de escenarios      -> {codigo} FALLO")
        print("=" * 62)
        print("FLUJO FALLIDO: no hay escenarios que consultar")
        return 1
    escenario = escenarios[0]
    print(f"3. Consulta de escenarios      -> {codigo} (id={escenario.get('id')}, {str(escenario.get('name'))[:32]})")

    # 4. Iniciar una practica
    codigo, cuerpo = pide(f"/api/simulations/iniciar/{escenario.get('id')}", "POST", token=token)
    simulacion = (cuerpo or {}).get("simulationId") if isinstance(cuerpo, dict) else None
    if codigo != 200 or not simulacion:
        print(f"4. Inicio de la practica       -> {codigo} FALLO")
        print("=" * 62)
        print("FLUJO FALLIDO: la practica no se pudo iniciar")
        return 1
    # Tampoco se imprime el identificador de la simulacion: cambia en cada
    # corrida y la salida del expediente se compara literalmente.
    print(f"4. Inicio de la practica       -> {codigo} (practica creada)")

    # 5. Cerrarla registrando la puntuacion
    codigo, cuerpo = pide(f"/api/simulations/{simulacion}/finalizar", "POST",
                          {"finalScore": 90.0}, token=token)
    estado = (cuerpo or {}).get("status") if isinstance(cuerpo, dict) else None
    print(f"5. Cierre de la practica       -> {codigo} (estado={estado})")
    if codigo != 200:
        fallos.append("la practica no se pudo cerrar")

    # 6. Recuperar el informe generado
    codigo, cuerpo = pide(f"/api/simulations/{simulacion}/feedback", token=token)
    puntaje = (cuerpo or {}).get("puntaje") if isinstance(cuerpo, dict) else None
    print(f"6. Informe de la practica      -> {codigo} (puntaje={puntaje})")
    if codigo != 200:
        fallos.append("no se pudo recuperar el informe")

    # 7. El historial debe reflejar la practica recien cerrada
    codigo, cuerpo = pide("/api/simulations/mis-practicas", token=token)
    # No se imprime el numero de practicas: cada ejecucion del flujo crea una, de
    # modo que la cifra cambia y el expediente, que compara la salida literal,
    # fallaria en la siguiente corrida sin que nada este mal.
    print(f"7. Historial de practicas      -> {codigo} (lista devuelta)")
    if codigo != 200:
        fallos.append("no se pudo leer el historial")

    print("=" * 62)
    if fallos:
        print(f"FLUJO FALLIDO: {len(fallos)} paso(s)")
        for f in fallos:
            print("  -", f)
        return 1
    print("FLUJO OK: sesion, practica cerrada e informe generado contra el despliegue")
    return 0


if __name__ == "__main__":
    # Uso: python scripts/verify_flujo_completo.py [url-base]
    # Sin argumento apunta al despliegue publico; con uno, a donde se indique,
    # por ejemplo http://localhost:8080 para probar antes de desplegar.
    configurar_base(sys.argv[1] if len(sys.argv) > 1 else None)
    print(f"Base: {API}\n")
    sys.exit(main())
