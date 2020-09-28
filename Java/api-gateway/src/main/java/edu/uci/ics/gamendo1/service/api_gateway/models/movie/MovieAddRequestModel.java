package edu.uci.ics.gamendo1.service.api_gateway.models.movie;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.gamendo1.service.api_gateway.models.RequestModel;

public class MovieAddRequestModel extends RequestModel {
    @JsonProperty(value = "title", required = true)
    private String title;
    @JsonProperty(value = "director", required = true)
    private String director;
    @JsonProperty(value = "year", required = true)
    private Integer year;
    @JsonProperty(value = "backdrop_path")
    private String backdrop_path;
    @JsonProperty(value = "budget")
    private Integer budget;
    @JsonProperty(value = "overview")
    private String overview;
    @JsonProperty(value = "poster_path")
    private String poster_path;
    @JsonProperty(value = "revenue")
    private Integer revenue;
    @JsonProperty(value = "genres", required = true)
    private GenreModel[] genres;

    public MovieAddRequestModel() {
    }

    public MovieAddRequestModel(@JsonProperty(value = "title", required = true) String title,
                                @JsonProperty(value = "director", required = true) String director,
                                @JsonProperty(value = "year", required = true) Integer year,
                                @JsonProperty(value = "backdrop_path") String backdrop_path,
                                @JsonProperty(value = "budget") Integer budget,
                                @JsonProperty(value = "overview") String overview,
                                @JsonProperty(value = "poster_path") String poster_path,
                                @JsonProperty(value = "revenue") Integer revenue,
                                @JsonProperty(value = "genres", required = true) GenreModel[] genres) {
        this.title = title;
        this.year = year;
        this.director = director;
        this.backdrop_path = backdrop_path;
        this.budget = budget;
        this.overview = overview;
        this.poster_path = poster_path;
        this.revenue = revenue;
        this.genres = genres;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public Integer getBudget() {
        return budget;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public Integer getRevenue() {
        return revenue;
    }

    public void setRevenue(Integer revenue) {
        this.revenue = revenue;
    }

    public GenreModel[] getGenres() {
        return genres;
    }

    public void setGenres(GenreModel[] genres) {
        this.genres = genres;
    }
}
