package moe;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import moe.routes.NewVote;
import moe.routes.VoteInfo;
import moe.storage.HarunaStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Haruna {
    public final Logger HarunaLog = LoggerFactory.getLogger(Sortie.class);
    public final HarunaStore store = new HarunaStore(this, this.getLocation());

    private final Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(20));

    private final HttpServer server;
    private final Router routes;

    Haruna() {
        server = vertx.createHttpServer();
        routes = Router.router(vertx);
    }

    public void routes(NewVote newVote, VoteInfo voteInfo) {
        routes.route(HttpMethod.POST, "/newVote/")
                .consumes("application/json")
                .blockingHandler(newVote::execute, true)
                .enable();
        routes.route(HttpMethod.GET, "/voteInfo/")
                .produces("application/json")
                .blockingHandler(voteInfo::execute, true)
                .enable();
    }


    private String getLocation() {
        String dir = null;
        try {
            File file = new File(Sortie.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            dir = file.getPath().replace(file.getName(), "");
        } catch (Exception error) {
            formatTrace(error.getStackTrace());
        }
        return dir;
    }

    public void formatTrace(StackTraceElement[] traces) {
       List<String> trace = Arrays.stream(traces)
                .map(v -> v.toString() + "\n")
                .collect(Collectors.toList());
       HarunaLog.error(trace.toString());
    }
}
