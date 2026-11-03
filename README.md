# Staturefy

Staturefy is a client-side visual body-profile mod for Minecraft Java Edition. It currently offers Vanilla and Chibi profiles. It does not change player dimensions, hitboxes, movement, or server data; other players do not need to install it.

## Compatibility

- Minecraft Java Edition 26.3
- Fabric Loader 0.19.5 or newer
- Java 25
- Fabric API (required)
- Mod Menu (optional; adds the configuration button)

The mod is client-only. Compatibility with rendering, animation, cosmetic, cape, skin, and resource-pack mods varies by version and setup; see the release notes and report the exact versions when filing an issue.

## Features

- Select Vanilla or Chibi for your own player model.
- Choose the default profile used to render other players, with per-player overrides.
- Open settings using Mod Menu or `/staturefy` and `/staturefy config`.
- Settings are stored locally in `config/staturefy.json`.
- Keeps vanilla gameplay dimensions and works without server installation or network synchronization.

## Install

1. Install Minecraft 26.3 with Fabric Loader and Fabric API.
2. Optionally install Mod Menu for its Mods-screen configuration button.
3. Put the Staturefy Fabric JAR in the instance's `mods` folder.

## Build from source

Use JDK 25 and run:

```powershell
.\gradlew.bat jar
```

The distributable JAR is written to `build\libs\staturefy-0.1.0+26.3-fabric.jar`.

## Compatibility reports

Include the Minecraft version, Fabric Loader/API versions, Staturefy version, affected mod/resource-pack versions, relevant settings, and `latest.log`. Do not assume compatibility with an untested version combination.

## Project identity

The displayed mod name is **Staturefy**. The Fabric mod ID remains `chibiplayer` to preserve existing installations and configuration identity.

## License

The source code is available under the MIT License; see [LICENSE](LICENSE).

