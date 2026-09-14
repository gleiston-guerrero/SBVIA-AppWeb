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

mean_score = sum(scores) / len(scores)

fig, ax = plt.subplots(figsize=(8, 5))
ax.hist(scores, bins=10, color='#009E73', edgecolor='black', alpha=0.7)
ax.axvline(mean_score, color='red', linestyle='dashed', linewidth=2, label=f'Mean: {mean_score:.2f}')
ax.axvline(68, color='orange', linestyle='dashed', linewidth=2, label='Threshold (68)')
ax.set_title('SUS Score Distribution (N=15)')
ax.set_xlabel('SUS Score')
ax.set_ylabel('Frequency')
ax.legend()
fig.tight_layout()
fig.savefig('docs/mediciones/sus/sus-distribution.png', dpi=120)
print('Generated sus-distribution.png')
