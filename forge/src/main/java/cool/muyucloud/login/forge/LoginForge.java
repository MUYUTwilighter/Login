package cool.muyucloud.login.forge;

import cool.muyucloud.login.Login;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Login.MOD_ID)
public class LoginForge {
    public LoginForge() {
        Login.init();
    }
}