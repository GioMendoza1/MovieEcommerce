package edu.uci.ics.gamendo1.service.idm.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PrivilegeRequestModel {
    @JsonProperty (value = "email", required = true)
    private String email;
    @JsonProperty (value = "plevel", required = true)
    private int plevel;

    public PrivilegeRequestModel() {}

    @JsonCreator
    public PrivilegeRequestModel(
            @JsonProperty (value = "email", required = true) String email,
            @JsonProperty (value = "plevel", required = true) int plevel) {
        this.email = email;
        this.plevel = plevel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPlevel() {
        return plevel;
    }

    public void setPlevel(int plevel) {
        this.plevel = plevel;
    }
}
