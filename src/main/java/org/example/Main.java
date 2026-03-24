package org.example;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.metadata.Dataset;
import org.example.metadata.QueryResult;

public class Main {

    public static void main(String[] args) throws Exception {

        AsterixClient client = new AsterixClient();
        String query = "SELECT VALUE ds FROM Metadata.`Dataset` ds WHERE ds.DataverseName = 'Test';";
        String response = client.Query(query);
        System.out.println("Response ############\n" + response);

        System.out.println("##############################################");
        ObjectMapper mapper = new ObjectMapper();
        QueryResult<Dataset> result = mapper.readValue(
                response,
                new TypeReference<QueryResult<Dataset>>() {}
        );
        for (Dataset d : result.results) {
            System.out.println(d.DatasetName);
            System.out.println("\tDataverse: " + d.DataverseName);
            System.out.println("\tDatatype: " + d.DatatypeName);
            System.out.println("\tDatatype's Dataverse: " + d.DatatypeDataverseName);
            System.out.println("\tDataset Type: " + d.DatasetType);
            System.out.println("\tPrimary Keys: ");
            for (List<String> lst : d.InternalDetails.PrimaryKey) {
                for (String pk : lst) {
                    System.out.println("\t\t" + pk);
                }
            }
        }
        System.out.println("##############################################");
    }
}