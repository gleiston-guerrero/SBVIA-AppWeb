import os
import re

directories = [
    r"backend\src\main\java\com\sbvia\backend\controller",
    r"backend\src\main\java\com\sbvia\backend\service",
    r"backend\src\main\java\com\sbvia\backend\security"
]

method_pattern = re.compile(r'^\s*(@\w+(?:\([^)]*\))?\s*)*public\s+(?:<[^>]+>\s+)?(?:static\s+)?([\w<>\[\]]+)\s+(\w+)\s*\(([^)]*)\)\s*(?:throws\s+[\w,\s]+)?\s*\{')
class_pattern = re.compile(r'^\s*public\s+(?:class|interface|record)\s+(\w+)')

def analyze_java_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    lines = content.split('\n')
    methods = []
    
    for i, line in enumerate(lines):
        match = re.search(r'public\s+(?:<[^>]+>\s+)?(?:static\s+)?(?:final\s+)?([\w<>\[\]]+)\s+(\w+)\s*\(([^)]*)\)\s*(?:throws\s+[\w,\s]+)?\s*\{', line)
        if match and not (" class " in line or " interface " in line or " record " in line):
            # check backwards for javadoc
            has_javadoc = False
            has_param = False
            has_return = False
            j = i - 1
            while j >= 0 and (lines[j].strip().startswith('@') or lines[j].strip() == ''):
                j -= 1
            if j >= 0 and lines[j].strip() == '*/':
                has_javadoc = True
                # Check what is in the javadoc
                k = j
                while k >= 0 and not lines[k].strip() == '/**':
                    if '@param' in lines[k]:
                        has_param = True
                    if '@return' in lines[k]:
                        has_return = True
                    k -= 1
            methods.append({
                'line': i + 1,
                'name': match.group(2),
                'return_type': match.group(1),
                'args': match.group(3),
                'has_javadoc': has_javadoc,
                'has_param': has_param,
                'has_return': has_return
            })
            
    return methods

total_methods = 0
undoc_methods = 0

for d in directories:
    for root, _, files in os.walk(d):
        for file in files:
            if file.endswith('.java'):
                path = os.path.join(root, file)
                methods = analyze_java_file(path)
                if methods:
                    print(f"\n--- {file} ---")
                    for m in methods:
                        total_methods += 1
                        if not m['has_javadoc'] or (m['args'] and not m['has_param']) or (m['return_type'] != 'void' and not m['has_return']):
                            undoc_methods += 1
                            print(f"L{m['line']}: {m['return_type']} {m['name']}({m['args']})")
                            print(f"   Javadoc: {m['has_javadoc']}, Param: {m['has_param']}, Return: {m['has_return']}")

print(f"\nTotal methods: {total_methods}")
print(f"Needs docs: {undoc_methods}")
