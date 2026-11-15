package dev.chibi.player;

import java.util.ArrayDeque;
import java.util.Deque;

public final class StaturefyRenderContext {
    private static final ThreadLocal<Deque<BodyProfile>> PROFILES = ThreadLocal.withInitial(ArrayDeque::new);

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
}
