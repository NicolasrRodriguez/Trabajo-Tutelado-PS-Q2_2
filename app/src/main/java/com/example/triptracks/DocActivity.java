package com.example.triptracks;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.triptracks.Datos.FirebaseMediaHandler;
import com.example.triptracks.Domain.Entities.Document;
import com.example.triptracks.Domain.LogicaNegocio.DocumentAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DocActivity extends AppCompatActivity {

    private static final int ADD_DOCUMENT_REQUEST = 1;

    private ListView documentListView;
    private DocumentAdapter documentAdapter;
    private ArrayList<Document> documentList;
    private FirebaseUser user;
    private FirebaseMediaHandler firebaseMediaHandler;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc);
        documentListView = findViewById(R.id.documents_list);
        documentList = new ArrayList<>();
        documentAdapter = new DocumentAdapter(this, documentList);
        documentListView.setAdapter(documentAdapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseMediaHandler = new FirebaseMediaHandler();
        refreshDocuments();
    }

    private void refreshDocuments() {
        Log.d("DocActivity", "Loading documents from Firebase Storage");
        if (user != null) {
            firebaseMediaHandler.getImagesFromDocumentsFolder(imageUrls -> {
                Log.d("DocActivity", "Received image URLs: " + imageUrls.size());
                documentList.clear();
                for (String imageUrl : imageUrls) {
                    Document document = new Document("","", "", imageUrl);
                    documentList.add(document);
                }
                documentAdapter.notifyDataSetChanged();
            }, errorMessage -> {
                Log.e("DocActivity", "Error loading documents: " + errorMessage);
                Toast.makeText(DocActivity.this, "Error loading documents: " + errorMessage, Toast.LENGTH_SHORT).show();
            });
        } else {
            Log.e("DocActivity", "User is null");
            Toast.makeText(DocActivity.this, "User is null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_DOCUMENT_REQUEST && resultCode == RESULT_OK && data != null) {
            String documentName = data.getStringExtra("documentName");
            String documentDescription = data.getStringExtra("documentDescription");
            String documentUri = data.getStringExtra("documentUri");
            Document document = new Document("",documentName, documentDescription, documentUri);
            documentList.add(document);
            documentAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Document added successfully", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.documentation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_document_op) {
            Intent intent = new Intent(DocActivity.this, AddDocumentActivity.class);
            startActivityForResult(intent, ADD_DOCUMENT_REQUEST);
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


}
