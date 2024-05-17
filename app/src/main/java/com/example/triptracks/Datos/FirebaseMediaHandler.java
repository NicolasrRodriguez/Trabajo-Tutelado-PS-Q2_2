package com.example.triptracks.Datos;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.triptracks.Domain.Entities.Document;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class FirebaseMediaHandler {
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseUser user;

    public FirebaseMediaHandler() {
        storageReference = FirebaseStorage.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userPath = user.getEmail().replace(".", ",");
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userPath).child("documents");
        } else {
            Log.e("FirebaseAuth", "User not logged in.");
        }
    }

    public void uploadImage(Uri fileUri, String documentName, String documentDescription, Consumer<String> onSuccess, Consumer<String> onFailure) {
        if (fileUri != null && user != null) {
            String userPath = user.getEmail().replace(".", ",");
            String documentId = UUID.randomUUID().toString();
            final StorageReference fileReference = storageReference.child("users/" + userPath + "/documents/" + documentId + ".jpeg");
            fileReference.putFile(fileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    saveDocumentInfo(documentId, documentName, documentDescription, downloadUrl, onSuccess, onFailure);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            onFailure.accept("Failed to upload image: " + e.getMessage());
                        }
                    });
        } else {
            onFailure.accept("File URI or user is null");
        }
    }
    private void saveDocumentInfo(String documentId, String documentName, String documentDescription, String downloadUrl, Consumer<String> onSuccess, Consumer<String> onFailure) {
        if (user != null) {
            Document document = new Document(documentId, documentName, documentDescription, downloadUrl);
            databaseReference.child(documentId).setValue(document)
                    .addOnSuccessListener(aVoid -> {
                        onSuccess.accept("Document added successfully");
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("documentId", documentId);
                        databaseReference.child(documentId).updateChildren(updates);
                    })
                    .addOnFailureListener(e -> onFailure.accept("Failed to add document: " + e.getMessage()));
        } else {
            onFailure.accept("User not logged in");
        }
    }



    public void getImagesFromDocumentsFolder(Consumer<List<String>> onSuccess, Consumer<String> onFailure) {
        if (user != null) {
            String userPath = user.getEmail().replace(".", ",");
            StorageReference userDocumentsRef = storageReference.child("users/" + userPath + "/documents");

            userDocumentsRef.listAll().addOnSuccessListener(listResult -> {
                List<String> imageUrls = new ArrayList<>();
                int itemCount = listResult.getItems().size();
                int[] counter = {0};

                for (StorageReference item : listResult.getItems()) {
                    item.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrls.add(uri.toString());
                        counter[0]++;

                        if (counter[0] == itemCount) {
                            onSuccess.accept(imageUrls);
                        }
                    }).addOnFailureListener(e -> onFailure.accept("Failed to download image: " + e.getMessage()));
                }
            }).addOnFailureListener(e -> onFailure.accept("Failed to list documents: " + e.getMessage()));
        } else {
            onFailure.accept("User not logged in");
        }
    }

    public void deleteDocument(String imageUrl, Consumer<String> onSuccess, Consumer<String> onFailure) {
        if (user != null) {

            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
            imageRef.delete().addOnSuccessListener(aVoid -> {
                onSuccess.accept("Document image deleted successfully");
            }).addOnFailureListener(e -> onFailure.accept("Failed to delete image: " + e.getMessage()));
        } else {
            onFailure.accept("User not logged in");
        }
    }

}
