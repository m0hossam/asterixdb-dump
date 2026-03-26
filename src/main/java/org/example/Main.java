package org.example;

import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.metadata.*;
import org.example.metadata.datatype.*;

public class Main {

    public static void main(String[] args) throws Exception {

        AsterixClient client = new AsterixClient();

        List<Dataverse> dataverses = GetDataverses(client);
        List<Dataset> datasets = GetDatasets(client);
        List<Datatype> datatypes = GetDatatypes(client, dataverses);
        System.out.println("DATAVERSES #############################################");
        for (Dataverse dv : dataverses) {
            System.out.println(dv.DataverseName);
        }
        System.out.println("DATASETS ###############################################");
        for (Dataset ds : datasets) {
            System.out.println(ds.DatasetName);
            System.out.println("\tDataverse: " + ds.DataverseName);
            System.out.println("\tDatatype: " + ds.DatatypeName);
            System.out.println("\tDatatype's Dataverse: " + ds.DatatypeDataverseName);
            System.out.println("\tPrimary Keys: ");
            for (List<String> lst : ds.InternalDetails.PrimaryKey) {
                for (String pk : lst) {
                    System.out.println("\t\t" + pk);
                }
                System.out.println("\t\t------------------------------");
            }
        }
        System.out.println("DATATYPES ##############################################");
        for (Datatype dt : datatypes) {
            System.out.println(dt.DatatypeName);
            System.out.println("\tDataverse: " + dt.DataverseName);
            System.out.println("\tFields: ");
            for (Field field : dt.Derived.Record.Fields) {
                System.out.println("\t\tName: " + field.FieldName);
                System.out.println("\t\tType: " + field.FieldType);
                System.out.println("\t\tIsNullable: " + field.IsNullable);
                System.out.println("\t\t------------------------------");
            }
        }
        System.out.println("########################################################");
    }

    public static List<Dataverse> GetDataverses(AsterixClient client) throws Exception {
        String query = "SELECT VALUE dv FROM Metadata.`Dataverse` dv WHERE dv.DataverseName NOT IN ['Metadata', 'Default'];";
        String response = client.Query(query);
        ObjectMapper mapper = new ObjectMapper();
        QueryResult<Dataverse> result = mapper.readValue(
                response,
                new TypeReference<QueryResult<Dataverse>>() {
                }
        );
        return result.results;
    }

    public static List<Dataset> GetDatasets(AsterixClient client) throws Exception {
        String query = "SELECT VALUE ds FROM Metadata.`Dataset` ds WHERE ds.DataverseName NOT IN ['Metadata', 'Default'];";
        String response = client.Query(query);
        ObjectMapper mapper = new ObjectMapper();
        QueryResult<Dataset> result = mapper.readValue(
                response,
                new TypeReference<QueryResult<Dataset>>() {}
        );
        return result.results;
    }

    public static List<Datatype> GetDatatypes(AsterixClient client, List<Dataverse> dataverses) throws Exception {
        List<Datatype> dts = new ArrayList<>();
        for (Dataverse dv: dataverses) {
            String query = String.format("SELECT VALUE t FROM Metadata.`Datatype` t WHERE t.DataverseName = '%s'", dv.DataverseName);
            String response = client.Query(query);
            ObjectMapper mapper = new ObjectMapper();
            QueryResult<Datatype> result = mapper.readValue(
                    response,
                    new TypeReference<QueryResult<Datatype>>() {
                    }
            );
            dts.addAll(result.results);
        }
        return dts;
    }
}