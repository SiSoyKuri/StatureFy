package dev.chibi.player;

import java.util.List;

public final class BodyProfiles {
    public static final BodyProfile VANILLA = new BodyProfile("vanilla", "staturefy.profile.vanilla", 1.0F, 1.0F, 1.0F, 1.0F);
    public static final BodyProfile TEEN = new BodyProfile("teen", "staturefy.profile.teen", 0.90F, 0.84F, 0.90F, 1.30F);
    public static final BodyProfile CHIBI = new BodyProfile("chibi", "staturefy.profile.chibi", 0.78F, 0.68F, 0.78F, 1.55F);
    public static final BodyProfile TALL = new BodyProfile("tall", "staturefy.profile.tall", 1.0F, 1.06F, 1.0F, 1.0F);
    private static final List<BodyProfile> ALL = List.of(VANILLA, TEEN, CHIBI, TALL);

    private BodyProfiles() {
    }

    public static List<BodyProfile> all() {
        return ALL;
    }

    public static BodyProfile get(String id) {
        return ALL.stream().filter(profile -> profile.id().equals(id)).findFirst().orElse(VANILLA);
    }

    public static boolean contains(String id) {
        return ALL.stream().anyMatch(profile -> profile.id().equals(id));
    }
}
