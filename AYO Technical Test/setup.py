"""
setup.py — Jalankan sekali sebelum buka project di IntelliJ.
Otomatis download JUnit 5 JAR dan buat konfigurasi IntelliJ.

Cara pakai:
    python setup.py
"""

import urllib.request
import os
import sys
import subprocess

ROOT = os.path.dirname(os.path.abspath(__file__))

JUNIT_URL = (
    "https://repo1.maven.org/maven2/org/junit/platform/"
    "junit-platform-console-standalone/1.10.2/"
    "junit-platform-console-standalone-1.10.2.jar"
)
JUNIT_JAR  = os.path.join(ROOT, "lib", "junit-platform-console-standalone-1.10.2.jar")
MODULE_IML = os.path.join(ROOT, "booking-test.iml")
IDEA_DIR   = os.path.join(ROOT, ".idea")


# ── 1. Download JUnit JAR ────────────────────────────────────

def download_junit():
    os.makedirs(os.path.join(ROOT, "lib"), exist_ok=True)

    if os.path.exists(JUNIT_JAR):
        print(f"  [ok] JUnit JAR sudah ada: lib/{os.path.basename(JUNIT_JAR)}")
        return

    print("  [..] Mendownload JUnit 5 standalone JAR (~3 MB)...")
    try:
        def progress(count, block, total):
            pct = min(count * block * 100 // total, 100)
            print(f"\r       {pct}%", end="", flush=True)

        urllib.request.urlretrieve(JUNIT_URL, JUNIT_JAR, reporthook=progress)
        print(f"\r  [ok] Download selesai: lib/{os.path.basename(JUNIT_JAR)}")
    except Exception as e:
        print(f"\r  [!!] Gagal download: {e}")
        print("       Download manual dari:")
        print(f"       {JUNIT_URL}")
        print(f"       Simpan ke: lib/")
        sys.exit(1)


# ── 2. Deteksi versi Java yang terinstall ────────────────────

def detect_java_version():
    """Baca versi Java dari `java -version`, kembalikan int major version."""
    try:
        out = subprocess.check_output(
            ["java", "-version"], stderr=subprocess.STDOUT
        ).decode(errors="replace")
        # Output: 'openjdk version "21.0.1" ...' atau 'java version "1.8.0_..."'
        import re
        m = re.search(r'"(\d+)[\.\d]*"', out)
        if m:
            major = int(m.group(1))
            # Java 8 dilaporkan sebagai "1.8", ambil bagian kedua
            if major == 1:
                m2 = re.search(r'"1\.(\d+)', out)
                if m2:
                    major = int(m2.group(1))
            return major
    except Exception:
        pass
    return None


def java_level_string(version):
    """Konversi major version ke string IntelliJ language level."""
    level_map = {
        8:  "JDK_1_8",
        9:  "JDK_9",
        10: "JDK_10",
        11: "JDK_11",
        12: "JDK_12",
        13: "JDK_13",
        14: "JDK_14",
        15: "JDK_15",
        16: "JDK_16",
        17: "JDK_17",
        18: "JDK_18",
        19: "JDK_19",
        20: "JDK_20",
        21: "JDK_21",
    }
    return level_map.get(version, f"JDK_{version}")


# ── 3. Buat .iml (IntelliJ module config) ───────────────────

def write_iml():
    IML = """\
<?xml version="1.0" encoding="UTF-8"?>
<module type="JAVA_MODULE" version="4">
  <component name="NewModuleRootManager" inherit-compiler-output="true">
    <exclude-output />
    <content url="file://$MODULE_DIR$">
      <sourceFolder url="file://$MODULE_DIR$/src/main/java" isTestSource="false" />
      <sourceFolder url="file://$MODULE_DIR$/src/test/java" isTestSource="true" />
    </content>
    <orderEntry type="inheritedJdk" />
    <orderEntry type="sourceFolder" forTests="false" />
    <orderEntry type="module-library" scope="TEST">
      <library>
        <CLASSES>
          <root url="jar://$MODULE_DIR$/lib/junit-platform-console-standalone-1.10.2.jar!/" />
        </CLASSES>
        <JAVADOC />
        <SOURCES />
      </library>
    </orderEntry>
  </component>
</module>
"""
    with open(MODULE_IML, "w", encoding="utf-8") as f:
        f.write(IML)
    print(f"  [ok] booking-test.iml ditulis")


# ── 4. Buat .idea/modules.xml ────────────────────────────────

def write_modules_xml():
    os.makedirs(IDEA_DIR, exist_ok=True)
    MODULES = """\
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="ProjectModuleManager">
    <modules>
      <module fileurl="file://$PROJECT_DIR$/booking-test.iml"
              filepath="$PROJECT_DIR$/booking-test.iml" />
    </modules>
  </component>
</project>
"""
    with open(os.path.join(IDEA_DIR, "modules.xml"), "w", encoding="utf-8") as f:
        f.write(MODULES)
    print(f"  [ok] .idea/modules.xml ditulis")


# ── 5. Buat .idea/misc.xml (set Java SDK sesuai yang terinstall) ──

def write_misc_xml(java_version):
    level = java_level_string(java_version)
    sdk   = str(java_version)
    MISC = f"""\
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="ProjectRootManager" version="2" languageLevel="{level}"
             project-jdk-name="{sdk}" project-jdk-type="JavaSDK">
    <output url="file://$PROJECT_DIR$/out" />
  </component>
</project>
"""
    with open(os.path.join(IDEA_DIR, "misc.xml"), "w", encoding="utf-8") as f:
        f.write(MISC)
    print(f"  [ok] .idea/misc.xml ditulis  (SDK: Java {java_version}, level: {level})")


# ── 6. Pastikan data/ berisi Excel ──────────────────────────

def check_excel_files():
    data_dir = os.path.join(ROOT, "data")
    os.makedirs(data_dir, exist_ok=True)

    for fname in ("schedules.xlsx", "bookings.xlsx"):
        dest = os.path.join(data_dir, fname)
        if os.path.exists(dest):
            print(f"  [ok] data/{fname}")
            continue

        # Coba cari di root folder (kalau user taruh di sini)
        src = os.path.join(ROOT, fname)
        if os.path.exists(src):
            import shutil
            shutil.move(src, dest)
            print(f"  [ok] {fname} dipindah ke data/")
        else:
            print(f"  [!!] data/{fname} tidak ditemukan — taruh file Excel di folder data/")


# ── Main ─────────────────────────────────────────────────────

if __name__ == "__main__":
    print("\n=== Setup Booking Test Project ===\n")

    print("[1] Download JUnit JAR...")
    download_junit()

    print("\n[2] Deteksi Java...")
    java_ver = detect_java_version()
    if java_ver is None:
        print("  [!!] Java tidak ditemukan di PATH.")
        print("       Install JDK 11+ dari https://adoptium.net lalu jalankan ulang setup ini.")
        sys.exit(1)
    elif java_ver < 11:
        print(f"  [!!] Java {java_ver} terlalu lama. Butuh minimal Java 11.")
        print("       Download JDK terbaru dari https://adoptium.net")
        sys.exit(1)
    else:
        print(f"  [ok] Java {java_ver} terdeteksi")

    print("\n[3] Generate konfigurasi IntelliJ...")
    write_iml()
    write_modules_xml()
    write_misc_xml(java_ver)

    print("\n[4] Cek file Excel...")
    check_excel_files()

    print("\n=== Selesai! ===")
    print("\nLangkah selanjutnya:")
    print("  1. Buka IntelliJ → File → Open → pilih folder ini")
    print("  2. IntelliJ akan langsung mengenali konfigurasi")
    print("  3. Buka BookingTest.java → klik tombol ▶ untuk run test")
    print()
