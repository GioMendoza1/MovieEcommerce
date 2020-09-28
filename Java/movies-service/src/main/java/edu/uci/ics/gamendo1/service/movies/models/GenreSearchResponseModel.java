package edu.uci.ics.gamendo1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenreSearchResponseModel {
    private int resultCode;
    private String message;
    private GenreModel[] genres;

    public GenreSearchResponseModel() {
    }

    public GenreSearchResponseModel(int resultCode, String message, GenreModel[] genres) {
        this.resultCode = resultCode;
        this.message = message;
        this.genres = genres;
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

    public GenreModel[] getGenres() {
        return genres;
    }

    public void setGenres(GenreModel[] genres) {
        this.genres = genres;
    }

}
