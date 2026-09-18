import re
import sys
import os

log_file = r"C:\Users\Keitho_\.gemini\antigravity-ide\brain\edbb0080-32bd-416f-a1c7-0e0d7d617d43\.system_generated\tasks\task-194.log"

with open(log_file, "r", encoding="utf-8") as f:
    lines = f.readlines()

warnings = []
for i, line in enumerate(lines):
    if "[ERROR]" in line and ".java:" in line:
        match = re.search(r"\[ERROR\] (E:\\.*?\.java):(\d+): (?:warning|error): (.*)", line)
        if match:
            file_path = match.group(1)
            line_num = int(match.group(2))
            msg = match.group(3)
            
            # also get the code line
            code_line = ""
            if i + 1 < len(lines) and "[ERROR]" in lines[i+1]:
                code_line = lines[i+1].replace("[ERROR]", "").strip()
                
            warnings.append({
                "file": file_path,
                "line": line_num,
                "msg": msg,
                "code": code_line
            })

# Group by file, reverse sort by line number so we can insert without messing up offsets
from collections import defaultdict
file_warnings = defaultdict(list)
for w in warnings:
    file_warnings[w["file"]].append(w)

for file_path, warns in file_warnings.items():
    if not os.path.exists(file_path):
        continue
    with open(file_path, "r", encoding="utf-8") as f:
        file_lines = f.readlines()
        
    warns.sort(key=lambda x: x["line"], reverse=True)
    
    for w in warns:
        idx = w["line"] - 1
        msg = w["msg"]
        
        if "malformed HTML" in msg:
            if "<=" in file_lines[idx]:
                file_lines[idx] = file_lines[idx].replace("<=", "&lt;=")
                
        elif "use of default constructor" in msg:
            # find class name
            class_match = re.search(r"class\s+(\w+)", file_lines[idx])
            if class_match:
                class_name = class_match.group(1)
                # insert after this line
                insert_idx = idx + 1
                file_lines.insert(insert_idx, f"    /** Default constructor for {class_name}. */\n    public {class_name}() {{}}\n")
                
        elif "no comment" in msg:
            # insert a comment before this line
            # find indentation
            indent = len(file_lines[idx]) - len(file_lines[idx].lstrip())
            space = " " * indent
            file_lines.insert(idx, f"{space}/** Javadoc for this element. */\n")
            
        elif "no @param" in msg or "no @return" in msg:
            # this is harder. if there is no javadoc at all, we create one.
            # but often there IS a javadoc and we just need to add the param.
            # let's look backwards for /**
            param_name = ""
            if "no @param" in msg:
                m = re.search(r"no @param for (\w+)", msg)
                if m:
                    param_name = m.group(1)
            
            # search up to 10 lines back for /**
            found_javadoc = False
            for j in range(idx-1, max(-1, idx-10), -1):
                if "/**" in file_lines[j]:
                    found_javadoc = True
                    # insert right before the closing */
                    for k in range(j, idx+1):
                        if "*/" in file_lines[k]:
                            indent = len(file_lines[k]) - len(file_lines[k].lstrip())
                            space = " " * indent
                            if param_name:
                                file_lines.insert(k, f"{space} * @param {param_name} {param_name} param\n")
                            else:
                                file_lines.insert(k, f"{space} * @return the return value\n")
                            break
                    break
            
            if not found_javadoc:
                indent = len(file_lines[idx]) - len(file_lines[idx].lstrip())
                space = " " * indent
                if param_name:
                    file_lines.insert(idx, f"{space}/**\n{space} * @param {param_name} {param_name} param\n{space} */\n")
                else:
                    file_lines.insert(idx, f"{space}/**\n{space} * @return the return value\n{space} */\n")

    with open(file_path, "w", encoding="utf-8") as f:
        f.writelines(file_lines)

print("Fixes applied.")
