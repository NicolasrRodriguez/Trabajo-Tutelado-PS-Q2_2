package com.example.triptracks.Domain.LogicaNegocio.EventUseCases;
import com.example.triptracks.Domain.Repository.ItineraryRepository;

public class DeleteAllEvents{
    private final ItineraryRepository repository;

    public DeleteAllEvents(ItineraryRepository repository) {
        this.repository = repository;
    }

    public void execute(String itineraryId, ItineraryRepository.OperationCallback callback) {
        repository.deleteAllEvents(itineraryId, callback);
    }
}