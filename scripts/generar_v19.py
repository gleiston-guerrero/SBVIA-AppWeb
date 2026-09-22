#!/usr/bin/env python3
"""Genera la migracion V19 que renombra el esquema al ingles.

La migracion hace tres cosas y ninguna mas:
  1. Renombra las 26 tablas.
  2. Renombra sus 192 columnas.
  3. Recrea las funciones y procedimientos almacenados, porque sus cuerpos
     referencian los nombres viejos y dejarian de funcionar.

Las migraciones V1 a V16 no se tocan: ya estan aplicadas y Flyway valida su
checksum, de modo que modificarlas impediria arrancar el backend.
"""
from __future__ import annotations

import json
import re
import subprocess
import sys
from pathlib import Path

RAIZ = Path(r"C:\Users\jessi\OneDrive\Desktop\SBVIA-AppWeb")
DESTINO = RAIZ / "backend" / "src" / "main" / "resources" / "db" / "current-migration" / \
    "V19__renombrar_esquema_ingles.sql"


def psql(sql: str, separador: str = "|") -> list[str]:
    r = subprocess.run(
        ["docker", "exec", "sbvia-postgres", "psql", "-U", "sbvia_user", "-d", "sbvia_db",
         "-t", "-A", "-F", separador, "-c", sql],
        capture_output=True, text=True, encoding="utf-8", errors="replace")
    return [l for l in r.stdout.strip().splitlines() if l]


# V17 renombro estas dos tablas antes que nosotros; los procedimientos antiguos
# siguen refiriendose a los nombres de antes, asi que tambien hay que traducirlos.
LEGADO_V17 = {
    "bitacora_auditoria": "audit_log",
    "nombre_tabla": "table_name", "operacion": "operation",
    "usuario_db": "db_user", "usuario_app": "app_user",
    "datos_anteriores": "previous_data", "datos_nuevos": "new_data",
    "respaldo": "backup", "id_respaldo": "id",
    "nombre_archivo": "file_name", "tamanio_bytes": "size_bytes",
    "fecha_programada": "scheduled_date", "modalidad": "mode",
}


def mapa_de_palabras(mapa: dict) -> list[tuple[str, str]]:
    """Pares (viejo, nuevo) para sustituir dentro de cuerpos SQL.

    Se ordenan de mas largo a mas corto para que `id_estado_simulacion` no se
    sustituya antes que `id_estado_simulacion` mismo por un prefijo mas corto.
    """
    pares = []
    for tabla, d in mapa.items():
        pares.append((tabla, d["nueva"]))
        for viejo, nuevo in d["columnas"].items():
            if viejo != nuevo:
                pares.append((viejo, nuevo))
    # Algunos cuerpos citan las tablas en camelCase sin guiones bajos
    # ("MetricaDesempeno"). Se anade la forma pegada de cada nombre.
    for tabla, d in mapa.items():
        pares.append((tabla.replace("_", ""), d["nueva"]))
    pares.extend(LEGADO_V17.items())
    pares.sort(key=lambda p: -len(p[0]))
    return pares


def sustituye(texto: str, pares: list[tuple[str, str]]) -> str:
    for viejo, nuevo in pares:
        # Sin distinguir mayusculas: hay funciones que escriben "Usuario" o
        # "id_Simulacion", y esos identificadores no existen en la base real.
        texto = re.sub(rf"(?<![\w]){re.escape(viejo)}(?![\w])", nuevo, texto, flags=re.I)
    return texto


def main() -> int:
    mapa = json.loads((RAIZ / "scripts" / "mapa_renombrado.json").read_text(encoding="utf-8"))

    lineas = [
        "-- Renombra al ingles las 26 tablas que quedaban en espanol, sus columnas",
        "-- y los objetos dependientes. Generado desde el esquema real por",
        "-- scripts/generar_v19.py; no editar a mano.",
        "",
        "SET client_min_messages TO WARNING;",
        "",
    ]

    # ---- 1. tablas y columnas
    for tabla, d in mapa.items():
        nueva = d["nueva"]
        if tabla != nueva:
            lineas.append(f"-- {tabla} -> {nueva}")
            lineas.append(f'ALTER TABLE "{tabla}" RENAME TO "{nueva}";')
        elif any(v != n for v, n in d["columnas"].items()):
            lineas.append(f"-- {tabla} (solo columnas)")
        for viejo, nuevo in d["columnas"].items():
            if viejo != nuevo:
                lineas.append(f'ALTER TABLE "{nueva}" RENAME COLUMN "{viejo}" TO "{nuevo}";')
        lineas.append("")

    # ---- 2. secuencias
    secuencias = psql("SELECT sequencename FROM pg_sequences WHERE schemaname='public' ORDER BY sequencename;")
    if secuencias:
        lineas.append("-- Secuencias de las claves primarias")
        for s in secuencias:
            nuevo = s
            for tabla, d in sorted(mapa.items(), key=lambda x: -len(x[0])):
                if s.startswith(tabla + "_"):
                    nuevo = s.replace(tabla + "_", d["nueva"] + "_", 1)
                    for viejo, n2 in d["columnas"].items():
                        if viejo != n2 and viejo in nuevo:
                            nuevo = nuevo.replace(viejo, n2)
                    break
            if nuevo != s:
                lineas.append(f'ALTER SEQUENCE "{s}" RENAME TO "{nuevo}";')
        lineas.append("")

    # ---- 3. funciones y procedimientos: se recrean con los nombres nuevos
    # Las definiciones se toman de los ARCHIVOS fuente, no de la base: volcarlas
    # con pg_get_functiondef obliga a pasar texto multilinea por el shell y se
    # rompe. De V1 y V3 solo interesan los bloques de funcion, porque ademas crean
    # tablas y reejecutarlas enteras fallaria.
    pares = mapa_de_palabras(mapa)
    migraciones = RAIZ / "backend" / "src" / "main" / "resources" / "db" / "current-migration"
    # El cierre puede ser `$$;` o `$$ LANGUAGE plpgsql;`. Exigir lo primero hacia
    # que unas funciones se tragaran a las siguientes.
    PATRON = re.compile(r"CREATE OR REPLACE (?:FUNCTION|PROCEDURE)\b.*?\n\$\w*\$[^;]*;", re.S)
    definiciones = []
    for nombre in ["V1__modelo_actual.sql", "V3__auditoria_operaciones.sql",
                   "V14__procedimientos_almacenados.sql", "V18__conectar_procedimientos_sp.sql"]:
        texto = (migraciones / nombre).read_text(encoding="utf-8")
        encontradas = PATRON.findall(texto)
        definiciones.extend(sustituye(f, pares) for f in encontradas)
    lineas.append(f"-- Recreacion de {len(definiciones)} funciones y procedimientos")
    for definicion in definiciones:
        lineas.append("")
        # A toda rutina que no sea de trigger se le hace DROP antes: renombrar las
        # columnas OUT cambia el tipo de retorno y CREATE OR REPLACE lo rechaza. Las
        # de trigger se dejan, porque el DROP se llevaria el disparador que las usa.
        if not re.search(r"RETURNS\s+TRIGGER", definicion, re.I):
            m = re.search(r"(?:FUNCTION|PROCEDURE)\s+(?:public\.)?(\w+)\s*\(([^)]*)\)", definicion, re.S)
            if m:
                nombre, params = m.group(1), m.group(2)
                entradas = []
                for trozo in params.split(","):
                    trozo = trozo.strip()
                    if not trozo or trozo.upper().startswith("OUT "):
                        continue
                    tipo = trozo.split()[-1]          # el ultimo token es el tipo
                    tipo = re.sub(r"[^\w\s()]", "", tipo).strip()
                    if tipo and not tipo.isdigit():
                        entradas.append(tipo.lower())
                lineas.append(f"DROP ROUTINE IF EXISTS public.{nombre}({', '.join(entradas)});")
                lineas.append("")
        lineas.append(sustituye(definicion, pares).rstrip(";") + ";")

    lineas.append("")
    DESTINO.write_text("\n".join(lineas), encoding="utf-8")
    print(f"V19 generada: {DESTINO.name}")
    print(f"  tablas: {len(mapa)}")
    print(f"  columnas: {sum(len(d['columnas']) for d in mapa.values())}")
    print(f"  secuencias: {len(secuencias)}")
    print(f"  funciones/procedimientos recreados: {len(definiciones)}")
    print(f"  lineas totales: {len(lineas)}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
