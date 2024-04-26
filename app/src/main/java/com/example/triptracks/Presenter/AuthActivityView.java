package com.example.triptracks.Presenter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.triptracks.ItinActivity;
import com.example.triptracks.Domain.LogicaNegocio.AuthLogic;
import com.example.triptracks.Domain.LogicaNegocio.AuthResult;
import com.example.triptracks.R;
import com.example.triptracks.databinding.ActivityAuthViewBinding;

public class AuthActivityView extends AppCompatActivity implements View.OnClickListener, AuthResult {

    public static final int RESULT_SESION_CLOSED = 1;

    private ActivityAuthViewBinding binding;

    private final AuthLogic authLogic = new AuthLogic();//referencia a capa Dominio
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_view);
        binding = ActivityAuthViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.text.setText("diablo");//QUITAR ESTE TEXTVIEW CUANDO SE QUITE AuthActivity
        binding.SingUpBut.setOnClickListener(this);
        binding.LogInBut.setOnClickListener(this);
        authLogic.setAuthResult(this);
    }

    //lanza la siguiente actividad una vez se autentica el usuario
    ActivityResultLauncher<Intent> myStartActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_SESION_CLOSED) {
                    Log.d("_AUTHTAG", "Se ha cerrado la sesion");
                }
            }
    );

    @Override
    public void onClick(View v) {

        String email = binding.EmailEdit.getText().toString();
        String pass = binding.PassEdit.getText().toString();


        Log.d("_AUTHTAG", "Intent creado");

        if(v == binding.LogInBut){
            Log.d("_AUTHTAG", "Pulsado Log In");
            //LLama a logIn de la logica de negocio
            authLogic.LogIn(email,pass);



        }
        else if(v == binding.SingUpBut){

            Log.d("_AUTHTAG", "Pulsado Sing Up");
            //Llamar a SingUp de la logica de negocio
            authLogic.Register(email,pass);



        }
    }

    //funciones para comprobar si se ha autenticado correctamente o no
    @Override
    public void onResultSuccess() {//autenticacion exitosa
        Intent intent = new Intent(this, ItneraryActivityView.class);
        myStartActivityForResult.launch(intent);//arranca la siguiente actividad
    }

    @Override
    public void onReultFail() {//autenticacion fallida
        //avisa de que ha fallado
        Toast.makeText(getApplicationContext(), R.string.athu_fail , Toast.LENGTH_LONG).show();
        Log.d("_AUTHTAG", " no se pudo iniciar o regsitrar");
    }



}