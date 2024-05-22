
package com.example.triptracks;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.triptracks.Presenter.ItneraryActivityView;
import com.example.triptracks.databinding.ActivityAuthBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityAuthBinding binding;

    public static final int RESULT_SESION_CLOSED = 1;

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
        String email = binding.EmailEdit.getText().toString();
        String pass = binding.PassEdit.getText().toString();
        if(v == binding.LogInBut){
            Log.d("_AUTHTAG", "Pulsado Log In");

            if(!email.isEmpty() && !pass.isEmpty()){
                LogIn(email,pass);
            }
            else{
                Log.d("_AUTHTAG", "Se deben cubrir todos los campos");
            }

        }
        else if(v == binding.SingUpBut){

            Log.d("_AUTHTAG", "Pulsado Sing Up");

            if(!email.isEmpty() && !pass.isEmpty()){
                Register(email,pass);
            }
            else{
                Log.d("_AUTHTAG", "Se deben cubrir todos los campos");
            }
        }
    }

    ActivityResultLauncher<Intent> myStartActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_SESION_CLOSED) {
                    Log.d("_AUTHTAG", "Se ha cerrado la sesion");
                }
            }
    );

    private void Register(String email, String pass){
        Log.d("_AUTHTAG", "Registrando a: " + email);

        Intent intent = new Intent(this, ItneraryActivityView.class);
        intent.putExtra("UserEmail" , email);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d("_AUTHTAG",  email + " registrado Correctamente");

                    myStartActivityForResult.launch(intent);
                }
                else{
                    Log.d("_AUTHTAG", "No se pudo registrar " + email );
                }
            }
        });
    }

    private void LogIn(String email, String pass){
        Log.d("_AUTHTAG", "Iniciando Sesion de: " + email);
        Intent intent = new Intent(this, ItneraryActivityView.class);
        intent.putExtra("UserEmail" , email);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d("_AUTHTAG",  email + " Sesion iniciada correctamente");

                    myStartActivityForResult.launch(intent);
                }
                else{
                    Log.d("_AUTHTAG", "No se pudo iniciar sesion" + email );
                }
            }
        });
    }
}