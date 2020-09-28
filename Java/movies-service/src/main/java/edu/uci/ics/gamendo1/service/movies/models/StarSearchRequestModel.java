package edu.uci.ics.gamendo1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StarSearchRequestModel {
    @JsonProperty(value = "name")
    private String name;
    @JsonProperty(value = "birthYear")
    private Integer birthYear;
    @JsonProperty(value = "movieTitle")
    private String movieTitle;
    @JsonProperty(value = "offset")
    private Integer offset;
    @JsonProperty(value = "limit")
    private Integer limit;
    @JsonProperty(value = "orderby")
    private String orderby;
    @JsonProperty(value = "direction")
    private String direction;

    public StarSearchRequestModel() {
    }

    public StarSearchRequestModel(@JsonProperty(value = "name") String name,
                                  @JsonProperty(value = "birthYear") Integer birthYear,
                                  @JsonProperty(value = "movieTitle") String movieTitle,
                                  @JsonProperty(value = "offset") Integer offset,
                                  @JsonProperty(value = "limit") Integer limit,
                                  @JsonProperty(value = "orderby") String orderby,
                                  @JsonProperty(value = "direction") String direction)
    {
        this.name = name;
        this.birthYear = birthYear;
        this.movieTitle = movieTitle;
        this.offset = offset;
        this.limit = limit;
        this.orderby = orderby;
        this.direction = direction;
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

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getOrderby() {
        return orderby;
    }

    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "StarSearchRequestModel{" +
                "name='" + name + '\'' +
                ", birthYear=" + birthYear +
                ", movieTitle='" + movieTitle + '\'' +
                ", offset=" + offset +
                ", limit=" + limit +
                ", orderby='" + orderby + '\'' +
                ", direction='" + direction + '\'' +
                '}';
    }
}
