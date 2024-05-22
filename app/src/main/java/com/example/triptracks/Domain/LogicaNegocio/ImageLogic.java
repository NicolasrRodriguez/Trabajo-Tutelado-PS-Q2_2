package com.example.triptracks.Domain.LogicaNegocio;

import android.net.Uri;
import android.util.Log;

import com.example.triptracks.Datos.FirebaseImages;
import com.example.triptracks.Datos.FirebaseItineraryHandler;
import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.Repository.ItineraryRepository;

import java.util.ArrayList;

public class ImageLogic {

    private FirebaseItineraryHandler itineraryHandler = new FirebaseItineraryHandler(updatedItineraries -> {});

    private FirebaseImages firebaseImages = new FirebaseImages();

    private ImageAdapter adapter;
    private Itinerary itineraryaux;

    public void setAdapter(ImageAdapter adapter) {
        this.adapter = adapter;
    }

    public void uploadImage(Uri imageUri, Itinerary oldItinerary){
        if (imageUri != null) {

            Log.d("_IMM","en el itinerario hay ");
            firebaseImages.uploadImage(imageUri,oldItinerary);
            firebaseImages.setAdapter(adapter);


        }
        else{
            Log.d("_IMGTAG","No hay imagen");

        }
    }
}
