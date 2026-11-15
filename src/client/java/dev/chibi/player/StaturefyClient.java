package dev.chibi.player;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import com.mojang.brigadier.Command;
import net.fabricmc.loader.api.FabricLoader;

public final class StaturefyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        StaturefyConfig.load();
        if (FabricLoader.getInstance().isModLoaded("punchy")) {
            try {
                PunchyIntegration.init();
            } catch (Throwable throwable) {
                StaturefyConfig.LOGGER.error("Punchy integration failed; continuing without it", throwable);
            }
        }
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(ClientCommands.literal("staturefy")
                .executes(context -> openConfig(context.getSource()))
                .then(ClientCommands.literal("config")
                    .executes(context -> openConfig(context.getSource())))));
    }

    private static int openConfig(FabricClientCommandSource source) {
        source.getClient().schedule(() -> source.getClient().setScreenAndShow(new StaturefyConfigScreen(null)));
        return Command.SINGLE_SUCCESS;
    }
}
