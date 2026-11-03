# Staturefy release checklist

## Current release metadata

- Display name: Staturefy
- Stable Fabric mod ID: `chibiplayer` (keep this to preserve existing installations/configuration)
- Current project version: `0.1.0`
- Minecraft: `26.3`
- Loader: Fabric (`0.19.5` minimum)
- Java: `25`
- Runtime: client only
- Artifact: `build/libs/staturefy-0.1.0+26.3-fabric.jar`
- Required dependency: Fabric API
- Optional integration: Mod Menu

## Before the first public upload

- [x] MIT license selected and added in `LICENSE` (copyright attribution: Staturefy Contributors).
- [ ] Create the GitHub repository and push the source and workflows.
- [ ] Create the Staturefy project on Modrinth and CurseForge; confirm the project pages, IDs/slugs, author attribution, categories, and project visibility.
- [x] Project icon and listing copy prepared; screenshots and source/issues links still need the public pages/repository.
- [ ] Review the changelog against the exact JAR and test this release candidate in the target modpack.
- [ ] Confirm the platform's current supported Minecraft version list includes 26.3 before uploading.

## Publish a version

1. Update `mod_version` in `gradle.properties` and add a matching section to `CHANGELOG.md`.
2. Build and inspect the JAR with JDK 25: `./gradlew jar` (Windows: `./gradlew.bat jar`).
3. Commit the release-ready changes and tag the commit `v<mod_version>` (for example, `v0.1.0`).
4. Push the commit and tag. GitHub Actions validates the tag, builds the JAR, and attaches it to a GitHub Release.
5. Upload that exact JAR to Modrinth and CurseForge, using the metadata below. Do not recompile separate platform files.

## Platform metadata

Use the release version `0.1.0+26.3` for the uploaded file/version, and `v0.1.0` for the Git tag.

### Modrinth

- Project type: Mod; loader: Fabric; game version: 26.3; environment: client-side.
- Set Fabric API as required and Mod Menu as optional if those project records are available.
- Add the matching changelog section and the single Fabric JAR.
- Use a Modrinth personal access token only if later automating uploads; store it as a repository secret named `MODRINTH_TOKEN`, never in Git.

### CurseForge

- Game: Minecraft; class: Mods; loader: Fabric; game version: 26.3; client environment.
- Declare Fabric API required and Mod Menu optional if available in CurseForge dependency metadata.
- Add the matching changelog and the same Fabric JAR.
- If later automating uploads, store the API token as a repository secret named `CURSEFORGE_TOKEN`, never in Git.

### GitHub

- The tag workflow creates a release and attaches the distributable JAR.
- `GITHUB_TOKEN` is provided by GitHub Actions with release-content permission scoped to the workflow.
- GitHub repository publication and release tagging still require a GitHub repository and remote; none exists in this local project yet.

## Automated checks

GitHub Actions runs Gradle's `jar` task for pushes and pull requests. A `v*` tag also checks that the version matches `mod_version` and that its changelog section is present before creating the GitHub Release.

Modrinth and CurseForge uploads are intentionally manual until the project pages and their identifiers are created and confirmed.
