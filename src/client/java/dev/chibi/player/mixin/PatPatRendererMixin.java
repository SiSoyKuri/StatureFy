package dev.chibi.player.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.chibi.player.BodyProfile;
import dev.chibi.player.BodyProfiles;
import dev.chibi.player.StaturefyConfig;
import dev.chibi.player.StaturefyRenderContext;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.lopymine.patpat.client.render.PatPatClientRenderer", remap = false)
public abstract class PatPatRendererMixin {
    private static final String SUBMIT =
            "submit(Lnet/minecraft/client/renderer/SubmitNodeCollector;Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Quaternionf;Lnet/lopymine/patpat/entity/PatEntity;Lnet/minecraft/world/entity/Entity;Lnet/lopymine/patpat/common/config/vector/Vec3f;FI)Lnet/lopymine/patpat/client/render/PatPatClientRenderer$RenderResult;";

    @ModifyVariable(method = SUBMIT, at = @At("HEAD"), argsOnly = true, index = 4, require = 0)
    private static Entity staturefy$trackProfile(Entity entity) {
        BodyProfile profile = entity instanceof Player player
                ? BodyProfiles.get(StaturefyConfig.effectiveProfile(player.getUUID()))
                : BodyProfiles.VANILLA;
        StaturefyRenderContext.push(profile);
        return entity;
    }

    @ModifyArg(
            method = SUBMIT,
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", remap = false),
            index = 1,
            require = 0
    )
    private static float staturefy$raiseGif(float y) {
        BodyProfile profile = StaturefyRenderContext.current();
        return profile == BodyProfiles.CHIBI || profile == BodyProfiles.TEEN
                ? y + 0.20F / profile.bodyScaleY() : y;
    }

    @Inject(method = SUBMIT, at = @At("RETURN"), require = 0)
    private static void staturefy$clearProfile(CallbackInfoReturnable<?> cir) {
        StaturefyRenderContext.pop();
    }
}
