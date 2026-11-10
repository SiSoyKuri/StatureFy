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
    private final float u;

    ProfileCardButton(int x, int y, int width, int height, BodyProfile profile, Font font, float u,
                      BooleanSupplier selected, Runnable onSelect) {
        super(x, y, width, height, Component.translatable(profile.translationKey()), button -> onSelect.run(),
                DEFAULT_NARRATION);
        this.profile = profile;
        this.font = font;
        this.selected = selected;
        this.u = u;
    }

    BodyProfile profile() {
        return profile;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        int right = x + getWidth();
        int bottom = y + getHeight();
        boolean active = selected.getAsBoolean();
        int bg = isHoveredOrFocused() ? 0xFF383838 : 0xFF333333;

        graphics.fill(x, y, right, bottom, bg);
        // Todas las tarjetas: borde dorado + esquinas celestes; verde si está seleccionada.
        int c = active ? 0xFF3FD35A : 0xFFFFD45A;
        int k = active ? 0xFF9FFFA8 : 0xFF4FD8E8;
        graphics.fill(x, y, right, y + 1, c);
        graphics.fill(x, bottom - 1, right, bottom, c);
        graphics.fill(x, y, x + 1, bottom, c);
        graphics.fill(right - 1, y, right, bottom, c);
        graphics.fill(x + 1, y + 1, x + 3, y + 3, k);
        graphics.fill(right - 3, y + 1, right - 1, y + 3, k);
        graphics.fill(x + 1, bottom - 3, x + 3, bottom - 1, k);
        graphics.fill(right - 3, bottom - 3, right - 1, bottom - 1, k);

        int labelTop = bottom - LABEL_HEIGHT - 2;
        graphics.fill(x + 2, labelTop, right - 2, bottom - 2, 0xFFC7C7C7);
        graphics.pose().pushMatrix();
        graphics.pose().scale(u, u);
        graphics.centeredText(font,
                Component.translatable(profile.translationKey()).withStyle(ChatFormatting.BOLD),
                Math.round((x + getWidth() / 2) / u), Math.round((labelTop + 6) / u), 0xFF3A3A3A);
        graphics.pose().popMatrix();
        drawFigure(graphics, x + getWidth() / 2, y + 6, labelTop - (y + 8));
    }

    private void drawFigure(GuiGraphicsExtractor graphics, int centerX, int top, int availableHeight) {
        // Amplificamos las diferencias de escala para que se distingan a simple vista
        // (vanilla 1.0 vs alto 1.06 sería invisible sin esto). Manteniendo el centro.
        float vy = Math.max(0.55F, Math.min(1.35F, 1.0F + (profile.bodyScaleY() - 1.0F) * 2.5F));
        float vx = Math.max(0.55F, Math.min(1.35F, 1.0F + (profile.bodyScaleX() - 1.0F) * 2.5F));
        float headFactor = profile.headScale();
        float bodyFactor = vy;
        float totalUnits = 5.0F * headFactor + 7.0F * bodyFactor + 6.0F * bodyFactor;
        float unit = Math.min(3.6F, (availableHeight - 2) / totalUnits);

        int headWidth = Math.round(6.0F * headFactor * unit);
        int headHeight = Math.round(5.0F * headFactor * unit);
        int torsoWidth = Math.round(6.0F * vx * unit);
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
