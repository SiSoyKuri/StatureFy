package dev.chibi.player.mixin;

import dev.chibi.player.StaturefyConfig;
import dev.chibi.player.StaturefyRenderContext;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelFeatureRenderer.class)
public abstract class ModelFeatureRendererMixin {
    @Inject(method = "prepareModel", at = @At("HEAD"))
    private <S> void staturefy$begin(ModelFeatureRenderer.Submit<S> submit, CallbackInfo ci) {
        if (submit.state() instanceof AvatarRenderState state) {
            StaturefyRenderContext.push(StaturefyConfig.profileFor(state));
        }
    }

    @Inject(method = "prepareModel", at = @At("RETURN"))
    private <S> void staturefy$end(ModelFeatureRenderer.Submit<S> submit, CallbackInfo ci) {
        if (submit.state() instanceof AvatarRenderState) {
            StaturefyRenderContext.pop();
        }
    }
}
