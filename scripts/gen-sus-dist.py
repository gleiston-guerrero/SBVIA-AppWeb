import csv
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import os

scores = []
with open('docs/mediciones/sus/sus-raw-data.csv', 'r') as f:
    reader = csv.DictReader(f)
    for row in reader:
        scores.append(float(row['sus_score']))

# El estudio SUS esta RETIRADO: la fecha declarada de aplicacion (2026-07-28/29)
# es anterior a los commits 55201f5 (2026-09-03) y 5e852c6 (2026-09-04) que
# introducen la funcionalidad de simulacion evaluada. Esta figura solo registra
# las respuestas crudas: no se dibuja la media ni el umbral, y no se clasifica al
# sistema en ninguna escala de interpretacion del SUS.
fig, ax = plt.subplots(figsize=(8, 5))
ax.hist(scores, bins=10, color='#009E73', edgecolor='black', alpha=0.7)
ax.set_title('Individual SUS responses (study retracted - raw material only)')
ax.set_xlabel('SUS Score')
ax.set_ylabel('Frequency')
fig.tight_layout()
fig.savefig('docs/mediciones/sus/sus-distribution.png', dpi=120)
print('Generated sus-distribution.png (retracted study: raw responses only, no statistics)')
