#!/usr/bin/env python3
"""Pruebas de mutacion sobre el verificador de integridad de mediciones.

Aplica una manipulacion, ejecuta `verify_integridad_mediciones.py` y comprueba
que el verificador FALLA. Restaura siempre el archivo original, incluso si algo
sale mal.

Uso:  python scripts/test_mutaciones_integridad.py
Sale con codigo 0 solo si el verificador detecta TODAS las mutaciones.
"""
from __future__ import annotations

import json
import subprocess
import sys
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent
VERIFICADOR = RAIZ / "scripts" / "verify_integridad_mediciones.py"

LH_MOBILE = sorted((RAIZ / "docs/mediciones/lighthouse/mobile").glob("*.report.json"))[0]
LH_MANIFEST = RAIZ / "docs/mediciones/lighthouse/mobile/manifest.json"
TEX = RAIZ / "docs/informe-final.tex"
PDF = RAIZ / "docs/informe-final.pdf"
JAVA = RAIZ / "backend/src/main/java/com/sbvia/backend/controller/BackupController.java"
TS_MODEL = RAIZ / "frontend/src/app/features/backups/backup.model.ts"


def corre() -> int:
    # UTF-8 explicito, por la misma razon que en el verificador del expediente:
    # en Windows text=True usa la codificacion del sistema y la salida con
    # acentos vuelve rota.
    r = subprocess.run([sys.executable, str(VERIFICADOR)],
                       capture_output=True, text=True, encoding="utf-8",
                       errors="replace", cwd=str(RAIZ))
    return r.returncode


def muta_texto(t: str) -> str:
    return t


MUTACIONES = [
    ("Lighthouse apunta a localhost", LH_MOBILE, "texto",
     lambda t: t.replace("sbvia-frontend.onrender.com", "localhost:4201")),

    ("Puntuacion de accesibilidad falseada", LH_MOBILE, "json",
     lambda d: d["categories"]["accessibility"].update({"score": 0.91})),

    ("Auditoria de accesibilidad marcada como fallida", LH_MOBILE, "json",
     lambda d: d["audits"]["color-contrast"].update({"score": 0})),

    ("Resumen del manifiesto falseado", LH_MANIFEST, "json",
     lambda d: d[0]["summary"].update({"accessibility": 0.5})),

    ("Corrida borrada del arbol de trabajo", LH_MOBILE, "borrar", None),

    ("Cifra de cobertura alterada en el informe", TEX, "texto",
     lambda t: t.replace("79.73", "85.00")),

    ("@CrossOrigin(\"*\") anadido", JAVA, "texto",
     lambda t: t.replace("public class BackupController {",
                         '@CrossOrigin("*")\npublic class BackupController {')),

    ("Campo del contrato roto en el frontend", TS_MODEL, "texto",
     lambda t: t.replace("sizeBytes?: number;", "bytesSize?: number;")),

    ("PDF del informe sustituido por un archivo de un byte", PDF, "bytes",
     lambda b: b"x"),
]

print("=" * 70)
print("  PRUEBAS DE MUTACION - verificador de integridad de mediciones")
print("=" * 70)

if corre() != 0:
    print("ABORTA: el verificador no pasa antes de mutar.")
    sys.exit(1)
print("Base: el verificador pasa sin mutaciones. OK\n")

detectadas, no_detectadas = 0, []
for nombre, archivo, modo, mutar in MUTACIONES:
    original = archivo.read_bytes()
    try:
        if modo == "texto":
            archivo.write_text(mutar(original.decode("utf-8")), encoding="utf-8")
        elif modo == "bytes":
            archivo.write_bytes(mutar(original))
        elif modo == "json":
            d = json.loads(original.decode("utf-8"))
            mutar(d)
            archivo.write_text(json.dumps(d, ensure_ascii=False), encoding="utf-8")
        elif modo == "borrar":
            archivo.unlink()

        if modo != "borrar" and archivo.read_bytes() == original:
            print(f"  OMITE   {nombre}: la mutacion no cambio el archivo")
            continue

        if corre() != 0:
            print(f"  DETECTA  {nombre}")
            detectadas += 1
        else:
            print(f"  SOBREVIVE {nombre}  <-- el verificador no la ve")
            no_detectadas.append(nombre)
    finally:
        archivo.write_bytes(original)

if corre() != 0:
    print("\nATENCION: tras restaurar, el verificador no pasa. Revisar el arbol.")
    sys.exit(1)

print("\n" + "=" * 70)
print(f"Mutaciones probadas   : {len(MUTACIONES)}")
print(f"Detectadas            : {detectadas}")
print(f"Sobrevivientes        : {len(no_detectadas)}")
for n in no_detectadas:
    print(f"  - {n}")
print("Arbol restaurado y verificador en verde.")
sys.exit(0 if not no_detectadas else 1)
