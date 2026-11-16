package dev.chibi.player;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import com.mojang.brigadier.Command;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

public final class StaturefyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        StaturefyConfig.load();
        // El atlas de la fuente Silkscreen muere en cada recarga de recursos;
        // sin esto el menú dibuja píxeles basura tras activar mods o packs.
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(
            new SimpleSynchronousResourceReloadListener() {
                @Override
                public Identifier getFabricId() {
                    return Identifier.fromNamespaceAndPath("staturefy", "font_reset");
                }

                @Override
                public void onResourceManagerReload(ResourceManager manager) {
                    StaturefyConfigScreen.resetFont();
                }
            });
        if (FabricLoader.getInstance().isModLoaded("punchy")) {
            try {
                PunchyIntegration.init();
            } catch (Throwable throwable) {
                StaturefyConfig.LOGGER.error("Punchy integration failed; continuing without it", throwable);
            }
        }
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(ClientCommandManager.literal("staturefy")
                .executes(context -> openConfig(context.getSource()))
                .then(ClientCommandManager.literal("config")
                    .executes(context -> openConfig(context.getSource())))));
    }

    private static int openConfig(FabricClientCommandSource source) {
        source.getClient().schedule(() -> source.getClient().setScreenAndShow(new StaturefyConfigScreen(null)));
        return Command.SINGLE_SUCCESS;
    }
}
