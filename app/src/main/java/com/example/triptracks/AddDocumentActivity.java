package com.example.triptracks;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.triptracks.Datos.FirebaseMediaHandler;

public class AddDocumentActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etDocumentName;
    private EditText etDocumentDescription;
    private Uri selectedImageUri;
    private FirebaseMediaHandler firebaseMediaHandler;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_document);
        firebaseMediaHandler = new FirebaseMediaHandler();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        etDocumentName = findViewById(R.id.etDocumentName);
        etDocumentDescription = findViewById(R.id.etDocumentDescription);
        Button btnAddDocument = findViewById(R.id.btnAddDocument);

        btnAddDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String documentName = etDocumentName.getText().toString().trim();
                String documentDescription = etDocumentDescription.getText().toString().trim();
                if (documentName.isEmpty()) {
                    Toast.makeText(AddDocumentActivity.this, "Please enter document name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedImageUri == null) {
                    Toast.makeText(AddDocumentActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseMediaHandler.uploadImage(selectedImageUri, documentName, documentDescription,
                        successMessage -> {
                            Toast.makeText(AddDocumentActivity.this, successMessage, Toast.LENGTH_SHORT).show();
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("documentName", documentName);
                            resultIntent.putExtra("documentDescription", documentDescription);
                            resultIntent.putExtra("imageUrl", selectedImageUri);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        },
                        errorMessage -> {
                            Toast.makeText(AddDocumentActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), PICK_IMAGE_REQUEST);
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
