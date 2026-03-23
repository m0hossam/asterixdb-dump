package org.example;

public class Main {

    public static void main(String[] args) throws Exception {

        AsterixClient client = new AsterixClient();
        String query = "SELECT VALUE dv FROM Metadata.`Dataverse` dv;";
        String response = client.Query(query);
        System.out.println(response);
    }
}