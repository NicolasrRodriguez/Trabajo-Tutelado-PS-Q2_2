package com.example.triptracks.Domain.LogicaNegocio;

import android.util.Log;

import com.example.triptracks.Data.FirebaseAuthData;




public class AuthLogic implements FirebaseAuthData.ResultCallback {


    private final FirebaseAuthData firebaseAuthData = new FirebaseAuthData(); //referencia a capa datos

    private AuthResult  authResult;//referencia  a interfaz para "comunicarse" con View



    public void setAuthResult(AuthResult authResult) {
        this.authResult = authResult;
    }

    @Override
    public void onResultSuccess() {
        //respuesta de data autenticacion exitosa
        Log.d("_AUTHLOGTAG", " recivido Succsess");
        authResult.onResultSuccess();//notifica a view

    }

    @Override
    public void onReultFail() {
        //respuesta de data autenticacion fallida
        Log.d("_AUTHLOGTAG", " recivido Fail");
        authResult.onReultFail();//notifica a view
    }


    public void Register(String email, String pass){
        //Registra a un nuevo usuario
        if(!email.isEmpty() && !pass.isEmpty()){ //añadir mas comprobaciones para asegurar que sea seguro
            Log.d("_AUTHLOGTAG", "Registrando a: " + email);
            firebaseAuthData.Register(email,pass,this);//llama a la capa Data para que registre al usuario
        }
        else{

            Log.d("_AUTHLOGTAG", "Se deben cubrir todos los campos");
            authResult.onReultFail();//notifica a view
        }

    }


    public void LogIn(String email, String pass){

        if(!email.isEmpty() && !pass.isEmpty()){
            Log.d("_AUTHLOGTAG", "Iniciando Sesion: " + email);
            firebaseAuthData.LogIn(email,pass,this);//llama a la capa Data para que inicie sesion con el usuario
        }
        else{

            Log.d("_AUTHLOGTAG", "Se deben cubrir todos los campos");
            authResult.onReultFail();//notifica a view
        }

    }



}