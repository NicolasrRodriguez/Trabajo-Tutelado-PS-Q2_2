package com.example.triptracks.Domain.LogicaNegocio;

import android.util.Log;

import com.example.triptracks.Calendar;
import com.example.triptracks.Domain.Entities.Event;
import com.example.triptracks.Domain.Repository.ItineraryRepository;
import com.example.triptracks.ItineraryDetailActivity;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Objects;

public class DetailActLogic {


    private boolean startDateSelected;


    private Calendar calendar;
    private ItineraryDetailActivity it;

    public DetailActLogic(ItineraryDetailActivity activity, Calendar calendar) {
        this.it = activity;
        this.calendar = calendar;

    }

    public void handleDateSelection(CalendarDay date, boolean isEditing) {

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

    public void confirmarFechaInicio(CalendarDay date) {

        if(it.selectedDateMin == null) {

            it.selectedDateMin = date;
            startDateSelected = true;
            it.showConfirmstartDateDialog(date);

        }

    }

    public void confirmarFechaFin(CalendarDay date) {

        if(it.selectedDateMax == null) {

            if (it.selectedDateMin != null && date.getDate().after(it.selectedDateMin.getDate())) {
                it.selectedDateMax = date;
                startDateSelected = false;
                it.showConfirmendDateDialog(date);

            } else if (it.selectedDateMin != null && it.selectedDateMin == date) {
                it.selectedDateMax = date;
                startDateSelected = false;
                it.showConfirmendDateDialog(date);

            } else {
                it.selectedDateMin = null;
                it.selectedDateMax = null;
                confirmarFechaInicio(date);
            }
        }

    }

    public void share(String email) {
        ArrayList<String> collaborators = it.itinerary.getColaborators();
        if (!collaborators.contains(email) && Objects.equals(it.user.getEmail(), it.itinerary.getAdmin())) {
            collaborators.add(email);
            it.itinerary.setColaborators(collaborators);
            it.shareItinerary.execute(it.itinerary, email, new ItineraryRepository.OperationCallback() {
                @Override
                public void onSuccess() {}
                @Override
                public void onFailure(Exception e) {}
            });
        }
        Log.d("_ITDETTAG", "Compartiendo con " + email + " por " + it.itinerary.getAdmin());
    }
}


