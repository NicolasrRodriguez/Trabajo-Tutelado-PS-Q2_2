package com.example.triptracks.Presenter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresExtension;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.example.triptracks.Datos.FirebaseAuthData;
import com.example.triptracks.R;
import com.example.triptracks.databinding.ActivityImagesViewBinding;

public class ImagesActivityVIew extends AppCompatActivity implements View.OnClickListener {

    ActivityImagesViewBinding binding;

    private String UserEmail;

    private String imageSelected;

    private FirebaseAuthData firebaseAuth = new FirebaseAuthData();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImagesViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UserEmail = firebaseAuth.email();
        binding.botonVolver.setOnClickListener(this);
        binding.escogerimagen.setOnClickListener(this);



    }

    ActivityResultLauncher<Intent> myStartActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                assert result.getData() != null;
                Uri iamgeuri = result.getData().getData();
                imageSelected = iamgeuri.toString();
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
            Intent resultImageIntent = new Intent(MediaStore.ACTION_PICK_IMAGES);
            myStartActivityForResult.launch(resultImageIntent);
        }
    }
}