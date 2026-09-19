import math
import csv
import os

# --- Funciones auxiliares: p-valor exacto de la t de Student (sin dependencias) ---
# P(T > t) se calcula con la función beta incompleta regularizada I_x(a,b),
# usando el algoritmo de fracción continua de Lentz (Numerical Recipes).

def _betacf(a, b, x):
    MAXIT = 200
    EPS = 3.0e-12
    FPMIN = 1.0e-300
    qab = a + b
    qap = a + 1.0
    qam = a - 1.0
    c = 1.0
    d = 1.0 - qab * x / qap
    if abs(d) < FPMIN:
        d = FPMIN
    d = 1.0 / d
    h = d
    for m in range(1, MAXIT + 1):
        m2 = 2 * m
        aa = m * (b - m) * x / ((qam + m2) * (a + m2))
        d = 1.0 + aa * d
        if abs(d) < FPMIN:
            d = FPMIN
        c = 1.0 + aa / c
        if abs(c) < FPMIN:
            c = FPMIN
        d = 1.0 / d
        h *= d * c
        aa = -(a + m) * (qab + m) * x / ((a + m2) * (qap + m2))
        d = 1.0 + aa * d
        if abs(d) < FPMIN:
            d = FPMIN
        c = 1.0 + aa / c
        if abs(c) < FPMIN:
            c = FPMIN
        d = 1.0 / d
        delta = d * c
        h *= delta
        if abs(delta - 1.0) < EPS:
            break
    return h


def _regularized_beta(x, a, b):
    if x <= 0.0:
        return 0.0
    if x >= 1.0:
        return 1.0
    lbeta = (math.lgamma(a + b) - math.lgamma(a) - math.lgamma(b)
             + a * math.log(x) + b * math.log(1.0 - x))
    if x < (a + 1.0) / (a + b + 2.0):
        return math.exp(lbeta) * _betacf(a, b, x) / a
    return 1.0 - math.exp(lbeta) * _betacf(b, a, 1.0 - x) / b


def t_survival(t, df):
    """P(T > t) para la t de Student con df grados de libertad (cola derecha)."""
    if t < 0:
        return 1.0 - t_survival(-t, df)
    x = df / (df + t * t)
    return 0.5 * _regularized_beta(x, df / 2.0, 0.5)


# --- 1. Estudio SUS: RETIRADO (no se evalúa hipótesis de usabilidad) ---
# Motivo: la fecha declarada de aplicación del instrumento (2026-07-28/29) es
# anterior a la incorporación al repositorio de la funcionalidad de simulación
# que el instrumento dice haber evaluado (commit 55201f5, 2026-09-03,
# "práctica vial interactiva"; commit 5e852c6, 2026-09-04, "simulador de
# conducción 2D"). Las respuestas crudas se conservan únicamente como material
# de registro, sin garantía de validez metodológica, y no se reporta media,
# desviación típica, intervalo de confianza ni valor p.
sus_file = os.path.join(os.path.dirname(__file__), '..', 'sus', 'sus-raw-data.csv')
sus_rows = 0
with open(sus_file, 'r', encoding='utf-8') as f:
    for _ in csv.DictReader(f):
        sus_rows += 1

# --- 2. Leer y calcular Rendimiento (Speedup) ---
speedup_file = os.path.join(os.path.dirname(__file__), 'speedup-realtime.txt')
cold = []
hot = []
with open(speedup_file, 'r', encoding='utf-8') as f:
    for line in f:
        if line.startswith('| ') and 'Cache' not in line:
            parts = line.split('|')
            if len(parts) >= 4:
                try:
                    c_val = float(parts[2].strip())
                    h_val = float(parts[3].strip())
                    cold.append(c_val)
                    hot.append(h_val)
                except ValueError:
                    pass

# T-test pareado: H0: cold <= hot vs H1: cold > hot
diffs = [c - h for c, h in zip(cold, hot)]
mean_diff = sum(diffs) / len(diffs)
var_diff = sum((x - mean_diff)**2 for x in diffs) / (len(diffs) - 1)
n_diff = len(diffs)

t_stat_perf = mean_diff / math.sqrt(var_diff / n_diff)
p_perf = t_survival(t_stat_perf, n_diff - 1)

# --- 3. Corrección Holm-Bonferroni ---
alpha = 0.05
p_values = [
    ('Rendimiento (Frio > Caliente)', p_perf)
]

# Ordenar de menor a mayor p-valor
p_values_sorted = sorted(p_values, key=lambda x: x[1])

print("=== Resultados de Evaluación Estadística ===")
print(f"Estudio SUS: RETIRADO. Respuestas crudas conservadas: N={sus_rows}, sin análisis estadístico.")
print(f"Muestra Rendimiento (N={n_diff}): Media Diferencia = {mean_diff:.2f}ms")
print(f"  t = {t_stat_perf:.4f}, p-valor (una cola, df={n_diff - 1}) = {p_perf:.3e}")
print("\n=== Aplicación de Holm-Bonferroni (alpha = 0.05) ===")

m = len(p_values_sorted)
for k, (test_name, p) in enumerate(p_values_sorted):
    adjusted_alpha = alpha / (m - k)
    is_significant = p < adjusted_alpha
    print(f"Paso {k+1}: {test_name}")
    print(f"  P-valor crudo = {p:.3e}")
    print(f"  Alpha ajustado ({alpha} / {m-k}) = {adjusted_alpha:.4f}")
    print(f"  Rechazar H0? {'SI' if is_significant else 'NO'}")
    if not is_significant:
        print("  -> El procedimiento se detiene aquí.")
        break
