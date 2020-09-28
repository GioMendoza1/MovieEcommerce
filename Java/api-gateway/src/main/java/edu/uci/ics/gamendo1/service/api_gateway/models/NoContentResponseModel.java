package edu.uci.ics.gamendo1.service.api_gateway.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoContentResponseModel  {
    private String message;
    private int delay;
    private String transactionID;

    public NoContentResponseModel(int delay, String transactionID) {
        this.message = "Request received.";
        this.delay = delay;
        this.transactionID = transactionID;
    }

    @JsonProperty(value = "message", required = true)
    public String getMessage() {
        return message;
    }

    @JsonProperty(value = "delay", required = true)
    public int getDelay() {
        return delay;
    }

    @JsonProperty(value = "transactionID", required = true)
    public String getTransactionID() {
        return transactionID;
    }
}
