package edu.uci.ics.gamendo1.service.api_gateway.models.billing;

public class ItemModel {
    private String email;
    private String movieId;
    private int quantity;

    public ItemModel() {}

    public ItemModel(String email, String movieId, int quantity) {
        this.email = email;
        this.movieId = movieId;
        this.quantity = quantity;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
