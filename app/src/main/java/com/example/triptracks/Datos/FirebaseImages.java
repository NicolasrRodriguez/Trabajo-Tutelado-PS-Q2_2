package com.example.triptracks.Datos;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseImages {

    private StorageReference ref = FirebaseStorage.getInstance().getReference();

    public void uploadImage(Uri image){


    }

    public void createImagesPath(String ItineraryId){

        ref.child("Itineraries/" + ItineraryId);

    }
}
