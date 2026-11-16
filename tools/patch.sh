#!/bin/bash
set -e
ART=$1; VER=$2
BASE="https://maven.fabricmc.net/net/fabricmc/fabric-api"
OUT="libs/maven/net/fabricmc/fabric-api"
ROOT="$(mktemp -d)/mav"
mkdir -p "$ROOT"
DIR="$ROOT/$ART/$VER"
JAR="$DIR/$ART-$VER.jar"
mkdir -p "$DIR"
curl -sf -o "$JAR" "$BASE/$ART/$VER/$ART-$VER.jar" || { echo "   MISSING"; rm -f "$JAR"; exit 1; }
WORK=$(mktemp -d)
unzip -q -o "$JAR" -d "$WORK"
find "$WORK" -name "*.accesswidener" -delete
if [ -f "$WORK/fabric.mod.json" ]; then
  perl tools/rmaw.pl < "$WORK/fabric.mod.json" > "$WORK/fabric.mod.json.new" && mv "$WORK/fabric.mod.json.new" "$WORK/fabric.mod.json"
fi

rm -f "$JAR"
(cd "$WORK" && "/c/Users/PcVIP/Desktop/Proyectos/MINECRAFT/Staturefy MOD/build/jdk25/jdk-25.0.5+8/bin/jar" --create --file "$JAR" .)
curl -s -o "$DIR/$ART-$VER.pom" "$BASE/$ART/$VER/$ART-$VER.pom"
# publish into the real repo dir
DEST="libs/maven/net/fabricmc/fabric-api/$ART/$VER"
mkdir -p "$DEST"
mv "$JAR" "$DEST/"
mv "$DIR/$ART-$VER.pom" "$DEST/" 2>/dev/null || true
rm -rf "$WORK"
echo "   ok"
