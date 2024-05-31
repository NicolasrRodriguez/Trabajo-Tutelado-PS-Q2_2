package com.example.triptracks.Domain.LogicaNegocio.EventUseCases;


import com.example.triptracks.Domain.Repository.ItineraryRepository;

public class DeleteOneEvent{
    private final ItineraryRepository repository;

    public DeleteOneEvent(ItineraryRepository repository) {
        this.repository = repository;
    }

    public void execute(String itineraryId, String eventID, ItineraryRepository.OperationCallback callback) {
        repository.deleteOneEvent(itineraryId,eventID,callback);
    }
}