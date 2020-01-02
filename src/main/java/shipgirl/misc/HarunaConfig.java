package shipgirl.misc;

import shipgirl.Haruna;
import org.json.JSONTokener;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;

public class HarunaConfig {
    String DBLAuth = null;

    public String Weebhook = null;
    public String PostWeebhook = null;
    public boolean Debug = false;
    public String Prefix = null;
    public String RestAuth = null;
    public int Port = 1024;
    public long UserTimeout = 43200000;

    public int Threads = Runtime.getRuntime().availableProcessors();

    private final String HarunaVersion = getClass().getPackage().getImplementationVersion();

    public String getHarunaVersion() {
        return HarunaVersion == null ? "Unofficial" : HarunaVersion;
    }

    public HarunaConfig(Haruna haruna, String location) {
        haruna.harunaLog.info("Version: " + getHarunaVersion() + "\n");
        try (InputStream input = new FileInputStream(location + "HarunaConfig.json")) {
            JSONTokener tokener = new JSONTokener(input);
            JSONObject config = new JSONObject(tokener);

            if (!config.has("RestAuth")) throw new Exception("RestAuth not found in config");
            if (!config.has("DBLAuth")) throw new Exception("DBLAuth not found in config");

            this.DBLAuth = config.getString("DBLAuth");
            this.RestAuth = config.getString("RestAuth");

            if (config.has("Debug")) this.Debug = config.getBoolean("Debug");

            if (config.has("Prefix")) this.Prefix = "/" + config.getString("Prefix");

            if (config.has("Weebhook")) this.Weebhook = config.getString("Weebhook");

            if (config.has("PostWeebhook")) this.PostWeebhook = config.getString("PostWeebhook");

            if (config.has("Port")) this.Port = config.getInt("Port");

            if (config.has("Threads")) this.Threads = config.getInt("Threads");

            if (config.has("UserTimeout")) this.UserTimeout = config.getLong("UserTimeout");

        } catch (Exception error) {
            haruna.harunaUtil.formatTrace(error.getMessage(), error.getStackTrace());
            System.exit(0);
        }
        haruna.harunaLog.info("Configuration Loaded!");
    }
}
