package com.example.triptracks;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.triptracks.databinding.ActivityAuthBinding;
import com.example.triptracks.databinding.ActivityMainBinding;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityAuthBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.SingUpBut.setOnClickListener(this);
        binding.LogInBut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == binding.LogInBut){
            Log.d("_AUTHTAG", "Pulsado Log In");
        }
        else if(v == binding.SingUpBut){
            Log.d("_AUTHTAG", "Pulsado Sing Up");
            if(!binding.EmailEdit.getText().toString().isEmpty() && !binding.PassEdit.getText().toString().isEmpty()){
                Log.d("_AUTHTAG", "Se puede registrar");
            }
            else{
                Log.d("_AUTHTAG", "No se puede registrar");
            }
        }
    }
}