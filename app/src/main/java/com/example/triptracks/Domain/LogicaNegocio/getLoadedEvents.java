package com.example.triptracks.Domain.LogicaNegocio;

import com.example.triptracks.Domain.Entities.Event;
import com.example.triptracks.Domain.Repository.ItineraryRepository;

import java.util.List;
import java.util.function.Consumer;

public class getLoadedEvents {
    private final ItineraryRepository repository;

    public getLoadedEvents(ItineraryRepository repository) {
        this.repository = repository;
    }

    public List<Event> execute() {
       return repository.getLoadedEvents();
    }
}
