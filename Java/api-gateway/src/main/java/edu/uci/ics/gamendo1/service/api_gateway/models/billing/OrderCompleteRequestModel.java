package edu.uci.ics.gamendo1.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.gamendo1.service.api_gateway.models.RequestModel;

public class OrderCompleteRequestModel extends RequestModel {
    @JsonProperty(value = "paymentId", required = true)
    private String paymentId;
    @JsonProperty(value = "token", required = true)
    private String token;
    @JsonProperty(value = "PayerID", required = true)
    private String PayerID;

    public OrderCompleteRequestModel(@JsonProperty(value = "paymentId", required = true) String paymentId,
                                     @JsonProperty(value = "token", required = true) String token,
                                     @JsonProperty(value = "PayerID", required = true) String PayerID) {
        this.paymentId = paymentId;
        this.token = token;
        this.PayerID = PayerID;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPayerID() {
        return PayerID;
    }

    public void setPayerID(String payerID) {
        this.PayerID = PayerID;
    }
}
