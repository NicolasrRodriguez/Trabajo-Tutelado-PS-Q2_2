package com.example.triptracks.Domain.Repository;


import com.example.triptracks.Domain.Entities.Event;
import com.example.triptracks.Domain.Entities.Itinerary;


public interface ItineraryRepository {

    interface OperationCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
    void saveItinerary(Itinerary itinerary, OperationCallback callback);
    void deleteItinerary(Itinerary itinerary, OperationCallback callback);

    void deleteAllEvents(String itineraryId, OperationCallback callback);
    void updateEvent(Itinerary itinerary , Event event,OperationCallback callback);
    void deleteOneEvent(String itineraryId , String eventId,OperationCallback callback);
}
