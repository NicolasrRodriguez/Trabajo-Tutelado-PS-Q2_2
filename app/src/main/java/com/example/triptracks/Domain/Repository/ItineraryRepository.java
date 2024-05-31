package com.example.triptracks.Domain.Repository;


import com.example.triptracks.Domain.Entities.Event;
import com.example.triptracks.Domain.Entities.Itinerary;

import java.util.List;
import java.util.function.Consumer;


public interface ItineraryRepository {

    interface OperationCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
    void saveItinerary(Itinerary itinerary, OperationCallback callback);
    void deleteItinerary(Itinerary itinerary, OperationCallback callback);
    void shareItinerary(Itinerary itinerary , String Target,OperationCallback callback);
    void updateItinerary(Itinerary itinerary,OperationCallback callback);

    void deleteAllEvents(String itineraryId, OperationCallback callback);
    void updateEvent(Itinerary itinerary , Event event,OperationCallback callback);

    void deleteOneEvent(String itineraryId , String eventId, OperationCallback callback);
    void loadEvents(String itineraryId, Consumer<List<Event>> callback);
    List<Event> getLoadedEvents();
}
