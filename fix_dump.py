import re

with open('tvudump.sql', 'rb') as f:
    content = f.read()

lines = content.split(b'\n')
fixed_lines = []

for line in lines:
    stripped = line.strip()

    # Fix 1: bare dash separator lines -> proper SQL comment
    if re.fullmatch(rb'-{3,}', stripped):
        fixed_lines.append(b'-- ' + stripped)
        continue

    # Fix 2: old MySQL 4.0 "TYPE=" syntax -> modern "ENGINE="
    if b'TYPE=' in line:
        line = line.replace(b'TYPE=', b'ENGINE=')

    fixed_lines.append(line)

with open('tvudump_fixed.sql', 'wb') as f:
    f.write(b'\n'.join(fixed_lines))

print("Done. Created tvudump_fixed.sql")