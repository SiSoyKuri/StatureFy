\# Functional Requirements



\## Project Scope



The first release focuses exclusively on the Chibi Player transformation.



No additional morphs should be implemented in Version 1.



\---



\# Player Rendering



The player should be able to render themselves as Chibi.



The transformation should affect only rendering.



Gameplay must remain identical to vanilla.



\---



\# Other Players



The user must be able to configure how other players are rendered.



Supported modes:



\- Everyone Normal

\- Everyone Chibi

\- Only Me

\- Custom Player List



The Custom Player List should allow enabling or disabling Chibi rendering per player.



\---



\# Skin Support



The Chibi renderer must automatically use the player's active skin.



Supported providers include:



\- Mojang Premium

\- SkinRestorer

\- Ely.by

\- Offline Mode

\- Other compatible skin providers



No manual setup should be required.



\---



\# Held Items



Held items must remain correctly attached to the player's hands.



Supported examples:



\- Sword

\- Shield

\- Food

\- Bow

\- Crossbow

\- Map

\- Trident

\- Spyglass

\- All vanilla items



Items must not float or become detached during animations.



\---



\# Capes



Cape rendering must remain attached to the Chibi body.



Supported cape systems include:



\- Vanilla

\- Better Capes

\- Wavey Capes

\- Cosmetica

\- Essential



Cape movement should remain natural.



\---



\# Animation Compatibility



The renderer must preserve compatibility with:



\- Punchy

\- Punchy Moves

\- Punchy Studio

\- SmoothPlayerAnimations

\- Touch'n Motion

\- Fresh Animations Player Extension



Animations should continue working without additional configuration.



\---



\# Cosmetic Compatibility



The renderer should remain compatible with:



\- Emotecraft

\- Essential

\- Cosmetica



Emotes and cosmetics should continue working correctly.



\---



\# Resource Pack Compatibility



The renderer should remain compatible with:



\- Just Expressions

\- Fresh Animations Player Extension



Facial features should remain correctly positioned.



\---



\# Configuration



The mod should integrate with Mod Menu.



Configuration should be intuitive.



Changing settings should not require restarting Minecraft whenever possible.



\---



\# Performance



The renderer should introduce minimal performance overhead.



Rendering should avoid unnecessary allocations.



Compatibility should never depend on expensive per-frame calculations when a cached solution is possible.



\---



\# Version 1 Non-Goals



Version 1 should NOT include:



\- Slime Morph

\- Axolotl Morph

\- Custom Morphs

\- Blockbench Import

\- Public API

\- Multiplayer synchronization

\- Server-side installation



These features belong to future versions.

