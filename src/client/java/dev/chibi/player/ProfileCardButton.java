package dev.chibi.player;

import java.util.function.BooleanSupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

final class ProfileCardButton extends Button {
    private static final int LABEL_HEIGHT = 20;
    private static final int FIGURE_COLOR = 0xFF898989;

    private final BodyProfile profile;
    private final Font font;
    private final BooleanSupplier selected;

    ProfileCardButton(int x, int y, int width, int height, BodyProfile profile, Font font,
                      BooleanSupplier selected, Runnable onSelect) {
        super(x, y, width, height, Component.translatable(profile.translationKey()), button -> onSelect.run(),
                DEFAULT_NARRATION);
        this.profile = profile;
        this.font = font;
        this.selected = selected;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        int right = x + getWidth();
        int bottom = y + getHeight();
        boolean active = selected.getAsBoolean();
        int border = active ? 0xFF40D52A : isHoveredOrFocused() ? 0xFFFFC247 : 0xFF242424;

        graphics.fill(x, y, right, bottom, 0xFF242424);
        graphics.fill(x + 2, y + 2, right - 2, bottom - 2, isHovered() ? 0xFF5A5A5A : 0xFF4B4B4B);
        graphics.fill(x, y, right, y + 2, border);
        graphics.fill(x, bottom - 2, right, bottom, border);
        graphics.fill(x, y, x + 2, bottom, border);
        graphics.fill(right - 2, y, right, bottom, border);

        int labelTop = bottom - LABEL_HEIGHT - 2;
        graphics.fill(x + 2, labelTop, right - 2, bottom - 2, 0xFFC7C7C7);
        drawFigure(graphics, x + getWidth() / 2, y + 5, labelTop - (y + 5));
        graphics.centeredText(font,
                Component.translatable(profile.translationKey()).withStyle(ChatFormatting.BOLD),
                x + getWidth() / 2, labelTop + 6, 0xFF202020);
    }

    private void drawFigure(GuiGraphicsExtractor graphics, int centerX, int top, int availableHeight) {
        float headFactor = profile.headScale();
        float bodyFactor = profile.bodyScaleY();
        float totalUnits = 5.0F * headFactor + 7.0F * bodyFactor + 6.0F * bodyFactor;
        float unit = Math.min(3.2F, availableHeight / totalUnits);

        int headWidth = Math.round(6.0F * headFactor * unit);
        int headHeight = Math.round(5.0F * headFactor * unit);
        int torsoWidth = Math.round(6.0F * unit);
        int torsoHeight = Math.round(7.0F * bodyFactor * unit);
        int armWidth = Math.max(2, Math.round(1.7F * unit));
        int armHeight = Math.max(2, Math.round(torsoHeight * 0.8F));
        int legWidth = Math.max(2, Math.round(2.2F * unit));
        int legHeight = Math.round(6.0F * bodyFactor * unit);
        int legGap = Math.max(1, Math.round(0.8F * unit));
        int totalHeight = headHeight + torsoHeight + legHeight;
        int figureTop = top + Math.max(0, (availableHeight - totalHeight) / 2);
        int headLeft = centerX - headWidth / 2;
        int torsoLeft = centerX - torsoWidth / 2;
        int torsoTop = figureTop + headHeight;
        int armTop = torsoTop + Math.max(1, Math.round(unit));
        int legTop = torsoTop + torsoHeight;

        graphics.fill(headLeft, figureTop, headLeft + headWidth, torsoTop, FIGURE_COLOR);
        graphics.fill(torsoLeft, torsoTop, torsoLeft + torsoWidth, legTop, FIGURE_COLOR);
        graphics.fill(torsoLeft - armWidth, armTop, torsoLeft, armTop + armHeight, FIGURE_COLOR);
        graphics.fill(torsoLeft + torsoWidth, armTop, torsoLeft + torsoWidth + armWidth,
                armTop + armHeight, FIGURE_COLOR);
        int legsWidth = legWidth * 2 + legGap;
        int legsLeft = centerX - legsWidth / 2;
        graphics.fill(legsLeft, legTop, legsLeft + legWidth, legTop + legHeight, FIGURE_COLOR);
        graphics.fill(legsLeft + legWidth + legGap, legTop, legsLeft + legsWidth,
                legTop + legHeight, FIGURE_COLOR);
    }
}
