package com.example.triptracks.Domain.LogicaNegocio;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.triptracks.Datos.FirebaseImages;
import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.R;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter  extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<String> images;

    private ImagesOnclick imagesOnclick;

    private FirebaseImages firebaseImages = new FirebaseImages();

    public ImageAdapter(List<String> images , ImagesOnclick imagesOnclick) {
        this.images = images;
        this.imagesOnclick= imagesOnclick;
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

            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(imagesOnclick != null) {
                        if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                            imagesOnclick.onItemLongClick(getAdapterPosition());
                        }
                    }
                    return true;
                }
            });
        }

        public ImageView getImageView() {
            return imageView;
        }
    }



    public void removeelem(int pos){
        Log.d("_IMGRCLY","Clickado en posicion");
        String url = images.get(pos);
        images.remove(pos);
        notifyItemRemoved(pos);
        firebaseImages.removeImage(url);

    }

    public void addElement(String url){
        int startIndex = images.size();
        images.add(url);
        notifyItemInserted(startIndex);

    }

    public String  dataAt(int pos){
        return  this.images.get(pos);
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
