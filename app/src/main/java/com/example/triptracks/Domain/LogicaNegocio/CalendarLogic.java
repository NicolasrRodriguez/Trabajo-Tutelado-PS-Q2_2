package com.example.triptracks.Domain.LogicaNegocio;


import android.widget.EditText;
import android.widget.Spinner;
import com.example.triptracks.Domain.Entities.Event;
import com.example.triptracks.Domain.LogicaNegocio.EventUseCases.DeleteOneEvent;
import com.example.triptracks.Domain.LogicaNegocio.EventUseCases.UpdateEvent;
import com.example.triptracks.Domain.LogicaNegocio.EventUseCases.getLoadedEvents;
import com.example.triptracks.Domain.Repository.ItineraryRepository;
import com.example.triptracks.Presenter.ItineraryDetailActivity;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CalendarLogic {
    private ItineraryDetailActivity it;
    public CalendarLogic(ItineraryDetailActivity it) {
        this.it = it;
    }



    public Event findEventByDate(CalendarDay date) {
        getLoadedEvents getLoadedEvents = new getLoadedEvents(it.firebaseItineraryHandler);
        List<Event> loadedEvents = getLoadedEvents.execute();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        String targetDate = dateFormat.format(date.getDate());
        for (Event event : loadedEvents) {
            if (event.getDate().equals(targetDate)) {
                return event;
            }
        }
        return null;
    }

    public void edit(Event event, EditText input, Spinner spinner){
        String description = input.getText().toString();
        String category = spinner.getSelectedItem().toString();

        event.setDescription(description);
        event.setCategory(category);

        if(Objects.equals(event.getCategory(), "Exploration")){
            event.setCategory("Exploración");
        } else if (Objects.equals(event.getCategory(), "Gastronomy")) {
            event.setCategory("Gastronomía");

        } else if (Objects.equals(event.getCategory(), "Entertainment")) {
            event.setCategory("Entretenimiento");
        }
        UpdateEvent UpdateEvent = new UpdateEvent(it.firebaseItineraryHandler);
        UpdateEvent.execute(it.itinerary, event,new ItineraryRepository.OperationCallback() {
            @Override
            public void onSuccess() {}
            @Override
            public void onFailure(Exception e) {}

        });
        it.binding.calendarView.invalidateDecorators();
    }


    public void delete(Event event){
        DeleteOneEvent deleteOneEvent = new DeleteOneEvent(it.firebaseItineraryHandler);
        deleteOneEvent.execute(it.itinerary.getId(), event.getId(),new ItineraryRepository.OperationCallback() {
            @Override
            public void onSuccess() {}

            @Override
            public void onFailure(Exception e) {}
        });
    }

    public void configureCalendarView() {
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

}
