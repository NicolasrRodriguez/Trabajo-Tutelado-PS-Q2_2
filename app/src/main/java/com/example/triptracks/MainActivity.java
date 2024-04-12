
package com.example.triptracks;

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
import android.widget.EditText;
import android.widget.Spinner;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.beastwall.localisation.model.City;
import com.beastwall.localisation.model.Country;
import com.beastwall.localisation.model.State;
import com.example.triptracks.databinding.ActivityMainBinding;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItineraryAdapter.OnItemClickListener, ItineraryAdapter.OnContextMenuClickListener {

    public static final String KEY_ITINERARY = "itinerary";
    public static final int RESULT_DELETE = 1;
    public static final int RESULT_UPDATE = 2;

    private ActivityMainBinding binding;

    public static ItineraryAdapter mAdapter;
    private ArrayList<Itinerary> mItineraryList = new ArrayList<>();

    public static List<Country> mCountries = new ArrayList<>();

    public static int selectedPosition = RecyclerView.NO_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAdapter = new ItineraryAdapter(mItineraryList, this, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.categoriesRv.setLayoutManager(linearLayoutManager);
        binding.categoriesRv.setAdapter(mAdapter);
        registerForContextMenu(binding.categoriesRv);
        mAdapter.mostrarbotones(true);
        new LoadCountriesTask(this).execute();
    }


    ActivityResultLauncher<Intent> myStartActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_DELETE) {
                    Intent data = result.getData();

                    if (data != null) {
                        Itinerary deletedItinerary = data.getParcelableExtra(KEY_ITINERARY);
                        String elemento = deletedItinerary.getElement();
                        mAdapter.eliminar_por_id(elemento);
                    }
                } else if (result.getResultCode() == RESULT_UPDATE) {
                    Intent data = result.getData();
                    if (data != null) {
                        Itinerary updatedItinerary = data.getParcelableExtra(KEY_ITINERARY);
                        mAdapter.actualizar_por_id(updatedItinerary);
                    }
                }
            }
    );

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_opcion_1) {
            showDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void detalle_actividad(Itinerary itinerary) {
        Intent intent = new Intent(this, ItineraryDetailActivity.class);
        intent.putExtra(MainActivity.KEY_ITINERARY, itinerary);
        myStartActivityForResult.launch(intent);
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.str_mensaje_dialog);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.crear_itinerario, null);

        final EditText inputItineraryName = dialogView.findViewById(R.id.inputItineraryName);
        final Spinner spinnerCountry = dialogView.findViewById(R.id.spinnerCountry);
        final Spinner spinnerState = dialogView.findViewById(R.id.spinnerState);
        final Spinner spinnerCity = dialogView.findViewById(R.id.spinnerCity);

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
                        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, stateNames);
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
                        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, cityNames);
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

                if (isValid) {
                    addItems(itineraryName, selectedCountryName, selectedStateName, selectedCityName);
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }


    private void addItems(String itineraryName, String countryName, String stateName, String cityName) {
        String elemento = "ID_" + (mAdapter.getItemCount() + 1);
        Log.d("_TAG", cityName);
        ArrayList<Itinerary> newItineraries = new ArrayList<>();
        newItineraries.add(new Itinerary(elemento, itineraryName, countryName, stateName, cityName));
        mAdapter.anadirelem(newItineraries);
    }

    @Override
    public void onItemClick(int position) {
        if (position != RecyclerView.NO_POSITION) {
            Itinerary selectedItinerary = mAdapter.getItem(position);
            detalle_actividad(selectedItinerary);
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (selectedPosition != RecyclerView.NO_POSITION) {
            if (id == R.id.info) {
                Intent intent = new Intent(this, ItineraryDetailActivity.class);
                intent.putExtra(MainActivity.KEY_ITINERARY, mItineraryList.get(selectedPosition));
                myStartActivityForResult.launch(intent);
                selectedPosition = RecyclerView.NO_POSITION;
                return true;
            }
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onContextMenuClick(int position) {
        Itinerary selectedItinerary = mItineraryList.get(position);
        Intent intent = new Intent(this, ItineraryDetailActivity.class);
        intent.putExtra(MainActivity.KEY_ITINERARY, selectedItinerary);
        myStartActivityForResult.launch(intent);
    }

    public void onCountriesLoaded(List<Country> countries) {
        mCountries = countries;
    }

}