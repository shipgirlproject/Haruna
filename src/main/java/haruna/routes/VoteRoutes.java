package haruna.routes;

import haruna.HarunaServer;
import haruna.structure.HarunaUser;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.time.Instant;

public class VoteRoutes {
    private final HarunaServer harunaServer;

    public VoteRoutes(HarunaServer harunaServer) { this.harunaServer = harunaServer; }

    public void trigger(String endpoint, RoutingContext context, HttpServerRequest request, HttpServerResponse response) throws Exception {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.equals(harunaServer.config.restAuth)) {
            response.setStatusMessage("Unauthorized");
            context.fail(401);
            return;
        }
        switch(endpoint) {
            case "newVote":
                newVote(context, request, response);
                break;
            case "voteInfo":
                voteInfo(context, request, response);
        }
    }

    private void newVote(RoutingContext context, HttpServerRequest request, HttpServerResponse response) throws Exception {
        JsonObject json = context.getBodyAsJson();
        if (json.isEmpty()) {
            response.setStatusMessage("JSON body is empty");
            context.fail(400);
            return;
        }

        String user = json.getString("user");
        Boolean isWeekend = json.getBoolean("isWeekend");

        if (user == null || isWeekend == null) {
            response.setStatusMessage("JSON body key 'user' or 'isWeekend' is equal to null");
            context.fail(400);
            return;
        }

        long store = Instant.now().plusMillis(harunaServer.config.userTimeout).toEpochMilli();

        harunaServer.store.save(user, store, isWeekend);
        harunaServer.rest.sendPostVoteRequest(user, isWeekend);
        harunaServer.rest.getUser(user)
                .thenAcceptAsync(tag -> {
                    if (tag == null) return;
                    harunaServer.rest.sendEmbed(
                            0x326600,
                            "\\📥 New vote stored **" + tag + "** `(" + user + ")`",
                            "➕ || New Vote Stored"
                    );
                })
                .exceptionally(error -> {
                    harunaServer.harunaLog.error(error);
                    return null;
                });

        response.setStatusMessage("Data Saved").end();
    }

    private void voteInfo(RoutingContext context, HttpServerRequest request, HttpServerResponse response) throws Exception {
        String user = request.getParam("user_id");
        if (user == null) {
            response.setStatusMessage("No query string 'user_id' found");
            context.fail(400);
            return;
        }
        HarunaUser harunaUser = harunaServer.store.get(user);
        if (harunaUser == null) {
            JsonObject json = new JsonObject();
            json.put("user", false);
            response.end(json.toString());
            return;
        }
        JsonObject json = new JsonObject();
        json.put("user", harunaUser.user);
        json.put("timestamp", harunaUser.timestamp);
        json.put("isWeekend", harunaUser.weekend);
        json.put("timeLeft", harunaUser.getRemaining());
        response.end(json.toString());
    }
}
