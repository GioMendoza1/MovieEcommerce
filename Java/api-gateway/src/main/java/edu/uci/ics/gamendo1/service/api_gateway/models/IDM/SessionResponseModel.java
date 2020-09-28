package edu.uci.ics.gamendo1.service.api_gateway.models.IDM;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.gamendo1.service.api_gateway.models.RequestModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionResponseModel extends RequestModel {
    private int resultCode;
    private String message;
    private String sessionID;

    public SessionResponseModel() {}

    public SessionResponseModel(int resultCode, String message, String sessionID) {
        this.resultCode = resultCode;
        this.message = message;
        this.sessionID = sessionID;
    }

    public SessionResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
        this.sessionID = null;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}
