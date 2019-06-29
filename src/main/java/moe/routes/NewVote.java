package moe.routes;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import moe.Haruna;

import java.time.Instant;

public class NewVote {
    private final Haruna haruna;

    public NewVote(Haruna haruna) { this.haruna = haruna; }

    public void execute(RoutingContext context) {
        HttpServerRequest request = context.request();
        HttpServerResponse response = context.response();

        try {
            String auth = request.getHeader("Authorization");
            if (auth == null || !auth.equals(haruna.config.RestAuth)) {
                response.setStatusCode(401).setStatusMessage("Unauthorized").end();
                haruna.harunaLog.debug("Rejected POST request in /newVote from " + request.remoteAddress());
                return;
            }

            JsonObject json = context.getBodyAsJson();

            if (json.isEmpty()) {
                response.setStatusCode(400).setStatusMessage("JSON is empty").end();
                haruna.harunaLog.debug("A POST request in /newVote from " + request.remoteAddress() + " don't contain a json body.");
                return;
            }

            String user = json.getString("user");
            Boolean isWeekend = json.getBoolean("isWeekend");

            if (user == null || isWeekend == null) {
                response.setStatusCode(400).setStatusMessage("User or IsWeekend is equal to null").end();
                haruna.harunaLog.debug("A POST request in /newVote from " + request.remoteAddress() + " don't contain a user or isWeekend property.");
                return;
            }

            long store = Instant.now().plusMillis(haruna.config.UserTimeout).toEpochMilli();

            haruna.store.save(user, store, isWeekend);

            response.setStatusCode(200).setStatusMessage("ok").end();

            sendVote(user);

            haruna.harunaLog.debug("A POST request in /newVote from " + request.remoteAddress() + " is saved. UserID: " + user);
        } catch (Exception error) {
            haruna.formatTrace(error.getMessage(), error.getStackTrace());
            response.setStatusCode(500).setStatusMessage(error.getMessage()).end();
        }
    }

    private void sendVote(String user) {
        haruna.rest.getUser(user)
                .thenAcceptAsync(tag -> {
                    if (tag == null) return;
                    haruna.rest.sendEmbed(
                            0x326600,
                            "\\📥 New vote stored **" + tag + "** `(" + user + ")`",
                            "➕ || New Vote Stored"
                    );
                })
                .exceptionally(error -> {
                    haruna.formatTrace(error.getMessage(), error.getStackTrace());
                    return null;
                });
    }
}
