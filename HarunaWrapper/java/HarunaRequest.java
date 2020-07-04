import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HarunaRequest {
    private final String url;
    private final String password;

    //HarunaRequest haruna = new HarunaRequest("http://localhost:6969", "your_password");
    public HarunaRequest(String url, String password) {
        this.url = url;
        this.password = password;
    }

    public JSONObject getStats() {
        return fetch("/stats", null);
    }

    public boolean hasVoted(Long userId) {
        if(userId == null) { throw new NullPointerException("The user id can't be null"); }
        return fetch("/voteInfo", userId).get("user") == userId;
    }

    private JSONObject fetch(String endpoint, Long userId) {
        String hURL = this.url + endpoint;

        if(userId != null) { hURL += "?user=" + userId; }

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(hURL))
                    .header("authorization", this.password)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return new JSONObject(response.body());
        } catch (Exception e) { e.printStackTrace(); }
        return new JSONObject(); //Returning an empty JSONObject instead of null
    }
}
