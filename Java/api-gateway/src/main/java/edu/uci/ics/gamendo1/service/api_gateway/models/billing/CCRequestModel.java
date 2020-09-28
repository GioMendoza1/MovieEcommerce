package edu.uci.ics.gamendo1.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.gamendo1.service.api_gateway.models.RequestModel;

public class CCRequestModel extends RequestModel {
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
