package com.example.triptracks.Domain.LogicaNegocio;

import com.example.triptracks.Calendar;
import com.example.triptracks.Domain.Entities.Event;
import com.example.triptracks.ItineraryDetailActivity;
import com.prolificinteractive.materialcalendarview.CalendarDay;

public class DateSelectionHandler {


    private boolean startDateSelected;


    private Calendar calendar;
    private ItineraryDetailActivity itineraryDetailActivity;

    public DateSelectionHandler(ItineraryDetailActivity activity,Calendar calendar) {
        this.itineraryDetailActivity = activity;
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

        if(itineraryDetailActivity.selectedDateMin == null) {

            itineraryDetailActivity.selectedDateMin = date;
            startDateSelected = true;
            itineraryDetailActivity.showConfirmstartDateDialog(date);

        }

    }

    public void confirmarFechaFin(CalendarDay date) {

        if(itineraryDetailActivity.selectedDateMax == null) {

            if (itineraryDetailActivity.selectedDateMin != null && date.getDate().after(itineraryDetailActivity.selectedDateMin.getDate())) {
                itineraryDetailActivity.selectedDateMax = date;
                startDateSelected = false;
                itineraryDetailActivity.showConfirmendDateDialog(date);

            } else if (itineraryDetailActivity.selectedDateMin != null && itineraryDetailActivity.selectedDateMin == date) {
                itineraryDetailActivity.selectedDateMax = date;
                startDateSelected = false;
                itineraryDetailActivity.showConfirmendDateDialog(date);

            } else {
                itineraryDetailActivity.selectedDateMin = null;
                itineraryDetailActivity.selectedDateMax = null;
                confirmarFechaInicio(date);
            }
        }

    }
}


