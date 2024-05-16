package com.example.triptracks.Presenter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.triptracks.Datos.FirebaseAuthData;
import com.example.triptracks.R;
import com.example.triptracks.databinding.ActivityImagesViewBinding;

public class ImagesActivityVIew extends AppCompatActivity implements View.OnClickListener {

    ActivityImagesViewBinding binding;

    private String UserEmail;

    private FirebaseAuthData firebaseAuth = new FirebaseAuthData();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImagesViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UserEmail = firebaseAuth.email();
        binding.botonVolver.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if(v == binding.botonVolver){
            Intent resultIntent = new Intent();;
            this.setResult(ItineraryDetailActivity.RESULT_OK, resultIntent);
            this.finish();
        }
    }
}