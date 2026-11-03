package dev.chibi.player.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.chibi.player.StaturefyRenderContext;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.huaxiaosheng.spapacks.client.RigBend")
public abstract class SmoothPlayerAnimationsArmorBendMixin {
    @Inject(
            method = "render(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)Z",
            at = @At("HEAD"), cancellable = true
    )
    private static void staturefy$allowVanillaHelmet(ModelPart part, PoseStack poseStack,
                                                     VertexConsumer vertexConsumer, int light,
                                                     int overlay, int tint,
                                                     CallbackInfoReturnable<Boolean> cir) {
        if (StaturefyRenderContext.vanillaRigTreeActive()) {
            cir.setReturnValue(false);
        }
    }
}
