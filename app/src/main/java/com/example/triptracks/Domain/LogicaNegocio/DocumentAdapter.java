package com.example.triptracks.Domain.LogicaNegocio;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.triptracks.Datos.FirebaseMediaHandler;
import com.example.triptracks.Domain.Entities.Document;
import com.example.triptracks.R;

import java.util.List;

public class DocumentAdapter extends ArrayAdapter<Document> implements View.OnClickListener, View.OnLongClickListener {

    private Context context;
    private FirebaseMediaHandler firebaseMediaHandler;

    public DocumentAdapter(Context context, List<Document> documents) {
        super(context, 0, documents);
        this.context = context;
        this.firebaseMediaHandler = new FirebaseMediaHandler();
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Document document = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_document, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.document_image);
        TextView nameTextView = convertView.findViewById(R.id.document_name);
        TextView descriptionTextView = convertView.findViewById(R.id.document_description);
        nameTextView.setVisibility(View.GONE);
        descriptionTextView.setVisibility(View.GONE);

        if (document != null) {
            if (document.getImageUrl() != null && !document.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(document.getImageUrl())
                        .into(imageView);
                imageView.setTag(R.id.document_image, document.getImageUrl());
                imageView.setTag(R.id.document_id, document.getDocumentId());
                imageView.setTag(R.id.document_name, nameTextView);
                imageView.setTag(R.id.document_description, descriptionTextView);
                imageView.setOnClickListener(this);
                imageView.setClickable(true);
                imageView.setOnLongClickListener(this);

                firebaseMediaHandler.getDocumentDetails(document.getDocumentId(),
                        doc -> {
                            TextView nameView = (TextView) imageView.getTag(R.id.document_name);
                            TextView descView = (TextView) imageView.getTag(R.id.document_description);
                            nameView.setText(doc.getName());
                            Log.e("Firebase", "Text " + nameView.getText().toString());
                            descView.setText(doc.getDescription());
                        },
                        error -> {
                            Log.e("Firebase", "Error fetching document details: " + error);
                            nameTextView.setText("Error");
                            descriptionTextView.setText("");
                        });
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.placeholder_image));
            }
        }

        return convertView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.document_image) {
            String imageUrl = (String) v.getTag(R.id.document_image);
            String documentId = (String) v.getTag(R.id.document_id);
            TextView nameTextView = (TextView) v.getTag(R.id.document_name);
            TextView descriptionTextView = (TextView) v.getTag(R.id.document_description);
            showImageDialog(documentId, imageUrl, nameTextView.getText().toString(), descriptionTextView.getText().toString());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.document_image) {
            String imageUrl = (String) v.getTag(R.id.document_image);

            if (imageUrl != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Eliminar documento");
                builder.setMessage("¿Está seguro de que desea eliminar este documento?");
                builder.setPositiveButton("Sí", (dialog, which) -> {
                    firebaseMediaHandler.deleteDocument(imageUrl,
                            onSuccess -> {
                                Log.d("Firebase", onSuccess);
                                notifyDataSetChanged();
                            },
                            onFailure -> Log.e("Firebase", onFailure));
                });
                builder.setNegativeButton("No", null);
                builder.show();

                return true;
            }
        }
        return false;
    }

    private void showImageDialog(String documentId, String imageUrl, String documentName, String documentDescription) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_image);

        ImageView imageView = dialog.findViewById(R.id.imageView);
        TextView titleTextView = dialog.findViewById(R.id.titleTextView);
        TextView descriptionTextView = dialog.findViewById(R.id.descriptionTextView);

        Glide.with(context)
                .load(imageUrl)
                .into(imageView);
        titleTextView.setText(documentName);
        descriptionTextView.setText(documentDescription);

        ImageView btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setTag(dialog);
        btnClose.setOnClickListener(this::onCloseButtonClick);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);
        dialog.show();
    }

    public void onCloseButtonClick(View view) {
        if (view.getId() == R.id.btnClose) {
            Dialog dialog = (Dialog) view.getTag();
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }
}
