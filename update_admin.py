import zipfile
from pathlib import Path
import shutil
import subprocess

root = Path("src/main/resources")
jar_path = Path("target/online-banking-system-1.0.0.jar")
updated_jar_path = Path("target/online-banking-system-1.0.0-updated.jar")

files_to_replace = {
    "BOOT-INF/classes/templates/public/login.html": root / "templates/public/login.html",
    "BOOT-INF/classes/templates/public/index.html": root / "templates/public/index.html",
    "BOOT-INF/classes/templates/admin/users.html": root / "templates/admin/users.html",
}

with zipfile.ZipFile(jar_path, "r") as jar:
    with zipfile.ZipFile(updated_jar_path, "w") as newjar:
        for item in jar.infolist():
            if item.filename in files_to_replace:
                src_path = files_to_replace[item.filename]
                if src_path.exists():
                    newjar.writestr(item, src_path.read_bytes())
                else:
                    newjar.writestr(item, jar.read(item.filename))
            else:
                newjar.writestr(item, jar.read(item.filename))

shutil.move(str(updated_jar_path), str(jar_path))
print("Updated jar entries to match source templates.")

# Copy updated source templates into target/classes as well
for rel, src in files_to_replace.items():
    target_path = Path("target/classes") / Path(rel).relative_to("BOOT-INF/classes")
    target_path.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(src, target_path)
print("Updated target/classes templates.")

# Verify current H2 admin record
sql_file = Path("target/check_admin_pw.sql")
sql_file.write_text("SELECT id, email, password FROM users WHERE email='Basavaraj@bank.com';")
cmd = [
    "java",
    "-cp",
    "target/BOOT-INF/lib/h2-2.2.224.jar",
    "org.h2.tools.RunScript",
    "-url",
    "jdbc:h2:file:./data/bankingdb",
    "-user",
    "SA",
    "-password",
    "",
    "-script",
    str(sql_file),
    "-showResults",
]
proc = subprocess.run(cmd, capture_output=True, text=True)
print("Return code:", proc.returncode)
print(proc.stdout)
print(proc.stderr)
