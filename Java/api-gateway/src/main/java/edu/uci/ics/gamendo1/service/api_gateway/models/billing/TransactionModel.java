package edu.uci.ics.gamendo1.service.api_gateway.models.billing;

public class TransactionModel {
    private String transactionId;
    private String state;
    private AmountModel amount;
    private TransactionFeeModel transactionFee;
    private String create_time;
    private String update_time;
    private OrderItemsModel items[];

    public TransactionModel() {}

    public TransactionModel(String transactionId, String state, AmountModel amount, TransactionFeeModel transactionFee, String create_time, String update_time, OrderItemsModel items[]) {
        this.transactionId = transactionId;
        this.state = state;
        this.amount = amount;
        this.transactionFee = transactionFee;
        this.create_time = create_time;
        this.update_time = update_time;
        this.items = items;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public AmountModel getAmount() {
        return amount;
    }

    public void setAmount(AmountModel amount) {
        this.amount = amount;
    }

    public TransactionFeeModel getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(TransactionFeeModel transactionFee) {
        this.transactionFee = transactionFee;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public OrderItemsModel[] getItems() {
        return items;
    }

    public void setItems(OrderItemsModel[] items) {
        this.items = items;
    }
}
