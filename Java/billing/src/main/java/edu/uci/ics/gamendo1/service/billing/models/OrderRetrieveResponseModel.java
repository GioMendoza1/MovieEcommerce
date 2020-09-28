package edu.uci.ics.gamendo1.service.billing.models;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRetrieveResponseModel {
    private int resultCode;
    private String message;
    private TransactionModel[] transactions;

    public OrderRetrieveResponseModel() {}

    public OrderRetrieveResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public OrderRetrieveResponseModel(int resultCode, String message, TransactionModel transactions[]) {
        this.resultCode = resultCode;
        this.message = message;
        this.transactions = transactions;
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

    public TransactionModel[] getTransactions() {
        return transactions;
    }

    public void setTransactions(TransactionModel[] transactions) {
        this.transactions = transactions;
    }
}
