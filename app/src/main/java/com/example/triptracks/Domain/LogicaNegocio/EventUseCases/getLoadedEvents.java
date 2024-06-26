package com.example.triptracks.Domain.LogicaNegocio.EventUseCases;

import com.example.triptracks.Domain.Entities.Event;
import com.example.triptracks.Domain.Repository.ItineraryRepository;

import java.util.List;


public class getLoadedEvents {
    private final ItineraryRepository repository;

    public getLoadedEvents(ItineraryRepository repository) {
        this.repository = repository;
    }

    public List<Event> execute() {
       return repository.getLoadedEvents();
    }
}
