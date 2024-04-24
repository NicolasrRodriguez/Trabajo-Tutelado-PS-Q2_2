package com.example.triptracks.Domain.LogicaNegocio;

import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.Repository.ItineraryRepository;

public class DeleteItinerary{
    private final ItineraryRepository repository;

    public DeleteItinerary(ItineraryRepository repository) {
        this.repository = repository;
    }

    public void execute(Itinerary itinerary,ItineraryRepository.OperationCallback callback) {
        repository.deleteItinerary(itinerary, callback);
    }
}