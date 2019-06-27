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
            if (!auth.equals("to_be_implemented")) {
                response.setStatusCode(401).setStatusMessage("Unauthorized").end();
                return;
            }

            JsonObject json = context.getBodyAsJson();
            String user = json.getString("user");
            Boolean isWeekend = json.getBoolean("isWeekend");

            if (user == null || isWeekend == null) {
                response.setStatusCode(400).setStatusMessage("User or IsWeekend is equal to null").end();
                return;
            }

            long store = Instant.now().plusSeconds(900 /*reimplement soon*/).toEpochMilli();
            haruna.store.save(user, store, isWeekend.toString());

            response.setStatusCode(200).setStatusMessage("ok").end();

        } catch (Exception error) {
            haruna.formatTrace(error.getStackTrace());
            response.setStatusCode(500).setStatusMessage(error.getMessage()).end();
        }
    }
}
