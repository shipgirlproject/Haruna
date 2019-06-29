package moe.misc;

import moe.Haruna;
import org.json.JSONTokener;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;

public class HarunaConfig {
    String DBLAuth = null;
    boolean Debug = false;
    String Weebhook = null;

    public String RestAuth = null;
    public int Port = 1024;
    public int Threads = 20;
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

            String RestAuth = config.getString("RestAuth");
            if (RestAuth == null) throw new Exception("RestAuth not found in config");
            String DBLAuth = config.getString("DBLAuth");
            if (DBLAuth == null) throw new Exception("DBLAuth not found in config");

            this.DBLAuth = DBLAuth;
            this.RestAuth = RestAuth;

            this.Debug = config.getBoolean("Debug");

            String Weebhook = config.getString("Weebhook");
            int Port = config.getInt("Port");
            int Threads  = config.getInt("Threads");
            long UserTimeout = config.getLong("UserTimeout");

            if (Weebhook != null) this.Weebhook = Weebhook;
            if (Threads != 0) this.Threads = Threads;
            if (Port != 0) this.Port = Port;
            if (UserTimeout != 0) this.UserTimeout = UserTimeout;

        } catch (Exception error) {
            haruna.formatTrace(error.getMessage(), error.getStackTrace());
            System.exit(0);
        }
        haruna.harunaLog.info("Configuration Loaded!");
    }
}
