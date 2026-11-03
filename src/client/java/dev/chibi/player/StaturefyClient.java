package dev.chibi.player;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import com.mojang.brigadier.Command;

public final class StaturefyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        StaturefyConfig.load();
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
