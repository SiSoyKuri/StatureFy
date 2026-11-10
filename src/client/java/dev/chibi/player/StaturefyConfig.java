package dev.chibi.player;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class StaturefyConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("staturefy");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("staturefy.json");
    private static final Map<UUID, PlayerOverride> PLAYER_OVERRIDES = new LinkedHashMap<>();
    private static String localProfile = BodyProfiles.VANILLA.id();
    private static String othersProfile = BodyProfiles.VANILLA.id();
    private static boolean loaded;

    private StaturefyConfig() {
    }

    public static void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        if (!Files.exists(FILE)) {
            return;
        }

        try (Reader reader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (!root.isJsonObject()) {
                throw new IllegalStateException("Root must be a JSON object");
            }
            JsonObject json = root.getAsJsonObject();
            localProfile = string(json, "local_profile", localProfile);
            othersProfile = string(json, "others_profile", othersProfile);
            JsonElement players = json.get("players");
            if (players != null && players.isJsonObject()) {
                for (Map.Entry<String, JsonElement> entry : players.getAsJsonObject().entrySet()) {
                    try {
                        UUID id = UUID.fromString(entry.getKey());
                        if (!entry.getValue().isJsonObject()) {
                            continue;
                        }
                        JsonObject player = entry.getValue().getAsJsonObject();
                        String profile = string(player, "profile", null);
                        if (profile != null) {
                            PLAYER_OVERRIDES.put(id, new PlayerOverride(id,
                                string(player, "name", id.toString()), profile));
                        }
                    } catch (IllegalArgumentException ignored) {
                        LOGGER.warn("Ignoring invalid player UUID in {}: {}", FILE, entry.getKey());
                    }
                }
            }
        } catch (IOException | RuntimeException exception) {
            LOGGER.error("Could not load {}; using default body profiles", FILE, exception);
        }
    }

    public static String localProfile() {
        return localProfile;
    }

    public static String othersProfile() {
        return othersProfile;
    }

    public static String effectiveProfile(UUID playerId) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.player.getUUID().equals(playerId)) {
            return localProfile;
        }
        PlayerOverride override = PLAYER_OVERRIDES.get(playerId);
        return override == null ? othersProfile : override.profileId();
    }

    public static BodyProfile profileFor(AvatarRenderState state) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return BodyProfiles.VANILLA;
        }
        Entity entity = minecraft.level.getEntity(state.id);
        return entity instanceof Player player
            ? BodyProfiles.get(effectiveProfile(player.getUUID())) : BodyProfiles.VANILLA;
    }

    public static Optional<PlayerOverride> override(UUID playerId) {
        return Optional.ofNullable(PLAYER_OVERRIDES.get(playerId));
    }

    public static List<PlayerOverride> playerOverrides() {
        return List.copyOf(PLAYER_OVERRIDES.values());
    }

    public static void setLocalProfile(String profileId) {
        if (!BodyProfiles.contains(profileId) || localProfile.equals(profileId)) {
            return;
        }
        localProfile = profileId;
        save();
    }

    public static void setOthersProfile(String profileId) {
        if (!BodyProfiles.contains(profileId) || othersProfile.equals(profileId)) {
            return;
        }
        othersProfile = profileId;
        save();
    }

    public static void setPlayerOverride(UUID playerId, String playerName, String profileId) {
        if (profileId == null) {
            if (PLAYER_OVERRIDES.remove(playerId) != null) {
                save();
            }
            return;
        }
        if (!BodyProfiles.contains(profileId)) {
            return;
        }
        String safeName = playerName == null || playerName.isBlank() ? playerId.toString() : playerName;
        PlayerOverride updated = new PlayerOverride(playerId, safeName, profileId);
        if (!updated.equals(PLAYER_OVERRIDES.put(playerId, updated))) {
            save();
        }
    }

    private static String string(JsonObject json, String key, String fallback) {
        JsonElement value = json.get(key);
        return value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()
            ? value.getAsString() : fallback;
    }

    private static void save() {
        JsonObject root = new JsonObject();
        root.addProperty("local_profile", localProfile);
        root.addProperty("others_profile", othersProfile);
        JsonObject players = new JsonObject();
        for (PlayerOverride override : PLAYER_OVERRIDES.values()) {
            JsonObject player = new JsonObject();
            player.addProperty("name", override.name());
            player.addProperty("profile", override.profileId());
            players.add(override.playerId().toString(), player);
        }
        root.add("players", players);

        Path temporary = FILE.resolveSibling(FILE.getFileName() + ".tmp");
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                GSON.toJson(root, writer);
            }
            try {
                Files.move(temporary, FILE, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            LOGGER.error("Could not save {}", FILE, exception);
        }
    }

    public record PlayerOverride(UUID playerId, String name, String profileId) {
    }
}
