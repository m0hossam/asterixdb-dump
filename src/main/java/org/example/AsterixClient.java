package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AsterixClient {

    private HttpClient client;

    public AsterixClient() {
        client = HttpClient.newHttpClient();
    }

    public String Query(String query) throws Exception {
        String body = "{ \"statement\": \"" + query + "\" }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:19002/query/service"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}