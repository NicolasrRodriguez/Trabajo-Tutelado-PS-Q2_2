package com.example.triptracks.Domain.LogicaNegocio.DocUseCases;


import com.example.triptracks.Domain.Repository.DocumentRepository;
import java.util.function.Consumer;

public class DeleteDocument {
    private final DocumentRepository docrepository;
    public DeleteDocument(DocumentRepository docrepository) {
        this.docrepository = docrepository;
    }

    public void execute(String imageUrl, Consumer<String> onSuccess, Consumer<String> onFailure) {
        docrepository.deleteDocument( imageUrl, onSuccess,onFailure);
    }
}
