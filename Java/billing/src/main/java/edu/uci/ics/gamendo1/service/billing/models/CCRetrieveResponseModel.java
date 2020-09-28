package edu.uci.ics.gamendo1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class CCRetrieveResponseModel {
    private int resultCode;
    private String message;
    private CreditCardModel creditcard;


    public CCRetrieveResponseModel() {}

    public CCRetrieveResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public CCRetrieveResponseModel(int resultCode, String message, CreditCardModel creditcard) {
        this.resultCode = resultCode;
        this.message = message;
        this.creditcard = creditcard;
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

    public CreditCardModel getCreditcard() {
        return creditcard;
    }

    public void setCreditcard(CreditCardModel creditcard) {
        this.creditcard = creditcard;
    }
}
