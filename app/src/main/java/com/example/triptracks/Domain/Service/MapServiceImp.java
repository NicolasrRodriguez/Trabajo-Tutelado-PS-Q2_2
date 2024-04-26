package com.example.triptracks.Domain.Service;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;
import android.util.Log;

import com.example.triptracks.Domain.Entities.Itinerary;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapServiceImp implements Map, OnMapReadyCallback {


    private GoogleMap mMap;
    private Context context;
    private Itinerary itinerary;

    public MapServiceImp(Context context, Itinerary itinerary) {
        this.context = context;
        this.itinerary = itinerary;
    }
    public void initializeMap() {
        if (mMap != null) {
            onMapReady(mMap);
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            mMap.clear();

            String countryName = itinerary.getCountry();
            String stateName = itinerary.getState();
            String cityName = itinerary.getCity();

            LatLng location = getLocationFromAddress(countryName, stateName, cityName);

            float zoomLevel = 1.0f;
            if (!TextUtils.isEmpty(countryName)) {
                zoomLevel = 6.0f;
            }
            if (!TextUtils.isEmpty(stateName)) {
                zoomLevel = 8.0f;
            }
            if (!TextUtils.isEmpty(cityName)) {
                zoomLevel = 10.0f;
            }

            if (location != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));
                mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title("Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                mMap.getUiSettings().setZoomControlsEnabled(true);
            } else {
                LatLng centerOfWorld = new LatLng(0, 0);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerOfWorld, zoomLevel));
            }
        } else {

            Log.e("ItineraryDetailActivity", "GoogleMap is null");
        }
    }


    public LatLng getLocationFromAddress(String countryName, String stateName, String cityName) {
        Geocoder geocoder = new Geocoder(context);
        String addressString = cityName + ", " + stateName + ", " + countryName;

        try {
            List<Address> addresses = geocoder.getFromLocationName(addressString, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();
                return new LatLng(latitude, longitude);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}