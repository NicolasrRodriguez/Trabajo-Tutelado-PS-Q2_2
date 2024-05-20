package com.example.triptracks.Domain.Entities;

public class Imagen {

    private String imageUrl;

    public Imagen(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
