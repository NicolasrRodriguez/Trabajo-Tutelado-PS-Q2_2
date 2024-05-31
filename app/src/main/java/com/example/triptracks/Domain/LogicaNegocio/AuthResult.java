package com.example.triptracks.Domain.LogicaNegocio;

//interfaz para "comunicar" AuthLogic con AuthView

public interface AuthResult {

    void onResultSuccess();//autenticacion exitosa

    void onReultFail();//autenticacion fallida

}