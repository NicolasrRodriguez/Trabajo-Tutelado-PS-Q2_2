package com.example.triptracks;

import android.text.InputType;
import android.util.TypedValue;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;

import com.example.triptracks.Domain.Entities.Event;
import com.example.triptracks.Domain.Repository.ItineraryRepository;
import com.example.triptracks.Presenter.EventDecorator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    public Calendar(ItineraryDetailActivity it) {
        this.it = it;
    }


    public void promptForActivity(CalendarDay date) {
        Event existingEvent = findEventByDate(date);
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
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ","))
                                    .child("itineraries").child(it.itinerary.getId()).child("events");
                            String id = databaseReference.push().getKey();
                            it.category = it.spinner_evento.getSelectedItem().toString();
                            if (id != null) {
                                Event event = new Event(id, date.getDate().toString(), it.category, activity);
                                databaseReference.child(id).setValue(event);
                                it.eventDecorator =new EventDecorator(it, Collections.singleton(date), it.category);
                                it.binding.calendarView.addDecorator(it.eventDecorator);
                            }
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .create();
            dialog.show();
        }
    }

    public Event findEventByDate(CalendarDay date) {
        List<Event> loadedEvents = it.getLoadedEvents.execute();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        String targetDate = dateFormat.format(date.getDate());
        for (Event event : loadedEvents) {
            if (event.getDate().equals(targetDate)) {
                return event;
            }
        }
        return null;
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
                    String description = input.getText().toString();
                    String category = spinner.getSelectedItem().toString();
                    event.setDescription(description);
                    event.setCategory(category);
                    it.UpdateEvent.execute(it.itinerary, event,new ItineraryRepository.OperationCallback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onFailure(Exception e) {

                                }

                                });
                    it.binding.calendarView.invalidateDecorators();

                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    private void deleteEvent(Event event) {
        new AlertDialog.Builder(it)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar este evento?")
                .setPositiveButton("Eliminar", (dialogInterface, i) -> {
                    it.deleteOneEvent.execute(it.itinerary.getId(), event.getId(),new ItineraryRepository.OperationCallback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onFailure(Exception e) {
                                }

                                });

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



    void configureCalendarView() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            Date startDate = format.parse(it.itinerary.getStartDate());
            Date endDate = format.parse(it.itinerary.getEndDate());

            CalendarDay startDay = CalendarDay.from(startDate);
            CalendarDay endDay = CalendarDay.from(endDate);

            it.binding.calendarView.state().edit()
                    .setMinimumDate(startDay)
                    .setMaximumDate(endDay)
                    .commit();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    void loadAndDecorateEvents() {
        it.loadEvents.execute(it.itinerary.getId(), events -> {
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
