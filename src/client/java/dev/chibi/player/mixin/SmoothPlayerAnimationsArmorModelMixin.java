package dev.chibi.player.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.chibi.player.BodyProfiles;
import dev.chibi.player.StaturefyRenderContext;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Unique;

@Pseudo
@Mixin(targets = "com.huaxiaosheng.spapacks.client.RigArmorModel")
public abstract class SmoothPlayerAnimationsArmorModelMixin {
    @Unique
    private ModelPart staturefy$root;

    @Shadow @Final private boolean[] slot;
    @Shadow @Final private ModelPart[] parts;

    @Inject(method = "<init>(Lnet/minecraft/client/model/geom/ModelPart;F[Z)V", at = @At("TAIL"))
    private void staturefy$captureRoot(ModelPart root, float dilation, boolean[] slot, CallbackInfo ci) {
        staturefy$root = root;
    }

    @Inject(
            method = "drawRig(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)Z",
            at = @At("HEAD"), cancellable = true
    )
    private void staturefy$useVanillaHelmet(PoseStack poseStack, VertexConsumer vertexConsumer,
                                            int light, int overlay, int tint,
                                            CallbackInfoReturnable<Boolean> cir) {
        if (slot != null && slot.length > 0 && slot[0]) {
            var profile = StaturefyRenderContext.current();
            // El casco skinned del rig 2.1.0 ignora ModelPart.xScale, así que con
            // un perfil no vanilla lo sustituimos por los cubos vanilla del rig,
            // enmascarados y escalados (siguen la pose del rig, paleta incluida).
            if (!profile.equals(BodyProfiles.VANILLA)) {
                StaturefyRenderContext.beginVanillaRigTree(
                        staturefy$root, parts, slot);
                cir.setReturnValue(false);
            }
        }
    }
}
