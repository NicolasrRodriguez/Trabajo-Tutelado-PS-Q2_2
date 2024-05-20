package com.example.triptracks.Domain.LogicaNegocio;

import android.net.Uri;
import android.util.Log;

import com.example.triptracks.Datos.FirebaseImages;
import com.example.triptracks.Datos.FirebaseItineraryHandler;
import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.Repository.ItineraryRepository;

public class ImageLogic {

    private FirebaseItineraryHandler itineraryHandler = new FirebaseItineraryHandler(updatedItineraries -> {});

    private FirebaseImages firebaseImages = new FirebaseImages();
    private Itinerary itineraryaux;

    public void uploadImage(Uri imageUri, Itinerary oldItinerary){
        if (imageUri != null) {

            firebaseImages.uploadImage(imageUri,oldItinerary.getId());
            itineraryaux = new Itinerary(oldItinerary.getId(),oldItinerary.getItineraryTitle(),oldItinerary.getCountry(),
                        oldItinerary.getState(),oldItinerary.getCity(), oldItinerary.getAdmin(),oldItinerary.getColaborators(),
                        oldItinerary.getStartDate(),oldItinerary.getEndDate(),oldItinerary.getImagesuris());
            itineraryaux.addImageUri(imageUri.toString());

            UpdateItinerary updateItinerary = new UpdateItinerary(itineraryHandler);
            updateItinerary.execute(itineraryaux,new ItineraryRepository.OperationCallback() {
                @Override
                public void onSuccess() {  Log.d("_IMGTAG","Uri de la imagen añadida"); }

                @Override
                public void onFailure(Exception e) {Log.d("_IMGTAG","Uri de la imagen no se pudo añadir");}
            });
            //return true;//si devuelve un boleano se puede notificar a la vista

        }
        else{
            Log.d("_IMGTAG","No hay imagen");
            //return false;
        }
    }
}
