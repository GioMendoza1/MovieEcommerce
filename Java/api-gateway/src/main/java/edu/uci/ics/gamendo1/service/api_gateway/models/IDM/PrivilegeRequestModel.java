package edu.uci.ics.gamendo1.service.api_gateway.models.IDM;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.gamendo1.service.api_gateway.models.RequestModel;

public class PrivilegeRequestModel extends RequestModel {
    private String email;
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
