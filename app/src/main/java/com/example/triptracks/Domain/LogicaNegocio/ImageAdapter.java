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
import com.example.triptracks.Datos.FirebaseImages;
import com.example.triptracks.Domain.Entities.Imagen;
import com.example.triptracks.R;

import java.util.List;

public class ImageAdapter  extends RecyclerView.Adapter<ImageAdapter.DocumentViewHolder> {


    private Context context;
    private List<Imagen> images;
    private FirebaseImages firebaseImages;
    private OnDocumentDeletedListener onImageDeletedListener;

    public ImageAdapter(Context context, List<Imagen> images) {
        this.context = context;
        this.images = images;
        this.firebaseImages = new FirebaseImages();
    }

    public void setOnImageDeletedListenerDeletedListener(OnDocumentDeletedListener listener) {
        this.onImageDeletedListener = listener;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Imagen imagen = images.get(position);

        if (imagen != null) {
            if (imagen.getImageUrl() != null && !imagen.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(imagen.getImageUrl())
                        .into(holder.imageView);

                holder.imageView.setTag(R.id.image, imagen.getImageUrl());
                holder.imageView.setOnClickListener(v -> {
                    String imageUrl = (String) v.getTag(R.id.image);
                    showImageDialog(imageUrl);
                });
                holder.imageView.setOnLongClickListener(v -> {
                    String imageUrl = (String) v.getTag(R.id.image);

                    if (imageUrl != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(R.string.eliminar_documento);
                        builder.setMessage(R.string.est_seguro_de_que_desea_eliminar_este_documento);
                        builder.setPositiveButton(R.string.si, (dialog, which) -> {
                            firebaseImages.deleteDocument(imageUrl,
                                    onSuccess -> {
                                        Log.d("Firebase", onSuccess);
                                        int currentPosition = holder.getAdapterPosition();
                                        images.remove(currentPosition);
                                        notifyItemRemoved(currentPosition);
                                        notifyItemRangeChanged(currentPosition, getItemCount());
                                        if (onImageDeletedListener != null) {
                                            onImageDeletedListener.onDocumentDeleted();
                                        }
                                    },
                                    onFailure -> Log.e("Firebase", onFailure));
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
                holder.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.background));
            }
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    private void showImageDialog(String imageUrl) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_image);

        ImageView imageView = dialog.findViewById(R.id.imageView);

        firebaseImages.getDocumentDetails(documentId,
                document -> {
                    Glide.with(context)
                            .load(imageUrl)
                            .into(imageView);
                },
                error -> {
                    Log.e("Firebase", "Error fetching document details: " + error);
                });

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
            imageView = itemView.findViewById(R.id.image);
        }
    }

    public interface OnDocumentDeletedListener {
        void onDocumentDeleted();
    }
}