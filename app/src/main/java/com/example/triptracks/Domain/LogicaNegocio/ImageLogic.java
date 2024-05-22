package com.example.triptracks.Domain.LogicaNegocio;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;

import com.example.triptracks.Datos.FirebaseImages;
import com.example.triptracks.Datos.FirebaseItineraryHandler;
import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.Repository.ItineraryRepository;

import java.util.ArrayList;

import javax.security.auth.callback.Callback;

public class ImageLogic {

    private FirebaseItineraryHandler itineraryHandler = new FirebaseItineraryHandler(updatedItineraries -> {});

    private FirebaseImages firebaseImages = new FirebaseImages();

    private ImageAdapter adapter;

    public void setAdapter(ImageAdapter adapter) {
        firebaseImages.setAdapter(adapter);
    }

    public void uploadImage(Uri imageUri, Itinerary oldItinerary){
        if (imageUri != null) {

            Log.d("_IMM","Actualizo el itinerario ");

            firebaseImages.uploadImage(imageUri, oldItinerary);



        }
        else{
            Log.d("_IMGTAG","No hay imagen");

        }
    }
}
