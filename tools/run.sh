#!/bin/bash
# Patches every fabric-api module jar for Loom 1.19 (removes legacy access
# wideners that Loom rejects) and publishes them into libs/maven.
# Self-contained: fetches the fabric-api POM from maven.fabricmc.net.
set -e
FAPI_VER=$(grep -oE '^fabric_api_version=.*' gradle.properties | cut -d= -f2)
BASE="https://maven.fabricmc.net/net/fabricmc/fabric-api"
POM=/tmp/fapi.pom
curl -sf -o "$POM" "$BASE/fabric-api/$FAPI_VER/fabric-api-$FAPI_VER.pom"
mapfile -t ARTS < <(grep -A4 "<dependency>" "$POM" | grep -oE "<artifactId>fabric-[^<]*</artifactId>" | sed 's/<[^>]*>//g' | grep -v '^$')
mapfile -t VERS < <(grep -A4 "<dependency>" "$POM" | grep -oE "<version>[^<]*</version>" | sed 's/<[^>]*>//g' | grep -v '^$')
for i in "${!ARTS[@]}"; do
  echo "== ${ARTS[$i]} ${VERS[$i]}"
  bash tools/patch.sh "${ARTS[$i]}" "${VERS[$i]}"
done
echo DONE
