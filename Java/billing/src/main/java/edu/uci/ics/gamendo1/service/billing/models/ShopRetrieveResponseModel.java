package edu.uci.ics.gamendo1.service.billing.models;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopRetrieveResponseModel {
    private int resultCode;
    private String message;
    private ItemModel items[];

    public ShopRetrieveResponseModel() {}

    public ShopRetrieveResponseModel(int resultCode, String message, ItemModel[] items) {
        this.resultCode = resultCode;
        this.message = message;
        this.items = items;
    }

    public ShopRetrieveResponseModel(int resultCode, String message) {
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

    public ItemModel[] getItems() {
        return items;
    }

    public void setItems(ItemModel[] items) {
        this.items = items;
    }
}
