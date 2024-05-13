package com.example.triptracks.Datos;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


//extiende AppCompatActivity por que si no falla FirebaseAuth
public class FirebaseAuthData extends AppCompatActivity {


    //interfaz para avisar a Logic el resultado de la autenticacion
    public interface ResultCallback {
        //se pueden añadir mas callbacks para diferenciar mejor el resultado de inicio de sesión y registro
        void onResultSuccess();// autenticacion exitosa

        void onReultFail();// autenticacion fallida

    }


    public void Register(String email, String pass, ResultCallback callback){
        //registra a un nuevo usuario
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass).addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Log.d("_AUTHTAG",  email + " registrado Correctamente");
                    callback.onResultSuccess();//si lo consigue "avisa" a AuthLogic
                }
                else{

                    Log.d("_AUTHTAG", "No se pudo registrar " + email );
                    callback.onReultFail();//si no lo consigue "avisa" a AuthLogic

                }
            }
        });

    }

    public void LogIn(String email, String pass, ResultCallback callback){
        //Inicia sesión con usuario que ya existe
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass).addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Log.d("_AUTHTAG",  email + " Sesion iniciada correctamente");
                    callback.onResultSuccess();//si lo consigue "avisa" a AuthLogic

                }
                else{

                    Log.d("_AUTHTAG", "No se pudo iniciar sesion" + email );
                    callback.onReultFail();//si no lo consigue "avisa" a AuthLogic
                }
            }
        });

    }

    public void closeSes(){
        FirebaseAuth.getInstance().signOut();
    }

    public String  email(){
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
    }

}