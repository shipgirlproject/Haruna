package moe.misc;

import moe.Haruna;
import org.json.JSONTokener;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;

public class HarunaConfig {
    String DBLAuth = null;
    String Weebhook = null;
    boolean Debug = false;

    public String Prefix = null;
    public String RestAuth = null;
    public int Port = 1024;
    public int Threads = 10;
    public long UserTimeout = 43200000;

    private final String HarunaVersion = getClass().getPackage().getImplementationVersion();

    public String getHarunaVersion() {
        return HarunaVersion == null ? "Unofficial" : HarunaVersion;
    }

    public HarunaConfig(Haruna haruna, String location) {
        haruna.harunaLog.info("Version: " + getHarunaVersion() + "\n");
        haruna.harunaLog.info("Reading the HarunaConfig.json configuration file....");
        try (InputStream input = new FileInputStream(location + "HarunaConfig.json")) {
            JSONTokener tokener = new JSONTokener(input);
            JSONObject config = new JSONObject(tokener);

            if (!config.has("RestAuth")) throw new Exception("RestAuth not found in config");
            if (!config.has("DBLAuth")) throw new Exception("RestAuth not found in config");

            String RestAuth = config.getString("RestAuth");
            if (RestAuth == null) throw new Exception("RestAuth not found in config");
            String DBLAuth = config.getString("DBLAuth");
            if (DBLAuth == null) throw new Exception("DBLAuth not found in config");

            this.DBLAuth = DBLAuth;
            this.RestAuth = RestAuth;

            if (config.has("Debug")) {
                this.Debug = config.getBoolean("Debug");
            }

            if (config.has("Prefix")) {
                String Prefix = config.getString("Prefix");
                if (Prefix != null) this.Prefix = "/" + Prefix;
            }

            if (config.has("Weebhook")) {
                String Weebhook = config.getString("Weebhook");
                if (Weebhook != null) this.Weebhook = Weebhook;
            }

            if (config.has("Port")) {
                int Port = config.getInt("Port");
                if (Port != 0) this.Port = Port;
            }

            if (config.has("Threads")) {
                int Threads = config.getInt("Threads");
                if (Threads != 0) this.Threads = Threads;
            }

            if (config.has("UserTimeout")) {
                long UserTimeout = config.getLong("UserTimeout");
                if (UserTimeout != 0) this.UserTimeout = UserTimeout;
            }

        } catch (Exception error) {
            haruna.formatTrace(error.getMessage(), error.getStackTrace());
            System.exit(0);
        }
        haruna.harunaLog.info("Configuration Loaded!");
    }
}
