package com.example.triptracks.Presenter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triptracks.Data.FirebaseMediaHandler;
import com.example.triptracks.Domain.Entities.Document;
import com.example.triptracks.Domain.LogicaNegocio.Adapter.DocumentAdapter;
import com.example.triptracks.Domain.LogicaNegocio.DocUseCases.GetDocuments;
import com.example.triptracks.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class DocActivityView extends AppCompatActivity {

    private static final int ADD_DOCUMENT_REQUEST = 1;

    private RecyclerView documentRecyclerView;
    private DocumentAdapter documentAdapter;
    private ArrayList<Document> documentList;
    private FirebaseUser user;
    private FirebaseMediaHandler firebaseMediaHandler;
    GetDocuments getDocuments;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc);
        setLanguage(getLanguageFromPreferences());
        setTitle(R.string.app_name);
        documentRecyclerView = findViewById(R.id.documents_list);
        documentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        documentList = new ArrayList<>();
        documentAdapter = new DocumentAdapter(this, documentList);
        documentRecyclerView.setAdapter(documentAdapter);
        documentAdapter.setOnDocumentDeletedListener(this::refreshDocuments);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseMediaHandler = new FirebaseMediaHandler();
        getDocuments = new GetDocuments(firebaseMediaHandler);
        refreshDocuments();
    }

    private String getLanguageFromPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("language_preference", "");
    }

    private void setLanguage(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = getResources().getConfiguration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshDocuments() {
        Log.d("DocActivityView", "Loading documents from Firebase Realtime Database");
        if (user != null) {

            getDocuments.execute(
                    documents -> {
                        Log.d("DocActivityView", "Received documents: " + documents.size());
                        documentList.clear();
                        Collections.sort(documents, Comparator.comparingLong(Document::getTimestamp));
                        documentList.addAll(documents);
                        documentAdapter.notifyDataSetChanged();
                    },
                    errorMessage -> {
                        Log.e("DocActivityView", "Error loading documents: " + errorMessage);
                    }
            );
        } else {
            Log.e("DocActivityView", "User is null");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_DOCUMENT_REQUEST && resultCode == RESULT_OK && data != null) {
            String documentName = data.getStringExtra("documentName");
            String documentDescription = data.getStringExtra("documentDescription");
            String imageUrl = data.getStringExtra("imageUrl");
            long timestamp = System.currentTimeMillis();
            Document document = new Document("", documentName, documentDescription, imageUrl, timestamp);
            addDocument(document);
            refreshDocuments();
            Toast.makeText(this, getString(R.string.document_added_successfully), Toast.LENGTH_SHORT).show();
        }
    }

    private void addDocument(Document document) {
        documentList.add(0, document);
        documentAdapter.notifyItemInserted(0);
        documentRecyclerView.post(() -> {
            documentRecyclerView.getLayoutManager().scrollToPosition(0);
            documentRecyclerView.post(() -> {
                View firstChild =  documentRecyclerView.getLayoutManager().findViewByPosition(0);
                if (firstChild != null) {
                    int offset = (documentRecyclerView.getHeight() - firstChild.getHeight()) / 2;
                    documentRecyclerView.getLayoutManager().scrollToPosition(0);
                }
            });
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.documentation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_document_op) {
            Intent intent = new Intent(DocActivityView.this, AddDocumentActivityView.class);
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
