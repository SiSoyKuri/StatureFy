package dev.chibi.player.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.chibi.player.AvatarPreviewDuck;
import dev.chibi.player.BodyProfile;
import dev.chibi.player.HeadScaleWindow;
import dev.chibi.player.StaturefyConfig;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Escalado de cabeza para perfiles chibi/teen.
 *
 * El factor se aplica como una escala extra en la matriz que construye
 * {@code ModelPart.translateAndRotate} (ModelPartHeadScaleMixin), activa solo
 * dentro de la ventana del jugador (HeadScaleWindow). Ese punto es el unico
 * por el que pasa toda la geometria del modelo, tanto si se hornea en
 * submit-time (Sodium/Iris/batched) como si se dibuja en el flush, y por eso
 * es compatible con EMF/FA+Player, Cosmetica/trender, Punchy, etc.
 */
@Mixin(ModelFeatureRenderer.class)
public abstract class ModelFeatureRenderScaleMixin {

    @Inject(
        method = "renderModel(Lnet/minecraft/client/renderer/SubmitNodeStorage$ModelSubmit;Lnet/minecraft/client/renderer/rendertype/RenderType;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/renderer/OutlineBufferSource;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V",
        at = @At("HEAD"))
    private <S> void staturefy$begin(SubmitNodeStorage.ModelSubmit<S> submit, RenderType renderType,
                                     VertexConsumer vertexConsumer, OutlineBufferSource outlineBufferSource,
                                     MultiBufferSource.BufferSource bufferSource, CallbackInfo ci) {
        if (!(submit.state() instanceof AvatarRenderState avatar)) {
            return;
        }
        BodyProfile forced = ((AvatarPreviewDuck) avatar).staturefy$previewProfile();
        BodyProfile profile = forced != null ? forced : StaturefyConfig.profileFor(avatar);
        if (profile.headScale() == 1.0F || !(submit.model() instanceof HumanoidModel<?> model)) {
            return;
        }
        float headFactor = profile.headScale();
        HeadScaleWindow.HEAD.set(model.head);
        HeadScaleWindow.FACTOR.set(headFactor);
    }

    @Inject(
        method = "renderModel(Lnet/minecraft/client/renderer/SubmitNodeStorage$ModelSubmit;Lnet/minecraft/client/renderer/rendertype/RenderType;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/renderer/OutlineBufferSource;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V",
        at = @At("RETURN"))
    private <S> void staturefy$end(SubmitNodeStorage.ModelSubmit<S> submit, RenderType renderType,
                                   VertexConsumer vertexConsumer, OutlineBufferSource outlineBufferSource,
                                   MultiBufferSource.BufferSource bufferSource, CallbackInfo ci) {
        HeadScaleWindow.HEAD.remove();
        HeadScaleWindow.FACTOR.remove();
    }
}
