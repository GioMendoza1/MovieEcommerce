package edu.uci.ics.gamendo1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ShopRequestModel {
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
