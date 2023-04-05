package haruna.misc;

import haruna.HarunaServer;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.nio.file.Files;
import java.nio.file.Paths;

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
    
    private static final int PORT_DEFAULT = 1024;
    private static final int USER_TIMEOUT_DEFAULT = 43200000;

    public HarunaConfig(HarunaServer harunaServer, String location) throws Exception {
        harunaServer.harunaLog.info("Version: " + this.HarunaVersion + "\n");
        JSONObject config = new JSONObject(new JSONTokener(Files.newInputStream(Paths.get(location + "HarunaConfig.json"))));
        if (!config.has("RestAuth")) throw new Exception("RestAuth not found in config");
        if (!config.has("DBLAuth")) throw new Exception("DBLAuth not found in config");
        this.DBLAuth = config.getString("DBLAuth");
        this.RestAuth = config.getString("RestAuth");
        this.Debug = config.has("Debug") && config.getBoolean("Debug");
        this.Prefix = config.has("Prefix") ? "/" + config.getString("Prefix") : "/";
        this.Weebhook = config.has("Weebhook") ? config.getString("Weebhook") : null;
        this.PostWeebhook = config.has("PostWeebhook") ? config.getString("PostWeebhook") : null;
        this.Port = config.has("Port") ? config.getInt("Port") : PORT_DEFAULT;
        this.Threads = config.has("Threads") ? config.getInt("Threads") : Runtime.getRuntime().availableProcessors();
        this.UserTimeout = config.has("UserTimeout") ? config.getLong("UserTimeout") : USER_TIMEOUT_DEFAULT;
        harunaServer.harunaLog.info("Configuration Loaded from config file!");
    }

    public HarunaConfig(HarunaServer harunaServer) throws Exception {
        harunaServer.harunaLog.info("Version: " + this.HarunaVersion + "\n");
        this.DBLAuth = System.getenv("DBL_AUTH");
        if (this.DBLAuth == null) throw new Exception("RestAuth not found in config");
        this.RestAuth = System.getenv("REST_AUTH");
        if (this.RestAuth == null) throw new Exception("DBLAuth not found in config");
        this.Debug = Boolean.parseBoolean(System.getenv("DEBUG_ENABLED"));
        this.Prefix = "/" + System.getenv("PREFIX");
        this.Weebhook = System.getenv("WEEBHOOK");
        this.PostWeebhook = System.getenv("POST_WEEBHOOK");
        String port = System.getenv("PORT");
        this.Port = port != null ? Integer.parseInt(port) : PORT_DEFAULT;
        String threads = System.getenv("THREADS_COUNT");
        this.Threads = threads != null ? Integer.parseInt(threads) : Runtime.getRuntime().availableProcessors();
        String userTimeout = System.getenv("USER_TIMEOUT");
        this.UserTimeout = userTimeout != null ? Long.parseLong(userTimeout) : USER_TIMEOUT_DEFAULT;
        harunaServer.harunaLog.info("Configuration Loaded from varenv!");
    }
}
