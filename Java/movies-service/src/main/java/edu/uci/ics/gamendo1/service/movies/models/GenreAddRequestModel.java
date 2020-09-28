package edu.uci.ics.gamendo1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenreAddRequestModel {
    @JsonProperty(value = "name", required = true)
    private String name;

    public GenreAddRequestModel() {
    }

    public GenreAddRequestModel(@JsonProperty(value = "name", required = true) String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
