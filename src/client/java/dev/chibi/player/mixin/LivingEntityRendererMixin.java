package dev.chibi.player.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.chibi.player.BodyProfile;
import dev.chibi.player.BodyProfiles;
import dev.chibi.player.StaturefyConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @Unique private ModelPart staturefy$scaledHead;
    @Unique private float staturefy$headX;
    @Unique private float staturefy$headY;
    @Unique private float staturefy$headZ;

    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("HEAD"))
    private void staturefy$begin(LivingEntityRenderState state, PoseStack poseStack,
                                   SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo ci) {
        if (!(state instanceof AvatarRenderState avatar)) {
            return;
        }
        BodyProfile profile = StaturefyConfig.profileFor(avatar);
        poseStack.pushPose();
        if (Minecraft.getInstance().level != null) {
            Entity entity = Minecraft.getInstance().level.getEntity(avatar.id);
            if (entity instanceof Player player) {
                if (profile == BodyProfiles.CHIBI && player.isPassenger()) {
                    poseStack.translate(0.0F, 0.20F, 0.0F);
                }
                if ((profile == BodyProfiles.CHIBI || profile == BodyProfiles.TEEN)
                        && player.getBlockStateOn().is(BlockTags.FENCES)
                        && staturefy$isFootGroundingEnabled()) {
                    poseStack.translate(0.0F, -0.60F * (1.0F - profile.bodyScaleY()), 0.0F);
                }
            }
        }
        poseStack.scale(profile.bodyScaleX(), profile.bodyScaleY(), profile.bodyScaleZ());
    }

    @Unique
    private static boolean staturefy$isFootGroundingEnabled() {
        try {
            Class<?> feature = Class.forName(
                    "strm.touchnmotion.footgrounding.FootGroundingFeature", false,
                    LivingEntityRendererMixin.class.getClassLoader());
            return Boolean.TRUE.equals(feature.getMethod("isEnabled").invoke(null));
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return false;
        }
    }

    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Ljava/lang/Object;)V", shift = At.Shift.AFTER))
    private void staturefy$scaleHeadPivots(LivingEntityRenderState state, PoseStack poseStack,
                                          SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo ci) {
        if (!(state instanceof AvatarRenderState avatar)) {
            return;
        }
        BodyProfile profile = StaturefyConfig.profileFor(avatar);
        if (profile.headScale() == 1.0F || !((Object) this instanceof LivingEntityRenderer<?, ?, ?> renderer)
                || !(renderer.getModel() instanceof HumanoidModel<?> model)) {
            return;
        }

        staturefy$scaledHead = model.head;
        staturefy$headX = model.head.xScale;
        staturefy$headY = model.head.yScale;
        staturefy$headZ = model.head.zScale;
        model.head.xScale *= profile.headScale();
        model.head.yScale *= profile.headScale();
        model.head.zScale *= profile.headScale();
    }

    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("RETURN"))
    private void staturefy$end(LivingEntityRenderState state, PoseStack poseStack,
                                 SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo ci) {
        if (state instanceof AvatarRenderState) {
            if (staturefy$scaledHead != null) {
                staturefy$scaledHead.xScale = staturefy$headX;
                staturefy$scaledHead.yScale = staturefy$headY;
                staturefy$scaledHead.zScale = staturefy$headZ;
                staturefy$scaledHead = null;
            }
            poseStack.popPose();
        }
    }
}
