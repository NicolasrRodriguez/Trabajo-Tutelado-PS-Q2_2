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
import android.view.View;
import android.widget.LinearLayout;

import com.example.triptracks.Datos.FirebaseAuthData;
import com.example.triptracks.Datos.FirebaseItineraryHandler;
import com.example.triptracks.Domain.Entities.Imagen;
import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.LogicaNegocio.ImageAdapter;
import com.example.triptracks.Domain.LogicaNegocio.ImageLogic;
import com.example.triptracks.Domain.LogicaNegocio.UpdateItinerary;
import com.example.triptracks.Domain.Repository.ItineraryRepository;
import com.example.triptracks.R;
import com.example.triptracks.databinding.ActivityImagesViewBinding;

import java.util.List;

public class ImagesActivityVIew extends AppCompatActivity implements View.OnClickListener {

    ActivityImagesViewBinding binding;


    private Itinerary itinerary;

    private Uri imageSelected;

    private FirebaseAuthData firebaseAuth = new FirebaseAuthData();

    RecyclerView imagesRecyclerView;

    private List<Imagen> images;

    ImageAdapter imageAdapter;


    private ImageLogic imageLogic = new ImageLogic();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImagesViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        itinerary = getIntent().getParcelableExtra(ItneraryActivityView.KEY_ITINERARY);
        imageAdapter = new ImageAdapter(images);
        imagesRecyclerView = findViewById(R.id.images_list);



    }

    ActivityResultLauncher<Intent> myStartActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                assert result.getData() != null;
                Uri iamgeuri = result.getData().getData();
                assert iamgeuri != null;
                imageSelected = iamgeuri;

                binding.image.setImageURI(iamgeuri);
            }
    );

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    @Override
    public void onClick(View v) {
        if(v == binding.botonVolver){
            Intent resultIntent = new Intent();
            this.setResult(ItineraryDetailActivity.RESULT_OK, resultIntent);
            this.finish();
        }
        else if (v == binding.escogerimagen){
            Log.d("_IMGTAG","Escoger imagen ");
            Intent resultImageIntent = new Intent(MediaStore.ACTION_PICK_IMAGES);
            myStartActivityForResult.launch(resultImageIntent);
        }
        else if (v == binding.compartir) {
            Log.d("_IMGTAG","Subir imagen " + imageSelected);

            imageLogic.uploadImage(imageSelected,itinerary);

        }
    }
}