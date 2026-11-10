package dev.chibi.player;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import java.util.Comparator;
import java.util.List;

public final class StaturefyConfigScreen extends Screen {
    private static final List<String> PROFILE_ORDER = List.of("chibi", "teen", "vanilla", "tall");
    private final Screen parent;

    public StaturefyConfigScreen(Screen parent) {
        super(Component.translatable("staturefy.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int center = width / 2;
        List<BodyProfile> profiles = BodyProfiles.all().stream()
            .sorted(Comparator.comparingInt(profile -> {
                int order = PROFILE_ORDER.indexOf(profile.id());
                return order < 0 ? PROFILE_ORDER.size() + BodyProfiles.all().indexOf(profile) : order;
            }))
            .toList();
        int contentWidth = contentWidth();
        int gap = 12;
        int cardWidth = (contentWidth - gap * (profiles.size() - 1)) / profiles.size();
        int cardHeight = Math.max(58, Math.min(84, (height - 168) / 3));
        int cardX = center - contentWidth / 2;
        int playerCardsY = 53;
        int globalLabelY = playerCardsY + cardHeight + 11;
        int globalCardsY = globalLabelY + 16;
        int listButtonY = globalCardsY + cardHeight + 16;

        for (int i = 0; i < profiles.size(); i++) {
            BodyProfile profile = profiles.get(i);
            int x = cardX + i * (cardWidth + gap);
            addRenderableWidget(new ProfileCardButton(x, playerCardsY, cardWidth, cardHeight, profile, font,
                () -> StaturefyConfig.localProfile().equals(profile.id()),
                () -> StaturefyConfig.setLocalProfile(profile.id())));
            addRenderableWidget(new ProfileCardButton(x, globalCardsY, cardWidth, cardHeight, profile, font,
                () -> StaturefyConfig.othersProfile().equals(profile.id()),
                () -> StaturefyConfig.setOthersProfile(profile.id())));
        }

        int listButtonWidth = Math.min(260, width - 40);
        addRenderableWidget(Button.builder(Component.translatable("staturefy.config.player_profiles").withStyle(ChatFormatting.GOLD),
            button -> minecraft.setScreenAndShow(new PlayerProfilesScreen(this)))
            .bounds(center - listButtonWidth / 2, listButtonY, listButtonWidth, 26).build());
        addRenderableWidget(Button.builder(Component.translatable("staturefy.common.done"), button -> onClose())
            .bounds(center - Math.min(100, listButtonWidth / 2), height - 32,
                Math.min(200, listButtonWidth), 24).build());
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int center = width / 2;
        int panelWidth = Math.min(width - 24, contentWidth() + 40);
        int left = center - panelWidth / 2;
        int right = left + panelWidth;
        int top = 0;
        int bottom = height - 4;
        graphics.fill(left, top, right, bottom, 0xD9212428);
        graphics.fill(left, top, right, top + 2, 0xFFFFB52D);
        graphics.fill(left, bottom - 2, right, bottom, 0xFFFFB52D);
        graphics.fill(left, top, left + 2, bottom, 0xFF777777);
        graphics.fill(right - 2, top, right, bottom, 0xFF777777);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        int center = width / 2;
        int panelWidth = Math.min(width - 24, contentWidth() + 40);
        int left = center - panelWidth / 2;
        int right = left + panelWidth;
        int playerCardsY = 53;
        int cardHeight = Math.max(58, Math.min(84, (height - 168) / 3));
        int globalLabelY = playerCardsY + cardHeight + 11;
        drawTitle(graphics, center);
        graphics.fill(left + 24, 34, right - 24, 35, 0xFF777777);
        graphics.fill(left + 24, globalLabelY - 5, right - 24, globalLabelY - 4, 0xFF777777);
        graphics.centeredText(font, Component.translatable("staturefy.config.player_section")
            .withStyle(ChatFormatting.BOLD), center, 39, 0xFFFFD45A);
        graphics.centeredText(font, Component.translatable("staturefy.config.global_section")
            .withStyle(ChatFormatting.BOLD), center, globalLabelY, 0xFFFFD45A);
    }

    private int contentWidth() {
        return Math.max(1, Math.min(460, width - 40));
    }

    private void drawTitle(GuiGraphicsExtractor graphics, int center) {
        Component title = Component.translatable("staturefy.config.title").withStyle(ChatFormatting.BOLD);
        float scale = 2.2F;
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale, scale);
        graphics.centeredText(font, title, Math.round(center / scale), 3, 0xFF8B4C00);
        graphics.pose().popMatrix();
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale, scale);
        graphics.centeredText(font, title, Math.round(center / scale), 2, 0xFFFFD45A);
        graphics.pose().popMatrix();
    }

    @Override
    public void onClose() {
        minecraft.setScreenAndShow(parent);
    }
}
