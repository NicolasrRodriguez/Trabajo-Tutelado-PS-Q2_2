package com.example.triptracks.Domain.LogicaNegocio.DocUseCases;

import android.net.Uri;

import com.example.triptracks.Domain.Repository.DocumentRepository;

import java.util.function.Consumer;

public class UploadDocument {
    private final DocumentRepository docrepository;
    public UploadDocument(DocumentRepository docrepository) {
        this.docrepository = docrepository;
    }

    public void execute(Uri fileUri, String documentName, String documentDescription, Consumer<String> onSuccess, Consumer<String> onFailure) {
        docrepository.uploadImage( fileUri, documentName,documentDescription,onSuccess,onFailure);
    }
}
