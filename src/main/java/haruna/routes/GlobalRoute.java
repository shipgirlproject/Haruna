package haruna.routes;

import haruna.HarunaServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class GlobalRoute {
    private final HarunaServer harunaServer;
    private final VoteRoutes voteRoutes;
    private final StatsRoutes statsRoutes;

    public GlobalRoute (HarunaServer harunaServer) {
        this.harunaServer = harunaServer;
        this.voteRoutes = new VoteRoutes(harunaServer);
        this.statsRoutes = new StatsRoutes(harunaServer);
    }

    public void triggerFail(RoutingContext context) {
        Throwable throwable = context.failure();
        HttpServerResponse response = context.response();
        if (throwable != null) {
            harunaServer.harunaLog.error("Failed REST Request; Error: ", throwable);
        } else {
            harunaServer.harunaLog.warn("Failed REST Request; Code: " + context.statusCode() + " Reason: " + response.getStatusMessage());
        }
        response.setStatusCode(context.statusCode()).end();
    }

    public void trigger(String endpoint, RoutingContext context) {
        harunaServer.requestsReceived++;
        HttpServerRequest request = context.request();
        HttpServerResponse response = context.response();
        try {
            switch (endpoint) {
                case "newVote":
                case "voteInfo":
                    this.voteRoutes.trigger(endpoint, context, request, response);
                    break;
                case "stats":
                    this.statsRoutes.trigger(response);
            }
        } catch (Exception error) {
            response.setStatusMessage("Internal Server Error");
            context.fail(500, error);
        }
    }
}
