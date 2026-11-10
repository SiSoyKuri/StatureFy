# StatureFy

**StatureFy** is a **client-side Fabric mod** that lets players freely change the size of their own player model without affecting gameplay.

Unlike mods that require server support, StatureFy works entirely on the client. Your hitbox, reach, collision, and every gameplay mechanic remain exactly the same. Only the rendered player model is scaled.

This allows you to create smaller (or larger) characters while keeping full compatibility with vanilla servers and most multiplayer environments.

## ✨ Features

- 🧍 Scale your player model independently from gameplay.
- 🌐 Fully client-side — no server installation required.
- 📦 Works on vanilla servers.
- ⚔️ No hitbox modifications.
- 🎨 Designed to be compatible with popular animation and expression mods.

---

# Compatibility

StatureFy has been built with compatibility in mind and works alongside the following projects:

## ✅ [Punchy](https://modrinth.com/mod/punchy-fpa)

Fully compatible with **Punchy** animations.

## ✅ [Punchy Moves](https://modrinth.com/mod/punchymoves)

Supports all animations from **Punchy Moves** while preserving the selected player scale.

## ✅ [SmoothPlayerAnimations](https://modrinth.com/mod/smoothplayeranimations)

Compatible with **SmoothPlayerAnimations**, allowing both mods to work together without breaking player animations.

## ✅ [FA: Player Extension](https://modrinth.com/resourcepack/fa-player-extension)

Compatible with **FA: Player Extension** and its animation system.

## ✅ [Just Expressions](https://modrinth.com/resourcepack/just-expressions)

Facial expressions continue to work correctly while using StatureFy.

## ✅ Punchy Moves (Faces)

Compatible with the facial animation extension included with Punchy Moves.

## ✅ [Cosmetica](https://modrinth.com/mod/cosmetica)

Cosmetica cosmetics automatically adjust to your selected player height.

---

# Dependencies

### Required

- Fabric Loader
- Fabric API
- Cloth Config API

### Optional

- Mod Menu *(recommended for quick access to the configuration screen)*

---

## How it works

StatureFy only changes how player models are rendered on your client.

Other players do **not** need to install StatureFy, and servers do not require any additional setup.

Because the hitbox is never modified, gameplay remains completely vanilla while giving you complete freedom to customize your character's appearance.

---

## Future Plans

StatureFy is just getting started! Some planned features include:

- 🌍 **Server-side visibility support**

  A future update is planned to allow other players to see your height changes **without requiring them to install the mod**.

  This will most likely be achieved through an optional **server plugin or server-side mod**, allowing all connected players to see height changes seamlessly.

- ⚙️ Additional customization options.
- 🎨 More scaling presets and configuration improvements.
- 🤝 Even broader compatibility with animation and cosmetic mods.

---

## Feedback

Found a bug, compatibility issue, or have a feature suggestion?

Feel free to open an Issue with as much information as possible.

Feedback and contributions are always welcome!

---

# Installation

1. Install **Minecraft 1.21.11** with **Fabric Loader**.
2. Install **Fabric API**.
3. Install **Cloth Config API**.
4. *(Optional)* Install **Mod Menu** for easy access to the configuration screen.
5. Place the **StatureFy** JAR inside your `mods` folder.

---

## Build from source

### Requirements

- JDK 25

Run:

```bash
./gradlew build
```

The compiled JAR will be generated inside:

```
build/libs/
```

---

## Compatibility reports

When reporting compatibility issues, please include:

- Minecraft version
- Fabric Loader version
- Fabric API version
- StatureFy version
- Versions of the affected mods/resource packs
- Your configuration
- `latest.log`

This information makes it much easier to reproduce and fix the issue.

---

## License

The source code is available under the **MIT License**. See the [LICENSE](LICENSE) file for details.
