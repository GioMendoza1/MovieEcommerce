package edu.uci.ics.gamendo1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderRequestModel {
    @JsonProperty(value = "email", required = true)
    private String email;

    public OrderRequestModel() {}

    public OrderRequestModel(@JsonProperty(value = "email", required = true) String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
