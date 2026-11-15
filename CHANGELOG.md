# Changelog

## [1.1.0] - 2026-11-15

### Added
- First-person hand scale: your hand and held items now render smaller with the Chibi and Teen profiles and slightly larger with the Tall profile (Vanilla stays at normal size).
- SmoothPlayerAnimations 2.1.0 compatibility: the skinned player and armor follow the head scale of Chibi and Teen, and the vanilla helmet keeps the correct size with every profile.
- Integration with the Punchy API: a "Hand scale" toggle is added to Punchy's Compat tab, and Punchy's first-person arms respect the Staturefy hand scale. Everything works normally when Punchy is not installed.
- The mod menu now survives every resource reload (F3+T, toggling mods or packs) without breaking its font.

### Fixed
- The menu now looks identical at every GUI scale: card label bands, corner accents and figure sizes were using fixed pixels that only matched the x3 scale.
- The "Player" section label no longer overlaps the top of the cards.
- The Done button label was corrupted in every language (it showed the file name instead of the translation).
- The "Player list" and "Hand scale" buttons are now properly centered, and the hand-scale toggle text is no longer clipped.

### Changed
- Player card figures, label bands and corner accents now scale proportionally with the GUI scale.
- The Silkscreen pixel font is rebuilt after each resource reload.

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
