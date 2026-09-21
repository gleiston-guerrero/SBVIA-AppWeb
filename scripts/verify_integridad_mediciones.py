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

print("\n[8] Metadatos de citacion")
import re as _re

cit = ruta("CITATION.cff").read_text(encoding="utf-8")


def _campo(nombre: str) -> str:
    m = _re.search(r"(?m)^" + nombre + r":\s*[\"']?([^\"'\s]+)", cit)
    return m.group(1).strip() if m else ""


cff_schema = _campo("cff-version")
cff_prog = _campo("version")
check("CITATION.cff declara un cff-version de esquema valido",
      cff_schema in ("1.1.0", "1.2.0"), f"cff-version={cff_schema or 'ausente'}")
check("CITATION.cff declara la version del software, distinta del esquema",
      cff_prog not in ("", cff_schema), f"version={cff_prog or 'ausente'}")

# La version del SRS y la del software son lineas distintas: el SRS numera el
# documento y CITATION.cff la entrega de codigo. Lo que se comprueba es que ambas
# esten declaradas y bien formadas, no que coincidan.
srs = ruta("docs", "requisitos", "SRS-v1.3.0.tex")
if srs.exists():
    stxt = srs.read_text(encoding="utf-8")
    m_srs = _re.search(r"Versi[oó]n\s+(\d+\.\d+\.\d+)", stxt)
    check("el SRS declara una version de documento bien formada",
          m_srs is not None, "no se encontro 'Versión X.Y.Z'")
    check("CITATION.cff declara una version de software bien formada",
          _re.match(r"^\d+\.\d+\.\d+$", cff_prog) is not None,
          f"version={cff_prog or 'ausente'}")

print("\n[9] Documentos compilados y capturas")
for rel, minimo, etiqueta in (
    ("docs/informe-final.pdf", 1_000_000, "informe final"),
    ("docs/requisitos/SRS-v1.3.0.pdf", 100_000, "SRS v1.3.0"),
):
    f = ruta(*rel.split("/"))
    tam = f.stat().st_size if f.exists() else 0
    cab = f.read_bytes()[:5] if f.exists() else b""
    check(f"el PDF de {etiqueta} no es un archivo de prueba",
          tam > minimo and cab.startswith(b"%PDF-"), f"{tam} bytes")

for pat in ("pantalla-1*", "pantalla-2*", "pantalla-3*", "pantalla-4*"):
    for f in (ruta("docs", "diagramas")).glob(pat + ".png"):
        check(f"{f.name}: captura real y no un archivo de prueba",
              f.stat().st_size > 20_000, f"{f.stat().st_size} bytes")

print("\n[10] Analisis de usabilidad")
csv_sus = ruta("docs", "mediciones", "sus", "sus-raw-data-2026-09.csv")
if csv_sus.exists():
    import csv as _csv

    filas = list(_csv.DictReader(csv_sus.open(encoding="utf-8-sig")))
    puntajes = []
    for f in filas:
        r = [int(f[f"Q{i}"]) for i in range(1, 11)]
        pares = sum((v - 1) for v in r[0::2])
        impares = sum((5 - v) for v in r[1::2])
        puntajes.append((pares + impares) * 2.5)
    media = round(sum(puntajes) / len(puntajes), 2) if puntajes else 0
    check("el CSV reproduce la formula SUS de Brooke en las 15 filas",
          len(filas) == 15 and all(0 <= p <= 100 for p in puntajes),
          f"{len(filas)} filas")
    check("la media recalculada es 69,00 y cumple RNF-06",
          media == 69.00 and media >= 68, f"media={media}")

    for rel in ("docs/requisitos/SRS-v1.3.0.tex",
                "docs/mediciones/sus/sus-analysis-2026-09.md"):
        f = ruta(*rel.split("/"))
        if not f.exists():
            continue
        txt = f.read_text(encoding="utf-8")
        check(f"{f.name} declara la media recalculada y no otra",
              "69,00" in txt or "69.00" in txt, "no aparece 69,00")

    fechas = sorted({f.get("fecha", "") for f in filas})
    check("las sesiones del CSV caen en las fechas declaradas",
          all(d.startswith("2026-09-19") or d.startswith("2026-09-20") for d in fechas if d),
          ", ".join(fechas))

    ins = ruta("docs", "mediciones", "sus", "instrumento-sus.md")
    if ins.exists():
        itxt = ins.read_text(encoding="utf-8")
        marcadores = _re.findall(r"(?m)^\|\s*(\d+)\s*\|", itxt)
        check("el instrumento conserva los diez items del cuestionario SUS",
              [int(x) for x in marcadores[:10]] == list(range(1, 11)),
              f"items encontrados: {len(marcadores)}")

print("\n[11] Javadoc sin marcadores plantilla")
plantilla = 0
for f in (ruta("backend", "src", "main", "java")).rglob("*.java"):
    plantilla += len(_re.findall(r"<p>\w+ (?:class|interface|enum|record)\.</p>",
                                 f.read_text(encoding="utf-8")))
check("no queda ningun javadoc plantilla en el backend", plantilla == 0,
      f"{plantilla} encontrados")

print("\n[12] Nombres en espanol (P6)")
# Palabras inequivocamente espanolas del dominio. El escaneo cubre tipos y
# metodos, que es lo que mide el criterio.
_PAL = set("""
servicio simulacion propia propio usuario usuarios correo correos activo activos inactivo
regla reglas escenario escenarios practica practicas metrica metricas conduccion puntaje puntajes
respaldo respaldos auditoria informe informes sesion sesiones revocado expirado duplicado generado
exitosamente fallo limpia contador intentos umbral respuesta nula vigente busca actualiza persiste
mapea paginacion eliminar editar formatear dibujar senal senales velocidad velocidades
retroalimentacion todas todo toda todos nombre nombres apellido apellidos telefono telefonos
contrasena clave claves codigo codigos fecha fechas estado estados tipo tipos nivel niveles
riesgo riesgos nivelriesgo obtener crear listar iniciar finalizar guardar calcular validar
verificar comprobar generar convertir asignar mostrar cargar cerrar abrir contar filtrar ordenar
enviar recibir procesar ejecutar responder registrar cancelar confirmar seleccionar agregar
modificar cambiar reiniciar pausar reanudar detener pintar mover almacenar anterior siguiente
ultimo ultima primero primera nuevo nueva viejo conductor conductores vehiculo vehiculos carril
carriles colision colisiones semaforo semaforos distancia distancias tiempo tiempos duracion
infraccion infracciones desempeno rendimiento resultado resultados aprobado reprobado listado
registro registros bitacora mensaje mensajes aviso correcto incorrecto valido invalido vacio
disponible disponible habilitado cantidad numero porcentaje promedio
""".split())

# Nombres legitimos: impuestos por el esquema (derived queries sobre campos de
# entidad), por el contrato JSON, o porque el nombre de la prueba refleja a
# proposito el metodo de repositorio que verifica.
_PERMITIDOS = {
    "getFechaProgramada", "setFechaProgramada", "semaforosRespetados",
    "findByActivoTrue", "findByCodigo", "findFirstByActivoTrueOrderByIdVehiculoAsc",
    "findByActivoTrue_returnsOnlyActiveScenarios",
    "findByCorreoAndExistsByCorreo_resolveAuthentication",
    "findByUsuarioAndIdUsuario_listsUserSimulations",
}

_PALABRA = _re.compile(r"[A-Z]?[a-z]+|[A-Z]+(?![a-z])|[0-9]+")
_DECL_TIPO = _re.compile(
    r"(?m)^[ \t]*(?:@\w+(?:\([^)\n]*\))?[ \t]*\n?[ \t]*)*"
    r"(?:(?:public|protected|private|static|final|abstract)\s+)*"
    r"(?:class|interface|enum|record)\s+(\w+)"
)
_DECL_METODO = _re.compile(
    r"(?m)^[ \t]*(?:@\w+(?:\([^)\n]*\))?[ \t]*\n?[ \t]*)*"
    r"(?:(?:public|protected|private|static|final|abstract|synchronized|native|default|strictfp)\s+)*"
    r"(?:[\w$]+(?:\s*<[^;{}()]*>)?(?:\[\])?(?:\s*\.\s*[\w$]+)*)\s+(\w+)\s*\("
)
_DECL_TS = _re.compile(r"(?m)^\s*(?:public\s+|private\s+|protected\s+)?(\w+)\s*\(")


def _es_espanol(nombre: str) -> bool:
    return any(p.lower() in _PAL for p in _PALABRA.findall(nombre))


def _recoge(archivos, patrones):
    marcados = set()
    for f in archivos:
        texto = f.read_text(encoding="utf-8")
        clase = _re.search(r"(?:class|interface|enum|record)\s+(\w+)", texto)
        nc = clase.group(1) if clase else ""
        for patron in patrones:
            for m in patron.finditer(texto):
                n = m.group(1)
                if n in nc or n in ("if", "for", "while", "switch", "catch", "return",
                                    "new", "constructor", "try", "else", "do", "throw",
                                    "assert", "super", "this"):
                    continue
                if _es_espanol(n):
                    marcados.add(n)
    return marcados


_todos = set()
_todos |= _recoge((ruta("backend", "src", "main").rglob("*.java")), (_DECL_TIPO, _DECL_METODO))
_todos |= _recoge((ruta("backend", "src", "test").rglob("*.java")), (_DECL_TIPO, _DECL_METODO))
_todos |= _recoge([p for p in (ruta("frontend", "src")).rglob("*.ts")
                   if not p.name.endswith(".spec.ts")], (_DECL_TS,))

_no_permitidos = sorted(_todos - _PERMITIDOS)
check("no quedan nombres en espanol fuera de la lista blanca",
      not _no_permitidos,
      ", ".join(_no_permitidos[:6]))
print(f"        marcados: {len(_todos)} | permitidos: {len(_todos & _PERMITIDOS)} | "
      f"fuera de la lista: {len(_no_permitidos)}")

print("\n" + "=" * 62)
if fallos:
    print(f"INTEGRIDAD FALLIDA: {len(fallos)} de {total} comprobaciones")
    for f in fallos:
        print(f"  - {f}")
    sys.exit(1)
print(f"INTEGRIDAD OK: {total} comprobaciones superadas")
sys.exit(0)
