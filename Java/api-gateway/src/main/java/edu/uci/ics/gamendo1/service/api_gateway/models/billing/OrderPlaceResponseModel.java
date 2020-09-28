package edu.uci.ics.gamendo1.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.gamendo1.service.api_gateway.models.RequestModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderPlaceResponseModel extends RequestModel {
    private int resultCode;
    private String message;
    private String redirectURL;
    private String token;


    public OrderPlaceResponseModel() {}

    public OrderPlaceResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public OrderPlaceResponseModel(int resultCode, String message, String redirectURL, String token) {
        this.resultCode = resultCode;
        this.message = message;
        this.redirectURL = redirectURL;
        this.token = token;
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

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
