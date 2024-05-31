package com.example.triptracks.Domain.Service;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public interface Map {
    void onMapReady(GoogleMap googleMap);
    void initializeMap();
    LatLng getLocationFromAddress(String countryName, String stateName, String cityName);
}