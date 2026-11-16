package dev.chibi.player.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.chibi.player.PunchyIntegration;
import dev.chibi.player.StaturefyConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Scales the vanilla first-person hand (empty-hand arm, held item and held map)
 * according to the local player's body profile.
 * Skipped when Punchy owns the hand, because Punchy's arm modifier already
 * scales its own arms (including the ones it draws through vanilla code).
 */
@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Inject(method = "renderPlayerArm",
        at = @At(value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V",
            ordinal = 0, shift = At.Shift.AFTER))
    private void staturefy$scaleVanillaArm(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords,
        float inverseArmHeight, float attackValue, HumanoidArm arm, CallbackInfo ci) {
        if (staturefy$skip(arm)) {
            return;
        }
        staturefy$apply(poseStack);
    }

    @Inject(method = "applyItemArmTransform",
        at = @At(value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V",
            ordinal = 0, shift = At.Shift.AFTER))
    private void staturefy$scaleHeldItem(PoseStack poseStack, HumanoidArm arm, float inverseArmHeight, CallbackInfo ci) {
        staturefy$apply(poseStack);
    }

    @Inject(method = "renderMap", at = @At("HEAD"))
    private void staturefy$scaleHeldMap(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords,
        ItemStack stack, CallbackInfo ci) {
        staturefy$apply(poseStack);
    }

    /** True when Punchy draws this arm itself (its own modifier scales it). */
    private static boolean staturefy$skip(HumanoidArm arm) {
        HumanoidArm key = staturefy$localMainArm();
        return FabricLoader.getInstance().isModLoaded("punchy")
            && PunchyIntegration.handOwnerIsPunchy(arm == key);
    }

    private static HumanoidArm staturefy$localMainArm() {
        return Minecraft.getInstance().player != null
            ? Minecraft.getInstance().player.getMainArm() : HumanoidArm.RIGHT;
    }

    private static void staturefy$apply(PoseStack poseStack) {
        if (!StaturefyConfig.firstPersonHandScale()) {
            return;
        }
        float factor = StaturefyConfig.handScaleFactor();
        if (factor != 1.0F) {
            poseStack.scale(factor, factor, factor);
        }
    }
}
