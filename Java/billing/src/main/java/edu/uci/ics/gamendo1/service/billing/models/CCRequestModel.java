package edu.uci.ics.gamendo1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CCRequestModel {
    @JsonProperty(value = "id", required = true)
    private String id;

    public CCRequestModel() {}

    public CCRequestModel(@JsonProperty(value = "id", required = true) String id)
    {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
