package dev.chibi.player.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Aplica el head factor directamente en la matriz que construye
 * translateAndRotate, dentro de la ventana activa (ThreadLocal). Es el unico
 * punto por el que pasa TODA la geometria del modelo, ya sea horneada en
 * submit-time (Sodium/Iris) o dibujada en el flush.
 */
@Mixin(net.minecraft.client.model.geom.ModelPart.class)
public abstract class ModelPartHeadScaleMixin {

    @Inject(method = "translateAndRotate", at = @At("RETURN"))
    private void staturefy$applyHeadFactor(PoseStack poseStack, CallbackInfo ci) {
        if ( (Object) this == dev.chibi.player.HeadScaleWindow.HEAD.get()) {
            float f = dev.chibi.player.HeadScaleWindow.FACTOR.get();
            if (f != 1.0F) {
                poseStack.scale(f, f, f);
            }
        }
    }
}
