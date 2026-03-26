package org.example.metadata.datatype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Datatype {

    public String DataverseName;
    public String DatatypeName;
    public Derived Derived;
}