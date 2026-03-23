package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {

    public static void main(String[] args) throws Exception {

        HttpClient client = HttpClient.newHttpClient();

        String query = "SELECT VALUE d.DataverseName FROM Metadata.`Dataverse` d;";
        String body = "{ \"statement\": \"" + query + "\" }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:19002/query/service"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response from AsterixDB: ###########################");
        System.out.println(response.body());
    }
}