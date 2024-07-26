package haruna;

import haruna.routes.GlobalRoute;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import haruna.misc.*;
import haruna.storage.HarunaStore;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HarunaServer {
    public final HarunaLog harunaLog;
    public final HarunaUtil harunaUtil;
    public final HarunaConfig config;
    public final HarunaRest rest;
    public final HarunaStore store;
    public final HarunaStats stats;
    public final Vertx vertx;
    public final RuntimeMXBean runtime;

    private final ScheduledExecutorService scheduledThreadPool;
    private final HttpServer server;
    private final Router mainRouter;
    private final GlobalRoute routeHandler;

    public long requestsReceived = 0;

    HarunaServer() throws Exception {
        this.harunaUtil = new HarunaUtil();
        this.harunaLog = new HarunaLog(this);
        this.config = new HarunaConfig(this);
        this.store = new HarunaStore(this, harunaUtil.getLocation());
        this.stats = new HarunaStats(this);
        this.vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(config.threads));
        this.runtime = ManagementFactory.getRuntimeMXBean();
        this.scheduledThreadPool = Executors.newSingleThreadScheduledExecutor();
        this.server = vertx.createHttpServer();
        this.mainRouter = Router.router(vertx);
        this.rest = new HarunaRest(this);
        this.routeHandler = new GlobalRoute(this);
    }

    HarunaServer buildRoutes() {
        mainRouter.route().handler(BodyHandler.create());
        mainRouter.route(HttpMethod.POST, "/newVote")
            .blockingHandler(context -> this.routeHandler.trigger("newVote", context), true)
            .failureHandler(this.routeHandler::triggerFail)
            .enable( );
        mainRouter.route(HttpMethod.GET, "/voteInfo")
            .produces("application/json")
            .blockingHandler(context -> this.routeHandler.trigger("voteInfo", context), false)
            .failureHandler(this.routeHandler::triggerFail)
            .enable( );
        mainRouter.route(HttpMethod.GET, "/stats")
            .produces("application/json")
            .blockingHandler(context -> this.routeHandler.trigger("stats", context), false)
            .failureHandler(this.routeHandler::triggerFail)
           .enable();
        mainRouter.route("/*")
            .handler(StaticHandler.create().setIndexPage("haruna.html"))
            .failureHandler(this.routeHandler::triggerFail)
            .enable();
        harunaLog.info("API route handlers are now set!");
        return this;
    }

    void start() {
        setCronJobs();
        logDebugItems();
        server.requestHandler(mainRouter).listen(config.port);
        harunaLog.info("Success. Haruna is now online and ready! Configured to listen @ port: " + config.port);
        rest.sendEmbed(
                0x2f6276,
                "\\✅ **Haruna** is now online",
                "\uD83D\uDCE1 || Haruna's version: " + config.version
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
        if (!config.debug) return;
        harunaLog.debug("Haruna is running in debug mode.");
        harunaLog.debug("Rest Auth is set to: " + this.config.restAuth);
        harunaLog.debug("Webhook is set to: " + this.config.webhook != null ? this.config.webhook: "Disabled");
        harunaLog.debug("Post Webhook is set to: " + this.config.postWebhook != null ? this.config.postWebhook : "Disabled");
        harunaLog.debug("Thread pool is set to: " + this.config.threads);
        harunaLog.debug("Users will be cleaned after: " + this.config.userTimeout + "ms");
    }
}
