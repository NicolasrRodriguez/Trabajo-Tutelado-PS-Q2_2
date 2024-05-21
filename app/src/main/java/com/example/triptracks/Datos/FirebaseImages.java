package com.example.triptracks.Datos;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.LogicaNegocio.UpdateItinerary;
import com.example.triptracks.Domain.Repository.ItineraryRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class FirebaseImages {

    private StorageReference ref = FirebaseStorage.getInstance().getReference();

    private UserInfo user = FirebaseAuth.getInstance().getCurrentUser();

    private FirebaseItineraryHandler itineraryHandler = new FirebaseItineraryHandler(updatedItineraries -> {});

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
                                    updateitinerary( oldItinerary,downloadUrl);
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
            Log.d("_IMAGETAG", "File URI or user is null");
        }

    }

    public void updateitinerary(Itinerary oldItinerary, String imageUrl){

        Itinerary itineraryaux = new Itinerary(oldItinerary.getId(),oldItinerary.getItineraryTitle(),oldItinerary.getCountry(),
                oldItinerary.getState(),oldItinerary.getCity(), oldItinerary.getAdmin(),oldItinerary.getColaborators(),
                oldItinerary.getStartDate(),oldItinerary.getEndDate(),oldItinerary.getImagesuris());
        itineraryaux.addImageUri(imageUrl);

        Log.d("_IMGTAG","Itinerario auxiliar creado");

        UpdateItinerary updateItinerary = new UpdateItinerary(itineraryHandler);
        updateItinerary.execute(itineraryaux,new ItineraryRepository.OperationCallback() {
            @Override
            public void onSuccess() {  Log.d("_IMGTAG","Uri de la imagen añadida"); }

            @Override
            public void onFailure(Exception e) {Log.d("_IMGTAG","Uri de la imagen no se pudo añadir");}
        });
    }




}
