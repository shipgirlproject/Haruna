package haruna.misc;

import haruna.HarunaServer;
import org.json.JSONTokener;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class HarunaConfig {
    String DBLAuth;
    public final String Weebhook;
    public final String PostWeebhook;
    public final boolean Debug;
    public final String Prefix;
    public final String RestAuth;
    public final int Port;
    public final long UserTimeout;
    public final int Threads;
    public final String HarunaVersion = getClass().getPackage().getImplementationVersion() == null ? "Unofficial" : getClass().getPackage().getImplementationVersion();

    public HarunaConfig(HarunaServer harunaServer, String location) throws FileNotFoundException, Exception {
        harunaServer.harunaLog.info("Version: " + this.HarunaVersion + "\n");
        JSONObject config = new JSONObject(new JSONTokener(new FileInputStream(location + "HarunaConfig.json")));
        if (!config.has("RestAuth")) throw new Exception("RestAuth not found in config");
        if (!config.has("DBLAuth")) throw new Exception("DBLAuth not found in config");
        this.DBLAuth = config.getString("DBLAuth");
        this.RestAuth = config.getString("RestAuth");
        this.Debug = config.has("Debug") && config.getBoolean("Debug");
        this.Prefix = config.has("Prefix") ? "/" + config.getString("Prefix") : "/";
        this.Weebhook = config.has("Weebhook") ? config.getString("Weebhook") : null;
        this.PostWeebhook = config.has("PostWeebhook") ? config.getString("PostWeebhook") : null;
        this.Port = config.has("Port") ? config.getInt("Port") : 1024;
        this.Threads = config.has("Threads") ? config.getInt("Threads") : Runtime.getRuntime().availableProcessors();
        this.UserTimeout = config.has("UserTimeout") ? config.getLong("UserTimeout") : 43200000;
        harunaServer.harunaLog.info("Configuration Loaded!");
    }
}
