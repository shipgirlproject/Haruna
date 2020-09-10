package haruna.misc;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import haruna.HarunaServer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class HarunaRest {
    private final HarunaServer harunaServer;
    private final String weebhook;
    private final String postWeebhook;
    private final String DBLAuth;
    private final WebClient client;

    public HarunaRest(HarunaServer harunaServer) {
        this.harunaServer = harunaServer;
        this.weebhook = harunaServer.config.Weebhook;
        this.postWeebhook = harunaServer.config.PostWeebhook;
        this.DBLAuth = harunaServer.config.DBLAuth;
        WebClientOptions options = new WebClientOptions()
                .setUserAgent("Haruna/" + harunaServer.config.HarunaVersion);
        this.client = WebClient.create(harunaServer.vertx, options);
    }

    public CompletableFuture<String> getUser(String id) {
        CompletableFuture<String> result = new CompletableFuture<>();
        client.requestAbs(HttpMethod.GET, "https://top.gg/api/users/" + id)
                .putHeader("authorization", DBLAuth)
                .send(res -> {
                    try {
                        if (res.failed()) {
                            Throwable error = res.cause();
                            if (error == null) {
                                result.complete(null);
                                return;
                            }
                            throw error;
                        }
                        HttpResponse<Buffer> response = res.result();
                        if (!response.getHeader("Content-Type").startsWith("application/json")) {
                            result.complete(null);
                            return;
                        }
                        JsonObject body = response.bodyAsJsonObject();
                        String username = body.getString("username") == null ? "Unknown" : body.getString("username");
                        String discriminator = body.getString("discriminator") == null ? "0000" : body.getString("discriminator");
                        result.complete(username + "#" + discriminator);
                    } catch (Throwable error) {
                        harunaServer.harunaLog.error(error);
                        result.complete(null);
                    }
                });
        return result;
    }

    public void sendPostVoteRequest(String user, Boolean isWeekend) {
        if (postWeebhook == null) return;
        JsonObject json = new JsonObject()
                .put("user", user)
                .put("isWeekend", isWeekend);
        client.requestAbs(HttpMethod.POST, postWeebhook)
                .putHeader("Content-Type", "application/json")
                .sendJsonObject(
                        json,
                        res -> {
                            if (res.succeeded()) return;
                            harunaServer.harunaLog.error(res.cause());
                        }
                );
    }

    public void sendEmbed(int color, String description, String footerDescription) {
        if (weebhook == null) return;
        JsonObject footer = new JsonObject()
                .put("text", footerDescription);
        JsonObject embed = new JsonObject()
                .put("color", color)
                .put("description", description)
                .put("timestamp", Instant.now())
                .put("footer", footer);
        makeWebhookRequest(new JsonArray().add(embed));
    }

    private void makeWebhookRequest(JsonArray array) {
        client.requestAbs(HttpMethod.POST, weebhook)
                .putHeader("Content-Type", "application/json")
                .sendJsonObject(
                        new JsonObject().put("embeds", array),
                        res -> {
                            if (res.succeeded()) return;
                            harunaServer.harunaLog.error(res.cause());
                        });
    }
}
