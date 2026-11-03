package dev.chibi.player;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class PlayerProfilesScreen extends Screen {
    private static final int ROW_HEIGHT = 18;
    private static final int ROW_SPACING = 20;
    private final Screen parent;
    private final int requestedPage;
    private final String initialQuery;
    private final List<Button> rowButtons = new ArrayList<>();
    private List<PlayerEntryData> allPlayers = List.of();
    private List<PlayerEntryData> filteredPlayers = List.of();
    private EditBox searchBox;
    private Button previousButton;
    private Button nextButton;
    private String query = "";
    private int page;
    private int pageCount;
    private int pageSize;

    public PlayerProfilesScreen(Screen parent) {
        this(parent, 0, "");
    }

    private PlayerProfilesScreen(Screen parent, int page, String query) {
        super(Component.translatable("staturefy.players.title"));
        this.parent = parent;
        this.requestedPage = page;
        this.initialQuery = query;
    }

    @Override
    protected void init() {
        allPlayers = knownPlayers();
        pageSize = Math.max(1, Math.min(8, (height - 158) / ROW_SPACING));

        searchBox = new EditBox(font, width / 2 - 110, 56, 220, 20,
            Component.translatable("staturefy.players.search"));
        searchBox.setHint(Component.translatable("staturefy.players.search_hint"));
        searchBox.setMaxLength(64);
        searchBox.setValue(initialQuery);
        searchBox.setResponder(this::searchChanged);
        addRenderableWidget(searchBox);

        query = initialQuery;
        page = requestedPage;
        refreshRows();
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
            .bounds(width / 2 - 100, height - 30, 200, 20).build());
        setInitialFocus(searchBox);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(font, Component.translatable("staturefy.players.help"), width / 2, 38, 0xFFFFFFFF);
        if (allPlayers.isEmpty()) {
            graphics.centeredText(font, Component.translatable("staturefy.players.empty"), width / 2,
                height / 2, 0xFFFFFFFF);
        } else if (filteredPlayers.isEmpty()) {
            graphics.centeredText(font, Component.translatable("staturefy.players.no_matches"), width / 2,
                height / 2, 0xFFFFFFFF);
        } else if (pageCount > 1) {
            graphics.centeredText(font, Component.translatable("staturefy.players.page", page + 1, pageCount),
                width / 2, height - 52, 0xFFFFFFFF);
        }
    }

    @Override
    public void onClose() {
        minecraft.setScreenAndShow(parent);
    }

    private void searchChanged(String value) {
        query = value;
        page = 0;
        refreshRows();
    }

    private void refreshRows() {
        rowButtons.forEach(this::removeWidget);
        rowButtons.clear();
        if (previousButton != null) {
            removeWidget(previousButton);
            previousButton = null;
        }
        if (nextButton != null) {
            removeWidget(nextButton);
            nextButton = null;
        }

        String needle = query.strip().toLowerCase(Locale.ROOT);
        filteredPlayers = needle.isEmpty() ? allPlayers : allPlayers.stream()
            .filter(player -> player.name().toLowerCase(Locale.ROOT).contains(needle))
            .toList();
        pageCount = Math.max(1, (filteredPlayers.size() + pageSize - 1) / pageSize);
        page = Math.max(0, Math.min(page, pageCount - 1));

        int center = width / 2;
        int start = page * pageSize;
        int end = Math.min(start + pageSize, filteredPlayers.size());
        for (int i = start; i < end; i++) {
            PlayerEntryData player = filteredPlayers.get(i);
            int y = 82 + (i - start) * ROW_SPACING;
            rowButtons.add(addRenderableWidget(Button.builder(playerLabel(player), button -> {
                    cycleProfile(player);
                    button.setMessage(playerLabel(player));
                })
                .bounds(center - 140, y, 280, ROW_HEIGHT).build()));
        }

        if (page > 0) {
            previousButton = addRenderableWidget(Button.builder(Component.translatable("staturefy.players.previous"),
                    button -> {
                        page--;
                        refreshRows();
                    })
                .bounds(center - 90, height - 56, 80, 20).build());
        }
        if (page + 1 < pageCount) {
            nextButton = addRenderableWidget(Button.builder(Component.translatable("staturefy.players.next"),
                    button -> {
                        page++;
                        refreshRows();
                    })
                .bounds(center + 10, height - 56, 80, 20).build());
        }
    }

    private List<PlayerEntryData> knownPlayers() {
        Map<UUID, String> known = new LinkedHashMap<>();
        if (minecraft.getConnection() != null) {
            for (PlayerInfo info : minecraft.getConnection().getOnlinePlayers()) {
                UUID id = info.getProfile().id();
                if (id != null && (minecraft.player == null || !id.equals(minecraft.player.getUUID()))) {
                    known.put(id, info.getProfile().name());
                }
            }
        }
        for (StaturefyConfig.PlayerOverride override : StaturefyConfig.playerOverrides()) {
            known.putIfAbsent(override.playerId(), override.name());
        }
        List<PlayerEntryData> result = new ArrayList<>();
        known.forEach((id, name) -> result.add(new PlayerEntryData(id, name)));
        result.sort(Comparator.comparing(PlayerEntryData::name, String.CASE_INSENSITIVE_ORDER));
        return result;
    }

    private Component playerLabel(PlayerEntryData player) {
        Optional<StaturefyConfig.PlayerOverride> override = StaturefyConfig.override(player.playerId());
        if (override.isPresent()) {
            return Component.translatable("staturefy.players.row_custom", player.name(),
                Component.translatable(BodyProfiles.get(override.get().profileId()).translationKey()));
        }
        return Component.translatable("staturefy.players.row_global", player.name(),
            Component.translatable(BodyProfiles.get(StaturefyConfig.othersProfile()).translationKey()));
    }

    private void cycleProfile(PlayerEntryData player) {
        List<BodyProfile> profiles = BodyProfiles.all();
        Optional<StaturefyConfig.PlayerOverride> override = StaturefyConfig.override(player.playerId());
        if (override.isEmpty()) {
            StaturefyConfig.setPlayerOverride(player.playerId(), player.name(), profiles.getFirst().id());
            return;
        }
        int index = -1;
        for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).id().equals(override.get().profileId())) {
                index = i;
                break;
            }
        }
        if (index >= 0 && index + 1 < profiles.size()) {
            StaturefyConfig.setPlayerOverride(player.playerId(), player.name(), profiles.get(index + 1).id());
        } else {
            StaturefyConfig.setPlayerOverride(player.playerId(), player.name(), null);
        }
    }

    private record PlayerEntryData(UUID playerId, String name) {
    }
}
