package com.example.triptracks.Domain.LogicaNegocio;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.triptracks.Datos.FirebaseMediaHandler;
import com.example.triptracks.Domain.Entities.Document;
import com.example.triptracks.R;

import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    private Context context;
    private List<Document> documents;
    private FirebaseMediaHandler firebaseMediaHandler;
    private OnDocumentDeletedListener onDocumentDeletedListener;

    public DocumentAdapter(Context context, List<Document> documents) {
        this.context = context;
        this.documents = documents;
        this.firebaseMediaHandler = new FirebaseMediaHandler();
    }

    public void setOnDocumentDeletedListener(OnDocumentDeletedListener listener) {
        this.onDocumentDeletedListener = listener;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_document, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Document document = documents.get(position);

        if (document != null) {
            if (document.getImageUrl() != null && !document.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(document.getImageUrl())
                        .into(holder.imageView);

                holder.imageView.setTag(R.id.document_image, document.getImageUrl());
                holder.imageView.setTag(R.id.document_id, document.getDocumentId());
                holder.imageView.setOnClickListener(v -> {
                    String imageUrl = (String) v.getTag(R.id.document_image);
                    String documentId = (String) v.getTag(R.id.document_id);
                    showImageDialog(documentId, imageUrl);
                });
                holder.imageView.setOnLongClickListener(v -> {
                    String imageUrl = (String) v.getTag(R.id.document_image);

                    if (imageUrl != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(R.string.eliminar_documento);
                        builder.setMessage(R.string.est_seguro_de_que_desea_eliminar_este_documento);
                        builder.setPositiveButton(R.string.si, (dialog, which) -> {
                            DeleteDocument deleteDocumentUseCase = new DeleteDocument(firebaseMediaHandler);
                            deleteDocumentUseCase.execute(
                                    imageUrl,
                                    onSuccess -> {
                                        Log.d("Firebase", onSuccess);
                                        int currentPosition = holder.getAdapterPosition();
                                        documents.remove(currentPosition);
                                        notifyItemRemoved(currentPosition);
                                        notifyItemRangeChanged(currentPosition, getItemCount());
                                        if (onDocumentDeletedListener != null) {
                                            onDocumentDeletedListener.onDocumentDeleted();
                                        }
                                    },
                                    onFailure -> {
                                        Log.e("Firebase", onFailure);
                                    }
                            );

                        });
                        builder.setNegativeButton(R.string.no, null);
                        builder.show();

                        return true;
                    }
                    return false;
                });

                holder.nameTextView.setVisibility(View.GONE);
                holder.descriptionTextView.setVisibility(View.GONE);
            } else {
                holder.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.placeholder_image));
            }
        }
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    private void showImageDialog(String documentId, String imageUrl) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_image);

        ImageView imageView = dialog.findViewById(R.id.imageView);
        TextView titleTextView = dialog.findViewById(R.id.titleTextView);
        TextView descriptionTextView = dialog.findViewById(R.id.descriptionTextView);
        GetDocDetails getDocDetails = new GetDocDetails(firebaseMediaHandler);
        getDocDetails.execute(
                documentId,
                document -> {
                    Glide.with(context)
                            .load(imageUrl)
                            .into(imageView);
                    titleTextView.setText(document.getName());
                    descriptionTextView.setText(document.getDescription());
                },
                error -> {
                    Log.e("Firebase", "Error fetching document details: " + error);
                }
        );

        ImageView btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setTag(dialog);
        btnClose.setOnClickListener(v -> {
            Dialog d = (Dialog) v.getTag();
            if (d != null) {
                d.dismiss();
            }
        });

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);
        dialog.show();
    }

    public static class DocumentViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView descriptionTextView;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.document_image);
            nameTextView = itemView.findViewById(R.id.document_name);
            descriptionTextView = itemView.findViewById(R.id.document_description);
        }
    }

    public interface OnDocumentDeletedListener {
        void onDocumentDeleted();
    }
}
