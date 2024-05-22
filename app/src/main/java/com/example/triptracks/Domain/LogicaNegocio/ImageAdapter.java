package com.example.triptracks.Domain.LogicaNegocio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.triptracks.R;

import java.util.List;

public class ImageAdapter  extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<String> images;

    private ItineraryAdapter.OnItemClickListener listener;

    public ImageAdapter(List<String> images , ItineraryAdapter.OnItemClickListener listener) {
        this.images = images;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageView;
        public ImageViewHolder(@NonNull View itemView, final ItineraryAdapter.OnItemClickListener listener) {
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
        return new ImageViewHolder(view , listener);
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
