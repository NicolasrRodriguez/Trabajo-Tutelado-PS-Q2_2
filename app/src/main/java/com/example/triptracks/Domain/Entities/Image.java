package com.example.triptracks.Domain.Entities;

public class Image {

    private String ImageId;

    private String ImageURL;

    public Image(String imageId, String imageURL) {
        ImageId = imageId;
        ImageURL = imageURL;
    }

    public String getImageId() {
        return ImageId;
    }

    public void setImageId(String imageId) {
        ImageId = imageId;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }
}
