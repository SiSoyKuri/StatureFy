package dev.chibi.player;

import punchy.api.PunchyApi;
import punchy.client.menu.api.Text;

/**
 * All calls into the Punchy API live in this class so Staturefy keeps working
 * when Punchy is not installed. Only load it after checking that the mod
 * "punchy" is present (see StaturefyClient).
 */
public final class PunchyIntegration {
    private static final String MOD_ID = "staturefy";

    private PunchyIntegration() {
    }

    public static void init() {
        PunchyApi.menuRows(MOD_ID, "compat", null, tab -> tab.toggle(
            "hand_scale",
            Text.key("staturefy.punchy.hand_scale", "Staturefy: first-person hand scale"),
            Text.key("staturefy.punchy.hand_scale_desc",
                "Scales the first-person hand and held items with the selected body profile"),
            StaturefyConfig::firstPersonHandScale,
            StaturefyConfig::setFirstPersonHandScale,
            true));
        PunchyApi.armPose(MOD_ID + "/hand_scale", "final", pose -> {
            if (!StaturefyConfig.firstPersonHandScale()) {
                return;
            }
            float factor = StaturefyConfig.handScaleFactor();
            if (factor != 1.0F) {
                pose.pivot("shoulder").scale(factor);
            }
        });
    }

    /** Whether Punchy is currently drawing the requested hand itself. */
    public static boolean handOwnerIsPunchy(boolean mainHand) {
        return PunchyApi.isActive() && "punchy".equals(PunchyApi.handOwner(mainHand));
    }
}
