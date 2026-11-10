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
    @Unique private float staturefy$headX;
    @Unique private float staturefy$headY;
    @Unique private float staturefy$headZ;
    @Unique private PlayerModel staturefy$model;

    // shortcut: body and limbs keep their vanilla proportions; replace with a tested proportion rig after the first visual pass.
    @Inject(method = "renderToBuffer", at = @At("HEAD"))
    private void staturefy$enlargeHead(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                                         int packedOverlay, int tint, CallbackInfo ci) {
        staturefy$model = null;
        BodyProfile profile = StaturefyRenderContext.current();
        if (profile.headScale() == 1.0F || !((Object) this instanceof PlayerModel model)) {
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

    @Inject(method = "renderToBuffer", at = @At("RETURN"))
    private void staturefy$restoreHead(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                                         int packedOverlay, int tint, CallbackInfo ci) {
        if (staturefy$model == null) {
            return;
        }
        staturefy$model.head.xScale = staturefy$headX;
        staturefy$model.head.yScale = staturefy$headY;
        staturefy$model.head.zScale = staturefy$headZ;
        staturefy$model = null;
    }
}
