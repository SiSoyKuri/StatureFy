package dev.chibi.player;

/**
 * Duck interface implemented on {@code AvatarRenderState} through a mixin.
 * Carries an optional profile forced by UI previews, so a single render state
 * can be rendered with a profile that differs from the entity's own.
 */
public interface AvatarPreviewDuck {
    BodyProfile staturefy$previewProfile();

    void staturefy$setPreviewProfile(BodyProfile profile);
}
