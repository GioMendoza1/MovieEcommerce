package edu.uci.ics.gamendo1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateRatingRequestModel {
    @JsonProperty(value = "id", required = true)
    private String id;
    @JsonProperty(value = "rating", required = true)
    private float rating;

    public UpdateRatingRequestModel() {
    }

    public UpdateRatingRequestModel(@JsonProperty(value = "id", required = true) String id, @JsonProperty(value = "rating", required = true) float rating) {
        this.id = id;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
