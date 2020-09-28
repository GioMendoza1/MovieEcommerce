package edu.uci.ics.gamendo1.service.idm.models;

public class PrivilegeResponseModel {
    private int resultCode;
    private String message;

    public PrivilegeResponseModel() {}

    public PrivilegeResponseModel(int resultCode, String message) {
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
