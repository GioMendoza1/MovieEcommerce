package edu.uci.ics.gamendo1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ShopDeleteRequestModel {
    @JsonProperty(value = "email", required = true)
    private String email;
    @JsonProperty(value = "movieId", required = true)
    private String movieId;

    public ShopDeleteRequestModel(){}

    public ShopDeleteRequestModel(
    @JsonProperty(value = "email", required = true) String email,
    @JsonProperty(value = "movieId", required = true) String movieId
    )
    {
        this.email = email;
        this.movieId = movieId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }
}
