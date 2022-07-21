package cool.muyucloud.login.util;

import cool.muyucloud.login.Login;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.Properties;

public class Accounts {
    private static final Accounts ACCOUNTS = new Accounts();

    private final Properties RECORDS = new Properties();
    private final Path RECORD_PATH = PathHandler.GAME_PATH.resolve("config/login.properties");
    private final HashSet<String> LOGGED_IN_LIST = new HashSet<>();

    private Accounts() {
        this.loadFromFile();
    }

    public static void addList(String name) {
        ACCOUNTS.LOGGED_IN_LIST.add(name);
    }

    public static void removeList(String name) {
        ACCOUNTS.LOGGED_IN_LIST.remove(name);
    }

    public static boolean isLoggedIn(String name) {
        return ACCOUNTS.LOGGED_IN_LIST.contains(name);
    }

    public static boolean isNewPlayer(String name) {
        return !ACCOUNTS.RECORDS.containsKey(name);
    }

    public static boolean verify(String name, String password) {
        return Objects.equals(ACCOUNTS.RECORDS.getProperty(name), password);
    }

    public static void register(String name, String password) {
        ACCOUNTS.RECORDS.setProperty(name, password);
    }

    public static void dump() {
        try {
            ACCOUNTS.RECORDS.store(Files.newOutputStream(ACCOUNTS.RECORD_PATH), "");
        } catch (Exception e) {
            Login.LOGGER.warn("Failed to store account records.");
            e.printStackTrace();
        }
    }

    public static void changePassword(String name, String password) {
        ACCOUNTS.RECORDS.setProperty(name, password);
    }

    private void loadFromFile() {
        Login.LOGGER.info("Reading account records.");
        if (!Files.exists(RECORD_PATH)) {
            Login.LOGGER.info("Failed to find record files, generating a new one.");
            // try to create new record file
            try {
                Files.createFile(RECORD_PATH);
                Login.LOGGER.info("Record file generated.");
            } catch (Exception e) {
                Login.LOGGER.warn("Failed to generate record file.");
                e.printStackTrace();
            }
        } else {
            // try to read records from file
            try (InputStream inputStream = Files.newInputStream(RECORD_PATH)) {
                this.RECORDS.load(inputStream);
                Login.LOGGER.info("Records successfully read.");
            } catch (Exception e) {
                Login.LOGGER.warn("Failed to read record file.");
                e.printStackTrace();
            }
        }
    }

    public static void load() {
        ACCOUNTS.loadFromFile();
    }

    public static String query(String name) {
        return ACCOUNTS.RECORDS.getProperty(name);
    }
}
