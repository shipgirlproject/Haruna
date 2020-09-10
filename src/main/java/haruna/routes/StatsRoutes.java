package haruna.routes;

import haruna.HarunaServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class StatsRoutes {
    private final HarunaServer harunaServer;

    public StatsRoutes(HarunaServer harunaServer) { this.harunaServer = harunaServer; }

    public void trigger(HttpServerResponse response) {
        response.end(harunaServer.stats.getStats().toString());
    }
}
