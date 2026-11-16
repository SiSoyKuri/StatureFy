package dev.chibi.player.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.chibi.player.AvatarPreviewDuck;
import dev.chibi.player.BodyProfile;
import dev.chibi.player.StaturefyConfig;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * In 1.21.11 the player and armor models are drawn in the flush phase by
 * ModelFeatureRenderer.renderModel, after LivingEntityRenderer.submit has
 * returned, so scaling there (instead of in the feature "prep" used by 26.3)
 * is what keeps the chibi/teen head size on skin, armor and through every
 * layer, including the SmoothPlayerAnimations-free path.
 */
@Mixin(ModelFeatureRenderer.class)
public abstract class ModelFeatureRenderScaleMixin {
    @Unique private HumanoidModel<?> staturefy$model;
    @Unique private float staturefy$headX;
    @Unique private float staturefy$headY;
    @Unique private float staturefy$headZ;

    @Inject(
        method = "renderModel(Lnet/minecraft/client/renderer/SubmitNodeStorage$ModelSubmit;Lnet/minecraft/client/renderer/rendertype/RenderType;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/renderer/OutlineBufferSource;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V",
        at = @At("HEAD"))
    private <S> void staturefy$begin(SubmitNodeStorage.ModelSubmit<S> submit, RenderType renderType,
                                     VertexConsumer vertexConsumer, OutlineBufferSource outlineBufferSource,
                                     MultiBufferSource.BufferSource bufferSource, CallbackInfo ci) {
        staturefy$model = null;
        if (!(submit.state() instanceof AvatarRenderState avatar)) {
            return;
        }
        BodyProfile forced = ((AvatarPreviewDuck) avatar).staturefy$previewProfile();
        BodyProfile profile = forced != null ? forced : StaturefyConfig.profileFor(avatar);
        if (profile.headScale() == 1.0F || !(submit.model() instanceof HumanoidModel<?> model)) {
            return;
        }
        staturefy$model = model;
        staturefy$headX = model.head.xScale;
        staturefy$headY = model.head.yScale;
        staturefy$headZ = model.head.zScale;
        model.head.xScale *= profile.headScale();
        model.head.yScale *= profile.headScale();
        model.head.zScale *= profile.headScale();
    }

    @Inject(
        method = "renderModel(Lnet/minecraft/client/renderer/SubmitNodeStorage$ModelSubmit;Lnet/minecraft/client/renderer/rendertype/RenderType;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/renderer/OutlineBufferSource;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V",
        at = @At("RETURN"))
    private <S> void staturefy$end(SubmitNodeStorage.ModelSubmit<S> submit, RenderType renderType,
                                   VertexConsumer vertexConsumer, OutlineBufferSource outlineBufferSource,
                                   MultiBufferSource.BufferSource bufferSource, CallbackInfo ci) {
        if (staturefy$model != null) {
            staturefy$model.head.xScale = staturefy$headX;
            staturefy$model.head.yScale = staturefy$headY;
            staturefy$model.head.zScale = staturefy$headZ;
            staturefy$model = null;
        }
    }
}
