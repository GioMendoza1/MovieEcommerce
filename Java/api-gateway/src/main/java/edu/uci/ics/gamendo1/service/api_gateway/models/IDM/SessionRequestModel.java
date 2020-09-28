package edu.uci.ics.gamendo1.service.api_gateway.models.IDM;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.gamendo1.service.api_gateway.models.RequestModel;

public class SessionRequestModel extends RequestModel {
    private String email;
    private String sessionID;

    public SessionRequestModel() {}

    @JsonCreator
    public SessionRequestModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "sessionID", required = true) String sessionID) {
        this.email = email;
        this.sessionID = sessionID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}
