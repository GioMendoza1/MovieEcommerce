package edu.uci.ics.gamendo1.service.billing.models;

public class AmountModel {
    private String total;
    private String currency;

    public AmountModel(String total, String currency) {
        this.total = total;
        this.currency = currency;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
