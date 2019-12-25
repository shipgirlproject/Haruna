package moe.routes;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import moe.Haruna;
import moe.structure.HarunaUser;

public class VoteInfo {
    private final Haruna haruna;

    public VoteInfo(Haruna haruna) { this.haruna = haruna; }

    public void execute(RoutingContext context) {
        HttpServerRequest request = context.request();
        HttpServerResponse response = context.response();
        haruna.requestsReceived++;
        try {
            String auth = request.getHeader("authorization");
            if (auth == null || !auth.equals(haruna.config.RestAuth)) {
                response.setStatusCode(401).setStatusMessage("Unauthorized").end();
                haruna.harunaLog.debug("Rejected GET request in /voteInfo from " + request.host());
                return;
            }

            String user = request.getParam("user_id");

            if (user == null) {
                JsonObject json = new JsonObject();
                json.put("user", false);
                response.end(json.toString());
                haruna.harunaLog.debug("Served GET request in /voteInfo without a query string from " + request.host());
                return;
            }

            HarunaUser harunaUser = haruna.store.get(user);
            if (harunaUser == null) {
                JsonObject json = new JsonObject();
                json.put("user", false);
                response.end(json.toString());
                haruna.harunaLog.debug("Served GET request in /voteInfo with a query string but not in the database from " + request.host());
                return;
            }

            JsonObject json = new JsonObject();
            json.put("user", harunaUser.user);
            json.put("timestamp", harunaUser.timestamp);
            json.put("isWeekend", harunaUser.weekend);
            json.put("timeLeft", harunaUser.getRemaining());

            response.end(json.toString());
            haruna.harunaLog.debug("Served GET request in /voteInfo from " + request.host());
        } catch (Exception error) {
            haruna.formatTrace(error.getMessage(), error.getStackTrace());
            response.setStatusCode(500).setStatusMessage(error.getMessage()).end();
        }
    }
}
