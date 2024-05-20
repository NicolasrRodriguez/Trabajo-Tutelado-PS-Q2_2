package com.example.triptracks.Datos;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class FirebaseImages {

    private StorageReference ref = FirebaseStorage.getInstance().getReference();

    private UserInfo user = FirebaseAuth.getInstance().getCurrentUser();

    public void uploadImage(Uri image, String ItineraryId){
        if (image != null && user != null) {
            String imageId = UUID.randomUUID().toString();
            final StorageReference fileReference = ref.child("Itineraries/" + ItineraryId + "/Images/" + imageId + ".jpeg");
            fileReference.putFile(image)
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
            long timestamp = System.currentTimeMillis();
            Document document = new Document(documentId, documentName, documentDescription, downloadUrl, timestamp);
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

}
