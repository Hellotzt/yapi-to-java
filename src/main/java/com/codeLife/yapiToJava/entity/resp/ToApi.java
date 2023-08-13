package com.codeLife.yapiToJava.entity.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ToApi {
    /**
     * 
     */
    @JsonProperty("$schema")
    private String schema;
    /**
     * 
     */
    private String type;
    /**
     * 
     */
    private Object properties;
    /**
     * 
     */
    private List<String> required;
}
