import os
import re

terms = {
    'fechaFin': 'endDate',
    'puntajeFinal': 'finalScore',
    'duracionSegundos': 'durationSeconds',
    'completada': 'completed',
    'observaciones': 'observations',
    'valor': 'value'
}

def replace_in_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    original = content
    
    hidden_strings = {}
    counter = 0
    def hide_match(m):
        nonlocal counter
        key = f'__HIDDEN_{counter}__'
        hidden_strings[key] = m.group(0)
        counter += 1
        return key

    pattern_hide = r'@(?:Table|Column|JoinColumn|Procedure)\s*\([^)]*name\s*=\s*"[^"]*"[^)]*\)'
    content = re.sub(pattern_hide, hide_match, content)

    for term in sorted(terms.keys(), key=len, reverse=True):
        replacement = terms[term]
        pattern = r'\b' + re.escape(term) + r'\b'
        content = re.sub(pattern, replacement, content)

    for key, val in hidden_strings.items():
        content = content.replace(key, val)

    if content != original:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Replaced {filepath}")

for root, dirs, files in os.walk('backend', topdown=False):
    if 'target' in root: continue
    for f in files:
        if f.endswith('.java'):
            replace_in_file(os.path.join(root, f))
