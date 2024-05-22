package com.example.triptracks.Presenter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.beastwall.localisation.model.Country;
import com.example.triptracks.Domain.LogicaNegocio.AuthLogic;
import com.example.triptracks.Domain.LogicaNegocio.AuthResult;
import com.example.triptracks.Domain.LogicaNegocio.LoadCountriesTask;
import com.example.triptracks.R;
import com.example.triptracks.databinding.ActivityAuthViewBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AuthActivityView extends AppCompatActivity implements View.OnClickListener, AuthResult {

    public static final int RESULT_SESION_CLOSED = 1;

    private SharedPreferences preferences;

    private ActivityAuthViewBinding binding;
    public static List<Country> mCountries = new ArrayList<>();
    private final AuthLogic authLogic = new AuthLogic();//referencia a capa Dominio
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_view);
        setLanguage(getLanguageFromPreferences());
        setTitle(R.string.app_name);
        binding = ActivityAuthViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.text.setText("diablo");//QUITAR ESTE TEXTVIEW CUANDO SE QUITE AuthActivity
        binding.SingUpBut.setOnClickListener(this);
        binding.LogInBut.setOnClickListener(this);
        authLogic.setAuthResult(this);
        new LoadCountriesTask(this).execute();

    }
    private String getLanguageFromPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("language_preference", ""); // Obtener el idioma preferido
    }

    @Override
    protected void onResume() {
        super.onResume();
        setLanguage(getLanguageFromPreferences());
        setTitle(R.string.app_name);
        binding.SingUpBut.setText(getResources().getString(R.string.sign_up));
        binding.LogInBut.setText(getResources().getString(R.string.log_in));
        binding.EmailEdit.setHint(getResources().getString(R.string.Email));
        binding.PassEdit.setHint(getResources().getString(R.string.Contraseña));
        preferences = PreferenceManager.getDefaultSharedPreferences(this);//this.getPreferences(Context.MODE_PRIVATE);
        setThemeApp(preferences.getBoolean("theme",false));

    }

    private void setThemeApp(boolean theme){

        if (theme){
            Log.d("_TAG1","Modo Oscuro");

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else{
            Log.d("_TAG1","Modo Claro");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }


    }



    private void setLanguage(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = getResources().getConfiguration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }


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
    public static void onCountriesLoaded(List<Country> countries) {
        mCountries = countries;
    }


}