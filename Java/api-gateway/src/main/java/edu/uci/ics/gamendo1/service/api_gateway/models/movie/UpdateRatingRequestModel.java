package edu.uci.ics.gamendo1.service.api_gateway.models.movie;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.gamendo1.service.api_gateway.models.RequestModel;

public class UpdateRatingRequestModel extends RequestModel {
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
