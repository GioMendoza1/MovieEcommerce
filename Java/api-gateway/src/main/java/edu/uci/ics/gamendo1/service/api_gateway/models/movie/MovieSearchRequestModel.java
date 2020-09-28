package edu.uci.ics.gamendo1.service.api_gateway.models.movie;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.gamendo1.service.api_gateway.models.RequestModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieSearchRequestModel extends RequestModel {
    @JsonProperty(value = "title")
    private String title;
    @JsonProperty(value = "genre")
    private String genre;
    @JsonProperty(value = "year")
    private Integer year;
    @JsonProperty(value = "director")
    private String director;
    @JsonProperty(value = "hidden")
    private Boolean hidden;
    @JsonProperty(value = "offset")
    private Integer offset;
    @JsonProperty(value = "limit")
    private Integer limit;
    @JsonProperty(value = "orderby")
    private String orderby;
    @JsonProperty(value = "direction")
    private String direction;
    @JsonProperty(value = "isHidden")
    private Boolean isHidden;

    public MovieSearchRequestModel() {}

    public MovieSearchRequestModel(
            @JsonProperty(value = "title") String title,
            @JsonProperty(value = "genre") String genre,
            @JsonProperty(value = "year") int year,
            @JsonProperty(value = "director") String director,
            @JsonProperty(value = "hidden") boolean hidden,
            @JsonProperty(value = "offset") int offset,
            @JsonProperty(value = "limit") int limit,
            @JsonProperty(value = "orderby") String orderby,
            @JsonProperty(value = "direction") String direction)
    {
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.director = director;
        this.hidden = hidden;
        this.offset = offset;
        this.limit = limit;
        this.orderby = orderby;
        this.direction = direction;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
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

    public Boolean getIsHidden(){
        return isHidden;
    }

    public void setIsHidden(Boolean isHidden)
    {
        this.isHidden = isHidden;
    }

    @Override
    public String toString() {
        return "MovieSearchRequestModel{" +
                "title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", year=" + year +
                ", director='" + director + '\'' +
                ", hidden=" + hidden +
                ", offset=" + offset +
                ", limit=" + limit +
                ", orderby='" + orderby + '\'' +
                ", direction='" + direction + '\'' +
                ", isHidden=" + isHidden +
                '}';
    }
}

