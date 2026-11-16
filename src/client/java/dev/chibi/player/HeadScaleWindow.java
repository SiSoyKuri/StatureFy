package dev.chibi.player;

import net.minecraft.client.model.geom.ModelPart;

/**
 * Ventana activa para el escalado de cabeza a nivel translateAndRotate.
 * Clase normal (no mixin) para poder compartirla entre varios mixins.
 */
public final class HeadScaleWindow {
    public static final ThreadLocal<ModelPart> HEAD = new ThreadLocal<>();
    public static final ThreadLocal<Float> FACTOR = new ThreadLocal<>();

    private HeadScaleWindow() {
    }
}
