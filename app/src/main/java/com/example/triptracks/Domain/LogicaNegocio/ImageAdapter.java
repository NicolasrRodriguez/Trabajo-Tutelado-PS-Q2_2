package com.example.triptracks.Domain.LogicaNegocio;

import android.app.Dialog;
import android.content.Context;
import android.media.Image;
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

public class ImageAdapter  extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<Imagen> images;

    public static class ImageViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image);
        }

        public ImageView getImageView() {
            return imageView;
        }
    }


    @NonNull
    @Override
    public ImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image,parent,false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ImageViewHolder holder, int position) {
        holder.getImageView().setImageURI(images.get(position).getImageUrl());
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
