package com.example.triptracks.Domain.LogicaNegocio;

import android.annotation.SuppressLint;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import com.example.triptracks.Datos.FirebaseImages;
import com.example.triptracks.Datos.FirebaseItineraryHandler;
import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.Repository.ItineraryRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class ItineraryLogic {

    ItineraryAdapter adapter;

    public void setAdapter(ItineraryAdapter adapter) {
        this.adapter = adapter;
    }


    private FirebaseItineraryHandler firebaseItineraryHandler = new FirebaseItineraryHandler(this::updateItineraryList);

    private FirebaseImages firebaseImages = new FirebaseImages();



    CreateItinerary createItinerary =  new CreateItinerary(firebaseItineraryHandler);;

    public void addItems(String itineraryName, String countryName, String stateName, String cityName,String startDate,String endDate ,String UserEmail) {

        String itineraryId = firebaseItineraryHandler.setId();
        ArrayList<String> shared = new  ArrayList<>();
        shared.add(UserEmail);
        ArrayList<String> images = new  ArrayList<>();
        Itinerary itinerary = new Itinerary(itineraryId, itineraryName, countryName, stateName, cityName,UserEmail ,shared,startDate,endDate,images);
        ArrayList<Itinerary> newItineraries = new ArrayList<>();
        newItineraries.add(itinerary);
        adapter.anadirelem(newItineraries);
        firebaseImages.createImagesPath(itineraryId);
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
    private void updateItineraryList(ArrayList<Itinerary> itineraries) {
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
