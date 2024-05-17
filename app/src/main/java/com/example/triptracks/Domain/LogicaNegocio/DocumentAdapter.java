package com.example.triptracks.Domain.LogicaNegocio;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.triptracks.Datos.FirebaseMediaHandler;
import com.example.triptracks.Domain.Entities.Document;
import com.example.triptracks.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class DocumentAdapter extends ArrayAdapter<Document> implements View.OnClickListener,View.OnLongClickListener{

    private Context context;

    public DocumentAdapter(Context context, List<Document> documents) {
        super(context, 0, documents);
        this.context = context;
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

        if (document != null) {
            if (document.getImageUrl() != null && !document.getImageUrl().isEmpty()) {

                Glide.with(context)
                        .load(document.getImageUrl())
                        .into(imageView);
                imageView.setTag(document.getImageUrl());
                imageView.setOnClickListener(this);
                imageView.setClickable(true);
                imageView.setOnLongClickListener(this);
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.placeholder_image));
            }
            nameTextView.setText(document.getName());
            descriptionTextView.setText(document.getDescription());
        }




        return convertView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.document_image) {
            String imageUrl = (String) v.getTag();
            showImageDialog(imageUrl);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.document_image) {
            String imageUrl = (String) v.getTag();

            // Verificar si el documento no es nulo
            if (imageUrl != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Eliminar documento");
                builder.setMessage("¿Está seguro de que desea eliminar este documento?");
                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseMediaHandler firebaseMediaHandler = new FirebaseMediaHandler();

                        firebaseMediaHandler.deleteDocument(imageUrl,
                                onSuccess -> {

                                    Log.d("Firebase", onSuccess);
                                    notifyDataSetChanged();
                                },
                                onFailure -> {
                                    Log.e("Firebase", onFailure);
                                });
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();

                return true;
            }
        }
        return false;
    }


    private void showImageDialog(String imageUrl) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_image);

        ImageView imageView = dialog.findViewById(R.id.imageView);
        Glide.with(context)
                .load(imageUrl)
                .into(imageView);

        ImageView btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setTag(dialog); // Asignar el diálogo al botón de cierre
        btnClose.setOnClickListener(this::onCloseButtonClick);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Cambiar a WRAP_CONTENT para que se ajuste al tamaño de la imagen
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
