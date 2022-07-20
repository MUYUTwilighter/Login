package cool.muyucloud.login.fabric;

import cool.muyucloud.login.Login;
import net.fabricmc.api.ModInitializer;

public class LoginFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Login.init();
    }
}