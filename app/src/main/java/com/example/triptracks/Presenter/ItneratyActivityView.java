package com.example.triptracks.Presenter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.beastwall.localisation.model.City;
import com.beastwall.localisation.model.Country;
import com.beastwall.localisation.model.State;
import com.example.triptracks.AuthActivity;
import com.example.triptracks.Datos.FirebaseAuthData;
import com.example.triptracks.Datos.FirebaseItineraryHandler;
import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.LogicaNegocio.CreateItinerary;
import com.example.triptracks.Domain.LogicaNegocio.ItineraryAdapter;
import com.example.triptracks.ItinActivity;
import com.example.triptracks.ItineraryDetailActivity;
import com.example.triptracks.R;
import com.example.triptracks.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ItneratyActivityView extends AppCompatActivity implements ItineraryAdapter.OnItemClickListener, ItineraryAdapter.OnContextMenuClickListener{

    public static final String KEY_ITINERARY = "itinerary";
    public static final int RESULT_DELETE = 1;
    public static final int RESULT_UPDATE = 2;

    public static final int RESULT_OK = 3;

    private String UserEmail;

    private FirebaseItineraryHandler firebaseItineraryHandler;

    private FirebaseAuthData firebaseAuth = new FirebaseAuthData();
    private ActivityMainBinding binding;

    public static ItineraryAdapter mAdapter;
    private ArrayList<Itinerary> mItineraryList = new ArrayList<>();


    public static List<Country> mCountries = new ArrayList<>();

    public static int selectedPosition = RecyclerView.NO_POSITION;

    CreateItinerary createItinerary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        if (intent != null) {
            UserEmail = intent.getStringExtra("UserEmail");
        }
        mAdapter = new ItineraryAdapter(mItineraryList, this, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.categoriesRv.setLayoutManager(linearLayoutManager);
        binding.categoriesRv.setAdapter(mAdapter);
        registerForContextMenu(binding.categoriesRv);

        mAdapter.mostrarbotones(true);
        //new LoadCountriesTask(this).execute();

        //firebaseItineraryHandler = new FirebaseItineraryHandler(this::updateItineraryList);

        createItinerary = new CreateItinerary(firebaseItineraryHandler);

    }

    //arranca la siguiente actividad, detalle de los itinerarios
    ActivityResultLauncher<Intent> myStartActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_DELETE) {
                    Intent data = result.getData();

                    if (data != null) {
                        Itinerary deletedItinerary = data.getParcelableExtra(KEY_ITINERARY);
                        String elemento = deletedItinerary.getId();
                        mAdapter.eliminar_por_id(elemento);
                    }
                } else if (result.getResultCode() == RESULT_UPDATE) {
                    Intent data = result.getData();
                    if (data != null) {
                        Itinerary updatedItinerary = data.getParcelableExtra(KEY_ITINERARY);
                        mAdapter.actualizar_por_id(updatedItinerary);
                    }
                } else if (result.getResultCode() == RESULT_OK) {
                    Log.d("_TAG","Succesfully returned to MainActivity");
                }
            }
    );

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent resultIntent = new Intent();
        setResult(AuthActivity.RESULT_SESION_CLOSED, resultIntent);
        if (id == R.id.menu_opcion_1) {
            showDialog(); //crea un nuevo itinerario
            return true;
        } else if (id == R.id.menu_opcion_cerrarSesion) {
            firebaseAuth.closeSes();//cierra la sesion y acaba la actividad
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    void detalle_actividad(Itinerary itinerary) {
        Intent intent = new Intent(this, ItineraryDetailActivity.class);
        intent.putExtra(ItinActivity.KEY_ITINERARY, itinerary);
        myStartActivityForResult.launch(intent);//arranca la siguiente actividad, detalle de los itinerarios
    }

    public void showDialog() {//Dialogo para la creación del itinerario
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.str_mensaje_dialog);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.crear_itinerario, null);

        final EditText inputItineraryName = dialogView.findViewById(R.id.inputItineraryName);
        final Spinner spinnerCountry = dialogView.findViewById(R.id.spinnerCountry);
        final Spinner spinnerState = dialogView.findViewById(R.id.spinnerState);
        final Spinner spinnerCity = dialogView.findViewById(R.id.spinnerCity);
        final DatePicker startDatePicker = dialogView.findViewById(R.id.startDatePicker);
        final DatePicker endDatePicker = dialogView.findViewById(R.id.endDatePicker);

        startDatePicker.setCalendarViewShown(false);
        startDatePicker.setSpinnersShown(true);
        endDatePicker.setCalendarViewShown(false);
        endDatePicker.setSpinnersShown(true);


        List<String> countryNames = new ArrayList<>();
        countryNames.add(getString(R.string.select_country));
        for (Country country : mCountries) {
            countryNames.add(country.getName());
        }
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countryNames);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(countryAdapter);

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedCountryName = (String) parent.getItemAtPosition(position);
                if (!selectedCountryName.equals(getString(R.string.select_country))) {
                    Country selectedCountry = null;
                    for (Country country : mCountries) {
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
                        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(ItinActivity.this, android.R.layout.simple_spinner_item, stateNames);
                        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerState.setAdapter(stateAdapter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedStateName = (String) parent.getItemAtPosition(position);
                if (!selectedStateName.equals(getString(R.string.select_state))) {
                    Country selectedCountry = null;
                    for (Country country : mCountries) {
                        for (State state : country.getStates()) {
                            if (state.getName().equals(selectedStateName)) {
                                selectedCountry = country;
                                break;
                            }
                        }
                        if (selectedCountry != null) {
                            break;
                        }
                    }
                    if (selectedCountry != null) {

                        List<String> cityNames = new ArrayList<>();
                        cityNames.add(getString(R.string.select_city));
                        for (State state : selectedCountry.getStates()) {
                            if (state.getName().equals(selectedStateName)) {
                                for (City city : state.getCities()) {
                                    cityNames.add(city.getName());
                                }
                                break;
                            }
                        }
                        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(ItinActivity.this, android.R.layout.simple_spinner_item, cityNames);
                        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCity.setAdapter(cityAdapter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{getString(R.string.select_state)});
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(stateAdapter);
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{getString(R.string.select_city)});
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(cityAdapter);

        TextWatcher removeErrorBackgroundTextWatcher = new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    inputItineraryName.setBackgroundResource(android.R.color.transparent);
                    spinnerCountry.setBackgroundResource(android.R.color.transparent);
                    spinnerState.setBackgroundResource(android.R.color.transparent);
                    spinnerCity.setBackgroundResource(android.R.color.transparent);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        inputItineraryName.addTextChangedListener(removeErrorBackgroundTextWatcher);

        builder.setView(dialogView);
        builder.setPositiveButton(R.string.str_OK_dialog, null);
        builder.setNegativeButton(R.string.str_Cancel_dialog, (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                boolean isValid = true;

                inputItineraryName.setBackgroundResource(android.R.color.transparent);
                spinnerCountry.setBackgroundResource(android.R.color.transparent);
                spinnerState.setBackgroundResource(android.R.color.transparent);
                spinnerCity.setBackgroundResource(android.R.color.transparent);

                String itineraryName = inputItineraryName.getText().toString();
                String selectedCountryName = (String) spinnerCountry.getSelectedItem();
                String selectedStateName = (String) spinnerState.getSelectedItem();
                String selectedCityName = spinnerCity.getSelectedItem().toString();
                selectedCityName = selectedCityName.equals(getString(R.string.select_city)) ? "" : selectedCityName;
                selectedCountryName = selectedCountryName.equals(getString(R.string.select_country)) ? "" : selectedCountryName;
                selectedStateName = selectedStateName.equals(getString(R.string.select_state)) ? "" : selectedStateName;


                if (itineraryName.isEmpty()) {
                    inputItineraryName.setError(getString(R.string.error_nombre_itinerario));
                    inputItineraryName.setBackgroundResource(R.drawable.error_background);
                    isValid = false;
                }
                String startDate = formatDate(startDatePicker);
                String endDate = formatDate(endDatePicker);
                if (startDate.compareTo(endDate) > 0) {
                    Toast.makeText(getApplicationContext(), "La fecha de inicio no puede ser posterior a la fecha de fin.", Toast.LENGTH_LONG).show();
                    isValid = false;
                }


                if (isValid) {
                    addItems(itineraryName, selectedCountryName, selectedStateName, selectedCityName,startDate,endDate);
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    @Override
    public void onContextMenuClick(int position) {

    }
}