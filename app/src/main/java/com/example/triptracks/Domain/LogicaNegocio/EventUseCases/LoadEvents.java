package com.example.triptracks.Domain.LogicaNegocio.EventUseCases;

import com.example.triptracks.Domain.Entities.Event;
import com.example.triptracks.Domain.Repository.ItineraryRepository;

import java.util.List;
import java.util.function.Consumer;

public class LoadEvents {
    private final ItineraryRepository repository;

    public LoadEvents(ItineraryRepository repository) {
        this.repository = repository;
    }

    public void execute(String itineraryId, Consumer<List<Event>> callback) {
        repository.loadEvents( itineraryId , callback);
    }
}
