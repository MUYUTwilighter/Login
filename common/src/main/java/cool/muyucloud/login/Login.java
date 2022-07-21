package cool.muyucloud.login;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class Login {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String MOD_ID = "login";

    public static void init() {
        LOGGER.info("Initializing Login mod.");
    }
}