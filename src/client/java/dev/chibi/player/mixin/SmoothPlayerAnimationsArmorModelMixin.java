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
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Pseudo
@Mixin(targets = "com.huaxiaosheng.spapacks.client.RigArmorModel")
public abstract class SmoothPlayerAnimationsArmorModelMixin {
    @Unique private static Method staturefy$configGetter;
    @Unique private static Field staturefy$enabledField;
    @Unique private static Field staturefy$cemEnabledField;
    @Unique private static boolean staturefy$configLookupDone;
    @Unique private static boolean staturefy$configAvailable;
    @Unique private ModelPart staturefy$root;

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
            if (!profile.equals(BodyProfiles.VANILLA) && staturefy$smoothCemEnabled()) {
                StaturefyRenderContext.beginVanillaRigTree(
                        staturefy$root, parts, slot, profile.headScale());
                cir.setReturnValue(false);
            }
        }
    }

    @Unique
    private static boolean staturefy$smoothCemEnabled() {
        if (!staturefy$configLookupDone) {
            staturefy$configLookupDone = true;
            try {
                Class<?> configClass = Class.forName("com.huaxiaosheng.spapacks.client.ModConfig", false,
                        SmoothPlayerAnimationsArmorModelMixin.class.getClassLoader());
                staturefy$configGetter = configClass.getMethod("get");
                staturefy$enabledField = configClass.getField("enabled");
                staturefy$cemEnabledField = configClass.getField("cemEnabled");
                staturefy$configAvailable = true;
            } catch (ReflectiveOperationException | LinkageError exception) {
                staturefy$configAvailable = false;
            }
        }
        if (!staturefy$configAvailable) {
            return false;
        }
        try {
            Object config = staturefy$configGetter.invoke(null);
            return config != null && staturefy$enabledField.getBoolean(config)
                    && staturefy$cemEnabledField.getBoolean(config);
        } catch (ReflectiveOperationException exception) {
            return false;
        }
    }
}
