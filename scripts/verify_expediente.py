import re
import subprocess
import sys


def normalize(s):
    """Normaliza texto para comparar salidas: colapsa espacios y saltos de linea."""
    return ' '.join(s.split())


def expected_output(section):
    """Extrae la salida literal esperada de una seccion de VERIFICACION.md.

    Devuelve None si no hay salida literal (p. ej. 'Salida esperada:' descriptiva).
    """
    # Formato inline: **Salida:** `texto`
    m = re.search(r'\*\*Salida:\*\*\s*`([^`]+)`', section)
    if m:
        return m.group(1).strip()
    # Formato en bloque: **Salida:** / **Salida / Resumen:** / **Salida parcial:**
    # seguido de un bloque de codigo con triple backtick.
    m = re.search(
        r'\*\*Salida(?:\s*/\s*Resumen)?(?:\s+parcial)?:\*\*\s*\n?```[^\n]*\n(.*?)```',
        section,
        re.DOTALL,
    )
    if m:
        return m.group(1).strip()
    return None


print("=== Iniciando Verificacion de Expediente ===")
with open('VERIFICACION.md', 'r', encoding='utf-8') as f:
    content = f.read()

# Divide el documento por cada "- **Orden exacta:** `comando`".
parts = re.split(r'-\s*\*\*Orden exacta:\*\*\s*`([^`]+)`', content)
entries = []
for idx in range(1, len(parts), 2):
    cmd = parts[idx].strip()
    section = parts[idx + 1] if idx + 1 < len(parts) else ''
    entries.append((cmd, section))

if not entries:
    print("No se encontraron comandos en VERIFICACION.md")
    sys.exit(1)

fail_count = 0
for i, (cmd, section) in enumerate(entries):
    expected = expected_output(section)
    print(f"\n[{i+1}/{len(entries)}] Ejecutando: {cmd}")
    try:
        if sys.platform == 'win32':
            result = subprocess.run(cmd, shell=True, capture_output=True, text=True, timeout=120)
        else:
            result = subprocess.run(["bash", "-c", cmd], capture_output=True, text=True, timeout=120)
    except Exception as e:
        print(f"ERROR: No se pudo ejecutar el comando. Exception: {e}")
        fail_count += 1
        continue

    # CUALQUIER codigo de salida distinto de cero hace fallar el verificador.
    if result.returncode != 0:
        print(f"FAILED (Exit code {result.returncode})")
        if result.stdout.strip():
            print("STDOUT:", result.stdout.strip()[:300])
        if result.stderr.strip():
            print("STDERR:", result.stderr.strip()[:300])
        fail_count += 1
        continue

    # Comparar la salida real contra la esperada cuando hay salida literal.
    if expected is not None and normalize(expected):
        if normalize(expected) in normalize(result.stdout):
            print("SUCCESS (salida coincide con la esperada)")
        else:
            print("OUTPUT MISMATCH")
            print("  Esperado:", normalize(expected)[:300])
            print("  Real    :", normalize(result.stdout)[:300])
            fail_count += 1
            continue
    else:
        print("SUCCESS")

if fail_count > 0:
    print(f"\n=== VERIFICACION FALLIDA ({fail_count} errores) ===")
    sys.exit(1)
else:
    print("\n=== VERIFICACION COMPLETADA CON EXITO ===")
    sys.exit(0)
