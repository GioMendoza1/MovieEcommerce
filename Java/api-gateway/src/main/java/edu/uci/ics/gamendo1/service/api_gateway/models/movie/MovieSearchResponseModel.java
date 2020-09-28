package edu.uci.ics.gamendo1.service.api_gateway.models.movie;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieSearchResponseModel {
    private int resultCode;
    private String message;
    private MovieModel[] movies;

    public MovieSearchResponseModel()
    {
        movies = null;
    }

    public MovieSearchResponseModel(int resultCode, String message, MovieModel[] movies) {
        this.resultCode = resultCode;
        this.message = message;
        this.movies = movies;
    }

    public MovieSearchResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
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

    public MovieModel[] getMovies() {
        return movies;
    }

    public void setMovies(MovieModel[] movies) {
        this.movies = movies;
    }
}
