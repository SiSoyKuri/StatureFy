package dev.chibi.player.mixin;

import dev.chibi.player.BodyProfiles;
import dev.chibi.player.StaturefyConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

/**
 * SmoothPlayerAnimations 2.1.0 dibuja el jugador y la armadura como malla
 * skinned a partir de una paleta de matrices de huesos por frame; esa paleta
 * ignora los ModelPart.xScale, así que el tamaño de cabeza chibi/teen se pierde
 * en el casco y en la piel. Escalamos la matriz del hueso "head" (parte lineal,
 * respecto al pivote, idempotente) justo después de que SPA muestrea la paleta,
 * de modo que piel, casco y superficies de armadura escalen juntas y sigan
 * sincronizadas con las animaciones.
 */
@Pseudo
@Mixin(targets = "com.huaxiaosheng.spapacks.client.RigPlayerModel")
public abstract class SmoothPlayerAnimationsPaletteMixin {
    private static Field staturefy$shownField;
    private static Field staturefy$paletteField;
    private static Field staturefy$bonesField;
    private static Field staturefy$playerField;
    private static boolean staturefy$lookupDone;

    /** Columnas lineales de la paleta (column-major 3x4); la traslación queda fija. */
    private static final int[] staturefy$LINEAR = {0, 1, 2, 4, 5, 6, 8, 9, 10};

    @Inject(
        method = "pose(Lcom/huaxiaosheng/spapacks/client/RigPlayerModel$Frame;Lnet/minecraft/client/player/AbstractClientPlayer;FFF)V",
        at = @At(value = "INVOKE",
            target = "Lcom/huaxiaosheng/spapacks/client/PlayerAnimations;sample(Lnet/minecraft/client/player/AbstractClientPlayer;D[[FLcom/huaxiaosheng/spapacks/animation/external/ExternalPose;[[F[Lorg/joml/Quaternionf;[Lorg/joml/Vector3f;)V",
            shift = At.Shift.AFTER))
    private void staturefy$scaleHeadBone(CallbackInfo ci) {
        try {
            Object frame = staturefy$shown().get(this);
            if (frame == null) {
                return;
            }
            float[][] palette = (float[][]) staturefy$palette().get(frame);
            if (palette == null || palette.length == 0) {
                return;
            }
            int[] bones = (int[]) staturefy$bones().get(this);
            if (bones == null || bones.length == 0) {
                return;
            }
            Object playerObject = staturefy$player().get(frame);
            if (!(playerObject instanceof net.minecraft.world.entity.player.Player player)) {
                return;
            }
            if (BodyProfiles.VANILLA.id().equals(StaturefyConfig.effectiveProfile(player.getUUID()))) {
                return;
            }
            float scale = StaturefyConfig.profileFor(player.getUUID()).headScale();
            if (scale == 1.0F) {
                return;
            }
            float[] m = palette[bones[0]];
            if (m == null || m.length < 12) {
                return;
            }
            float norm = (float) Math.sqrt(m[0] * m[0] + m[1] * m[1] + m[2] * m[2]);
            if (norm < 1.0e-6F) {
                return;
            }
            float factor = scale / norm;
            for (int index : staturefy$LINEAR) {
                m[index] *= factor;
            }
        } catch (ReflectiveOperationException | LinkageError | ClassCastException ignored) {
            // SPA no está o cambió su estructura interna: seguimos sin escalar.
        }
    }

    private static Field staturefy$shown() throws ReflectiveOperationException {
        staturefy$lookup();
        return staturefy$shownField;
    }

    private static Field staturefy$palette() throws ReflectiveOperationException {
        staturefy$lookup();
        return staturefy$paletteField;
    }

    private static Field staturefy$bones() throws ReflectiveOperationException {
        staturefy$lookup();
        return staturefy$bonesField;
    }

    private static Field staturefy$player() throws ReflectiveOperationException {
        staturefy$lookup();
        return staturefy$playerField;
    }

    private static void staturefy$lookup() throws ReflectiveOperationException {
        if (staturefy$lookupDone) {
            return;
        }
        staturefy$lookupDone = true;
        ClassLoader loader = SmoothPlayerAnimationsPaletteMixin.class.getClassLoader();
        Class<?> rig = Class.forName("com.huaxiaosheng.spapacks.client.RigPlayerModel", false, loader);
        Class<?> frame = Class.forName("com.huaxiaosheng.spapacks.client.RigPlayerModel$Frame", false, loader);
        staturefy$shownField = rig.getDeclaredField("shown");
        staturefy$shownField.setAccessible(true);
        staturefy$bonesField = rig.getDeclaredField("bones");
        staturefy$bonesField.setAccessible(true);
        staturefy$paletteField = frame.getDeclaredField("palette");
        staturefy$paletteField.setAccessible(true);
        staturefy$playerField = frame.getDeclaredField("player");
        staturefy$playerField.setAccessible(true);
    }
}
