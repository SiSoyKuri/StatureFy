# Changelog

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
