package com.example.triptracks.Domain.LogicaNegocio;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.R;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter  extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> implements View.OnCreateContextMenuListener{

    private List<String> images;

    private ImagesOnclick imagesOnclick;

    public ImageAdapter(List<String> images , ImagesOnclick imagesOnclick) {
        this.images = images;
        this.imagesOnclick= imagesOnclick;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageView;
        public ImageViewHolder(@NonNull View itemView , ImagesOnclick imagesOnclick) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(imagesOnclick != null){
                        if(getAdapterPosition() != RecyclerView.NO_POSITION){
                            imagesOnclick.onItemClick(getAdapterPosition());
                        }
                    }
                }
            });
        }

        public ImageView getImageView() {
            return imageView;
        }
    }

    public void anadirelem() {
        int startIndex = images.size();
        notifyItemChanged(startIndex + 1);
    }


    @NonNull
    @Override
    public ImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image,parent,false);
        return new ImageViewHolder(view, imagesOnclick);
    }


    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ImageViewHolder holder, int position) {
        Glide.with(holder.getImageView()).load(images.get(position)).into(holder.getImageView());

    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}
