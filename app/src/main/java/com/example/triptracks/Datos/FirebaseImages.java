package com.example.triptracks.Datos;

import android.net.Uri;
import android.util.Log;

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
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        } else {
            Log.d("_IMAGETAG", "File URI or user is null");
        }

    }

}
