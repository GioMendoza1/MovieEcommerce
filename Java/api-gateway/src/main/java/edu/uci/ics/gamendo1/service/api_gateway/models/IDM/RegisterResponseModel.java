package edu.uci.ics.gamendo1.service.api_gateway.models.IDM;

import edu.uci.ics.gamendo1.service.api_gateway.models.RequestModel;

public class RegisterResponseModel extends RequestModel {

    private int resultCode;
    private String message;

    public RegisterResponseModel() {}

    public RegisterResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
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
}
