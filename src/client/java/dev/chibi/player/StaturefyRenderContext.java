package dev.chibi.player;

import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.model.geom.ModelPart;

public final class StaturefyRenderContext {
    private static final ThreadLocal<Deque<BodyProfile>> PROFILES = ThreadLocal.withInitial(ArrayDeque::new);
    private static final ThreadLocal<VanillaRigTree> VANILLA_RIG_TREE = new ThreadLocal<>();

    private StaturefyRenderContext() {
    }

    public static void push(BodyProfile profile) {
        PROFILES.get().push(profile);
    }

    public static void pop() {
        Deque<BodyProfile> profiles = PROFILES.get();
        if (!profiles.isEmpty()) {
            profiles.pop();
        }
        if (profiles.isEmpty()) {
            PROFILES.remove();
        }
    }

    public static BodyProfile current() {
        Deque<BodyProfile> profiles = PROFILES.get();
        return profiles.isEmpty() ? BodyProfiles.VANILLA : profiles.peek();
    }

    public static void beginVanillaRigTree(ModelPart root, ModelPart[] parts, boolean[] slot) {
        if (VANILLA_RIG_TREE.get() != null) {
            return;
        }
        boolean[] wasVisible = new boolean[parts.length];
        for (int i = 0; i < parts.length; i++) {
            wasVisible[i] = parts[i].visible;
            parts[i].visible = wasVisible[i] && i < slot.length && slot[i];
        }
        // La cabeza la escala ModelMixin (cubre todo HumanoidModel); aquí solo
        // enmascaramos visibilidad para que el casco vanilla reemplace el rig.
        VANILLA_RIG_TREE.set(new VanillaRigTree(root, parts, wasVisible));
    }

    public static boolean vanillaRigTreeActive() {
        return VANILLA_RIG_TREE.get() != null;
    }

    public static void exitModelPart(ModelPart part) {
        VanillaRigTree tree = VANILLA_RIG_TREE.get();
        if (tree == null || tree.root != part) {
            return;
        }
        for (int i = 0; i < tree.parts.length; i++) {
            tree.parts[i].visible = tree.wasVisible[i];
        }
        VANILLA_RIG_TREE.remove();
    }

    private static final class VanillaRigTree {
        private final ModelPart root;
        private final ModelPart[] parts;
        private final boolean[] wasVisible;

        private VanillaRigTree(ModelPart root, ModelPart[] parts, boolean[] wasVisible) {
            this.root = root;
            this.parts = parts;
            this.wasVisible = wasVisible;
        }
    }
}
