package edu.uci.ics.gamendo1.service.api_gateway.models.movie;

public class GenreModel {
    private int id;
    private String name;

    public GenreModel() {
    }

    public GenreModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}