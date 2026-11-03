\# Chibi Player - Project Vision



\## Overview



Chibi Player is a client-side Minecraft Java mod that recreates and expands the "Morph" experience from Actions \& Stuff (Minecraft Bedrock Edition).



The first transformation implemented by the project will be a Chibi version of the player's own skin.



Unlike Bedrock, the project aims to become a flexible rendering framework capable of supporting additional transformations in the future.



Examples include:



\- Chibi

\- Slime

\- Axolotl

\- Baby Zombie

\- Custom models

\- Community-created morphs



The architecture should always prioritize extensibility.



\---



\# Core Philosophy



This project is NOT about making the player smaller.



This project is about creating an alternative player rendering system that integrates naturally with the modern Minecraft Java visual ecosystem.



Every technical decision should prioritize compatibility over visual perfection.



If a solution looks slightly better but breaks compatibility with existing mods or resource packs, it should be rejected.



\---



\# Primary Goals



\- Client-side only.

\- Vanilla server compatible.

\- No gameplay modifications.

\- No networking required.

\- No server installation required.

\- No protocol modifications.



The mod should work in singleplayer and multiplayer exactly like any cosmetic client-side mod.



\---



\# Version 1 Goals



Version 1 only focuses on the Chibi transformation.



The player should be able to:



\- Enable or disable Chibi mode.

\- View themselves as Chibi.

\- View other players as Chibi.

\- Keep specific players normal.

\- Configure who is rendered using the Chibi model.



\---



\# Compatibility Philosophy



Compatibility is the highest priority of the project.



The renderer should integrate with existing animation systems whenever possible.



The project should avoid replacing existing systems if extending them is possible.



The renderer should remain compatible with:



\- Resource Packs

\- Animation Mods

\- Cosmetic Mods

\- Skin Providers

\- Cape Mods

\- Emote Mods



\---



\# Skin Compatibility



The renderer should automatically use the player's active skin.



Supported examples include:



\- Mojang Premium

\- SkinRestorer

\- Ely.by

\- Offline skins

\- Custom launchers



No manual configuration should be required.



\---



\# Future Goals



Future versions may include:



\- Multiple morph types

\- Custom Blockbench morphs

\- Animation API

\- Morph API

\- Presets

\- Public SDK



These features should NOT influence Version 1 complexity.



Version 1 should remain focused on delivering an excellent Chibi experience.



\---



\# Development Philosophy



The project should avoid unnecessary complexity.



Before implementing a new system, always check whether Minecraft, Fabric or an existing dependency already provides a suitable solution.



Prefer extending existing rendering systems over replacing them.



Avoid duplicated code whenever possible.



Design every major component to be modular and replaceable.



\---



\# Success Criteria



Version 1 will be considered successful when:



\- Chibi rendering works correctly.

\- Vanilla multiplayer works.

\- Existing player skins work automatically.

\- Major animation mods remain functional.

\- Major cosmetic mods remain functional.

\- Resource packs continue working.

\- Capes remain correctly attached.

\- Held items remain correctly positioned.

\- The user experience is simple and intuitive.



Visual quality is important.



Compatibility is mandatory.

