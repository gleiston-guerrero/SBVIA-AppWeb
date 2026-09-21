#!/usr/bin/env python3
"""Verificacion de integridad de las mediciones del proyecto SBVIA.

Comprueba el CONTENIDO de los artefactos de medicion y su presencia en el ARBOL
DE TRABAJO (no en el indice de git), que es lo que el verificador del expediente
no hacia: `git ls-files` sigue listando un archivo borrado del disco, y ninguna
orden inspeccionaba el interior de las mediciones.

Cubre, entre otras, estas manipulaciones que antes sobrevivian:
  - apuntar las corridas de Lighthouse a localhost
  - falsear una puntuacion de accesibilidad
  - alterar las cifras de cobertura del informe
  - anadir @CrossOrigin("*")
  - borrar corridas del arbol de trabajo
  - sustituir el PDF del informe por un archivo de un byte
  - romper un campo del contrato backend-frontend

Uso:  python scripts/verify_integridad_mediciones.py
Sale con codigo 0 solo si TODAS las comprobaciones pasan.
"""
from __future__ import annotations

import csv
import json
import re
import sys
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent
PROD = "sbvia-frontend.onrender.com"

fallos: list[str] = []
total = 0


def check(descripcion: str, ok: bool, detalle: str = "") -> None:
    global total
    total += 1
    if ok:
        print(f"  OK    {descripcion}")
    else:
        print(f"  FALLA {descripcion}" + (f" -> {detalle}" if detalle else ""))
        fallos.append(descripcion)


def ruta(*partes: str) -> Path:
    return RAIZ.joinpath(*partes)


# ---------------------------------------------------------------- Lighthouse
LH = ruta("docs", "mediciones", "lighthouse")
CORRIDAS = []
for perfil in ("mobile", "desktop"):
    CORRIDAS += sorted((LH / perfil).glob("*.report.json"))

print("\n[1] Corridas de Lighthouse")

check("existen 3 corridas moviles y 3 de escritorio en el arbol de trabajo",
      len(CORRIDAS) == 6,
      f"encontradas {len(CORRIDAS)}")

for f in CORRIDAS:
    d = json.loads(f.read_text(encoding="utf-8"))
    url = d.get("finalUrl", "") or d.get("requestedUrl", "")
    check(f"{f.name}: apunta al despliegue publico",
          PROD in url and "localhost" not in url,
          url[:80])

for f in CORRIDAS:
    d = json.loads(f.read_text(encoding="utf-8"))
    acc = d.get("categories", {}).get("accessibility", {}).get("score")
    check(f"{f.name}: accesibilidad = 1,00", acc == 1, f"score={acc}")

for f in CORRIDAS:
    d = json.loads(f.read_text(encoding="utf-8"))
    fallidas = [k for k, a in d.get("audits", {}).items()
                if a.get("scoreDisplayMode") == "binary" and a.get("score") == 0]
    check(f"{f.name}: cero auditorias binarias fallidas", not fallidas,
          ", ".join(fallidas[:4]))

print("\n[2] Manifiestos de Lighthouse")
for perfil in ("mobile", "desktop"):
    man = LH / perfil / "manifest.json"
    if not man.exists():
        check(f"manifest {perfil} presente", False, "no existe")
        continue
    entradas = json.loads(man.read_text(encoding="utf-8"))
    check(f"manifest {perfil}: 3 corridas declaradas", len(entradas) == 3,
          f"{len(entradas)}")
    for e in entradas:
        resumen = e.get("summary", {}).get("accessibility")
        json_path = RAIZ / Path(e.get("jsonPath", "").replace("\\", "/"))
        real = None
        objetivo = LH / perfil / Path(str(e.get("jsonPath", "")).replace("\\", "/")).name
        if objetivo.exists():
            real = json.loads(objetivo.read_text(encoding="utf-8")).get(
                "categories", {}).get("accessibility", {}).get("score")
        check(f"manifest {perfil}: resumen coincide con el informe ({objetivo.name})",
              real is not None and resumen == real, f"manifest={resumen} informe={real}")

print("\n[3] HTML de las corridas en el arbol de trabajo")
htmls = [f.with_suffix(".html") for f in CORRIDAS]
faltan = [h.name for h in htmls if not h.exists()]
check("los 6 HTML acompanan a los 6 JSON", not faltan, ", ".join(faltan))

print("\n[4] Cobertura JaCoCo")
csv_path = ruta("docs", "mediciones", "jacoco", "coverage-summary.csv")
filas = [r for r in csv.DictReader(csv_path.open(encoding="utf-8-sig"))
         if r["package_name"].strip().upper() != "TOTAL"]
tl = sum(int(r["total_lines"]) for r in filas)
cl = sum(int(r["covered_lines"]) for r in filas)
tb = sum(int(r["total_branches"]) for r in filas)
cb = sum(int(r["covered_branches"]) for r in filas)
pct_l, pct_r = round(100 * cl / tl, 2), round(100 * cb / tb, 2)
check("el resumen de JaCoCo da 79,73 % de lineas y 70,41 % de ramas",
      (pct_l, pct_r) == (79.73, 70.41), f"calculado {pct_l} / {pct_r}")

tex = ruta("docs", "informe-final.tex").read_text(encoding="utf-8")
check("el informe cita esas mismas cifras",
      f"{pct_l:.2f}" in tex and f"{pct_r:.2f}" in tex,
      f"buscadas {pct_l:.2f} y {pct_r:.2f}")

# Contadores globales del XML, como control cruzado del CSV.
xml = ruta("docs", "mediciones", "jacoco", "jacoco.xml").read_text(encoding="utf-8")
m = re.findall(r'<counter type="LINE" missed="(\d+)" covered="(\d+)"/>', xml)
if m:
    mis, cov = int(m[-1][0]), int(m[-1][1])
    check("el XML de JaCoCo coincide con el CSV",
          cov == cl and (cov + mis) == tl, f"xml {cov}/{cov+mis} vs csv {cl}/{tl}")

print("\n[5] Configuracion de seguridad")
java = list(ruta("backend", "src", "main", "java").rglob("*.java"))
malas = []
for f in java:
    t = f.read_text(encoding="utf-8", errors="replace")
    if re.search(r'@CrossOrigin\s*\(\s*(value\s*=\s*)?["\']\*["\']', t) or \
       re.search(r'@CrossOrigin\s*\(\s*origins\s*=\s*["\']\*["\']', t):
        malas.append(f.name)
check('no hay @CrossOrigin("*") en el backend', not malas, ", ".join(malas))

prod = ruta("backend", "src", "main", "resources", "application-prod.yml")
ptxt = prod.read_text(encoding="utf-8") if prod.exists() else ""
check("application-prod.yml fija secure: true",
      re.search(r"secure:\s*true", ptxt) is not None)

print("\n[6] Informe final")
pdf = ruta("docs", "informe-final.pdf")
tam = pdf.stat().st_size if pdf.exists() else 0
cabecera = pdf.read_bytes()[:5] if pdf.exists() else b""
check("el PDF del informe existe y no es un archivo de prueba",
      tam > 1_000_000 and cabecera.startswith(b"%PDF-"),
      f"{tam} bytes")

print("\n[7] Contratos backend-frontend")


def campos_java(ruta_rel: str) -> set[str]:
    t = ruta(*ruta_rel.split("/")).read_text(encoding="utf-8")
    return set(re.findall(r"private\s+[\w<>\[\],. ]+?\s+(\w+)\s*;", t))


def campos_ts(ruta_rel: str, interfaz: str) -> set[str]:
    t = ruta(*ruta_rel.split("/")).read_text(encoding="utf-8")
    m = re.search(r"export\s+interface\s+" + interfaz + r"\s*\{(.*?)\}", t, re.DOTALL)
    return set(re.findall(r"^\s*(\w+)\??\s*:", m.group(1), re.MULTILINE)) if m else set()


b_back = campos_java("backend/src/main/java/com/sbvia/backend/model/Backup.java")
b_front = campos_ts("frontend/src/app/features/backups/backup.model.ts", "Respaldo")
check(f"contrato Backup: {len(b_back)} campos identicos en backend y frontend",
      b_back == b_front and len(b_back) > 0,
      f"backend={sorted(b_back - b_front)} frontend={sorted(b_front - b_back)}")

a_back = campos_java("backend/src/main/java/com/sbvia/backend/entity/AuditLog.java")
a_front = campos_ts("frontend/src/app/features/audit/audit.service.ts", "AuditLog")
check(f"contrato AuditLog: {len(a_back)} campos identicos en backend y frontend",
      a_back == a_front and len(a_back) > 0,
      f"backend={sorted(a_back - a_front)} frontend={sorted(a_front - a_back)}")

print("\n" + "=" * 62)
if fallos:
    print(f"INTEGRIDAD FALLIDA: {len(fallos)} de {total} comprobaciones")
    for f in fallos:
        print(f"  - {f}")
    sys.exit(1)
print(f"INTEGRIDAD OK: {total} comprobaciones superadas")
sys.exit(0)
