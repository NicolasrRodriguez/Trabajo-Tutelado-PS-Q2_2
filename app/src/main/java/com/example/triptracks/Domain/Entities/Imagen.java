package com.example.triptracks.Domain.Entities;

import java.net.URL;

public class Imagen {

    private URL imageUrl;



    public Imagen(URL imageUrl ) {
        this.imageUrl = imageUrl;

    }



    public URL getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(URL imageUrl) {
        this.imageUrl = imageUrl;
    }
}
