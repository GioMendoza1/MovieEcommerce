package edu.uci.ics.gamendo1.service.api_gateway.models.billing;

public class TransactionFeeModel {
    private String value;
    private String currency;

    public TransactionFeeModel(String value, String currency) {
        this.value = value;
        this.currency = currency;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
