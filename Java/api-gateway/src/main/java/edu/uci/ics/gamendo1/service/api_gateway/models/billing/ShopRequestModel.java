package edu.uci.ics.gamendo1.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.gamendo1.service.api_gateway.models.RequestModel;

public class ShopRequestModel extends RequestModel {
    @JsonProperty(value = "email", required = true)
    private String email;

    public ShopRequestModel() {}

    public ShopRequestModel(@JsonProperty(value = "email", required = true) String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}