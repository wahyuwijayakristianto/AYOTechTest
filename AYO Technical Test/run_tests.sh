#!/bin/bash
# Jalankan dari root folder project ini
# Requirement: Java 11+, download JUnit standalone launcher dulu
#   curl -L -o lib/junit.jar https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar

set -e
ROOT="$(cd "$(dirname "$0")" && pwd)"
JUNIT="$ROOT/lib/junit.jar"
OUT="$ROOT/out"
mkdir -p "$OUT/classes" "$OUT/test-classes"

echo "==> Compiling..."
javac -cp "$JUNIT" -d "$OUT/classes" $(find "$ROOT/src/main" -name "*.java")
javac -cp "$JUNIT:$OUT/classes" -d "$OUT/test-classes" $(find "$ROOT/src/test" -name "*.java")

echo "==> Running tests..."
java -jar "$JUNIT" --class-path "$OUT/classes:$OUT/test-classes" --scan-class-path
