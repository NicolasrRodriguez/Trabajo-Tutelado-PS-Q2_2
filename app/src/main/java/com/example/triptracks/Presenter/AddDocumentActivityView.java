package com.example.triptracks.Presenter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.triptracks.Data.FirebaseMediaHandler;
import com.example.triptracks.Domain.LogicaNegocio.DocUseCases.UploadDocument;
import com.example.triptracks.R;

import java.util.Locale;

public class AddDocumentActivityView extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etDocumentName;
    private EditText etDocumentDescription;
    private Uri selectedImageUri;
    UploadDocument uploadDocument;
    private FirebaseMediaHandler firebaseMediaHandler;
    private ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_document);
        setLanguage(getLanguageFromPreferences());
        setTitle(R.string.app_name);
        firebaseMediaHandler = new FirebaseMediaHandler();
        uploadDocument = new UploadDocument(firebaseMediaHandler);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        etDocumentName = findViewById(R.id.etDocumentName);
        etDocumentDescription = findViewById(R.id.etDocumentDescription);
        Button btnAddDocument = findViewById(R.id.btnAddDocument);
        progressBar = findViewById(R.id.progressBar);

        btnAddDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String documentName = etDocumentName.getText().toString().trim();
                String documentDescription = etDocumentDescription.getText().toString().trim();
                if (documentName.isEmpty()) {
                    Toast.makeText(AddDocumentActivityView.this, R.string.please_enter_document_name, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedImageUri == null) {
                    Toast.makeText(AddDocumentActivityView.this, R.string.please_select_an_image, Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                uploadDocument.execute(selectedImageUri, documentName, documentDescription,
                        successMessage -> {
                            progressBar.setVisibility(View.GONE);;
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("documentName", documentName);
                            resultIntent.putExtra("documentDescription", documentDescription);
                            resultIntent.putExtra("imageUrl", selectedImageUri);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        },
                        errorMessage -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(AddDocumentActivityView.this, errorMessage, Toast.LENGTH_SHORT).show();
                        });

            }
        });

        Button btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
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

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(galleryIntent, getString(R.string.select_picture)), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
