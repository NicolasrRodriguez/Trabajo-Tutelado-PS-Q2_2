package com.example.triptracks.Domain.LogicaNegocio.ItineraryUseCases;

import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.Repository.ItineraryRepository;

public class ShareItinerary {
    private final ItineraryRepository repository;

    public ShareItinerary(ItineraryRepository repository) {
        this.repository = repository;
    }

    public void execute(Itinerary itinerary,String Target, ItineraryRepository.OperationCallback callback) {
        repository.shareItinerary( itinerary ,  Target, callback);
    }
}
