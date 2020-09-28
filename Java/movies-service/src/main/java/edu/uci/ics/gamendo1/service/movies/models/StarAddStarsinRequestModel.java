package edu.uci.ics.gamendo1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StarAddStarsinRequestModel {
    @JsonProperty(value = "starid", required = true)
    private String starid;
    @JsonProperty(value = "movieid", required = true)
    private String movieid;

    public StarAddStarsinRequestModel() {
    }

    public StarAddStarsinRequestModel(@JsonProperty(value = "starid", required = true) String starid, @JsonProperty(value = "movieid", required = true) String movieid) {
        this.starid = starid;
        this.movieid = movieid;
    }

    public String getStarid() {
        return starid;
    }

    public void setStarid(String starid) {
        this.starid = starid;
    }

    public String getMovieid() {
        return movieid;
    }

    public void setMovieid(String movieid) {
        this.movieid = movieid;
    }
}