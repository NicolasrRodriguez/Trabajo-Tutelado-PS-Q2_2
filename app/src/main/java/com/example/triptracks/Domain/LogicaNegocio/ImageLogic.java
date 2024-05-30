package com.example.triptracks.Domain.LogicaNegocio;

import android.net.Uri;
import android.util.Log;

import com.example.triptracks.Datos.FirebaseImages;
import com.example.triptracks.Datos.FirebaseItineraryHandler;
import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.LogicaNegocio.Adapter.ImageAdapter;

public class ImageLogic {


    private FirebaseImages firebaseImages = new FirebaseImages();


    public void setAdapter(ImageAdapter adapter) {
        firebaseImages.setAdapter(adapter);
    }

    public void uploadImage(Uri imageUri, Itinerary oldItinerary){
        if (imageUri != null) {

            Log.d("_IMGLOGIC","Actualizo el itinerario ");

            firebaseImages.uploadImage(imageUri, oldItinerary);//llama a la base de datos para actualizar el itinerario



        }
        else{
            Log.d("_IMGLOGIC","No hay imagen");

        }
    }
}
