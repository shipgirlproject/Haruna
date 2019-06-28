package moe;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import moe.misc.HarunaConfig;
import moe.misc.HarunaRest;
import moe.routes.NewVote;
import moe.routes.VoteInfo;
import moe.storage.HarunaStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Haruna {
    public final HarunaConfig config = new HarunaConfig(this, this.getLocation());
    public final Logger HarunaLog = LoggerFactory.getLogger(Sortie.class);
    public final Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(config.Threads));
    public final HarunaStore store = new HarunaStore(this, this.getLocation());
    public final HarunaRest rest = new HarunaRest(this, config);

    private final HttpServer server;
    private final Router routes;

    Haruna() {
        server = vertx.createHttpServer();
        routes = Router.router(vertx);
    }

    void routes(NewVote newVote, VoteInfo voteInfo) {
        routes.route(HttpMethod.POST, "/newVote/")
                .consumes("application/json")
                .blockingHandler(newVote::execute, true)
                .enable();
        routes.route(HttpMethod.GET, "/voteInfo/")
                .produces("application/json")
                .blockingHandler(voteInfo::execute)
                .enable();
    }

    void listen() {
        server.requestHandler(routes).listen(config.Port);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            int cleaned;
            try {
                cleaned = store.clean();
            } catch (Exception error) {
                formatTrace(error.getMessage(), error.getStackTrace());
            }
        }, 60, 60, TimeUnit.SECONDS);
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

    public void formatTrace(String message, StackTraceElement[] traces) {
       List<String> trace = Arrays.stream(traces)
                .map(v -> v.toString() + "\n")
                .collect(Collectors.toList());
       trace.add(0, message + "\n");
       HarunaLog.error(trace.toString());
    }
}
