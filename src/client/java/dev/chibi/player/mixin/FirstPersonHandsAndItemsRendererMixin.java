package dev.chibi.player.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.chibi.player.PunchyIntegration;
import dev.chibi.player.StaturefyConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
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
@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class FirstPersonHandsAndItemsRendererMixin {

    @Inject(method = "renderPlayerArm",
        at = @At(value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V",
            ordinal = 0, shift = At.Shift.AFTER))
    private void staturefy$scaleVanillaArm(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords,
        float inverseArmHeight, float attackValue, HumanoidArm arm, PlayerRenderState playerState, CallbackInfo ci) {
        if (staturefy$skip(arm, playerState)) {
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

    @Inject(method = "renderMap",
        at = @At(value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V",
            ordinal = 0, shift = At.Shift.AFTER))
    private void staturefy$scaleHeldMap(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords,
        ItemStack stack, boolean mainHand, FirstPersonHandsAndItemsRenderState state, CallbackInfo ci) {
        staturefy$apply(poseStack);
    }

    /** True when Punchy draws this arm itself (its own modifier scales it). */
    private static boolean staturefy$skip(HumanoidArm arm, PlayerRenderState playerState) {
        AvatarRenderState avatar = playerState != null ? playerState.avatarRenderState : null;
        boolean mainHand = avatar != null ? arm == avatar.mainArm
            : staturefy$localMainArm() == arm;
        return FabricLoader.getInstance().isModLoaded("punchy")
            && PunchyIntegration.handOwnerIsPunchy(mainHand);
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
