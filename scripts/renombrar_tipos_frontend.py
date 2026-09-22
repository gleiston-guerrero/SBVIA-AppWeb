#!/usr/bin/env python3
"""Renombra al ingles los tipos del frontend que quedan en espanol (P6).

La revision midio 9 tipos en espanol de 42 (21,4 %), por encima del 5 % que exige
el criterio. Son tipos de TypeScript, no campos del contrato JSON, asi que
renombrarlos no toca lo que viaja entre frontend y backend.

Antes de renombrar se comprueba que el nombre destino no exista ya, porque una
colision romperia la compilacion.
"""
from __future__ import annotations

import re
import sys
from pathlib import Path

R = Path(r"C:\Users\jessi\OneDrive\Desktop\SBVIA-AppWeb") / "frontend" / "src"

MAPA = {
    "Respaldo": "BackupRecord",
    "MetricasConduccion": "DrivingMetrics",
    "ResultadoConduccion": "DrivingResult",
    "EscenarioService": "ScenarioService",
    "VehiculoNpc": "NpcVehicle",
    "SemaforoEvento": "TrafficLightEvent",
    "UsuarioService": "UserService",
    "InformeIA": "AiReport",
    "EstadoJuego": "GameState",
}

archivos = [f for f in R.rglob("*.ts")]

# --- comprobacion previa: que el destino no exista ya
existentes = set()
TIPOS = re.compile(r"(?m)^\s*(?:export\s+)?(?:declare\s+)?(?:abstract\s+)?(?:class|interface|enum|type)\s+(\w+)")
for f in archivos:
    existentes.update(TIPOS.findall(f.read_text(encoding="utf-8", errors="replace")))

choques = [n for v, n in MAPA.items() if n in existentes]
if choques:
    print("COLISION: estos nombres ya existen, hay que elegir otro:", choques)
    sys.exit(1)
print("sin colisiones")

# --- renombrado, palabra completa, en todos los .ts del frontend
total = {}
for f in archivos:
    t = o = f.read_text(encoding="utf-8")
    for viejo, nuevo in MAPA.items():
        t2 = re.sub(rf"(?<![\w$]){re.escape(viejo)}(?![\w$])", nuevo, t)
        if t2 != t:
            total[viejo] = total.get(viejo, 0) + 1
            t = t2
    if t != o:
        f.write_text(t, encoding="utf-8")

print(f"tipos renombrados: {len(total)} de {len(MAPA)}")
for v, n in sorted(total.items()):
    print(f"  {v:24} -> {MAPA[v]:20} ({n} archivo/s)")
faltan = [v for v in MAPA if v not in total]
if faltan:
    print("  AVISO: sin apariciones ->", faltan)
