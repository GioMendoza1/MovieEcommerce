package edu.uci.ics.gamendo1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieSearchByIDResponseModel {
    private int resultCode;
    private String message;
    private MovieModelFull movies;

    public MovieSearchByIDResponseModel() {
    }

    public MovieSearchByIDResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public MovieSearchByIDResponseModel(int resultCode, String message, MovieModelFull movies) {
        this.resultCode = resultCode;
        this.message = message;
        this.movies = movies;
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

    public MovieModelFull getMovies() {
        return movies;
    }

    public void setMovies(MovieModelFull movies) {
        this.movies = movies;
    }
}
