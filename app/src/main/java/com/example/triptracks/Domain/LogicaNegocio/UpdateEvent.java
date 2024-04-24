package com.example.triptracks.Domain.LogicaNegocio;

import com.example.triptracks.Domain.Entities.Event;
import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.Repository.ItineraryRepository;

public class UpdateEvent{
    private final ItineraryRepository repository;

    public UpdateEvent(ItineraryRepository repository) {
        this.repository = repository;
    }

    public void execute(Itinerary itinerary, Event event, ItineraryRepository.OperationCallback callback) {
        repository.updateEvent(itinerary,event, callback);
    }
}