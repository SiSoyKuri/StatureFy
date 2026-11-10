package dev.chibi.player.mixin;

import dev.chibi.player.AvatarPreviewDuck;
import dev.chibi.player.BodyProfile;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public abstract class AvatarRenderStateMixin implements AvatarPreviewDuck {
    @Unique
    private BodyProfile staturefy$preview;

    @Override
    public BodyProfile staturefy$previewProfile() {
        return staturefy$preview;
    }

    @Override
    public void staturefy$setPreviewProfile(BodyProfile profile) {
        staturefy$preview = profile;
    }
}
