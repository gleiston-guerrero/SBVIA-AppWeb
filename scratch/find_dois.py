import re
import json
import urllib.request
import urllib.parse
import time

bib_file = r'e:\SBVIA-AppWeb\docs\refs.bib'
with open(bib_file, 'r', encoding='utf-8') as f:
    content = f.read()

entries = re.split(r'\n@', '\n' + content)
entries = [e for e in entries if e.strip()]

new_entries = []
log_output = []

for entry in entries:
    entry = '@' + entry
    if 'doi=' in entry.replace(' ', '') or 'doi =' in entry:
        new_entries.append(entry)
        
        # Extract DOI
        m = re.search(r'doi\s*=\s*[{"]([^}"]+)[}"]', entry)
        if m:
            doi = m.group(1)
            log_output.append(f"OK - {doi} already present")
        continue

    # Extract title
    title_match = re.search(r'title\s*=\s*[{"]([^}"]+)[}"]', entry)
    if not title_match:
        new_entries.append(entry)
        continue
    
    title = title_match.group(1).replace('\n', ' ')
    
    try:
        url = "https://api.crossref.org/works?query.bibliographic=" + urllib.parse.quote(title) + "&rows=1"
        req = urllib.request.Request(url, headers={'User-Agent': 'mailto:keithdrox@gmail.com'})
        with urllib.request.urlopen(req) as response:
            data = json.loads(response.read().decode())
            items = data['message']['items']
            if items:
                doi = items[0]['DOI']
                # Insert DOI before the last brace
                parts = entry.rsplit('}', 1)
                entry = parts[0] + f',\n  doi={{{doi}}}\n}}'
                log_output.append(f"FOUND - {title[:30]}... -> {doi}")
            else:
                log_output.append(f"NOT FOUND - {title[:30]}...")
    except Exception as e:
        log_output.append(f"ERROR - {title[:30]}... : {e}")
    
    new_entries.append(entry)
    time.sleep(0.1)

with open(bib_file, 'w', encoding='utf-8') as f:
    f.write('\n'.join(new_entries))

with open(r'e:\SBVIA-AppWeb\docs\doi_check.log', 'w', encoding='utf-8') as f:
    f.write('\n'.join(log_output))

print("DONE")
