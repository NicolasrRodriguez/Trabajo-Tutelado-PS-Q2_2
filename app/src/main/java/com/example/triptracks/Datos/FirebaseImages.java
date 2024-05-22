package com.example.triptracks.Datos;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.LogicaNegocio.ImageAdapter;
import com.example.triptracks.Domain.LogicaNegocio.ItineraryUseCases.UpdateItinerary;
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

import javax.security.auth.callback.Callback;

public class FirebaseImages {

    private StorageReference ref = FirebaseStorage.getInstance().getReference();

    private UserInfo user = FirebaseAuth.getInstance().getCurrentUser();


    private FirebaseItineraryHandler itineraryHandler = new FirebaseItineraryHandler(updatedItineraries -> {});

    UpdateItinerary updateItinerary = new UpdateItinerary(itineraryHandler);

    private ImageAdapter adapter;

    public void setAdapter(ImageAdapter adapter) {
        this.adapter = adapter;
    }


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

    public void  removeImage(String url, Itinerary itinerary){
        removeFromStorage(url);
        removeFromitinerary(url,itinerary);

        Log.d("_IMGRCLY", "Eliminando imagen");
    }

    public void removeFromitinerary(String url , Itinerary itinerary){
        ArrayList<String> newImages = itinerary.getImageUris();
        newImages.remove(url);
        itinerary.setImageUris(newImages);

        updateItinerary.execute(itinerary,new ItineraryRepository.OperationCallback() {
            @Override
            public void onSuccess() {  Log.d("_IMGTAG","Uri de la imagen añadida"); }

            @Override
            public void onFailure(Exception e) {Log.d("_IMGTAG","Uri de la imagen no se pudo añadir");}
        });

    }
    private void removeFromStorage(String url){
        StorageReference ref =  FirebaseStorage.getInstance().getReferenceFromUrl(url);
        ref.delete();



    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateitinerary(Itinerary oldItinerary, String imageUrl){
        boolean local = false;
        ArrayList<String> newImages;
        if (oldItinerary.getImageUris() != null){

            Log.d("_IMM","en el itinerario hay " + oldItinerary.getImageUris().size());
            newImages = oldItinerary.getImageUris();
            newImages.add(imageUrl);
        }
        else{
            local = true;
            Log.d("_IMM","no hay imagenes en el itinerario ");
            newImages = new ArrayList<>();
            newImages.add(imageUrl);
        }


        Log.d("_IMM","ahora hay  " + newImages.size() +"imagenes en el itinerario " );

        oldItinerary.setImageUris(newImages);



        updateItinerary.execute(oldItinerary,new ItineraryRepository.OperationCallback() {
            @Override
            public void onSuccess() {  Log.d("_IMGTAG","Uri de la imagen añadida"); }

            @Override
            public void onFailure(Exception e) {Log.d("_IMGTAG","Uri de la imagen no se pudo añadir");}
        });

        if(local){
            adapter.addElement(imageUrl);
        }else{
            adapter.notifyDataSetChanged();
        }



    }





}
