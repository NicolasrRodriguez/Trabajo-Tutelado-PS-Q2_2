package com.example.triptracks.Presenter;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.triptracks.Data.FirebaseItineraryHandler;
import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.LogicaNegocio.DetailActLogic;
import com.example.triptracks.Domain.Service.MapServiceImp;

import com.example.triptracks.R;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.triptracks.databinding.ItineraryTileBinding;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;


public class ItineraryDetailActivity extends AppCompatActivity {

    public Itinerary itinerary;
    public Calendar calendar;
    public FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public CalendarDay selectedDateMin = null;
    public CalendarDay selectedDateMax = null;
    public MapServiceImp mapServiceImp;
    public ItineraryTileBinding binding;
    public Spinner spinnerCountry;
    public Spinner spinnerState;
    public Spinner spinnerCity;
    public MaterialCalendarView calendarView;
    public FirebaseItineraryHandler firebaseItineraryHandler;
    public boolean isEditing = false;
    boolean detailsVisible = false;
    boolean startDateSelected = false;
    public EventDecorator eventDecorator;
    SupportMapFragment fragmentmap;
    Fragment fragmentcalendar;
    public Spinner spinner_evento;
    public String category;

    public static final String KEY_ITINERARY = "itinerary";



    DetailActLogic detailActLogic;

    public ItineraryDetailActivity() {}

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ItineraryTileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        itinerary = getIntent().getParcelableExtra(ItneraryActivityView.KEY_ITINERARY);
        assert itinerary != null;
        int nightModeFlags = this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        binding.itineraryTitle.setText(itinerary.getItineraryTitle());
        binding.itineraryCountry.setText(itinerary.getCountry());
        binding.itineraryState.setText(itinerary.getState());
        binding.itineraryCity.setText(itinerary.getCity());
        binding.itineraryTitle.setVisibility(View.VISIBLE);
        binding.itineraryTitle.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_more, 0, 0, 0);
        binding.itineraryTitle.setOnClickListener(v -> pulsarDesplegable());
        binding.layoutmapcontainer.setVisibility(View.GONE);
        binding.layoutcalendarcontainer.setVisibility(View.GONE);
        binding.getRoot().setBackgroundResource(R.drawable.fondo);
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            binding.itineraryTitle.setBackgroundResource(R.drawable.background_black);
            binding.itineraryCountry.setBackgroundResource(R.drawable.background_black);
            binding.itineraryState.setBackgroundResource(R.drawable.background_black);
            binding.itineraryCity.setBackgroundResource(R.drawable.background_black);
            binding.spinnerCountryAct2.setBackgroundResource(R.drawable.background_black);
            binding.spinnerStateAct2.setBackgroundResource(R.drawable.background_black);
            binding.spinnerCityAct2.setBackgroundResource(R.drawable.background_black);
        } else {
            binding.itineraryTitle.setBackgroundResource(R.drawable.background);
            binding.itineraryCountry.setBackgroundResource(R.drawable.background);
            binding.itineraryState.setBackgroundResource(R.drawable.background);
            binding.itineraryCity.setBackgroundResource(R.drawable.background);
            binding.spinnerCountryAct2.setBackgroundResource(R.drawable.background);
            binding.spinnerStateAct2.setBackgroundResource(R.drawable.background);
            binding.spinnerCityAct2.setBackgroundResource(R.drawable.background);
        }

        firebaseItineraryHandler = new FirebaseItineraryHandler(updatedItineraries -> {});
        calendar = new Calendar(this);
        calendar.calendlogic.configureCalendarView();
        calendar.loadAndDecorateEvents();
        mapServiceImp = new MapServiceImp(this, itinerary);
        detailActLogic = new DetailActLogic(this, calendar, itinerary, user, selectedDateMin, selectedDateMax);
        calendarView = findViewById(R.id.calendarView);

        configurarSpinners();
        configurarBotones();
        setTitle(R.string.app_name);

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {

            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                detailActLogic.handleDateSelection(date);
            }
        });
    }

    public void showConfirmstartDateDialog(CalendarDay date) {
        new AlertDialog.Builder(this)
                .setTitle( getResources().getString(R.string.confirmaci_n))
                .setMessage( getResources().getString(R.string.guardar_esta_fecha_como_fecha_de_inicio))
                .setPositiveButton( getResources().getString(R.string.ok), (dialog, which) -> {
                    detailActLogic.confirmarFechaInicio(date);
                    Toast.makeText(this,  getResources().getString(R.string.fecha_de_inicio_seleccionada), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton( getResources().getString(R.string.cancelar), (dialog, which) -> {
                    selectedDateMin = null;
                    startDateSelected = false;
                })
                .show();
    }
    public void showConfirmendDateDialog(CalendarDay date) {
        new AlertDialog.Builder(this)
                .setTitle( getResources().getString(R.string.confirmaci_n))
                .setMessage( getResources().getString(R.string.guardar_esta_fecha_como_fecha_de_fin))
                .setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {
                    detailActLogic.confirmarFechaFin(date);
                   ;
                    Toast.makeText(this, getResources().getString(R.string.fecha_de_fin_seleccionada), Toast.LENGTH_LONG).show();

                })
                .setNegativeButton(getResources().getString(R.string.cancelar), (dialog, which) -> {
                    selectedDateMax = null;
                    startDateSelected = false;

                })
                .show();
    }

    ActivityResultLauncher<Intent> myStartActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Log.d("_ITDETA","Succesfully returned to MainActivity");
                }
            }
    );

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.itinirary_detail_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent resultIntent = new Intent();
        setResult(AuthActivityView.RESULT_SESION_CLOSED, resultIntent);
        if (id == R.id.menu_compartir) {
            Log.d("_ITDETA", "Compartir itinerario");
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
        } else if (id == R.id.menu_galeria) {
            Intent intent = new Intent(this, ImagesActivityView.class);
            intent.putExtra(ItneraryActivityView.KEY_ITINERARY, itinerary);
            myStartActivityForResult.launch(intent);;
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
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.str_compartir, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String text = Targetemail.getText().toString();//juan123456@gmail.com
                detailActLogic.share(text);
                Log.d("_ITDETA", "Compartiendo con " + text + "por " + itinerary.getAdmin());
            }
        });
        builder.setNegativeButton(R.string.str_cancelar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("_ITDETA", "Cancelado compartir");
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void pulsarDesplegable() {
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
        binding.layoutCountry.setVisibility(View.VISIBLE);
        binding.layoutState.setVisibility(View.VISIBLE);
        binding.layoutCity.setVisibility(View.VISIBLE);
        binding.mapContainer.setVisibility(View.VISIBLE);
        binding.calendarContainer.setVisibility(View.VISIBLE);
    }

    private void ocultardetalles() {
        binding.layoutCountry.setVisibility(View.GONE);
        binding.layoutState.setVisibility(View.GONE);
        binding.layoutCity.setVisibility(View.GONE);
    }


    private void configurarSpinners() {

        spinnerCountry = findViewById(R.id.spinnerCountryAct2);
        spinnerState = findViewById(R.id.spinnerStateAct2);
        spinnerCity = findViewById(R.id.spinnerCityAct2);

        List<String> countryNames = new ArrayList<>();
        detailActLogic.llenarListaPaises(countryNames);
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
                detailActLogic.seleccionarPais(selectedCountryName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void configurarBotones() {

        binding.butBorrar.setOnClickListener(v -> {detailActLogic.handleDeleteButtonClick(itinerary);});

        binding.butEdit.setOnClickListener(v -> {
            detailActLogic.handleEditButtonClick(binding.itineraryTitle);
            findViewById(R.id.itineraryCountry).setVisibility(View.GONE);
            findViewById(R.id.spinnerCountryAct2).setVisibility(View.VISIBLE);
            findViewById(R.id.itineraryState).setVisibility(View.GONE);
            findViewById(R.id.spinnerStateAct2).setVisibility(View.VISIBLE);
            findViewById(R.id.itineraryCity).setVisibility(View.GONE);
            findViewById(R.id.spinnerCityAct2).setVisibility(View.VISIBLE);
        });

        binding.butVolver.setOnClickListener(v -> {detailActLogic.handleVolverButtonClick(itinerary);});

        binding.butOk.setOnClickListener(v -> {
            binding.calendarView.clearSelection();
            binding.calendarView.setClickable(false);
            binding.calendarView.setEnabled(false);
            detailActLogic.handleOkButtonClick();
            findViewById(R.id.itineraryCountry).setVisibility(View.VISIBLE);
            findViewById(R.id.spinnerCountryAct2).setVisibility(View.GONE);
            findViewById(R.id.itineraryState).setVisibility(View.VISIBLE);
            findViewById(R.id.spinnerStateAct2).setVisibility(View.GONE);
            findViewById(R.id.itineraryCity).setVisibility(View.VISIBLE);
            findViewById(R.id.spinnerCityAct2).setVisibility(View.GONE);
        });
    }

    private void showMapFragment() {
        fragmentmap = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mapContainer, fragmentmap)
                .commit();
        fragmentmap.getMapAsync(mapServiceImp);
    }

    private void showCalendarFragment() {
        fragmentcalendar = new Fragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.calendarContainer, fragmentcalendar)
                .commit();
    }
}
