package edu.uci.ics.gamendo1.service.billing.models;


public class ShopModifyResponseModel {
    private int resultCode;
    private String message;

    public ShopModifyResponseModel() {}

    public ShopModifyResponseModel(int resultCode, String message) {
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
