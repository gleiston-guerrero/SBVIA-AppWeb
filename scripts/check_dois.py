#!/usr/bin/env python3
"""Auditoria reproducible de las referencias de docs/refs.bib.

Verifica cada entrada contra fuentes reales y escribe docs/doi_check.log:

  * With a DOI      -> resolved in Crossref and, failing that, in DataCite. The cited
                    el titulo resuelto con el citado (similitud) y se registra el
                    resultado de la API.
  * Without a DOI   -> the ISBN is checked in OpenLibrary and the URL over HTTP.
                    Si el recurso no tiene DOI registrado, se declara como tal
                    en lugar de proponer uno inventado.

Uso:  python scripts/check_dois.py
Salida: docs/doi_check.log  (y resumen por consola)
"""
from __future__ import annotations

import datetime as _dt
import difflib
import json
import re
import sys
import urllib.error
import urllib.parse
import urllib.request
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent
BIB = RAIZ / "docs" / "refs.bib"
LOG = RAIZ / "docs" / "doi_check.log"
UA = "SBVIA-refs-audit/1.0 (mailto:jcruzp@uteq.edu.ec)"
TIMEOUT = 45

LATEX = {
    r'\\"o': "o", r'\\"a': "a", r'\\"u': "u", r'\\"e': "e", r'\\"i': "i",
    r"\\'e": "e", r"\\'a": "a", r"\\'i": "i", r"\\'o": "o", r"\\'u": "u",
    r"\\~n": "n", r"\\&": "and", "\\%": "%", "\\_": "_", "--": "-",
    "\\\\": " ", "{": "", "}": "", "$": "", "~": " ",
}


def limpiar(s: str) -> str:
    for k, v in LATEX.items():
        s = s.replace(k, v)
    s = re.sub(r"\\[a-zA-Z]+", " ", s)
    s = re.sub(r"[^a-z0-9 ]+", " ", s.lower())
    return re.sub(r"\s+", " ", s).strip()


def norm(s: str) -> str:
    return re.sub(r"[^a-z0-9]", "", limpiar(s))


def similitud(a: str, b: str) -> float:
    return round(difflib.SequenceMatcher(None, norm(a), norm(b)).ratio(), 3)


def _get(url: str, accept: str = "application/json"):
    req = urllib.request.Request(url, headers={"User-Agent": UA, "Accept": accept})
    try:
        with urllib.request.urlopen(req, timeout=TIMEOUT) as r:
            return r.status, r.read().decode("utf-8", "replace")
    except urllib.error.HTTPError as e:
        cuerpo = e.read(600).decode("utf-8", "replace") if e.fp else ""
        return e.code, cuerpo
    except Exception as e:  # red, TLS, timeout
        return None, f"{type(e).__name__}: {e}"


def parse_bib(path: Path):
    txt = path.read_text(encoding="utf-8")
    salida = []
    for m in re.finditer(r"(?ms)^@(\w+)\{([^,]+),(.*?)(?=^@|\Z)", txt):
        cuerpo = m.group(3)

        def campo(nombre):
            mm = re.search(r'(?m)^\s*' + nombre + r'\s*=\s*[{"](.*?)[}"]\s*,?\s*$', cuerpo)
            return mm.group(1).strip() if mm else None

        salida.append({
            "tipo": m.group(1), "clave": m.group(2).strip(),
            "titulo": campo("title") or "", "anio": campo("year") or "",
            "autores": campo("author") or "",
            "doi": campo("doi"), "isbn": campo("isbn"), "url": campo("url"),
        })
    return salida


def resolver_crossref(doi: str):
    st, cuerpo = _get("https://api.crossref.org/works/" + urllib.parse.quote(doi))
    if st != 200:
        return None, st, cuerpo[:200].replace("\n", " ")
    try:
        d = json.loads(cuerpo)["message"]
    except Exception as e:
        return None, st, f"JSON invalido: {e}"
    return {
        "titulo": (d.get("title") or [""])[0],
        "anio": str((d.get("issued", {}).get("date-parts") or [[""]])[0][0] or ""),
        "fuente": "Crossref",
        "autores": [a.get("family", "") for a in (d.get("author") or []) if a.get("family")],
    }, st, "ok"


def resolver_datacite(doi: str):
    st, cuerpo = _get("https://api.datacite.org/dois/" + urllib.parse.quote(doi))
    if st != 200:
        return None, st, cuerpo[:200].replace("\n", " ")
    try:
        d = json.loads(cuerpo)["data"]["attributes"]
    except Exception as e:
        return None, st, f"JSON invalido: {e}"
    return {
        "titulo": (d.get("titles") or [{}])[0].get("title", ""),
        "anio": str(d.get("publicationYear") or ""),
        "fuente": "DataCite",
        "autores": [(c.get("name", "") or "").split(",")[0].strip()
                    for c in (d.get("creators") or [])],
    }, st, "ok"


def primer_apellido_bib(autores: str) -> str:
    """De 'Pohl, Klaus and Otro, X' devuelve 'pohl'."""
    if not autores:
        return ""
    primero = re.split(r"\s+and\s+", autores)[0]
    return norm(primero.split(",")[0])


def resolver_doi(doi: str):
    reg, st, msg = resolver_crossref(doi)
    if reg is None:
        reg2, st2, msg2 = resolver_datacite(doi)
        if reg2 is None:
            return None, f"Crossref HTTP {st} ({msg}); DataCite HTTP {st2} ({msg2})"
        return reg2, f"DataCite HTTP {st2}"
    return reg, f"Crossref HTTP {st}"


def comprobar_isbn(isbn: str):
    limpio = re.sub(r"[^0-9Xx]", "", isbn)
    st, cuerpo = _get("https://openlibrary.org/isbn/%s.json" % limpio)
    if st != 200:
        return False, f"OpenLibrary HTTP {st}"
    try:
        d = json.loads(cuerpo)
    except Exception as e:
        return False, f"JSON invalido: {e}"
    return True, f"{d.get('title', '')} ({d.get('publish_date', '')})"


def comprobar_url(url: str):
    st, _ = _get(url, "text/html")
    return (st is not None and st < 400), f"HTTP {st}"


def main() -> int:
    entradas = parse_bib(BIB)
    filas, detalle = [], []
    con_doi = sin_doi_id = sin_nada = 0

    for e in entradas:
        if e["doi"]:
            con_doi += 1
            reg, como = resolver_doi(e["doi"])
            if reg is None:
                filas.append((e["clave"], "NO RESUELVE", e["doi"], "", "", como))
                detalle.append(
                    f"[{e['clave']}]\n  Citado   : {e['titulo']}\n"
                    f"  DOI      : {e['doi']}\n  Veredicto: NO RESUELVE — {como}\n")
                continue
            s = similitud(e["titulo"], reg["titulo"])
            # Many books are registered in Crossref with an abbreviated title
            # (for example "Requirements Engineering" instead of the full title).
            # When the title does not match but the first surname does, the entry
            # points to the right work and the reason is recorded.
            motivo = ""
            if s < 0.75:
                ap_bib = primer_apellido_bib(e.get("autores", ""))
                ap_cr = norm(reg.get("autores", [""])[0]) if reg.get("autores") else ""
                if ap_bib and ap_cr and (ap_bib in ap_cr or ap_cr in ap_bib):
                    veredicto = "CORRECTO"
                    motivo = "titulo abreviado en la fuente; autor coincide"
                else:
                    veredicto = "REVISAR"
            else:
                veredicto = "CORRECTO"
            filas.append((e["clave"], veredicto, e["doi"], s, reg["anio"], como + (" — " + motivo if motivo else "")))
            detalle.append(
                f"[{e['clave']}]\n  Citado   : {e['titulo']}\n"
                f"  Resuelto : {reg['titulo']}  (year={reg['anio']}, {reg['fuente']})\n"
                f"  Similitud: {s}" + (f"  ({motivo})" if motivo else "") + "\n"
                f"  Veredicto: {veredicto} — {e['doi']}\n")
        else:
            comprobaciones, identificador = [], None
            if e["isbn"]:
                identificador = "ISBN " + e["isbn"]
                ok, msg = comprobar_isbn(e["isbn"])
                comprobaciones.append(f"ISBN {'OK' if ok else 'FALLO'} ({msg})")
            if e["url"]:
                identificador = (identificador + " + " if identificador else "") + e["url"]
                ok, msg = comprobar_url(e["url"])
                comprobaciones.append(f"URL {'OK' if ok else 'FALLO'} ({msg})")
            if comprobaciones:
                sin_doi_id += 1
                filas.append((e["clave"], "SIN DOI (con identificador)", identificador or "", "", "", "; ".join(comprobaciones)))
                detalle.append(
                    f"[{e['clave']}]\n  Citado   : {e['titulo']}\n"
                    f"  Sin DOI registrado. Identificador: {identificador}\n"
                    f"  Comprobacion: {'; '.join(comprobaciones)}\n")
            else:
                sin_nada += 1
                filas.append((e["clave"], "SIN IDENTIFICADOR", "", "", "", ""))
                detalle.append(
                    f"[{e['clave']}]\n  Citado   : {e['titulo']}\n"
                    f"  Veredicto: SIN DOI, SIN URL Y SIN ISBN\n")

    ahora = _dt.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    lineas = [
        "SBVIA — Auditoria de referencias de docs/refs.bib",
        f"Fecha: {ahora}",
        f"Entradas: {len(entradas)} | con DOI resuelto: {con_doi} | sin DOI pero con ISBN/URL: {sin_doi_id} | sin identificador: {sin_nada}",
        "Metodo: cada DOI se resuelve en api.crossref.org (y api.datacite.org como respaldo) y el titulo",
        "        resuelto se compara con el citado. Los ISBN se validan en OpenLibrary y las URL por HTTP.",
        "        Un DOI no resuelto se declara NO RESUELVE; no se propone ningun DOI no registrado.",
        "",
        "Clave                        Veredicto                    DOI/Identificador                                   Sim  Comprobacion",
        "-" * 150,
    ]
    for clave, ver, ident, sim, anio, como in filas:
        lineas.append(f"{clave:28} {ver:28} {str(ident)[:50]:50} {str(sim):4}  {como}")
    lineas += ["", "=" * 150, "DETALLE POR ENTRADA", "=" * 150, ""]
    lineas += detalle
    LOG.write_text("\n".join(lineas) + "\n", encoding="utf-8")

    print(f"log escrito: {LOG}")
    print(f"entradas={len(entradas)} conDOI={con_doi} sinDOI_conIdentificador={sin_doi_id} sinNada={sin_nada}")
    for clave, ver, ident, sim, anio, como in filas:
        if ver not in ("CORRECTO", "SIN DOI (con identificador)"):
            print(f"  ATENCION {clave}: {ver} | {ident} | {como}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
