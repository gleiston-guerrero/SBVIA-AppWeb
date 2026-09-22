#!/usr/bin/env python3
"""Consultas locales del expediente, portables.

Las ordenes originales usaban `grep`, `ls` y `awk`, que no existen en todos los
entornos: en Windows fallaban por herramienta ausente y el verificador no podia
comprobarse mas que en Linux. Cada subcomando demuestra exactamente el mismo
hecho que la orden que sustituye, sin depender de utilidades del sistema.

Uso:  python scripts/expediente_consultas.py <consulta>
"""
from __future__ import annotations

import re
import sys
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent


def _lineas(rel: str) -> list[str]:
    return (RAIZ / rel).read_text(encoding="utf-8", errors="replace").splitlines()


def requestparam() -> int:
    """Lineas con @RequestParam en AuditController."""
    for n, l in enumerate(_lineas("backend/src/main/java/com/sbvia/backend/controller/AuditController.java"), 1):
        if "@RequestParam" in l:
            print(l.strip())
    return 0


def tokencookie() -> int:
    """Declaracion de tokenCookie y las cinco lineas siguientes."""
    lineas = _lineas("backend/src/main/java/com/sbvia/backend/controller/AuthController.java")
    for i, l in enumerate(lineas):
        if "private ResponseCookie tokenCookie(" in l:
            for x in lineas[i:i + 6]:
                print(x.rstrip())
            return 0
    print("no encontrado", file=sys.stderr)
    return 1


def secure() -> int:
    """Lineas de application-prod.yml con 'secure', con su numero."""
    for n, l in enumerate(_lineas("backend/src/main/resources/application-prod.yml"), 1):
        if "secure" in l:
            print(f"{n}:{l}")
    return 0


def pdf_size() -> int:
    """Tamano del informe final en bytes."""
    p = RAIZ / "docs" / "informe-final.pdf"
    if not p.exists():
        print("no existe", file=sys.stderr)
        return 1
    print(f"{p.stat().st_size} bytes")
    return 0


def doi_log_count() -> int:
    """Numero de entradas del registro de auditoria de DOI."""
    print(sum(1 for l in _lineas("docs/doi_check.log") if l.startswith("[")))
    return 0


def gqm_count() -> int:
    """Numero de objetivos GQM citados en el informe."""
    texto = (RAIZ / "docs" / "informe-final.tex").read_text(encoding="utf-8", errors="replace")
    print(len(re.findall(r"textbf\{G[0-9]", texto)))
    return 0


CONSULTAS = {
    "requestparam": requestparam,
    "tokencookie": tokencookie,
    "secure": secure,
    "pdf-size": pdf_size,
    "doi-log-count": doi_log_count,
    "gqm-count": gqm_count,
}


def main() -> int:
    if len(sys.argv) != 2 or sys.argv[1] not in CONSULTAS:
        print("uso: expediente_consultas.py <" + "|".join(CONSULTAS) + ">", file=sys.stderr)
        return 2
    return CONSULTAS[sys.argv[1]]()


if __name__ == "__main__":
    sys.exit(main())
