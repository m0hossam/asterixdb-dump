package org.example.metadata.datatype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Field {

    public String FieldName;
    public String FieldType;
    public Boolean IsNullable;
}