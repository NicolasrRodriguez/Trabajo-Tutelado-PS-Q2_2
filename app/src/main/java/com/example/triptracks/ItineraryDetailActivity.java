package com.example.triptracks;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.beastwall.localisation.model.City;
import com.beastwall.localisation.model.Country;
import com.beastwall.localisation.model.State;
import com.example.triptracks.databinding.ItineraryTileBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ItineraryDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Itinerary itinerary;
    private boolean detailsVisible = false;
    private ItineraryTileBinding binding;
    private Spinner spinnerCountry, spinnerState, spinnerCity;
    private GoogleMap mMap;
    private boolean isEditing = false;

    private ItineraryHandler itineraryHandler;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ItineraryTileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        itinerary = getIntent().getParcelableExtra(ItinActivity.KEY_ITINERARY);
        assert itinerary != null;
        binding.itineraryTitle.setText(itinerary.getItineraryTitle());
        binding.itineraryCountry.setText(itinerary.getCountry());
        binding.itineraryState.setText(itinerary.getState());
        binding.itineraryCity.setText(itinerary.getCity());
        binding.itineraryTitle.setVisibility(View.VISIBLE);

        int resourceId = android.R.drawable.ic_menu_more;
        binding.itineraryTitle.setCompoundDrawablesWithIntrinsicBounds(resourceId, 0, 0, 0);
        binding.itineraryTitle.setOnClickListener(v -> pulsar());

        configurar(binding);


        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mapContainer, mapFragment)
                .commit();

        mapFragment.getMapAsync(this);

        itineraryHandler = new ItineraryHandler(updatedItineraries -> {});
    }


    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
    }

    private LatLng getLocationFromAddress(String countryName, String stateName, String cityName) {
        Geocoder geocoder = new Geocoder(this);
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

    private void pulsar() {
        if (!isEditing) {
            detailsVisible = !detailsVisible;
            if (detailsVisible) {
                mostrardetalles();
            } else {
                ocultardetalles();
            }
        }
    }

    private void mostrardetalles() {
        findViewById(R.id.layoutCountry).setVisibility(View.VISIBLE);
        findViewById(R.id.layoutState).setVisibility(View.VISIBLE);
        findViewById(R.id.layoutCity).setVisibility(View.VISIBLE);
        findViewById(R.id.mapContainer).setVisibility(View.VISIBLE);
    }

    private void ocultardetalles() {

        findViewById(R.id.layoutCountry).setVisibility(View.GONE);
        findViewById(R.id.layoutState).setVisibility(View.GONE);
        findViewById(R.id.layoutCity).setVisibility(View.GONE);
        findViewById(R.id.mapContainer).setVisibility(View.GONE);

    }

    private void configurar(ItineraryTileBinding binding) {

        spinnerCountry = findViewById(R.id.spinnerCountryAct2);
        spinnerState = findViewById(R.id.spinnerStateAct2);
        spinnerCity = findViewById(R.id.spinnerCityAct2);

        List<String> countryNames = new ArrayList<>();
        countryNames.add(getString(R.string.select_country));
        for (Country country : ItinActivity.mCountries) {
            countryNames.add(country.getName());
        }
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countryNames);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(countryAdapter);


        List<String> stateNames = new ArrayList<>();
        stateNames.add(getString(R.string.select_state));
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stateNames);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(stateAdapter);


        List<String> cityNames = new ArrayList<>();
        cityNames.add(getString(R.string.select_city));
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityNames);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(cityAdapter);
        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCountryName = (String) parent.getItemAtPosition(position);
                if (!selectedCountryName.equals(getString(R.string.select_country))) {

                    Country selectedCountry = null;
                    for (Country country : ItinActivity.mCountries) {
                        if (country.getName().equals(selectedCountryName)) {
                            selectedCountry = country;
                            break;
                        }
                    }
                    if (selectedCountry != null) {
                        List<String> stateNames = new ArrayList<>();
                        stateNames.add(getString(R.string.select_state));
                        for (State state : selectedCountry.getStates()) {
                            stateNames.add(state.getName());
                        }
                        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(ItineraryDetailActivity.this, android.R.layout.simple_spinner_item, stateNames);
                        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerState.setAdapter(stateAdapter);
                    }

                    if (selectedCountry != null) {

                        List<String> cityNames = new ArrayList<>();
                        cityNames.add(getString(R.string.select_city));
                        for (State state : selectedCountry.getStates()) {
                            if (state.getName().equals(state.getName())) {
                                for (City city : state.getCities()) {
                                    cityNames.add(city.getName());
                                }
                                break;
                            }
                        }
                        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(ItineraryDetailActivity.this, android.R.layout.simple_spinner_item, cityNames);
                        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCity.setAdapter(cityAdapter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        binding.butBorrar.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("ACTION", "DELETE");
            resultIntent.putExtra(ItinActivity.KEY_ITINERARY, itinerary);
            setResult(ItinActivity.RESULT_DELETE, resultIntent);
            itineraryHandler.deleteItinerary(itinerary);
            finish();

        });

        binding.butEdit.setOnClickListener(v -> {
            editar_atributos(getTitleEditText());
        });
        binding.butVolver.setOnClickListener(v -> {
            finish();
        });

        binding.butOk.setOnClickListener(v -> {
            if (isEditing) {
                isEditing = false;
                EditText titleEditText = getTitleEditText();
                titleEditText.setFocusable(false);
                titleEditText.setFocusableInTouchMode(false);
                titleEditText.setCursorVisible(false);
                titleEditText.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(titleEditText.getWindowToken(), 0);
                String editedTitle = getTitleEditText().getText().toString();
                String editedCountry = itinerary.getCountry();
                String editedState = itinerary.getState();
                String editedCity = itinerary.getCity();

                if (spinnerCountry.getSelectedItem() != null) {
                    String selectedCountry = spinnerCountry.getSelectedItem().toString();
                    if (!selectedCountry.equals(getString(R.string.select_country))) {
                        editedCountry = selectedCountry;
                        editedState = "";
                        editedCity = "";
                    }
                }

                if (spinnerState.getSelectedItem() != null) {
                    String selectedState = spinnerState.getSelectedItem().toString();
                    if (!selectedState.equals(getString(R.string.select_state))) {
                        editedState = selectedState;
                    }
                }

                if (spinnerCity.getSelectedItem() != null) {
                    String selectedCity = spinnerCity.getSelectedItem().toString();
                    if (!selectedCity.equals(getString(R.string.select_city))) {
                        editedCity = selectedCity;
                    }
                }

                itinerary.setItineraryTitle(editedTitle);
                itinerary.setCountry(editedCountry);
                itinerary.setState(editedState);
                itinerary.setCity(editedCity);

                binding.itineraryTitle.setText(itinerary.getItineraryTitle());
                binding.itineraryCountry.setText(itinerary.getCountry());
                binding.itineraryState.setText(itinerary.getState());
                binding.itineraryCity.setText(itinerary.getCity());
                onMapReady(mMap);
                ItinActivity.mAdapter.actualizar_por_id(itinerary);
                findViewById(R.id.itineraryCountry).setVisibility(View.VISIBLE);
                findViewById(R.id.spinnerCountryAct2).setVisibility(View.GONE);
                findViewById(R.id.itineraryState).setVisibility(View.VISIBLE);
                findViewById(R.id.spinnerStateAct2).setVisibility(View.GONE);
                findViewById(R.id.itineraryCity).setVisibility(View.VISIBLE);
                findViewById(R.id.spinnerCityAct2).setVisibility(View.GONE);
                itineraryHandler.updateItinerary(itinerary);
            }
        });
    }

    public void editar_atributos(EditText editText) {
        isEditing = true;
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setCursorVisible(true);
        Context context = editText.getContext();
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.editTextBackground, outValue, true);
        findViewById(R.id.itineraryCountry).setVisibility(View.GONE);
        findViewById(R.id.spinnerCountryAct2).setVisibility(View.VISIBLE);
        findViewById(R.id.itineraryState).setVisibility(View.GONE);
        findViewById(R.id.spinnerStateAct2).setVisibility(View.VISIBLE);
        findViewById(R.id.itineraryCity).setVisibility(View.GONE);
        findViewById(R.id.spinnerCityAct2).setVisibility(View.VISIBLE);
    }

    public EditText getTitleEditText() {
        return binding.itineraryTitle;
    }





}
