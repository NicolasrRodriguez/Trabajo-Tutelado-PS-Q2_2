package com.example.triptracks.Domain.LogicaNegocio;

import android.annotation.SuppressLint;
import android.widget.DatePicker;

import com.example.triptracks.Datos.FirebaseItineraryHandler;
import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.LogicaNegocio.Adapter.ItineraryAdapter;
import com.example.triptracks.Domain.LogicaNegocio.ItineraryUseCases.CreateItinerary;
import com.example.triptracks.Domain.Repository.ItineraryRepository;

import java.util.ArrayList;
import java.util.Locale;

public class ItineraryLogic {

    ItineraryAdapter adapter;

    public void setAdapter(ItineraryAdapter adapter) {
        this.adapter = adapter;
    }


    private FirebaseItineraryHandler firebaseItineraryHandler = new FirebaseItineraryHandler(this::updateItineraryList);



    CreateItinerary createItinerary =  new CreateItinerary(firebaseItineraryHandler);;

    public void addItems(String itineraryName, String countryName, String stateName, String cityName,String startDate,String endDate ,String UserEmail) {//añade un nuevo itinerario

        String itineraryId = firebaseItineraryHandler.setId();
        ArrayList<String> colaborators = new  ArrayList<>();
        colaborators.add(UserEmail);
        ArrayList<String> images = new  ArrayList<>();
        Itinerary itinerary = new Itinerary(itineraryId, itineraryName, countryName, stateName, cityName,UserEmail ,colaborators,startDate,endDate,images);
        ArrayList<Itinerary> newItineraries = new ArrayList<>();
        newItineraries.add(itinerary);
        adapter.anadirelem(newItineraries);
        createItinerary.execute(itinerary, new ItineraryRepository.OperationCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(Exception e) {


            }

        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateItineraryList(ArrayList<Itinerary> itineraries) {//actualiza el RecyclerView y le notifica el cambio
        adapter.updateData(itineraries);
        adapter.notifyDataSetChanged();
    }

    public String formatDate(DatePicker datePicker) {
        int year = datePicker.getYear();
        int month = datePicker.getMonth() + 1;
        int day = datePicker.getDayOfMonth();
        return String.format(Locale.US, "%04d-%02d-%02d", year, month, day);
    }
}
