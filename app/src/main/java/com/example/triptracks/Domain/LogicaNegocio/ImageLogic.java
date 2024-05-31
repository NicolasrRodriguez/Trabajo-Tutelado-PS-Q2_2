package com.example.triptracks.Domain.LogicaNegocio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.example.triptracks.Data.FirebaseImages;
import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.LogicaNegocio.Adapter.ImageAdapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageLogic {


    private Context context;
    private FirebaseImages firebaseImages = new FirebaseImages();

    public void setContext(Context context) {
        this.context = context;
    }

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

    public File createImageFile() throws IOException {
        Log.d("_IMAGEVIEW","voy a crear la imagen");
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        Log.d("_IMAGEVIEW","nombre de la imagen " + imageFileName);
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.d("_IMAGEVIEW","fichero creado");
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }
}
