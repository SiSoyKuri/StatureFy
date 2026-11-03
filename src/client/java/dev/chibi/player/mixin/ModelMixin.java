package dev.chibi.player.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.chibi.player.BodyProfile;
import dev.chibi.player.StaturefyRenderContext;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.player.PlayerModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Model.class)
public abstract class ModelMixin {
    @Unique private float chibiplayer$headX;
    @Unique private float chibiplayer$headY;
    @Unique private float chibiplayer$headZ;
    @Unique private PlayerModel chibiplayer$model;

    // shortcut: body and limbs keep their vanilla proportions; replace with a tested proportion rig after the first visual pass.
    @Inject(method = "renderToBuffer", at = @At("HEAD"))
    private void chibiplayer$enlargeHead(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                                         int packedOverlay, int tint, CallbackInfo ci) {
        chibiplayer$model = null;
        BodyProfile profile = StaturefyRenderContext.current();
        if (profile.headScale() == 1.0F || !((Object) this instanceof PlayerModel model)) {
            return;
        }

        chibiplayer$model = model;
        chibiplayer$headX = model.head.xScale;
        chibiplayer$headY = model.head.yScale;
        chibiplayer$headZ = model.head.zScale;
        model.head.xScale *= profile.headScale();
        model.head.yScale *= profile.headScale();
        model.head.zScale *= profile.headScale();
    }

    @Inject(method = "renderToBuffer", at = @At("RETURN"))
    private void chibiplayer$restoreHead(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                                         int packedOverlay, int tint, CallbackInfo ci) {
        if (chibiplayer$model == null) {
            return;
        }
        chibiplayer$model.head.xScale = chibiplayer$headX;
        chibiplayer$model.head.yScale = chibiplayer$headY;
        chibiplayer$model.head.zScale = chibiplayer$headZ;
        chibiplayer$model = null;
    }
}
