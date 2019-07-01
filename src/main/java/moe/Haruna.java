package moe;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import moe.misc.*;
import moe.routes.NewVote;
import moe.routes.VoteInfo;
import moe.storage.HarunaStore;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Haruna {
    public final HarunaLog harunaLog = new HarunaLog(this);
    public final HarunaConfig config = new HarunaConfig(this, this.getLocation());
    public final Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(config.Threads));
    public final HarunaStore store = new HarunaStore(this, this.getLocation());
    public final HarunaRest rest = new HarunaRest(this, config);

    private final HarunaStats stats = new HarunaStats(this);

    private final HttpServer server;
    private final Router mainRouter;
    private final Router apiRoutes;

    Haruna() {
        server = vertx.createHttpServer();
        mainRouter = Router.router(vertx);
        apiRoutes = Router.router(vertx);
    }

    void routes(NewVote newVote, VoteInfo voteInfo) {
        harunaLog.info("Setting the API routes....");
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
            mainRouter.route(HttpMethod.GET, "/favicon.ico")
                    .handler(context -> context.response().sendFile("webroot/favicon.ico").close())
                    .enable();
        } else {
            mainRouter.mountSubRouter("/", apiRoutes);
        }
        harunaLog.info("API routes configured!");
    }

    void listen() {
        harunaLog.info("Initializing the Cron Jobs....");
        HarunaCron harunaCron = new HarunaCron(this);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                harunaCron::execute, 30, 360, TimeUnit.SECONDS
        );
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                stats::updateJsonObject, 0, 240, TimeUnit.SECONDS
        );
        harunaLog.info("Cron Jobs are now armed!");
        harunaLog.info("Setting the configured routes and trying to listen @ Port " + config.Port);
        server.requestHandler(mainRouter).listen(config.Port);
        harunaLog.info("Success. Haruna is now online, configured to listen @ Port " + config.Port);
        sendEmbed();
    }

    public void formatTrace(String message, StackTraceElement[] traces) {
        List<String> trace = Arrays.stream(traces)
                .map(v -> v.toString() + "\n")
                .collect(Collectors.toList());
        trace.add(0, message + "\n");
        harunaLog.error(trace.toString());
    }

    private String getLocation() {
        String dir = null;
        try {
            File file = new File(Sortie.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            dir = file.getPath().replace(file.getName(), "");
        } catch (Exception error) {
            formatTrace(error.getMessage(), error.getStackTrace());
        }
        return dir;
    }

    private void sendEmbed() {
        rest.sendEmbed(
                0x2f6276,
                "\\✅ **Haruna is now online**",
                "\uD83D\uDCE1 || Haruna's version: " + config.getHarunaVersion()
        );
    }
}
