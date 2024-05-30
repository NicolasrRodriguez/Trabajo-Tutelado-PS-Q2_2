package com.example.triptracks.Datos;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.LogicaNegocio.Adapter.ImageAdapter;
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

public class FirebaseImages {

    private StorageReference ref = FirebaseStorage.getInstance().getReference(); //Referencia a la base de datos "Storage"

    private UserInfo user = FirebaseAuth.getInstance().getCurrentUser(); //usuario que realiza las operaciones


    private FirebaseItineraryHandler itineraryHandler = new FirebaseItineraryHandler(updatedItineraries -> {}); //Clase que se encarga de gestionar los itinerairos en la base de datos

    UpdateItinerary updateItinerary = new UpdateItinerary(itineraryHandler);//Funcion  para actualizar el itinerario

    private ImageAdapter adapter;//adapter del RecyclerView de imagenes

    public void setAdapter(ImageAdapter adapter) {
        this.adapter = adapter;
    }


    public void uploadImage(Uri image, Itinerary oldItinerary){

        if (image != null && user != null) {
            String imageId = UUID.randomUUID().toString();//id aleatoria para la imagen
            final StorageReference fileReference = ref.child("Itineraries/" + oldItinerary.getId() + "/Images/" + imageId + ".jpeg");
            fileReference.putFile(image)//carga  la imagen en la base de datos "Storage"
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    Log.d("_FIREMASEIMG","URL: " + downloadUrl);
                                    updateitinerary( oldItinerary, downloadUrl);//Añade la URL al itinerario en la base de datos "RealTime"

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
            Log.d("_FIREMASEIMG", "File URI or user is null");
        }

    }

    public void  removeImage(String url, Itinerary itinerary){
        removeFromStorage(url);//Borra la imagen de la base de datos "Storage"
        removeFromitinerary(url,itinerary);//Borra la imagen del itinerario en la base de datos "RealTime"

        Log.d("_FIREMASEIMG", "Eliminando imagen");
    }

    public void removeFromitinerary(String url , Itinerary itinerary){
        ArrayList<String> newImages = itinerary.getImageUris();
        newImages.remove(url);
        itinerary.setImageUris(newImages);

        updateItinerary.execute(itinerary,new ItineraryRepository.OperationCallback() {//Acutaliza el itinerario quitando la imagen
            @Override
            public void onSuccess() {  Log.d("_FIREMASEIMG","Uri de la imagen añadida"); }

            @Override
            public void onFailure(Exception e) {Log.d("_FIREMASEIMG","Uri de la imagen no se pudo añadir");}
        });
    }

    private void removeFromStorage(String url){
        StorageReference ref =  FirebaseStorage.getInstance().getReferenceFromUrl(url); //Borra la imagen de la base de datos
        ref.delete();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateitinerary(Itinerary oldItinerary, String imageUrl){//Añade la imagen a la lista de URLs del itinerario
        boolean local = false;
        ArrayList<String> newImages;
        if (oldItinerary.getImageUris() != null){// Si ya hay imagenes la añade
            Log.d("_FIREMASEIMG","en el itinerario hay " + oldItinerary.getImageUris().size());
            newImages = oldItinerary.getImageUris();
            newImages.add(imageUrl);
        }
        else{//Si no hay imegenes crea una lista nueva para añadirlas
            local = true;
            Log.d("_FIREMASEIMG","no hay imagenes en el itinerario ");
            newImages = new ArrayList<>();
            newImages.add(imageUrl);
        }

        Log.d("_FIREMASEIMG","ahora hay  " + newImages.size() +"imagenes en el itinerario " );

        oldItinerary.setImageUris(newImages);

        updateItinerary.execute(oldItinerary,new ItineraryRepository.OperationCallback() {//Actualiza el itinerario con la nueva imagen
            @Override
            public void onSuccess() {  Log.d("_FIREMASEIMG","Uri de la imagen añadida"); }

            @Override
            public void onFailure(Exception e) {Log.d("_FIREMASEIMG","Uri de la imagen no se pudo añadir");}
        });

        //notifica al adapter para actualizar el RecyclerView
        if(local){
            adapter.addElement(imageUrl);
        }else{
            adapter.notifyDataSetChanged();
        }

    }

}
