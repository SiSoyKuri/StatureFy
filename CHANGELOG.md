# Changelog

## [1.1.0+1.21.11] - 2026-11-16

First official build for **Minecraft 1.21.11 (Fabric)**. Same body profiles, same menu, same features as the 26.3 version, rebuilt for the 1.21.11 rendering pipeline.

### Added
- Full port to Minecraft 1.21.11: Vanilla, Teen, Chibi and Tall profiles, the Staturefy menu (Silkscreen font, live preview, proportional layout) and the `/staturefy` command all work exactly like in 26.x.
- Head scaling is applied at the transform level of the model tree, so it works through every render path of this version: vanilla pipeline, Sodium/Iris batched entity rendering, EMF/Fresh Animations (FA+Player) custom player models, Cosmetica/trender skins, Punchy and SmoothPlayerAnimations.
- Foot Grounding feature of Touch'n Motion is respected (when the mod is present, the chibi body keeps its vanilla ground alignment rules).

### Fixed
- Head size of Chibi and Teen now renders correctly in 1.21.11 (the first port drew the head at vanilla size because the vertex bake bypassed the previous hooks).
- First-person hands and items, held maps and the vanilla arm scale correctly in 1.21.11 (ported from the 26.3 hooks).
- Client commands (`/staturefy`) registered through the Fabric command API for 1.21.11.
- The mod now starts on any Fabric Loader 0.15.11+ and any Fabric API for 1.21.11; the build no longer depends on a patched Fabric API at runtime.

### Compat notes
- Requires: Minecraft 1.21.11, Fabric Loader 0.15.11+, Fabric API for 1.21.11, Java 21+.
- Tested together with: EMF/ETF + FreshAnimations + FA+Player, SmoothPlayerAnimations, Punchy (+Punchy Moves), PatPat, Cosmetica/trender, immediatelyfast, Sodium/Iris, Touch'n Motion.
- Found a bug? Please report it on our Discord so we can fix it in the next build.

# [1.1.0+1.21.11] - ESPAÑOL
Primer build oficial para **Minecraft 1.21.11 (Fabric)**. Los mismos perfiles corporales, el mismo menú y las mismas funciones de la versión 26.3, reconstruidos para el pipeline de renderizado de 1.21.11.

## Agregado
- Port completo a Minecraft 1.21.11: perfiles Vanilla, Adolescente, Chibi y Alto, el menú de Staturefy (fuente Silkscreen, vista previa, proporciones adaptadas) y el comando `/staturefy` funcionan exactamente igual que en 26.x.
- La escala de la cabeza se aplica a nivel de transformación del árbol del modelo, así funciona con todos los caminos de render de esta versión: pipeline vanilla, rendering batcheado de Sodium/Iris, modelos personalizados de EMF/Fresh Animations (FA+Player), skins de Cosmetica/trender, Punchy y SmoothPlayerAnimations.
- Se respeta la función Foot Grounding de Touch'n Motion (si el mod está presente, el cuerpo chibi mantiene sus reglas de alineación al suelo).

## Corregido
- El tamaño de la cabeza de Chibi y Adolescente ahora se renderiza correctamente en 1.21.11 (el primer port dibujaba la cabeza en tamaño vanilla porque el horneado de vértices esquivaba los hooks anteriores).
- Las manos y objetos en primera persona, los mapas y el brazo vanilla escalan correctamente en 1.21.11 (hooks portados de 26.3).
- Los comandos de cliente (`/staturefy`) registrados con la API de comandos de Fabric para 1.21.11.
- El mod arranca con cualquier Fabric Loader 0.15.11+ y cualquier Fabric API de 1.21.11; el build ya no depende de una Fabric API parcheada en runtime.

## Notas de compatibilidad
- Requiere: Minecraft 1.21.11, Fabric Loader 0.15.11+, Fabric API para 1.21.11, Java 21+.
- Probado junto a: EMF/ETF + FreshAnimations + FA+Player, SmoothPlayerAnimations, Punchy (+Punchy Moves), PatPat, Cosmetica/trender, immediatelyfast, Sodium/Iris, Touch'n Motion.
- ¿Encontraste un bug? Repórtalo en nuestro Discord para arreglarlo en el próximo build.

## [1.1.0] - 2026-11-15

### Added
- First-person hand scale: your hand and held items now render smaller with the Chibi and Teen profiles and slightly larger with the Tall profile (Vanilla stays at normal size).
- SmoothPlayerAnimations 2.1.0 compatibility: the skinned player and armor follow the head scale of Chibi and Teen, and the vanilla helmet keeps the correct size with every profile.
- Integration with the Punchy API: a "Hand scale" toggle is added to Punchy's Compat tab, and Punchy's first-person arms respect the Staturefy hand scale. Everything works normally when Punchy is not installed.
- The mod menu now survives every resource reload (F3+T, toggling mods or packs) without breaking its font.
- Clicking a player card now shows a live preview of how your character will look with that height profile.

### Fixed
- The menu now looks identical at every GUI scale: card label bands, corner accents and figure sizes were using fixed pixels that only matched the x3 scale.
- The "Player" section label no longer overlaps the top of the cards.
- The Done button label was corrupted in every language (it showed the file name instead of the translation).
- The "Player list" and "Hand scale" buttons are now properly centered, and the hand-scale toggle text is no longer clipped.

### Changed
- Player card figures, label bands and corner accents now scale proportionally with the GUI scale.
- The Silkscreen pixel font is rebuilt after each resource reload.

# [1.1.0] - ESPAÑOL
## Agregado
- Escala de la mano en primera persona: tu mano y los objetos ahora se ven más chicos con los perfiles Chibi y Adolescente, y un poco más grandes con el perfil Alto (Vanilla queda normal).
- Compatibilidad con SmoothPlayerAnimations 2.1.0: el jugador y la armadura skinned respetan la escala de cabeza de Chibi y Adolescente, y el casco vanilla mantiene el tamaño correcto con todos los perfiles.
- Integración con la API de Punchy: un interruptor de "Escala de la mano" se agrega a la pestaña Compat de Punchy, y los brazos en primera persona de Punchy respetan la escala de Staturefy. Todo funciona normal si Punchy no está instalado.
- El menú del mod ahora sobrevive a cualquier recarga de recursos (F3+T, activar/desactivar mods o packs) sin romper su fuente.
- Al hacer clic en una tarjeta de jugador se muestra una vista previa de cómo quedará tu personaje con esa estatura.

## Corregido
- El menú ahora se ve idéntico en todas las escalas de GUI: la banda de etiqueta de las tarjetas, las esquinas y las figuras usaban píxels fijos que solo coincidían con la escala x3.
- La etiqueta de la sección "Jugador" ya no invade la parte superior de las tarjetas.
- El texto del botón Listo estaba corrupto en todos los idiomas (mostraba el nombre del archivo en vez de la traducción).
- Los botones "Lista de jugadores" y "Escala de mano" ahora están bien centrados, y el texto del interruptor de escala ya no se corta.

## Cambiado
- Las figuras de las tarjetas, sus bandas de etiqueta y esquinas ahora escalan proporcionalmente con la escala de GUI.
- La fuente pixel Silkscreen se reconstruye después de cada recarga de recursos.

## [1.0.1] - 2026-11-11

### Added
- Config screen uses the Silkscreen pixel font for all its text, with automatic fallback to the vanilla font for unsupported scripts (Japanese, Chinese, Korean, Russian and more).
- Player profiles screen redesigned to match the main configuration screen style, including its gold Done button.

### Fixed
- Player name tags now render at a mid-height offset, staying visible for players of every height profile.
- Bottom button centering bug in the configuration screen (it was drawn offset to the left).
- Font loading failure that rendered every glyph of the config screen as empty boxes (TTF path and definition format corrected for Minecraft 26.3).

### Changed
- Profile cards redesigned: gold border with cyan corners on every card, turning green when selected.
- Body scale differences between profiles are now visually amplified in the card figures, making Vanilla vs. Tall clearly distinguishable while staying centered.
- Improved text legibility across the menu: dark gray text on cards and light text on gold buttons.
- "Staturefy" title moved up and recolored green.
- All menu text now scales proportionally across every GUI scale, using the x3 scale as the reference design.
