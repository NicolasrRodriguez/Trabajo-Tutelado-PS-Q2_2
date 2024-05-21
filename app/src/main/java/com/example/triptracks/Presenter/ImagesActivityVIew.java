package com.example.triptracks.Presenter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresExtension;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.triptracks.Datos.FirebaseAuthData;
import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.LogicaNegocio.ImageAdapter;
import com.example.triptracks.Domain.LogicaNegocio.ImageLogic;
import com.example.triptracks.R;
import com.example.triptracks.databinding.ActivityImagesViewBinding;

public class ImagesActivityVIew extends AppCompatActivity implements View.OnClickListener {

    ActivityImagesViewBinding binding;


    private Itinerary itinerary;

    private Uri imageSelected;

    private FirebaseAuthData firebaseAuth = new FirebaseAuthData();

    RecyclerView imagesRecyclerView;


    ImageAdapter imageAdapter;


    private ImageLogic imageLogic = new ImageLogic();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("_IMAGETAG", "Arranco imagenesView");
        super.onCreate(savedInstanceState);
        binding = ActivityImagesViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.botonVolver.setOnClickListener(this);
        itinerary = getIntent().getParcelableExtra(ItneraryActivityView.KEY_ITINERARY);
        assert itinerary != null;
        if (itinerary.getImageUris() != null){
            Log.d("_IMAGETAG", "Voy a crear el adapter "  );
            for (String iamge : itinerary.getImageUris()) {
                Log.d("_IMAGETAG", "URL:" + iamge);
            }
            imageAdapter = new ImageAdapter(itinerary.getImageUris());
            imagesRecyclerView = findViewById(R.id.images_list);
            Log.d("_IMAGETAG", "Voy a asignar el adapter el adapter");
            imagesRecyclerView.setAdapter(imageAdapter);
            imagesRecyclerView.setLayoutManager(linearLayoutManager);
        }
        else {
            Log.d("_IMAGETAG", "Aun no hay imagenes");
        }




    }

    ActivityResultLauncher<Intent> myStartActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                assert result.getData() != null;
                Uri iamgeuri = result.getData().getData();
                assert iamgeuri != null;
                imageSelected = iamgeuri;
                Log.d("_IMGTAG",iamgeuri.toString());
                imageLogic.uploadImage(iamgeuri,itinerary);

            }
    );

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.images_act_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent resultIntent = new Intent();
        setResult(AuthActivityView.RESULT_SESION_CLOSED, resultIntent);
        if (id == R.id.subirFoto) {
            Intent resultImageIntent = new Intent(MediaStore.ACTION_PICK_IMAGES);
            myStartActivityForResult.launch(resultImageIntent);

        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    @Override
    public void onClick(View v) {
        if(v == binding.botonVolver){
            Intent resultIntent = new Intent();
            this.setResult(ItineraryDetailActivity.RESULT_OK, resultIntent);
            this.finish();
        }
    }
}