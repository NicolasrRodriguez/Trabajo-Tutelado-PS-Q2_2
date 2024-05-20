package com.example.triptracks.Domain.LogicaNegocio;

import com.example.triptracks.Domain.Entities.Document;
import com.example.triptracks.Domain.Repository.DocumentRepository;
import java.util.function.Consumer;

public class GetDocDetails {
    private final DocumentRepository docrepository;
    public GetDocDetails(DocumentRepository docrepository) {
        this.docrepository = docrepository;
    }

    public void execute(String documentId, Consumer<Document> onSuccess, Consumer<String> onFailure) {
        docrepository.getDocumentDetails( documentId, onSuccess,onFailure);
    }
}
