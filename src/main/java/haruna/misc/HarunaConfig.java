package haruna.misc;

import haruna.HarunaServer;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.nio.file.Files;
import java.nio.file.Paths;

public class HarunaConfig {
    String topggAuth;
    public final String webhook;
    public final String postWebhook;
    public final boolean debug;
    public final String restAuth;
    public final int port;
    public final long userTimeout;
    public final int threads;
    public final String version = getClass().getPackage().getImplementationVersion() == null ? "Unofficial" : getClass().getPackage().getImplementationVersion();
    
    private static final int PORT_DEFAULT = 1024;
    private static final int USER_TIMEOUT_DEFAULT = 43200000;

    public HarunaConfig(HarunaServer harunaServer) throws Exception {
        harunaServer.harunaLog.info("Version: " + this.version + "\n");
        String configLocation = harunaServer.harunaUtil.getLocation() + "HarunaConfig.json";
        if (Files.exists(Paths.get(configLocation))) {
            JSONObject config = new JSONObject(new JSONTokener(Files.newInputStream(Paths.get(harunaServer.harunaUtil.getLocation() + "HarunaConfig.json"))));
            if (!config.has("restAuth")) throw new Exception("restAuth not found in config");
            if (!config.has("topggAuth")) throw new Exception("topggAuth not found in config");
            this.topggAuth = config.getString("topggAuth");
            this.restAuth = config.getString("restAuth");
            this.debug = config.has("debug") && config.getBoolean("debug");
            this.webhook = config.has("webhook") ? config.getString("webhook") : null;
            this.postWebhook = config.has("postWebhook") ? config.getString("postWebhook") : null;
            this.port = config.has("port") ? config.getInt("port") : PORT_DEFAULT;
            this.threads = config.has("threads") ? config.getInt("threads") : Runtime.getRuntime().availableProcessors();
            this.userTimeout = config.has("userTimeout") ? config.getLong("userTimeout") : USER_TIMEOUT_DEFAULT;
            harunaServer.harunaLog.info("Configuration Loaded from config file!");
        } else {
            harunaServer.harunaLog.info("Version: " + this.version + "\n");
            this.topggAuth = System.getenv("TOPGG_AUTH");
            if (this.topggAuth== null) throw new Exception("TOPGG_AUTH not found in env");
            this.restAuth = System.getenv("REST_AUTH");
            if (this.restAuth == null) throw new Exception("REST_AUTH not found in env");
            this.debug = Boolean.parseBoolean(System.getenv("DEBUG"));
            this.webhook = System.getenv("WEBHOOK");
            this.postWebhook = System.getenv("POST_WEBHOOK");
            String port = System.getenv("PORT");
            this.port = port != null ? Integer.parseInt(port) : PORT_DEFAULT;
            String threads = System.getenv("THREADS");
            this.threads = threads != null ? Integer.parseInt(threads) : Runtime.getRuntime().availableProcessors();
            String userTimeout = System.getenv("USER_TIMEOUT");
            this.userTimeout =  userTimeout != null ? Long.parseLong(userTimeout) : USER_TIMEOUT_DEFAULT;
            harunaServer.harunaLog.info("Configuration Loaded from varenv!");
        }
    }
}
