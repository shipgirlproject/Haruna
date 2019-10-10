package moe.misc;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import moe.Haruna;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class HarunaRest {
    private final Haruna haruna;
    private final String weebhook;
    private final String DBLAuth;
    private final WebClient client;

    public HarunaRest(Haruna haruna, HarunaConfig config) {
        this.haruna = haruna;
        this.weebhook = config.Weebhook;
        this.DBLAuth = config.DBLAuth;

        WebClientOptions options = new WebClientOptions()
                .setUserAgent("Haruna/" + haruna.config.getHarunaVersion());
        this.client = WebClient.create(haruna.vertx, options);
    }

    public CompletableFuture<String> getUser(String id) {
        CompletableFuture<String> result = new CompletableFuture<>();
        client.requestAbs(HttpMethod.GET, "https://top.gg/api/users/" + id)
                .putHeader("authorization", DBLAuth)
                .send(res -> {
                    try {
                        if (res.failed()) {
                            Throwable error = res.cause();
                            haruna.formatTrace(error.getMessage(), error.getStackTrace());
                            result.complete(null);
                            return;
                        }
                        HttpResponse<Buffer> response = res.result();
                        if (response.getHeader("Content-Type").startsWith("application/json")) {
                            JsonObject body = response.bodyAsJsonObject();
                            String username = body.getString("username");
                            if (username == null) username = "???";
                            String discrim = body.getString("discriminator");
                            if (discrim == null) discrim = "???";
                            result.complete(username + "#" + discrim);
                        } else result.complete(null);
                    } catch (Exception error) {
                        haruna.formatTrace(error.getMessage(), error.getStackTrace());
                        result.complete(null);
                    }
                });
        return result;
    }

    private void sendEmbed(JsonArray array) {
        client.requestAbs(HttpMethod.POST, weebhook)
                .putHeader("Content-Type", "application/json")
                .sendJsonObject(
                        new JsonObject().put("embeds", array),
                        res -> {
                            if (res.succeeded()) return;
                            Throwable error = res.cause();
                            haruna.formatTrace(error.getMessage(), error.getStackTrace());
                        });
    }

    public void sendEmbed(int color, String desc, String footerdesc) {
        if (weebhook == null) return;
        JsonObject footer = new JsonObject()
                .put("text", footerdesc);
        JsonObject embed = new JsonObject()
                .put("color", color)
                .put("description", desc)
                .put("timestamp", Instant.now())
                .put("footer", footer);
        sendEmbed(new JsonArray().add(embed));
    }
}
