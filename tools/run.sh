#!/bin/bash
set -e
mapfile -t ARTS < <(grep -A4 "<dependency>" /tmp/fapi.pom | grep -oE "<artifactId>fabric-[^<]*</artifactId>" | sed 's/<[^>]*>//g' | grep -v '^$')
mapfile -t VERS < <(grep -A4 "<dependency>" /tmp/fapi.pom | grep -oE "<version>[^<]*</version>" | sed 's/<[^>]*>//g' | grep -v '^$')
for i in "${!ARTS[@]}"; do
  echo "== ${ARTS[$i]} ${VERS[$i]}"
  bash tools/patch.sh "${ARTS[$i]}" "${VERS[$i]}"
done
echo DONE
