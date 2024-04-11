package com.example.triptracks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.triptracks.databinding.ItineraryTileBinding;

public class ItineraryDetailActivity extends AppCompatActivity {

    private Itinerary article;
    private boolean detailsVisible = false;
    private ItineraryTileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ItineraryTileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        article = getIntent().getParcelableExtra(MainActivity.KEY_ITINERARY);
        binding.tileTitleTv.setText(article.getItineraryTitle());
        binding.subtitle.setText(article.getCountry());
        binding.description.setText(article.getState());
        binding.description2.setText(article.getCity());
        binding.tileTitleTv.setVisibility(View.VISIBLE);
        binding.tileTitleTv.setBackgroundResource(R.drawable.background);
        int resourceId = android.R.drawable.ic_menu_more;
        binding.tileTitleTv.setCompoundDrawablesWithIntrinsicBounds(resourceId, 0, 0, 0);
        binding.tileTitleTv.setOnClickListener(v -> toggleDetails());
        configureTopButtons(binding);
    }

    private void toggleDetails() {

        detailsVisible = !detailsVisible;
        if (detailsVisible) {
            showDetails();
        } else {
            hideDetails();
        }
    }

    private void showDetails() {
        findViewById(R.id.layoutCountry).setVisibility(View.VISIBLE);
        findViewById(R.id.layoutState).setVisibility(View.VISIBLE);
        findViewById(R.id.layoutCity).setVisibility(View.VISIBLE);
    }

    private void hideDetails() {

        findViewById(R.id.layoutCountry).setVisibility(View.GONE);
        findViewById(R.id.layoutState).setVisibility(View.GONE);
        findViewById(R.id.layoutCity).setVisibility(View.GONE);
    }

    private void configureTopButtons(ItineraryTileBinding binding) {
        binding.butBorrar.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("ACTION", "DELETE");
            resultIntent.putExtra(MainActivity.KEY_ITINERARY, article);
            setResult(MainActivity.RESULT_DELETE, resultIntent);
            finish();
        });

        binding.butEdit.setOnClickListener(v -> {
            editar_atributos(getTitleEditText());
        });

        binding.butOk.setOnClickListener(v -> {
            String editedTitle = getTitleEditText().getText().toString();
            String editedCountry = getCountryEditText().getText().toString();
            String editedState = getStateEditText().getText().toString();
            String editedCity = getCityEditText().getText().toString();

            Intent resultIntent = new Intent();
            article.setItineraryTitle(editedTitle);
            article.setCountry(editedCountry);
            article.setState(editedState);
            article.setCity(editedCity);
            resultIntent.putExtra(MainActivity.KEY_ITINERARY, article);
            setResult(MainActivity.RESULT_UPDATE, resultIntent);
            finish();
        });
    }

    public void editar_atributos(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setCursorVisible(true);
        Context context = editText.getContext();
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.editTextBackground, outValue, true);
    }

    public EditText getTitleEditText() {
        return binding.tileTitleTv;
    }

    public EditText getCountryEditText() {
        return binding.subtitle;
    }

    public EditText getStateEditText() {
        return binding.description;
    }
    public EditText getCityEditText() {
        return binding.description2;
    }

}
