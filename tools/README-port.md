# Port 1.21.11 — build infrastructure

Fabric-api 0.141.6+1.21.11 publishes access wideners in the obsolete intermediary
namespace, which Loom (1.19.4, official-mojmap environment) rejects. The build
therefore consumes a locally patched fabric-api:

    bash tools/run.sh          # 1) patches all fabric-api module jars into libs/maven
    bash tools/patch.sh fabric-api 0.141.6+1.21.11
    (the deprecated aggregator extra modules:)
    grep -A4 "<dependency>" /tmp/fapi-dep.pom  # patch like the parent

Setup steps for a fresh checkout:
1. Download JDK 21 (Temurin) to build_jdk21 (gradle toolchain target).
2. Run tools/run.sh (needs /tmp/fapi.pom + /tmp/fapi-dep.pom from maven.fabricmc.net).
3. Download punchy-3.2b-fabric-1.21.11.jar into libs/ (Modrinth, compileOnly).
4. Gradle runs on JDK 25+ (loom JVM requirement) but compiles with release 21.
