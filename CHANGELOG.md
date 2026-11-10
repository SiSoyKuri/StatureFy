# Changelog

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

## [1.0.0] - 2026-11-07

### Added
- New Teen and Tall body profiles, alongside Vanilla and Chibi.
- Rendering compatibility with PatPat: the headpat animation now scales and positions correctly according to the Chibi or Teen profile.
- Mod icon added for Mod Menu.
- Translations added for Argentine and Mexican Spanish, French, Indonesian, Japanese, Korean, Russian, and Simplified Chinese.

### Changed
- Redesigned configuration menu featuring profile cards, preview figures, and a cleaner, centered layout.
- Positioning adjustments for Chibi while sitting on mounts.
- Fence rendering tweaks when using Touch'n Motion's Foot IK on Chibi and Teen profiles.
- Vanilla is now set as the default profile for both local and remote players.
- English and European Spanish texts updated for the menu and new profiles.

## [0.1.0] - 2026-11-03

### Added
- Initial release: client-side body profile system with Vanilla, Chibi, Teen and Tall profiles, per-player and global configuration, in-game preview, hitboxes unchanged.
