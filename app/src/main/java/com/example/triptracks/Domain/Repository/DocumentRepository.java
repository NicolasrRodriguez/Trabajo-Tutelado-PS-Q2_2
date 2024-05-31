package com.example.triptracks.Domain.Repository;

import android.net.Uri;

import com.example.triptracks.Domain.Entities.Document;

import java.util.List;
import java.util.function.Consumer;

public interface DocumentRepository {
    void uploadImage(Uri fileUri, String documentName, String documentDescription, Consumer<String> onSuccess, Consumer<String> onFailure);
    void getDocuments(Consumer<List<Document>> onSuccess, Consumer<String> onFailure);
    void deleteDocument(String imageUrl, Consumer<String> onSuccess, Consumer<String> onFailure);
    void getDocumentDetails(String documentId, Consumer<Document> onSuccess, Consumer<String> onFailure);
}
