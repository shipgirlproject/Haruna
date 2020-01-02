package shipgirl;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import shipgirl.misc.*;
import shipgirl.routes.NewVote;
import shipgirl.routes.VoteInfo;
import shipgirl.storage.HarunaStore;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Haruna {
    public final HarunaLog harunaLog = new HarunaLog(this);
    public final HarunaUtil harunaUtil = new HarunaUtil(this);
    public final HarunaConfig config = new HarunaConfig(this, harunaUtil.getLocation());
    public final Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(config.Threads));
    public final RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
    public final HarunaStore store = new HarunaStore(this, harunaUtil.getLocation());
    public final HarunaRest rest = new HarunaRest(this, config);

    private final HarunaStats stats = new HarunaStats(this);

    private final ScheduledExecutorService scheduledThreadPool = Executors.newSingleThreadScheduledExecutor();
    private final HttpServer server;
    private final Router mainRouter;
    private final Router apiRoutes;

    public long requestsReceived = 0;

    Haruna() {
        server = vertx.createHttpServer();
        mainRouter = Router.router(vertx);
        apiRoutes = Router.router(vertx);
    }

    void routes(NewVote newVote, VoteInfo voteInfo) {
        apiRoutes.route().handler(BodyHandler.create());
        apiRoutes.route(HttpMethod.POST, "/newVote/")
                .blockingHandler(newVote::execute, true)
                .enable();
        apiRoutes.route(HttpMethod.GET, "/voteInfo/")
                .produces("application/json")
                .blockingHandler(voteInfo::execute, false)
                .enable();
        apiRoutes.route(HttpMethod.GET, "/stats/")
                .produces("application/json")
                .handler(stats::execute)
                .enable();
        apiRoutes.route("/*")
                .handler(StaticHandler.create().setIndexPage("haruna.html"))
                .enable();
        if (config.Prefix != null) {
            mainRouter.mountSubRouter(config.Prefix, apiRoutes);
        } else {
            mainRouter.mountSubRouter("/", apiRoutes);
        }
        harunaLog.info("API route handlers are now set!");
    }

    void listen() {
        setCronJobs();
        logDebugItems();
        server.requestHandler(mainRouter).listen(config.Port);
        harunaLog.info("Success. Haruna is now online and ready! Configured to listen @ port: " + config.Port);
        rest.sendEmbed(
                0x2f6276,
                "\\✅ **Haruna** is now online",
                "\uD83D\uDCE1 || Haruna's version: " + config.getHarunaVersion()
        );
    }

    private void setCronJobs() {
        HarunaCron harunaCron = new HarunaCron(this);
        scheduledThreadPool.scheduleAtFixedRate(
                harunaCron::execute, 30, 360, TimeUnit.SECONDS
        );
        scheduledThreadPool.scheduleAtFixedRate(
                stats::updateJsonObject, 0, 240, TimeUnit.SECONDS
        );
        harunaLog.info("Cron Jobs are now scheduled!");
    }

    private void logDebugItems() {
        if (!config.Debug) return;
        harunaLog.debug("Haruna is running in debug mode.");
        harunaLog.debug("Rest Auth is set to: " + this.config.RestAuth);
        String prefix = this.config.Prefix != null ? this.config.Prefix : "/";
        harunaLog.debug("Route Prefix is set to: " + prefix);
        String link = this.config.Weebhook != null ? this.config.Weebhook : "Disabled";
        harunaLog.debug("Webhook is set to: " + link);
        String postLink = this.config.PostWeebhook != null ? this.config.PostWeebhook : "Disabled";
        harunaLog.debug("Post Webhook is set to: " + postLink);
        harunaLog.debug("Thread pool is set to: " + this.config.Threads);
        harunaLog.debug("Users will be cleaned after: " + this.config.UserTimeout + "ms");
    }
}
