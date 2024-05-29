package com.example.triptracks.Domain.LogicaNegocio;
import com.example.triptracks.Domain.LogicaNegocio.EventUseCases.DeleteAllEvents;
import com.example.triptracks.Domain.LogicaNegocio.ItineraryUseCases.DeleteItinerary;
import com.example.triptracks.Domain.LogicaNegocio.ItineraryUseCases.ShareItinerary;
import com.example.triptracks.Domain.LogicaNegocio.ItineraryUseCases.UpdateItinerary;
import com.example.triptracks.Presenter.AuthActivityView;
import com.example.triptracks.Presenter.ItneraryActivityView;
import static com.example.triptracks.Presenter.ItneraryActivityView.mAdapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.beastwall.localisation.model.City;
import com.beastwall.localisation.model.Country;
import com.beastwall.localisation.model.State;
import com.example.triptracks.Presenter.Calendar;
import com.example.triptracks.Domain.Entities.Event;
import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.Repository.ItineraryRepository;

import com.example.triptracks.Presenter.ItineraryDetailActivity;
import com.example.triptracks.R;
import com.google.firebase.auth.FirebaseUser;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DetailActLogic {

    public boolean startDateSelected;
    private Calendar calendar;
    private ItineraryDetailActivity it;
    private Itinerary itinerary;
    private FirebaseUser user;
    private CalendarDay selectedDateMin;
    private CalendarDay selectedDateMax;


    public DetailActLogic(ItineraryDetailActivity activity, Calendar calendar, Itinerary itinerary, FirebaseUser user, CalendarDay selectedDateMin, CalendarDay selectedDateMax) {
        this.it = activity;
        this.calendar = calendar;
        this.itinerary = itinerary;
        this.user = user;
        this.selectedDateMin = selectedDateMin;
        this.selectedDateMax = selectedDateMax;
    }

    public void handleDateSelection(CalendarDay date) {

        if (it.isEditing) {
            if (!startDateSelected) {
                confirmarFechaInicio(date);
            } else {
                confirmarFechaFin(date);
            }
        } else {
            Event existingEvent = calendar.calendlogic.findEventByDate(date);
            if (existingEvent != null) {
                calendar.showEventOptionsDialog(existingEvent);
            } else {
                calendar.promptForActivity(date);
            }
        }
    }

    public void confirmarFechaInicio(CalendarDay date) {

        if(selectedDateMin == null) {
            selectedDateMin = date;
            startDateSelected = true;
            it.showConfirmstartDateDialog(date);
        }
    }

    public void confirmarFechaFin(CalendarDay date) {

        if(selectedDateMax == null) {
            if (selectedDateMin != null && date.getDate().after(selectedDateMin.getDate())) {
                selectedDateMax = date;
                startDateSelected = false;
                it.showConfirmendDateDialog(date);

            } else if (selectedDateMin != null && selectedDateMin == date) {
                selectedDateMax = date;
                startDateSelected = false;
                it.showConfirmendDateDialog(date);

            } else {
                selectedDateMin = null;
                selectedDateMax = null;
                confirmarFechaInicio(date);
            }
        }
    }

    public void share(String email) {
        ArrayList<String> collaborators = itinerary.getColaborators();
        if (!collaborators.contains(email) && Objects.equals(user.getEmail(), itinerary.getAdmin())) {
            collaborators.add(email);
            itinerary.setColaborators(collaborators);
            ShareItinerary shareItinerary = new ShareItinerary(it.firebaseItineraryHandler);
            shareItinerary.execute(itinerary, email, new ItineraryRepository.OperationCallback() {
                @Override
                public void onSuccess() {}
                @Override
                public void onFailure(Exception e) {}
            });
        }
        Log.d("_ITDETTAG", "Compartiendo con " + email + " por " + it.itinerary.getAdmin());
    }

    public void llenarListaPaises(List<String> countryNames) {
        countryNames.add(it.getString(R.string.select_country));
        for (Country country : AuthActivityView.mCountries) {
            countryNames.add(country.getName());
        }
    }

    public void seleccionarPais(String selectedCountryName) {

        if (!selectedCountryName.equals(it.getString(R.string.select_country))) {

            Country selectedCountry = null;
            for (Country country : AuthActivityView.mCountries) {
                if (country.getName().equals(selectedCountryName)) {
                    selectedCountry = country;
                    break;
                }
            }
            if (selectedCountry != null) {
                List<String> stateNames = new ArrayList<>();
                stateNames.add(it.getString(R.string.select_state));
                for (State state : selectedCountry.getStates()) {
                    stateNames.add(state.getName());
                }
                ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(it, android.R.layout.simple_spinner_item, stateNames);
                stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                it.spinnerState.setAdapter(stateAdapter);
            }

            if (selectedCountry != null) {
                List<String> cityNames = new ArrayList<>();
                cityNames.add(it.getString(R.string.select_city));
                for (State state : selectedCountry.getStates()) {
                    if (state.getName().equals(state.getName())) {
                        for (City city : state.getCities()) {
                            cityNames.add(city.getName());
                        }
                        break;
                    }
                }
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(it, android.R.layout.simple_spinner_item, cityNames);
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                it.spinnerCity.setAdapter(cityAdapter);
            }

        }
    }

    private void hidekeyboard(){
        EditText titleEditText =it.binding.itineraryTitle;
        titleEditText.setFocusable(false);
        titleEditText.setFocusableInTouchMode(false);
        titleEditText.setCursorVisible(false);
        titleEditText.clearFocus();
        InputMethodManager imm = (InputMethodManager) it.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(titleEditText.getWindowToken(), 0);
    }

    private void updatefields(){

        String editedTitle = it.binding.itineraryTitle.getText().toString();
        String editedCountry = itinerary.getCountry();
        String editedState = itinerary.getState();
        String editedCity = itinerary.getCity();
        String Admin = itinerary.getAdmin();
        ArrayList<String> colaboratos = itinerary.getColaborators();
        ArrayList<String> images = new ArrayList<>();
        if (itinerary.getImageUris() != null){
             images = itinerary.getImageUris();
            Log.d("_IMM", "hay " + images.size() + " iamgenes");
        }
        if (it.spinnerCountry.getSelectedItem() != null) {
            String selectedCountry = it.spinnerCountry.getSelectedItem().toString();
            if (!selectedCountry.equals(it.getString(R.string.select_country))) {
                editedCountry = selectedCountry;
                editedState = "";
                editedCity = "";
            }
        }

        if (it.spinnerState.getSelectedItem() != null) {
            String selectedState = it.spinnerState.getSelectedItem().toString();
            if (!selectedState.equals(it.getString(R.string.select_state))) {
                editedState = selectedState;
            }
        }

        if (it.spinnerCity.getSelectedItem() != null) {
            String selectedCity = it.spinnerCity.getSelectedItem().toString();
            if (!selectedCity.equals(it.getString(R.string.select_city))) {
                editedCity = selectedCity;
            }
        }

        itinerary.setItineraryTitle(editedTitle);
        itinerary.setCountry(editedCountry);
        itinerary.setState(editedState);
        itinerary.setCity(editedCity);
        itinerary.setAdmin(Admin);//seguramente hay que tocarlo por que no se deberia poder modificar el Admin
        itinerary.setColaborators(colaboratos);
        itinerary.setImageUris(images);
        it.binding.itineraryTitle.setText(itinerary.getItineraryTitle());
        it.binding.itineraryCountry.setText(itinerary.getCountry());
        it.binding.itineraryState.setText(itinerary.getState());
        it. binding.itineraryCity.setText(itinerary.getCity());
        it. mapServiceImp.initializeMap();
        mAdapter.actualizar_por_id(itinerary);
        UpdateItinerary updateItinerary = new UpdateItinerary(it.firebaseItineraryHandler);
        updateItinerary.execute(itinerary,new ItineraryRepository.OperationCallback() {
            @Override
            public void onSuccess() {}

            @Override
            public void onFailure(Exception e) {}
        });

    }

    private void updateDates(){

        if (selectedDateMin != null && selectedDateMax != null) {
            Date startDate = selectedDateMin.getDate();
            Date endDate = selectedDateMax.getDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String editedStartDate = dateFormat.format(startDate);
            String editedEndDate = dateFormat.format(endDate);
            itinerary.setStartDate(editedStartDate);
            itinerary.setEndDate(editedEndDate);
            calendar.calendlogic.configureCalendarView();
            DeleteAllEvents deleteEvents = new DeleteAllEvents(it.firebaseItineraryHandler);
            deleteEvents.execute(itinerary.getId(),new ItineraryRepository.OperationCallback() {
                @Override
                public void onSuccess() {}

                @Override
                public void onFailure(Exception e) {}

            });
        } else if (selectedDateMin!=null && selectedDateMax==null ||selectedDateMin==null && selectedDateMax!=null ) {

            startDateSelected = false;
            calendar.loadAndDecorateEvents();
            calendar.calendlogic.configureCalendarView();
            it.binding.calendarView.setClickable(false);
            it.binding.calendarView.setLongClickable(false);
            it.binding.calendarView.setEnabled(false);

        } else{
            calendar.loadAndDecorateEvents();
            calendar.calendlogic.configureCalendarView();

        }
        it.binding.calendarView.setClickable(false);
        it.binding.calendarView.setLongClickable(false);
        it.binding.calendarView.setEnabled(false);

    }

    public void handleDeleteButtonClick(Itinerary itinerary) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("ACTION", "DELETE");
        resultIntent.putExtra(ItneraryActivityView.KEY_ITINERARY, itinerary);
        it.setResult(ItneraryActivityView.RESULT_DELETE, resultIntent);
        DeleteItinerary deleteItinerary = new DeleteItinerary(it.firebaseItineraryHandler);
        deleteItinerary.execute(itinerary, new ItineraryRepository.OperationCallback() {
            @Override
            public void onSuccess() {it.finish();}

            @Override
            public void onFailure(Exception e) {}
        });

    }


    public void handleVolverButtonClick(Itinerary itinerary) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("ACTION", "BACK");
        resultIntent.putExtra(ItneraryActivityView.KEY_ITINERARY, itinerary);
        it.setResult(ItneraryActivityView.RESULT_OK, resultIntent);
        it.finish();
    }


    public void handleEditButtonClick(EditText editText) {
        it.isEditing = true;
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setCursorVisible(true);
        Context context = editText.getContext();
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.editTextBackground, outValue, true);
        selectedDateMin = null;
        selectedDateMax = null;
        it.binding.calendarView.setClickable(false);
        it.binding.calendarView.setLongClickable(false);
        it.binding.calendarView.setEnabled(false);
        it.binding.calendarView.clearSelection();
        it.binding.calendarView.removeDecorators();
        it.binding.calendarView.state().edit()
                .setMinimumDate((java.util.Calendar) null)
                .setMaximumDate((java.util.Calendar) null)
                .commit();

        if (selectedDateMin != null && selectedDateMax != null) {
            it.binding.calendarView.state().edit()
                    .setMinimumDate(selectedDateMin)
                    .setMaximumDate(selectedDateMax)
                    .commit();
        }

    }

    public void handleOkButtonClick(){
        if (it.isEditing) {
            it.isEditing = false;
            hidekeyboard();
            updatefields();
            updateDates();
        }
    }
}