import os
import re

backend_dir = r"E:\SBVIA-AppWeb\backend\src\main\java\com\sbvia\backend"

for root, dirs, files in os.walk(backend_dir):
    for file in files:
        if file.endswith(".java"):
            filepath = os.path.join(root, file)
            with open(filepath, "r", encoding="utf-8") as f:
                content = f.read()
            
            if "/** Default constructor for" in content:
                # If it has the manual constructor, remove @NoArgsConstructor
                if "@NoArgsConstructor" in content:
                    new_content = content.replace("@NoArgsConstructor\n", "").replace("@NoArgsConstructor", "")
                    with open(filepath, "w", encoding="utf-8") as f:
                        f.write(new_content)
                    print(f"Removed @NoArgsConstructor from {file}")
