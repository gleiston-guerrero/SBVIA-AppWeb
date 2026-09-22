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

# The SUS study is RETRACTED: its declared application date (2026-07-28/29)
# predates commits 55201f5 (2026-09-03) and 5e852c6 (2026-09-04), which
# introduced the simulation feature it assessed. This figure only records
# the raw responses: neither the mean nor the threshold is drawn, and the
# system is not ranked on any SUS interpretation scale.
fig, ax = plt.subplots(figsize=(8, 5))
ax.hist(scores, bins=10, color='#009E73', edgecolor='black', alpha=0.7)
ax.set_title('Individual SUS responses (study retracted - raw material only)')
ax.set_xlabel('SUS Score')
ax.set_ylabel('Frequency')
fig.tight_layout()
fig.savefig('docs/mediciones/sus/sus-distribution.png', dpi=120)
print('Generated sus-distribution.png (retracted study: raw responses only, no statistics)')
