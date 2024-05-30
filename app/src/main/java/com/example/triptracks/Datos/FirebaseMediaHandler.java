package com.example.triptracks.Datos;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.triptracks.Domain.Entities.Document;
import com.example.triptracks.Domain.Repository.DocumentRepository;
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

public class FirebaseMediaHandler implements DocumentRepository {
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
            Log.e("_FIREBASEAUTH", "User not logged in.");
        }
    }


    @Override
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





    @Override
    public void getDocuments(Consumer<List<Document>> onSuccess, Consumer<String> onFailure) {
        if (user != null) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Document> documents = new ArrayList<>();
                    for (DataSnapshot documentSnapshot : snapshot.getChildren()) {
                        Document document = documentSnapshot.getValue(Document.class);
                        documents.add(document);
                    }
                    onSuccess.accept(documents);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    onFailure.accept("Error fetching documents: " + error.getMessage());
                }
            });
        } else {
            onFailure.accept("User not logged in");
        }
    }

    @Override
    public void deleteDocument(String imageUrl, Consumer<String> onSuccess, Consumer<String> onFailure) {
        if (user != null) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
            imageRef.delete().addOnSuccessListener(aVoid -> {

                databaseReference.orderByChild("imageUrl").equalTo(imageUrl).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot documentSnapshot : snapshot.getChildren()) {
                            documentSnapshot.getRef().removeValue().addOnSuccessListener(aVoid2 -> {
                                onSuccess.accept("Document deleted successfully");
                            }).addOnFailureListener(e -> onFailure.accept("Failed to delete document from database: " + e.getMessage()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        onFailure.accept("Failed to query document: " + error.getMessage());
                    }
                });
            }).addOnFailureListener(e -> onFailure.accept("Failed to delete image: " + e.getMessage()));
        } else {
            onFailure.accept("User not logged in");
        }
    }


    @Override
    public void getDocumentDetails(String documentId, Consumer<Document> onSuccess, Consumer<String> onFailure) {
        if (user != null) {
            databaseReference.child(documentId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Document document = snapshot.getValue(Document.class);
                        onSuccess.accept(document);
                    } else {
                        onFailure.accept("Document not found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    onFailure.accept("Error fetching document details: " + error.getMessage());
                }
            });
        } else {
            onFailure.accept("User not logged in");
        }
    }



}
