package org.example;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.example.metadata.*;
import org.example.metadata.datatype.*;

public class Main {

    public static void main(String[] args) throws Exception {

        AsterixClient client = new AsterixClient();

        List<Dataverse> dataverses = GenerateDataverses(client);
        List<Datatype> datatypes = GenerateDatatypes(client, dataverses);
        List<Dataset> datasets = GenerateDatasets(client);
        for (Dataset ds: datasets) {
            GenerateRows(client, ds.DataverseName, ds.DatasetName);
        }
    }

    public static List<Dataverse> GenerateDataverses(AsterixClient client) throws Exception {
        String query = "SELECT VALUE dv FROM Metadata.`Dataverse` dv WHERE dv.DataverseName NOT IN ['Metadata', 'Default'];";
        String response = client.Query(query);
        ObjectMapper mapper = new ObjectMapper();
        QueryResult<Dataverse> result = mapper.readValue(
                response,
                new TypeReference<QueryResult<Dataverse>>() {
                }
        );
        for (Dataverse dv : result.results) {
            System.out.println(String.format("CREATE DATAVERSE %s;\n", dv.DataverseName));
        }
        return result.results;
    }

    public static List<Dataset> GenerateDatasets(AsterixClient client) throws Exception {
        String query = "SELECT VALUE ds FROM Metadata.`Dataset` ds WHERE ds.DataverseName NOT IN ['Metadata', 'Default'];";
        String response = client.Query(query);
        ObjectMapper mapper = new ObjectMapper();
        QueryResult<Dataset> result = mapper.readValue(
                response,
                new TypeReference<QueryResult<Dataset>>() {}
        );
        for (Dataset ds : result.results) {
            StringBuilder sb = new StringBuilder();

            sb.append("CREATE DATASET ")
                    .append(ds.DataverseName)
                    .append(".")
                    .append(ds.DatasetName)
                    .append("(")
                    .append(ds.DatatypeDataverseName)
                    .append(".")
                    .append(ds.DatatypeName)
                    .append(")\n");

            String pks = ds.InternalDetails.PrimaryKey.stream()
                    .map(fields -> fields.stream().collect(Collectors.joining(", ")))
                    .collect(Collectors.joining(", "));
            sb.append("PRIMARY KEY ").append(pks).append(";\n");

            System.out.println(sb.toString());
        }
        return result.results;
    }

    public static List<Datatype> GenerateDatatypes(AsterixClient client, List<Dataverse> dataverses) throws Exception {
        List<Datatype> datatypes = new ArrayList<>();

        // Map the datatypes into Java objects:
        for (Dataverse dv: dataverses) {
            String query = String.format("SELECT VALUE t FROM Metadata.`Datatype` t WHERE t.DataverseName = '%s'", dv.DataverseName);
            String response = client.Query(query);
            ObjectMapper mapper = new ObjectMapper();
            QueryResult<Datatype> result = mapper.readValue(
                    response,
                    new TypeReference<QueryResult<Datatype>>() {
                    }
            );
            datatypes.addAll(result.results);
        }

        // Generate the CREATE TYPE statement:
        for (Datatype dt : datatypes) {
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TYPE ")
                    .append(dt.DataverseName)
                    .append(".")
                    .append(dt.DatatypeName)
                    .append(" AS {\n");
            for (int i = 0; i < dt.Derived.Record.Fields.size(); i++) {
                Field f = dt.Derived.Record.Fields.get(i);

                sb.append("\t")
                        .append(f.FieldName)
                        .append(": ")
                        .append(f.FieldType);

                if (f.IsNullable) sb.append("?");

                if (i < dt.Derived.Record.Fields.size() - 1) sb.append(",");

                sb.append("\n");
            }
            sb.append("};\n");
            System.out.println(sb.toString());
        }

        return datatypes;
    }

    public static void GenerateRows(AsterixClient client, String dataverseName, String datasetName) throws Exception {
        String query = String.format("SELECT VALUE u FROM %s.%s u;", dataverseName, datasetName);
        String response = client.Query(query);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);
        JsonNode results = root.get("results");
        for (JsonNode row : results) {

            String json = mapper.writeValueAsString(row);

            String insert = String.format(
                    "INSERT INTO %s.%s\nSELECT VALUE %s;\n",
                    dataverseName,
                    datasetName,
                    json
            );

            System.out.println(insert);
        }
    }
}