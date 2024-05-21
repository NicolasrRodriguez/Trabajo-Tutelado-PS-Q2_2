
package com.example.triptracks.Domain.LogicaNegocio;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Presenter.ItneraryActivityView;
import com.example.triptracks.R;
import com.example.triptracks.databinding.ItineraryTileBinding;

public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.MyViewHolder> {

    private ArrayList<Itinerary> mDataset;
    private OnItemClickListener listener;
    private boolean showFirstButtonsOnly = false;
    private OnContextMenuClickListener contextMenuClickListener;


    public ItineraryAdapter(ArrayList<Itinerary> myDataset, OnItemClickListener listener, OnContextMenuClickListener contextMenuClickListener) {
        this.mDataset = myDataset;
        this.listener = listener;
        this.contextMenuClickListener = contextMenuClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnContextMenuClickListener {
        void onContextMenuClick(int position);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ItineraryTileBinding binding;


        public MyViewHolder(ItineraryTileBinding binding, final OnItemClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;

            binding.itineraryTitle.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
                menu.add(Menu.NONE, R.id.info, Menu.NONE, "Info").setOnMenuItemClickListener(item -> {
                    ItneraryActivityView.selectedPosition = getAdapterPosition();
                    return false;
                });
            });
        }

        public void bind(Itinerary itinerary) {
            binding.element.setText(itinerary.getId());
            binding.itineraryTitle.setText(itinerary.getItineraryTitle());
            binding.itineraryCountry.setText(itinerary.getCountry());
        }
    }

    @NonNull
    @Override
    public ItineraryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItineraryTileBinding binding = ItineraryTileBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Itinerary itinerary = mDataset.get(position);
        holder.bind(itinerary);
        holder.binding.element.setVisibility(View.GONE);
        holder.binding.itineraryTitle.setVisibility(View.VISIBLE);
        holder.binding.itineraryCountry.setVisibility(View.GONE);
        holder.binding.itineraryState.setVisibility(View.GONE);
        holder.binding.itineraryCity.setVisibility(View.GONE);
        if (showFirstButtonsOnly) {
            holder.binding.butBorrar.setVisibility(View.GONE);
            holder.binding.butEdit.setVisibility(View.GONE);
            holder.binding.butOk.setVisibility(View.GONE);
            holder.binding.butVolver.setVisibility(View.GONE);
            holder.binding.layoutmapcontainer.setVisibility(View.GONE);
            holder.binding.layoutcalendarcontainer.setVisibility(View.GONE);

        } else {

            holder.binding.butBorrar.setVisibility(View.VISIBLE);
            holder.binding.butEdit.setVisibility(View.VISIBLE);
            holder.binding.butOk.setVisibility(View.VISIBLE);
            holder.binding.butVolver.setVisibility(View.VISIBLE);
            holder.binding.layoutmapcontainer.setVisibility(View.VISIBLE);
            holder.binding.layoutcalendarcontainer.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void anadirelem(ArrayList<Itinerary> newItneraries) {
        int startIndex = mDataset.size();
        mDataset.addAll(newItneraries);
        notifyItemRangeInserted(startIndex, newItneraries.size());
    }


    public Itinerary getItem(int position) {
        return mDataset.get(position);
    }

    public void removeItem(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    public void eliminar_por_id(String elemento) {
        for (int i = 0; i < mDataset.size(); i++) {
            if (mDataset.get(i).getId().equals(elemento)) {
                mDataset.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, mDataset.size());
                break;
            }
        }
    }

    public void actualizar_por_id(Itinerary itinerario_actualizado) {
        for (int i = 0; i < mDataset.size(); i++) {
            Itinerary itinerary = mDataset.get(i);
            if (itinerary.getId().equals(itinerario_actualizado.getId())) {
                itinerary.setItineraryTitle(itinerario_actualizado.getItineraryTitle());
                itinerary.setCountry(itinerario_actualizado.getCountry());
                itinerary.setState(itinerario_actualizado.getState());
                itinerary.setCity(itinerario_actualizado.getCity());
                itinerary.setAdmin(itinerario_actualizado.getAdmin());
                itinerary.setColaborators(itinerario_actualizado.getColaborators());
                itinerary.setImageUris(itinerario_actualizado.getImageUris());
                notifyItemChanged(i);
                return;
            }
        }
    }

    public void mostrarbotones(boolean showFirstButtonsOnly) {
        this.showFirstButtonsOnly = showFirstButtonsOnly;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(ArrayList<Itinerary> itineraries) {
        mDataset.clear();
        mDataset.addAll(itineraries);
        notifyDataSetChanged();
    }
}
