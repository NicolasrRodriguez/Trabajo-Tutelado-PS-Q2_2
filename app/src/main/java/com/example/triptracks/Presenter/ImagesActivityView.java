package com.example.triptracks.Presenter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresExtension;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.LogicaNegocio.Adapter.ImageAdapter;
import com.example.triptracks.Domain.LogicaNegocio.ImageLogic;
import com.example.triptracks.Domain.LogicaNegocio.ImagesOnclick;
import com.example.triptracks.R;
import com.example.triptracks.databinding.ActivityImagesViewBinding;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ImagesActivityView extends AppCompatActivity implements ImagesOnclick {

    ActivityImagesViewBinding binding;


    private Itinerary itinerary;


    RecyclerView imagesRecyclerView;


    ImageAdapter imageAdapter;


    private final ImageLogic imageLogic = new ImageLogic();


    private String currentPhotoPath;

    private int REQUEST_IMAGE_CAPTURE = 235616346;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("_IMAGEVIEW", "Arranco imagenesView");
        super.onCreate(savedInstanceState);
        binding = ActivityImagesViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        itinerary = getIntent().getParcelableExtra(ItneraryActivityView.KEY_ITINERARY);
        assert itinerary != null;
        if (itinerary.getImageUris() != null) {//si hay imagenes carga la lista de la base de datos
            Log.d("_IMAGEVIEW", "Voy a crear el adapter ");
            for (String iamge : itinerary.getImageUris()) {
                Log.d("_IMAGEVIEW", "URL:" + iamge);
            }
            imageAdapter = new ImageAdapter(itinerary.getImageUris(), this, itinerary);

        }
        else {//si no hay imagenes inizailiza la lista para el adapter
            Log.d("_IMAGEVIEW", "Aun no hay imagenes");
            imageAdapter = new ImageAdapter(new ArrayList<>(), this, itinerary);
        }

        imageLogic.setAdapter(imageAdapter);
        imagesRecyclerView = findViewById(R.id.images_list);
        Log.d("_IMAGEVIEW", "Voy a asignar el adapter el adapter");
        imagesRecyclerView.setAdapter(imageAdapter);
        imagesRecyclerView.setLayoutManager(linearLayoutManager);




    }

    ActivityResultLauncher<Intent> myStartActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        Log.d("_IMAGEVIEW", imageUri.toString());
                        imageLogic.uploadImage(imageUri, itinerary);
                    } else {
                        Log.e("_IMAGEVIEW", "Image URI is null");
                    }
                } else {
                    Log.e("_IMAGEVIEW", "Intent data is null");
                }
            }
    );



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File photo = new File(currentPhotoPath);
            Uri contentUri = Uri.fromFile(photo);
            imageLogic.uploadImage(contentUri,itinerary);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.images_act_menu, menu);
        return true;
    }
    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.subirFoto) {
            Intent resultImageIntent = new Intent(MediaStore.ACTION_PICK_IMAGES);
            myStartActivityForResult.launch(resultImageIntent);

        } else if (id == R.id.Volver) {
            Intent resultIntent = new Intent();
            this.setResult(ItineraryDetailActivity.RESULT_OK, resultIntent);
            this.finish();
        } else if (id == R.id.camara) {
            if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){//comprueba si hay camara
                if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{
                            android.Manifest.permission.CAMERA
                    }, 100);


                }
                else {
                    Log.d("_IMAGEVIEW","Tengo permisos");
                    Intent camara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (camara.resolveActivity(getPackageManager()) != null) {
                        File imageFile = null;
                        try {
                            imageFile = imageLogic.createImageFile();
                            Log.d("_IMAGEVIEW","fichero creado view");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (imageFile != null) {
                            currentPhotoPath = imageFile.getAbsolutePath();
                            Uri photoURI = FileProvider.getUriForFile(this,
                                    "com.example.triptracks.fileprovider",
                                    imageFile);

                            camara.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(camara, REQUEST_IMAGE_CAPTURE);

                        }



                    }
                }
            }

        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onItemClick(int position) {
        Log.d("_IMAGEVIEW","Clickado en posicion" + position);//enseñar imagen completa con un dialogo
        showImageDialog(imageAdapter.dataAt(position));

    }

    @Override
    public void onItemLongClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.eliminar_imagen);
        builder.setMessage(R.string.est_seguro_de_que_desea_eliminar_esta_imagen);
        builder.setPositiveButton(R.string.str_but_OK, (dialog, which) -> {
           //elimianr imagen en la posición position del view y de las bases de datos
            imageAdapter.removeelem(position);
        });
        builder.setNegativeButton(R.string.str_cancelar, null);
        builder.show();
    }

    private void showImageDialog(String imageUrl) {//muestra la iamfen completa
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.full_image_dialog);

        ImageView imageView = dialog.findViewById(R.id.imageView);

        Glide.with(this)
                            .load(imageUrl)
                            .into(imageView);


        ImageView btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setTag(dialog);
        btnClose.setOnClickListener(v -> {
            Dialog d = (Dialog) v.getTag();
            if (d != null) {
                d.dismiss();
            }
        });

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);
        dialog.show();
    }
}