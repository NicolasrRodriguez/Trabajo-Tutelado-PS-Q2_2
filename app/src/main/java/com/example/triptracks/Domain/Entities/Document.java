package com.example.triptracks.Domain.Entities;

public class Document {
    private String documentId;
    private String name;
    private String description;
    private String imageUrl;

    public Document() {

    }

    public Document(String documentId, String name, String description, String imageUrl) {
        this.documentId = documentId;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
