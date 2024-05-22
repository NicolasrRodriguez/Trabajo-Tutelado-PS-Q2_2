package com.example.triptracks.Presenter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresExtension;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.triptracks.Datos.FirebaseAuthData;
import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.LogicaNegocio.ImageAdapter;
import com.example.triptracks.Domain.LogicaNegocio.ImageLogic;
import com.example.triptracks.Domain.LogicaNegocio.ImagesOnclick;
import com.example.triptracks.R;
import com.example.triptracks.databinding.ActivityImagesViewBinding;

import java.util.ArrayList;

public class ImagesActivityVIew extends AppCompatActivity implements ImagesOnclick {

    ActivityImagesViewBinding binding;


    private Itinerary itinerary;


    RecyclerView imagesRecyclerView;


    ImageAdapter imageAdapter;


    private final ImageLogic imageLogic = new ImageLogic();

    public static int selectedPosition = RecyclerView.NO_POSITION;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("_IMAGETAG", "Arranco imagenesView");
        super.onCreate(savedInstanceState);
        binding = ActivityImagesViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        itinerary = getIntent().getParcelableExtra(ItneraryActivityView.KEY_ITINERARY);
        assert itinerary != null;
        if (itinerary.getImageUris() != null) {
            Log.d("_IMAGETAG", "Voy a crear el adapter ");
            for (String iamge : itinerary.getImageUris()) {
                Log.d("_IMAGETAG", "URL:" + iamge);
            }
            imageAdapter = new ImageAdapter(itinerary.getImageUris(), this);

        }
        else {
            Log.d("_IMAGETAG", "Aun no hay imagenes");
            imageAdapter = new ImageAdapter(new ArrayList<>(), this);
        }

        imageLogic.setAdapter(imageAdapter);
        imagesRecyclerView = findViewById(R.id.images_list);
        Log.d("_IMAGETAG", "Voy a asignar el adapter el adapter");
        imagesRecyclerView.setAdapter(imageAdapter);
        imagesRecyclerView.setLayoutManager(linearLayoutManager);




    }

    ActivityResultLauncher<Intent> myStartActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                assert result.getData() != null;
                Uri iamgeuri = result.getData().getData();
                assert iamgeuri != null;
                Log.d("_IMGTAG",iamgeuri.toString());
                imageLogic.uploadImage(iamgeuri,itinerary);


            }
    );

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.images_act_menu, menu);
        return true;
    }
    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.subirFoto) {
            Intent resultImageIntent = new Intent(MediaStore.ACTION_PICK_IMAGES);
            myStartActivityForResult.launch(resultImageIntent);

        } else if (id == R.id.Volver) {
            Intent resultIntent = new Intent();
            this.setResult(ItineraryDetailActivity.RESULT_OK, resultIntent);
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(int position) {
        Log.d("_IMGRCLY","Clickado en posicion" + position);//enseñar imagen completa con un dialogo
        showImageDialog(imageAdapter.dataAt(position));

    }

    @Override
    public void onItemLongClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.eliminar_imagen);
        builder.setMessage(R.string.est_seguro_de_que_desea_eliminar_esta_imagen);
        builder.setPositiveButton(R.string.str_but_OK, (dialog, which) -> {
           //elimianr imagen en la posición position del view y de las bases de datos
            imageAdapter.removeelem(position);
        });
        builder.setNegativeButton(R.string.str_cancelar, null);
        builder.show();
    }

    private void showImageDialog(String imageUrl) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.full_image_dialog);

        ImageView imageView = dialog.findViewById(R.id.imageView);

        Glide.with(this)
                            .load(imageUrl)
                            .into(imageView);


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
}