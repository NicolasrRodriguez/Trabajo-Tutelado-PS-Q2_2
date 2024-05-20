package com.example.triptracks.Domain.LogicaNegocio;

import android.util.Log;

import com.example.triptracks.Datos.FirebaseItineraryHandler;
import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.Repository.ItineraryRepository;

public class ImageLogic {

    private FirebaseItineraryHandler itineraryHandler = new FirebaseItineraryHandler(updatedItineraries -> {});
    private Itinerary itineraryaux;

    public void uploadImage(String imageUri, Itinerary oldItinerary){
        if (imageUri != null) {
            Log.d("_IMGTAG","imagen no nula");
                      Log.d("_IMGTAG","itineario aux");

            Log.d("_IMGTAG","Imageuris = " +  oldItinerary.getImagesuris());
                Log.d("_IMGTAG","No hay mas imagenes");
                itineraryaux = new Itinerary(oldItinerary.getId(),oldItinerary.getItineraryTitle(),oldItinerary.getCountry(),
                        oldItinerary.getState(),oldItinerary.getCity(), oldItinerary.getAdmin(),oldItinerary.getColaborators(),
                        oldItinerary.getStartDate(),oldItinerary.getEndDate());

                Log.d("_IMGTAG","Ya hay imagenes");

            Log.d("_IMGTAG","itineraio auxiliar creado con " + itineraryaux.getImagesuris() + "imagenes");
            itineraryaux.addImageUri(imageUri);
            Log.d("_IMGTAG","imagen añadida a la lista");
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
