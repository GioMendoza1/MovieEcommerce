package edu.uci.ics.gamendo1.service.api_gateway.models.movie;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StarSearchResponseModel {
    private int resultCode;
    private String message;
    private StarModel[] stars;

    public StarSearchResponseModel() {
    }

    public StarSearchResponseModel(int resultCode, String message, StarModel[] movies) {
        this.resultCode = resultCode;
        this.message = message;
        this.stars = stars;
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

    public StarModel[] getStars() {
        return stars;
    }

    public void setStars(StarModel[] stars) {
        this.stars = stars;
    }
}
