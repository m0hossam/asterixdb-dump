package org.example.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Dataset {

    public String DataverseName;
    public String DatasetName;
    public String DatatypeName;
    public String DatatypeDataverseName;
    public String DatasetType;
    public InternalDetails InternalDetails;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InternalDetails {
        public List<List<String>> PrimaryKey;
    }
}