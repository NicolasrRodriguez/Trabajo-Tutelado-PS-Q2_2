package com.example.triptracks.Presenter;

import android.text.InputType;
import android.util.TypedValue;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.example.triptracks.Domain.Entities.Event;
import com.example.triptracks.Domain.LogicaNegocio.CalendarLogic;
import com.example.triptracks.Domain.LogicaNegocio.LoadEvents;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class Calendar {

    private ItineraryDetailActivity it;
    public CalendarLogic calendlogic;

    public Calendar(ItineraryDetailActivity it) {
        this.it = it;
        this.calendlogic = new CalendarLogic(it);
    }

    public void promptForActivity(CalendarDay date) {
        Event existingEvent = calendlogic.findEventByDate(date);
        if (existingEvent != null) {
            showEventOptionsDialog(existingEvent);
        } else {
            final EditText input = new EditText(it);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            it.spinner_evento = new Spinner(it);
            List<String> categories = Arrays.asList("Exploración", "Gastronomía", "Entretenimiento");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(it, android.R.layout.simple_spinner_item, categories);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            it.spinner_evento.setAdapter(adapter);
            LinearLayout layout = new LinearLayout(it);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(it.spinner_evento);
            layout.addView(input);

            AlertDialog dialog = new AlertDialog.Builder(it)
                    .setTitle("Agregar actividad")
                    .setMessage("Ingresa la actividad para " + date.getDate().toString())
                    .setView(layout)
                    .setPositiveButton("Aceptar", (dialogInterface, i) -> {
                        String activity = input.getText().toString();
                        if (!activity.isEmpty()) {
                            calendlogic.createEvent(date,activity);
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .create();
            dialog.show();
        }
    }


    public void showEventOptionsDialog(Event event) {
        LinearLayout layout = new LinearLayout(it);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);
        AlertDialog.Builder builder = new AlertDialog.Builder(it)
                .setTitle("Opciones de evento")
                .setItems(new CharSequence[]{"Detalles", "Editar", "Eliminar"}, (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            showEventDetails(event, layout);
                            break;
                        case 1:
                            editEvent(event);
                            break;
                        case 2:
                            deleteEvent(event);
                            break;
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showEventDetails(Event event, LinearLayout layout) {
        layout.removeAllViews();
        showMessage("Tipo de actividad", event.getCategory(), layout);
        showMessage("Detalles del evento", event.getDescription(), layout);
        AlertDialog.Builder builder = new AlertDialog.Builder(it)
                .setTitle("Detalles del evento")
                .setView(layout)
                .setPositiveButton("OK", null);
        AlertDialog detailsDialog = builder.create();
        detailsDialog.show();
    }

    private void showMessage(String title, String message, LinearLayout layout) {
        TextView textView = new TextView(it);
        textView.setText(title + ": " + message);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        layout.addView(textView);
    }

    private void editEvent(Event event) {
        LinearLayout layout = new LinearLayout(it);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);
        Spinner spinner = new Spinner(it);
        List<String> categories = Arrays.asList("Exploración", "Gastronomía", "Entretenimiento");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(it, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        layout.addView(spinner);
        int position = categories.indexOf(event.getCategory());
        if (position != -1) {
            spinner.setSelection(position);
        }
        EditText input = new EditText(it);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(event.getDescription());
        layout.addView(input);
        new AlertDialog.Builder(it)
                .setTitle("Editar evento")
                .setView(layout)
                .setPositiveButton("Actualizar", (dialogInterface, i) -> {
                    calendlogic.edit(event,input,spinner);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    private void deleteEvent(Event event) {
        new AlertDialog.Builder(it)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar este evento?")
                .setPositiveButton("Eliminar", (dialogInterface, i) -> {
                    calendlogic.delete(event);
                    if (it.eventDecorator != null) {
                        it.binding.calendarView.removeDecorator(it.eventDecorator);
                        it.eventDecorator = null;
                    }
                    it.binding.calendarView.invalidateDecorators();
                    loadAndDecorateEvents();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void loadAndDecorateEvents() {
        LoadEvents loadEvents = new LoadEvents(it.firebaseItineraryHandler);
        loadEvents.execute(it.itinerary.getId(), events -> {
            it.runOnUiThread(() -> {

                it.binding.calendarView.removeDecorators();

                if (events != null && !events.isEmpty()) {
                    HashSet<CalendarDay> eventDays = new HashSet<>();
                    for (Event event : events) {
                        try {
                            SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                            Date eventDate = format.parse(event.getDate());
                            CalendarDay day = CalendarDay.from(eventDate);
                            eventDays.add(day);
                            EventDecorator eventDecorator = new EventDecorator(it, Collections.singleton(day), event.getCategory());
                            it.binding.calendarView.addDecorator(eventDecorator);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        });
    }
}