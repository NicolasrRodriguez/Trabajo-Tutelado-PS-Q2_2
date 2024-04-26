package com.example.triptracks.Presenter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.beastwall.localisation.model.Country;
import com.example.triptracks.Datos.FirebaseItineraryHandler;
import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.LogicaNegocio.CreateItinerary;
import com.example.triptracks.Domain.LogicaNegocio.ItineraryAdapter;
import com.example.triptracks.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class ItneratyActivityView extends AppCompatActivity implements ItineraryAdapter.OnItemClickListener, ItineraryAdapter.OnContextMenuClickListener{

    public static final String KEY_ITINERARY = "itinerary";
    public static final int RESULT_DELETE = 1;
    public static final int RESULT_UPDATE = 2;

    public static final int RESULT_OK = 3;

    private String UserEmail;

    private FirebaseItineraryHandler firebaseItineraryHandler;
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

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onContextMenuClick(int position) {

    }
}