package cool.muyucloud.login;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;


public class Login implements ModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        LOGGER.info("Login mod initializing.");
        LOGGER.info("Initializing commands");
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> LoginCommand.register(dispatcher, environment)
        );
        LOGGER.info("Initialized commands");
        LOGGER.info("Registering server-stopping events.");
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
        LOGGER.info("Server-stopping events registered");
    }

    private void onServerStopping(MinecraftServer server) {
        LOGGER.info("Saving account records.");
        Accounts.dump();
    }
}
