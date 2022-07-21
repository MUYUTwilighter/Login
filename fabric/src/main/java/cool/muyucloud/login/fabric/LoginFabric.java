package cool.muyucloud.login.fabric;

import cool.muyucloud.login.util.Accounts;
import cool.muyucloud.login.Login;
import cool.muyucloud.login.LoginCommand;
import cool.muyucloud.login.util.PathHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

public class LoginFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Login.init();
        Login.LOGGER.info("Loading Login mod in Fabric environment.");

        Login.LOGGER.info("Setting game path");
        PathHandler.GAME_PATH = FabricLoader.getInstance().getGameDir();

        Login.LOGGER.info("Registering commands");
        CommandRegistrationCallback.EVENT.register(LoginCommand::register);
        Login.LOGGER.info("Commands registered");

        Login.LOGGER.info("Registering server-stopping events.");
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
        Login.LOGGER.info("Server-stopping events registered");
    }

    private void onServerStopping(MinecraftServer server) {
        Login.LOGGER.info("Saving account records.");
        Accounts.dump();
    }
}