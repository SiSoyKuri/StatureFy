#!/bin/bash
# Patches every fabric-api module jar for Loom 1.19 (removes legacy access
# wideners that Loom rejects) and publishes them into libs/maven.
# Walks the full dependency graph of POMs until no new modules appear, so
# transitive fabric-api modules are covered too.
set -e
FAPI_VER=$(grep -oE '^fabric_api_version=.*' gradle.properties | cut -d= -f2)
BASE="https://maven.fabricmc.net/net/fabricmc/fabric-api"
POM=/tmp/fapi.pom
mkdir -p libs/maven/net/fabricmc/fabric-api
curl -sf -o "$POM" "$BASE/fabric-api/$FAPI_VER/fabric-api-$FAPI_VER.pom"
echo "== fabric-api $FAPI_VER"
bash tools/patch.sh fabric-api "$FAPI_VER"

pair_deps() {
  # prints "<artifactId> <version>" for every fabric-* dependency in a POM
  awk '/<dependency>/{indep=1; art=""} indep && /<artifactId>/{line=$0; gsub(/.*<artifactId>|<\/artifactId>.*/,"",line); art=line} indep && /<version>/{line=$0; gsub(/.*<version>|<\/version>.*/,"",line); if (art != "") {print art" "line; art=""}} /<\/dependency>/{indep=0}' "$1"
}

for pass in 1 2 3 4 5; do
  NEW=0
  while read -r ART VER; do
    [ -n "$ART" ] || continue
    [ -n "$VER" ] || continue
    if [ ! -d "libs/maven/net/fabricmc/fabric-api/$ART/$VER" ]; then
      echo "== $ART $VER"
      bash tools/patch.sh "$ART" "$VER" || NEW=$?
      NEW=1
    fi
  done < <(find libs/maven -name "*.pom" -exec awk 'BEGIN{IGNORECASE=0} /<dependency>/{indep=1; art=""} indep && /<artifactId>/{line=$0; gsub(/.*<artifactId>|<\/artifactId>.*/,"",line); art=line} indep && /<version>/{line=$0; gsub(/.*<version>|<\/version>.*/,"",line); if (art != "") {print art" "line; art=""}} /<\/dependency>/{indep=0}' {} \; | sort -u)
  if [ "$NEW" = "0" ]; then
    echo "graph closed after pass $pass"
    break
  fi
done
echo DONE
