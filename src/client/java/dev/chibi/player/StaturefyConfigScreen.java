package dev.chibi.player;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.EffectGlyph;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import dev.chibi.player.mixin.FontManagerAccessor;
import dev.chibi.player.mixin.MinecraftAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class StaturefyConfigScreen extends Screen {
    private static final List<String> PROFILE_ORDER = List.of("chibi", "teen", "vanilla", "tall");
    private static final long PREVIEW_DURATION_MS = 4000;
    private static Font staturefyFont;
    private final Screen parent;
    private final List<ProfileCardButton> cards = new ArrayList<>();
    private int[] previewBox;
    private BodyProfile previewProfile;
    private long previewShowUntil;
    @SuppressWarnings("unused")
    private final Font font = staturefyFont();

    public StaturefyConfigScreen(Screen parent) {
        super(Component.translatable("staturefy.config.title"));
        this.parent = parent;
    }

    static Font staturefyFont() {
        if (staturefyFont == null) {
            FontManager fm = ((MinecraftAccessor) Minecraft.getInstance()).staturefy$getFontManager();
            FontSet set = ((FontManagerAccessor) fm).staturefy$getFontSetRaw(
                    Identifier.fromNamespaceAndPath("staturefy", "silkscreen"));
            staturefyFont = new Font(new Font.Provider() {
                @Override
                public GlyphSource glyphs(FontDescription description) {
                    return set.source(false);
                }

                @Override
                public EffectGlyph effect() {
                    return set.whiteGlyph();
                }
            });
        }
        return staturefyFont;
    }

    @Override
    protected void init() {
        cards.clear();
        int center = width / 2;
        List<BodyProfile> profiles = BodyProfiles.all().stream()
            .sorted(Comparator.comparingInt(profile -> {
                int order = PROFILE_ORDER.indexOf(profile.id());
                return order < 0 ? PROFILE_ORDER.size() + BodyProfiles.all().indexOf(profile) : order;
            }))
            .toList();
        float u = height / 360.0F;
        int contentWidth = contentWidth(u);
        int gap = Math.round(12 * u);
        int cardWidth = (contentWidth - gap * (profiles.size() - 1)) / profiles.size();
        int cardHeight = Math.max(40, Math.round(60 * u));
        int cardX = center - contentWidth / 2;
        int playerCardsY = Math.round(60 * u);
        int globalLabelY = playerCardsY + cardHeight + Math.round(10 * u);
        int globalCardsY = globalLabelY + Math.round(16 * u);
        int bottomRowY = globalCardsY + cardHeight + Math.round(10 * u);

        for (int i = 0; i < profiles.size(); i++) {
            BodyProfile profile = profiles.get(i);
            int x = cardX + i * (cardWidth + gap);
            ProfileCardButton playerCard = new ProfileCardButton(x, playerCardsY, cardWidth, cardHeight, profile, font, u, 
                () -> StaturefyConfig.localProfile().equals(profile.id()),
                () -> {
                    StaturefyConfig.setLocalProfile(profile.id());
                    showPreview(profile);
                });
            ProfileCardButton globalCard = new ProfileCardButton(x, globalCardsY, cardWidth, cardHeight, profile, font, u, 
                () -> StaturefyConfig.othersProfile().equals(profile.id()),
                () -> StaturefyConfig.setOthersProfile(profile.id()));
            addRenderableWidget(playerCard);
            addRenderableWidget(globalCard);
            cards.add(playerCard);
            cards.add(globalCard);
        }

        int rowBottom = height - Math.round(34 * u);
        int rowHeight = rowBottom - bottomRowY;
        if (rowHeight >= Math.round(80 * u)) {
            int previewWidth = Math.min(Math.round(96 * u), width - 60);
            int previewHeight = Math.min(rowHeight, Math.round(110 * u));
            int buttonWidth = Math.min(Math.round(120 * u), width - 60);
            int groupWidth = previewWidth + 16 + buttonWidth;
            int groupX = center - groupWidth / 2;
            int previewY = bottomRowY + (rowHeight - previewHeight) / 2;
            previewBox = new int[]{groupX, previewY, groupX + previewWidth, previewY + previewHeight};
            int buttonY = previewY + (previewHeight - Math.round(26 * u)) / 2;
            addRenderableWidget(new TwoLineButton(groupX + previewWidth + Math.round(16 * u), buttonY, buttonWidth, Math.round(26 * u),
                Component.translatable("staturefy.config.player_profiles"), font, u,
                button -> minecraft.setScreenAndShow(new PlayerProfilesScreen(this))));
        } else {
            previewBox = null;
            int listButtonWidth = Math.min(Math.round(260 * u), width - 40);
            addRenderableWidget(Button.builder(Component.translatable("staturefy.config.player_profiles").withStyle(ChatFormatting.GOLD),
                button -> minecraft.setScreenAndShow(new PlayerProfilesScreen(this)))
                .bounds(center - listButtonWidth / 2, bottomRowY, listButtonWidth, Math.round(26 * u)).build());
        }
        int doneWidth = Math.min(200, Math.min(Math.round(260 * u), width - 40));
        addRenderableWidget(new TwoLineButton(
            center - doneWidth / 2, height - Math.round(32 * u),
            doneWidth, Math.round(24 * u),
            Component.translatable("staturefy.common.done"), font, u, button -> onClose()));
    }

    private void showPreview(BodyProfile profile) {
        previewProfile = profile;
        previewShowUntil = System.currentTimeMillis() + PREVIEW_DURATION_MS;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int center = width / 2;
        int panelWidth = Math.min(width - Math.round(24 * uiScale()), contentWidth(uiScale()) + Math.round(40 * uiScale()));
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
        int panelWidth = Math.min(width - Math.round(24 * uiScale()), contentWidth(uiScale()) + Math.round(40 * uiScale()));
        int left = center - panelWidth / 2;
        int right = left + panelWidth;
        float u = height / 360.0F;
        int playerCardsY = Math.round(60 * u);
        int cardHeight = Math.max(40, Math.round(60 * u));
        int globalLabelY = playerCardsY + cardHeight + Math.round(10 * u);
        int globalCardsY = globalLabelY + Math.round(16 * u);
        drawTitle(graphics, center);
        drawSectionLabel(graphics, font, "staturefy.config.player_section", center, Math.round(52 * u), u);
        drawSectionLabel(graphics, font, "staturefy.config.global_section", center, globalLabelY + Math.round(4 * u), u);

        if (previewBox != null && previewProfile != null
                && System.currentTimeMillis() < previewShowUntil) {
            drawPreviewBox(graphics, mouseX, mouseY, partialTick, previewProfile,
                previewBox[0], previewBox[1], previewBox[2], previewBox[3]);
        }
    }

    private void drawPreviewBox(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick,
                                BodyProfile profile, int x1, int y1, int x2, int y2) {

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) {
            return;
        }
        EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();
        @SuppressWarnings("unchecked")
        EntityRenderer<Player, EntityRenderState
> renderer =
                (EntityRenderer<Player, EntityRenderState>) (EntityRenderer<?, ?>) dispatcher.getRenderer(player);
        EntityRenderState state = renderer.createRenderState(player, partialTick);
        state.shadowPieces.clear();
        if (!(state instanceof AvatarRenderState avatar)) {
            return;
        }

        float centerX = (x1 + x2) / 2.0F;
        float centerY = (y1 + y2) / 2.0F;
        float xAngle = (float) Math.atan((centerX - mouseX) / 40.0F);
        float yAngle = (float) Math.atan((centerY - mouseY) / 40.0F);
        if (state instanceof LivingEntityRenderState living) {
            living.bodyRot = 180.0F + xAngle * 20.0F;
            living.yRot = xAngle * 20.0F;
            living.xRot = -yAngle * 20.0F;
            living.boundingBoxWidth /= living.scale;
            living.boundingBoxHeight /= living.scale;
            living.scale = 1.0F;
        }
        ((AvatarPreviewDuck) avatar).staturefy$setPreviewProfile(profile);

        int boxHeight = y2 - y1 - 10;
        int scale = Math.max(14, Math.round(boxHeight * 0.45F));
        Vector3f offset = new Vector3f(0.0F,
                state.boundingBoxHeight * profile.bodyScaleY() / 2.0F, 0.0F);
        Quaternionf flip = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf camera = new Quaternionf().rotateX(yAngle * 20.0F * 0.017453292F);
        graphics.entity(state, scale, offset, flip, camera, x1 + 2, y1 + 2, x2 - 2, y2 - 2);
    }

    static void drawSectionLabel(GuiGraphicsExtractor graphics, Font font, String key, int centerX, int deviceY, float u) {
        graphics.pose().pushMatrix();
        graphics.pose().scale(u, u);
        graphics.centeredText(font, Component.translatable(key).withStyle(ChatFormatting.BOLD),
                Math.round(centerX / u), Math.round(deviceY / u), 0xFFFFD45A);
        graphics.pose().popMatrix();
    }

    static float uiScale() {
        return Minecraft.getInstance().getWindow().getGuiScaledHeight() / 360.0F;
    }

    private int contentWidth(float u) {
        return Math.max(1, Math.min(Math.round(460 * u), width - Math.round(40 * u)));
    }

    private void drawTitle(GuiGraphicsExtractor graphics, int center) {
        Component title = Component.translatable("staturefy.config.title");
        float scale = 2.2F * height / 360.0F;
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale, scale);
        graphics.centeredText(font, title, Math.round(center / scale), 7, 0xFF1A6B1F);
        graphics.pose().popMatrix();
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale, scale);
        graphics.centeredText(font, title, Math.round(center / scale), 6, 0xFF2FBF3F);
        graphics.pose().popMatrix();
    }

    @Override
    public void onClose() {
        minecraft.setScreenAndShow(parent);
    }

    static final class TwoLineButton extends Button {
        private final Font font;
        private final float u;

        TwoLineButton(int x, int y, int width, int height, Component message, Font font, float u, OnPress onPress) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
            this.font = font;
            this.u = u;
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0xFFF2B138);
            if (isHoveredOrFocused()) {
                graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0xFFFFC65C);
            }
            graphics.fill(getX(), getY() + getHeight() - 2, getX() + getWidth(), getY() + getHeight(), 0xFF8A5A1D);
            graphics.fill(getX() + getWidth() - 2, getY(), getX() + getWidth(), getY() + getHeight(), 0xFF8A5A1D);
            String[] words = getMessage().getString().split(" ");
            Component line1;
            Component line2;
            if (words.length < 2) {
                line1 = getMessage();
                line2 = null;
            } else {
                line1 = Component.literal(String.join(" ",
                        java.util.Arrays.copyOfRange(words, 0, words.length - 1)));
                line2 = Component.literal(words[words.length - 1]);
            }
            int centerX = getX() + getWidth() / 2;
            int centerY = getY() + getHeight() / 2;
            graphics.pose().pushMatrix();
            graphics.pose().scale(u, u);
            if (line2 == null) {
                graphics.centeredText(font, line1, Math.round(centerX / u),
                        Math.round((centerY - 4) / u), 0xFFE8E8E8);
            } else {
                graphics.centeredText(font, line1, Math.round(centerX / u),
                        Math.round((centerY - 10) / u), 0xFFE8E8E8);
                graphics.centeredText(font, line2, Math.round(centerX / u),
                        Math.round((centerY + 1) / u), 0xFFE8E8E8);
            }
            graphics.pose().popMatrix();
        }
    }
}
