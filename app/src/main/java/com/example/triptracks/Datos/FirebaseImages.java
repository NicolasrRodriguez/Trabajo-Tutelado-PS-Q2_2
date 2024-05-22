package com.example.triptracks.Datos;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.LogicaNegocio.ImageAdapter;
import com.example.triptracks.Domain.LogicaNegocio.UpdateItinerary;
import com.example.triptracks.Domain.Repository.ItineraryRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.UUID;

public class FirebaseImages {

    private StorageReference ref = FirebaseStorage.getInstance().getReference();

    private UserInfo user = FirebaseAuth.getInstance().getCurrentUser();

    private ImageAdapter adapter;

    public void setAdapter(ImageAdapter adapter) {
        this.adapter = adapter;
    }

    private FirebaseItineraryHandler itineraryHandler = new FirebaseItineraryHandler(updatedItineraries -> {});

    UpdateItinerary updateItinerary = new UpdateItinerary(itineraryHandler);

    public void uploadImage(Uri image, Itinerary oldItinerary){

        if (image != null && user != null) {
            String imageId = UUID.randomUUID().toString();
            final StorageReference fileReference = ref.child("Itineraries/" + oldItinerary.getId() + "/Images/" + imageId + ".jpeg");
            fileReference.putFile(image)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    Log.d("_IMGTAG","URL: " + downloadUrl);
                                    updateitinerary( oldItinerary, downloadUrl);

                                }

                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        } else {
            Log.d("_IMGTAG", "File URI or user is null");
        }

    }

    public void  removeImage(String url){
        StorageReference ref =  FirebaseStorage.getInstance().getReferenceFromUrl(url);
        ref.delete();

        Log.d("_IMGRCLY", "Eliminando imagen");
    }

    public void updateitinerary(Itinerary oldItinerary, String imageUrl){
        ArrayList<String> newImages;
        if (oldItinerary.getImageUris() != null){
            Log.d("_IMM","en el itinerario hay " + oldItinerary.getImageUris().size());
            newImages = oldItinerary.getImageUris();
            newImages.add(imageUrl);
        }
        else{
            Log.d("_IMM","no hay imagenes en el itinerario ");
            newImages = new ArrayList<>();
            newImages.add(imageUrl);
        }


        adapter.anadirelem();
        ////actualiza el recyclerview

        Log.d("_IMM","ahora hay  " + newImages.size() +"imagenes en el itinerario " );

        Itinerary itineraryaux = new Itinerary(oldItinerary.getId(),oldItinerary.getItineraryTitle(),oldItinerary.getCountry(),
                oldItinerary.getState(),oldItinerary.getCity(), oldItinerary.getAdmin(),oldItinerary.getColaborators(),
                oldItinerary.getStartDate(),oldItinerary.getEndDate(),newImages);

        Log.d("_IMM","en el itinerario hay " + itineraryaux.getImageUris().size());

        Log.d("_IMGTAG","Itinerario auxiliar creado "  + itineraryaux.getColaborators().size());


        updateItinerary.execute(itineraryaux,new ItineraryRepository.OperationCallback() {
            @Override
            public void onSuccess() {  Log.d("_IMGTAG","Uri de la imagen añadida"); }

            @Override
            public void onFailure(Exception e) {Log.d("_IMGTAG","Uri de la imagen no se pudo añadir");}
        });
    }





}
