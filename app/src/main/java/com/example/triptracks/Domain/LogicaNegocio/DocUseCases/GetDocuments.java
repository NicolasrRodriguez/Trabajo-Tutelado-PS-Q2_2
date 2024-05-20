package com.example.triptracks.Domain.LogicaNegocio.DocUseCases;



import com.example.triptracks.Domain.Entities.Document;
import com.example.triptracks.Domain.Repository.DocumentRepository;
import java.util.List;
import java.util.function.Consumer;

public class GetDocuments {
    private final DocumentRepository docrepository;
    public GetDocuments(DocumentRepository docrepository) {
        this.docrepository = docrepository;
    }

    public void execute(Consumer<List<Document>> onSuccess, Consumer<String> onFailure) {
        docrepository.getDocuments( onSuccess, onFailure);
    }
}
