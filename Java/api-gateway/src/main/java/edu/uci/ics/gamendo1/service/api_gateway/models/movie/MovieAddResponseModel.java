package edu.uci.ics.gamendo1.service.api_gateway.models.movie;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieAddResponseModel {
    private int resultCode;
    private String message;
    private String movieid;
    private int[] genreid;

    public MovieAddResponseModel() {
    }

    public MovieAddResponseModel(int resultCode, String message, String movieid, int[] genreid) {
        this.resultCode = resultCode;
        this.message = message;
        this.movieid = movieid;
        this.genreid = genreid;
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

    public String getMovieid() {
        return movieid;
    }

    public void setMovieid(String movieid) {
        this.movieid = movieid;
    }

    public int[] getGenreid() {
        return genreid;
    }

    public void setGenreid(int[] genreid) {
        this.genreid = genreid;
    }
}
