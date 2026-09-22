#!/usr/bin/env python3
"""Pasa las entidades JPA y las consultas nativas a los nombres en ingles.

Cada entidad declara su tabla con @Table; a partir de ahi, el renombrado de sus
columnas usa el mapa de ESA tabla. Es imprescindible: `id_escenario` se convierte
en `id` dentro de Scenario, pero en `scenario_id` dentro de Simulation.
"""
from __future__ import annotations

import json
import re
import sys
from pathlib import Path

RAIZ = Path(r"C:\Users\jessi\OneDrive\Desktop\SBVIA-AppWeb")
ENTIDADES = RAIZ / "backend" / "src" / "main" / "java" / "com" / "sbvia" / "backend" / "entity"
REPOS = RAIZ / "backend" / "src" / "main" / "java" / "com" / "sbvia" / "backend" / "repository"
MAPA = json.loads((RAIZ / "scripts" / "mapa_renombrado.json").read_text(encoding="utf-8"))


def procesa_entidades() -> tuple[int, int]:
    """Cambia los nombres dentro de @Table, @Column y @JoinColumn."""
    archivos = 0
    cambios = 0
    for f in sorted(ENTIDADES.glob("*.java")):
        texto = original = f.read_text(encoding="utf-8")
        m = re.search(r'@Table\(\s*name\s*=\s*"([^"]+)"', texto)
        if not m:
            continue
        tabla = m.group(1)
        if tabla not in MAPA:
            continue
        d = MAPA[tabla]

        def sust(match: re.Match) -> str:
            nombre = match.group(2)
            nuevo = d["columnas"].get(nombre)
            if nuevo is None or nuevo == nombre:
                return match.group(0)
            return f'{match.group(1)}"{nuevo}"'

        # Solo las cadenas de las anotaciones, nunca los nombres de campo de Java.
        texto = re.sub(r'(@(?:Column|JoinColumn)\([^)]*?name\s*=\s*)"([^"]+)"', sust, texto)
        texto = texto.replace(f'@Table(name = "{tabla}")', f'@Table(name = "{d["nueva"]}")')
        if texto != original:
            f.write_text(texto, encoding="utf-8")
            archivos += 1
            cambios += len(re.findall(r'@(?:Column|JoinColumn)\(', texto))

    # La tabla tambien hay que cambiarla cuando @Table lleva mas atributos.
    for f in sorted(ENTIDADES.glob("*.java")):
        texto = original = f.read_text(encoding="utf-8")
        m = re.search(r'@Table\(\s*name\s*=\s*"([^"]+)"', texto)
        if m and m.group(1) in MAPA:
            texto = re.sub(r'(@Table\(\s*name\s*=\s*)"[^"]+"', rf'\g<1>"{MAPA[m.group(1)]["nueva"]}"', texto)
            if texto != original:
                f.write_text(texto, encoding="utf-8")
    return archivos, cambios


def pares_globales() -> list[tuple[str, str]]:
    pares = []
    for tabla, d in MAPA.items():
        pares.append((tabla, d["nueva"]))
        pares.extend((v, n) for v, n in d["columnas"].items() if v != n)
    pares.sort(key=lambda p: -len(p[0]))
    return pares


def procesa_nativas() -> int:
    """Sustituye dentro del SQL de las consultas marcadas como nativas."""
    pares = pares_globales()
    tocados = 0
    for f in sorted(REPOS.glob("*.java")):
        texto = original = f.read_text(encoding="utf-8")
        for m in list(re.finditer(r'nativeQuery\s*=\s*true', texto)):
            pass
        def sust_bloque(match: re.Match) -> str:
            cuerpo = match.group(0)
            for viejo, nuevo in pares:
                cuerpo = re.sub(rf"(?<![\w]){re.escape(viejo)}(?![\w])", nuevo, cuerpo, flags=re.I)
            return cuerpo
        # Sustituye solo el contenido de @Query, que es donde vive el SQL.
        texto = re.sub(r'@Query\((?:[^()]|\([^()]*\))*\)', sust_bloque, texto, flags=re.S)
        if texto != original:
            f.write_text(texto, encoding="utf-8")
            tocados += 1
    return tocados


if __name__ == "__main__":
    a, c = procesa_entidades()
    n = procesa_nativas()
    print(f"entidades modificadas: {a}")
    print(f"repositorios con consulta tocada: {n}")
    # Comprobar que no queda ningun nombre viejo en las anotaciones
    restos = []
    for f in sorted(ENTIDADES.glob("*.java")):
        for i, l in enumerate(f.read_text(encoding="utf-8").splitlines(), 1):
            if re.search(r'@(?:Table|Column|JoinColumn)\(.*"[^"]*"', l):
                for m in re.finditer(r'"([^"]+)"', l):
                    v = m.group(1)
                    for tabla, d in MAPA.items():
                        if v in d["columnas"] and d["columnas"][v] != v:
                            restos.append(f"  {f.name}:{i} {v}")
                        if v == tabla:
                            restos.append(f"  {f.name}:{i} tabla {v}")
    print(f"nombres viejos que quedan en anotaciones: {len(restos)}")
    for r in restos[:10]:
        print(r)
