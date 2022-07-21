package cool.muyucloud.login.forge;

import cool.muyucloud.login.util.Accounts;
import cool.muyucloud.login.Login;
import cool.muyucloud.login.LoginCommand;
import cool.muyucloud.login.util.PathHandler;
import net.minecraft.server.command.CommandManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(Login.MOD_ID)
public class LoginForge {
    public LoginForge() {
        Login.init();
        Login.LOGGER.info("Loading Login in Forge environment.");

        Login.LOGGER.info("Setting game path");
        PathHandler.GAME_PATH = FMLLoader.getGamePath();

        Login.LOGGER.info("Registering commands.");
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommandEvent);
        Login.LOGGER.info("Registering stop-server events.");
        MinecraftForge.EVENT_BUS.addListener(this::onServerStoppingEvent);
    }
    @SubscribeEvent
    public void onRegisterCommandEvent(RegisterCommandsEvent registerCommandsEvent) {
        LoginCommand.register(registerCommandsEvent.getDispatcher(), registerCommandsEvent.getEnvironment() == CommandManager.RegistrationEnvironment.DEDICATED);
    }

    @SubscribeEvent
    public void onServerStoppingEvent(ServerStoppedEvent serverStoppedEvent) {
        Login.LOGGER.info("Saving account records.");
        Accounts.dump();
    }
}