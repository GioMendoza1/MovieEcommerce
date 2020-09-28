package edu.uci.ics.gamendo1.service.api_gateway.models.movie;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.gamendo1.service.api_gateway.models.RequestModel;

public class StarAddRequestModel extends RequestModel {
    @JsonProperty(value = "name", required = true)
    private String name;
    @JsonProperty(value = "birthYear")
    private Integer birthYear;

    public StarAddRequestModel() {
    }

    public StarAddRequestModel(@JsonProperty(value = "name", required = true) String name, @JsonProperty(value = "birthYear") Integer birthYear) {
        this.name = name;
        this.birthYear = birthYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }
}
