package dev.chibi.player;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class StaturefyConfigScreen extends Screen {
    private final Screen parent;

    public StaturefyConfigScreen(Screen parent) {
        super(Component.translatable("staturefy.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int center = width / 2;
        addRenderableWidget(CycleButton.builder(profile -> Component.translatable(profile.translationKey()),
                BodyProfiles.get(StaturefyConfig.localProfile()))
            .withValues(BodyProfiles.all())
            .create(center - 100, 58, 200, 20, Component.translatable("staturefy.config.local_profile"),
                (button, profile) -> StaturefyConfig.setLocalProfile(profile.id())));
        addRenderableWidget(CycleButton.builder(profile -> Component.translatable(profile.translationKey()),
                BodyProfiles.get(StaturefyConfig.othersProfile()))
            .withValues(BodyProfiles.all())
            .create(center - 100, 105, 200, 20, Component.translatable("staturefy.config.others_profile"),
                (button, profile) -> StaturefyConfig.setOthersProfile(profile.id())));
        addRenderableWidget(Button.builder(Component.translatable("staturefy.config.player_profiles"),
            button -> minecraft.setScreenAndShow(new PlayerProfilesScreen(this)))
            .bounds(center - 100, 160, 200, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
            .bounds(center - 100, height - 30, 200, 20).build());
    }

    @Override
    public void onClose() {
        minecraft.setScreenAndShow(parent);
    }
}
