import os

files_to_update = [
    'CITATION.cff',
    'README.md',
    r'backend\src\main\java\com\sbvia\backend\config\OpenApiConfig.java',
    r'docs\entrega -1a.tex',
    r'docs\entrega-1b.tex',
    r'docs\informe-final.tex',
    r'docs\mediciones\sec\REPORT.md',
    r'docs\requisitos\SRS-v1.0.0.tex'
]

for f in files_to_update:
    filepath = os.path.join(r"E:\SBVIA-AppWeb", f)
    if os.path.exists(filepath):
        with open(filepath, 'r', encoding='utf-8') as file:
            content = file.read()
        
        if "keithdrox" in content:
            new_content = content.replace("keithdrox", "gleiston-guerrero")
            with open(filepath, 'w', encoding='utf-8') as file:
                file.write(new_content)
            print(f"Updated {f}")
        else:
            print(f"No match in {f}")
    else:
        print(f"File not found: {f}")
