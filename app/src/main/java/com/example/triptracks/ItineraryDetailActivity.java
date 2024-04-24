package com.example.triptracks;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.triptracks.Datos.FirebaseItineraryHandler;
import com.example.triptracks.Domain.Entities.Event;
import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.LogicaNegocio.DeleteAllEvents;
import com.example.triptracks.Domain.LogicaNegocio.DeleteItinerary;
import com.example.triptracks.Domain.LogicaNegocio.DeleteOneEvent;
import com.example.triptracks.Domain.LogicaNegocio.UpdateEvent;
import com.example.triptracks.Domain.Repository.ItineraryRepository;
import com.example.triptracks.Presenter.EventDecorator;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import static com.example.triptracks.ItinActivity.mAdapter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class ItineraryDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    Itinerary itinerary;
    boolean detailsVisible = false;
    ItineraryTileBinding binding;
    Spinner spinnerCountry, spinnerState, spinnerCity;
    GoogleMap mMap;
    boolean isEditing = false;

    EventDecorator eventDecorator;

    Spinner spinner_evento;
    SupportMapFragment fragmentmap;
    Fragment fragmentcalendar;

    String category;
    Calendar calendar;

    FirebaseItineraryHandler firebaseItineraryHandler;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference ref;
    private CalendarDay selectedDateMin = null;
    private CalendarDay selectedDateMax = null;

    boolean startDateSelected = false;

    MaterialCalendarView calendarView;
    DeleteItinerary deleteItinerary;
    DeleteAllEvents deleteEvents;
    DeleteOneEvent deleteOneEvent;
    UpdateEvent UpdateEvent;



    public ItineraryDetailActivity() {
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ItineraryTileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        calendar = new Calendar(this);


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
        binding.layoutmapcontainer.setVisibility(View.GONE);
        binding.layoutcalendarcontainer.setVisibility(View.GONE);
        binding.getRoot().setBackgroundResource(R.drawable.fondo);

        firebaseItineraryHandler = new FirebaseItineraryHandler(updatedItineraries -> {
        });

        calendarView = findViewById(R.id.calendarView);
        calendar.configureCalendarView();
        calendar.loadAndDecorateEvents();


        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {

            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if (isEditing) {
                    if (!startDateSelected) {
                        confirmarFechaInicio(date);
                    } else {
                        confirmarFechaFin(date);
                    }
                } else {
                    Event existingEvent = calendar.findEventByDate(date);
                    if (existingEvent != null) {
                        calendar.showEventOptionsDialog(existingEvent);
                    } else {
                        calendar.promptForActivity(date);
                    }
                }
            }

            private void confirmarFechaInicio(CalendarDay date) {
                new AlertDialog.Builder(ItineraryDetailActivity.this)
                        .setTitle("Confirmación")
                        .setMessage("¿Guardar esta fecha como fecha de inicio?")
                        .setPositiveButton("Ok", (dialog, which) -> {
                            selectedDateMin = date;
                            startDateSelected = true;
                            Toast.makeText(ItineraryDetailActivity.this, "Fecha de inicio seleccionada", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancelar", (dialog, which) -> {
                        })
                        .show();
            }

            private void confirmarFechaFin(CalendarDay date) {
                new AlertDialog.Builder(ItineraryDetailActivity.this)
                        .setTitle("Confirmación")
                        .setMessage("¿Guardar esta fecha como fecha de fin?")
                        .setPositiveButton("Ok", (dialog, which) -> {
                            if (selectedDateMin != null && date.getDate().after(selectedDateMin.getDate())) {
                                selectedDateMax = date;
                                startDateSelected = false;
                                Toast.makeText(ItineraryDetailActivity.this, "Fecha de fin seleccionada", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ItineraryDetailActivity.this, "La fecha de fin debe ser posterior a la fecha de inicio", Toast.LENGTH_SHORT).show();
                                selectedDateMin = null;
                                selectedDateMax = null;
                                confirmarFechaInicio(date);
                            }
                        })
                        .setNegativeButton("Cancelar", (dialog, which) -> {
                        })
                        .show();
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.itinirary_detail_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent resultIntent = new Intent();
        setResult(AuthActivity.RESULT_SESION_CLOSED, resultIntent);
        if (id == R.id.menu_compartir) {
            Log.d("_ITDETTAG", "Compartir itinerario");
            // abrir dialogo para escoger con que usuarios compartir el itineriario
           //juan123456@gmail.com email de ejemplo
            showDialog();
        }
        if (id == R.id.action_show_map) {
            binding.layoutmapcontainer.setVisibility(View.VISIBLE);
            binding.layoutcalendarcontainer.setVisibility(View.GONE);
            binding.map.setVisibility(View.VISIBLE);

            showMapFragment();
            return true;
        } else if (id == R.id.action_show_calendar) {

            binding.layoutcalendarcontainer.setVisibility(View.VISIBLE);
            binding.layoutmapcontainer.setVisibility(View.GONE);
            binding.map.setVisibility(View.GONE);
            showCalendarFragment();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void showDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.str_compartir);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.compartir_itinerario, null);
        final EditText Targetemail = dialogView.findViewById(R.id.TEmailEdit);

        ArrayList<String > colaborators = itinerary.getColaborators();


        builder.setView(dialogView);
        builder.setPositiveButton(R.string.str_compartir, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String text = Targetemail.getText().toString();//juan123456@gmail.com

                if(!colaborators.contains(text) && Objects.equals(user.getEmail(), itinerary.getAdmin())){

                    colaborators.add(text);

                    itinerary.setColaborators(colaborators);

                    firebaseItineraryHandler.shareItinerary(itinerary , text);//comparte el itinerario con el usuario elegido
                }

                Log.d("_ITDETTAG", "Compartiendo con " + text + "por " + itinerary.getAdmin());
            }
        });
        builder.setNegativeButton(R.string.str_cancelar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                Log.d("_ITDETTAG", "Cancelado compartir");
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
        findViewById(R.id.calendarContainer).setVisibility(View.VISIBLE);
    }

    private void ocultardetalles() {

        findViewById(R.id.layoutCountry).setVisibility(View.GONE);
        findViewById(R.id.layoutState).setVisibility(View.GONE);
        findViewById(R.id.layoutCity).setVisibility(View.GONE);


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
            deleteItinerary.execute(itinerary,new ItineraryRepository.OperationCallback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFailure(Exception e) {


                        }
                    });

                finish();

        });

        binding.butEdit.setOnClickListener(v -> {
            editar_atributos(getTitleEditText());
        });
        binding.butVolver.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("ACTION", "BACK");
            resultIntent.putExtra(ItinActivity.KEY_ITINERARY, itinerary);
            setResult(ItinActivity.RESULT_OK, resultIntent);
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
                String Admin = itinerary.getAdmin();
                ArrayList<String> colaboratos = itinerary.getColaborators();
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

                if (selectedDateMin != null && selectedDateMax != null) {
                    Date startDate = selectedDateMin.getDate();
                    Date endDate = selectedDateMax.getDate();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String editedStartDate = dateFormat.format(startDate);
                    String editedEndDate = dateFormat.format(endDate);
                    itinerary.setStartDate(editedStartDate);
                    itinerary.setEndDate(editedEndDate);
                    calendar.configureCalendarView();
                    deleteEvents.execute(itinerary.getId(),new ItineraryRepository.OperationCallback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFailure(Exception e) {

                        }


                        });


                } else if (selectedDateMin!=null && selectedDateMax==null ||selectedDateMin==null && selectedDateMax!=null ) {
                    Toast.makeText(ItineraryDetailActivity.this, "Edición cancelada: falta una fecha por seleccionar", Toast.LENGTH_LONG).show();
                    startDateSelected = false;
                    calendar.loadAndDecorateEvents();
                    calendar.configureCalendarView();
                    calendarView.setClickable(false);
                    calendarView.setLongClickable(false);
                    calendarView.setEnabled(false);

                } else{
                    calendar.loadAndDecorateEvents();
                    calendar.configureCalendarView();

                }

                itinerary.setItineraryTitle(editedTitle);
                itinerary.setCountry(editedCountry);
                itinerary.setState(editedState);
                itinerary.setCity(editedCity);

                itinerary.setAdmin(Admin);//seguramente hay que tocarlo por que no se deberia poder modificar el Admin
                itinerary.setColaborators(colaboratos);

                binding.itineraryTitle.setText(itinerary.getItineraryTitle());
                binding.itineraryCountry.setText(itinerary.getCountry());
                binding.itineraryState.setText(itinerary.getState());
                binding.itineraryCity.setText(itinerary.getCity());
                onMapReady(mMap);
                mAdapter.actualizar_por_id(itinerary);
                findViewById(R.id.itineraryCountry).setVisibility(View.VISIBLE);
                findViewById(R.id.spinnerCountryAct2).setVisibility(View.GONE);
                findViewById(R.id.itineraryState).setVisibility(View.VISIBLE);
                findViewById(R.id.spinnerStateAct2).setVisibility(View.GONE);
                findViewById(R.id.itineraryCity).setVisibility(View.VISIBLE);
                findViewById(R.id.spinnerCityAct2).setVisibility(View.GONE);
                firebaseItineraryHandler.updateItinerary(itinerary);
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
        selectedDateMin = null;
        selectedDateMax = null;
        calendarView.setClickable(false);
        calendarView.setLongClickable(false);
        calendarView.setEnabled(false);
        calendarView.clearSelection();
        calendarView.removeDecorators();
        calendarView.state().edit()
                .setMinimumDate((java.util.Calendar) null)
                .setMaximumDate((java.util.Calendar) null)
                .commit();

        if (selectedDateMin != null && selectedDateMax != null) {
            calendarView.state().edit()
                    .setMinimumDate(selectedDateMin)
                    .setMaximumDate(selectedDateMax)
                    .commit();
        }

        }





    public EditText getTitleEditText() {
        return binding.itineraryTitle;
    }



    private void showMapFragment() {
        fragmentmap = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mapContainer, fragmentmap)
                .commit();
        fragmentmap.getMapAsync(this);

    }

    private void showCalendarFragment() {
        fragmentcalendar = new Fragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.calendarContainer, fragmentcalendar)
                .commit();
    }

}
