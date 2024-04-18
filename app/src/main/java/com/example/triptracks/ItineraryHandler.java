package com.example.triptracks;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public class ItineraryHandler {
    private DatabaseReference ref;
    private ArrayList<Itinerary> mItineraryList;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private ItineraryHandler() {}

    public ItineraryHandler(Consumer<ArrayList<Itinerary>> onItinerariesUpdated) {
        if (user != null) {
            String userPath = user.getEmail().replace(".", ",");
            ref = FirebaseDatabase.getInstance().getReference("users")
                    .child(userPath).child("itineraries");
            this.mItineraryList = new ArrayList<>();

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mItineraryList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Itinerary itinerary = snapshot.getValue(Itinerary.class);
                        if (itinerary != null) {
                            mItineraryList.add(itinerary);
                        }
                    }
                    onItinerariesUpdated.accept(mItineraryList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("Firebase", "Fallo al leer el valor.", databaseError.toException());
                }
            });
        } else {
            Log.e("FirebaseAuth", "User no loggeado.");
        }
    }


    public void saveItinerary(Itinerary itinerary) {
        String key = ref.push().getKey();
        if (key == null) return;
        itinerary.setId(key);
        ref.child(key).setValue(itinerary)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Itinerario guardado por: " + itinerary.getAdmin()))
                .addOnFailureListener(e -> Log.e("Firebase", "Fallo al guardar el itinerario", e));
    }

    public void updateItinerary(Itinerary itinerary) {
        if (itinerary.getId() != null) {
            ArrayList<String> colaborators = itinerary.getColaborators();
            for (String colaborator : colaborators) {
                if(Objects.equals(colaborator, user.getEmail())){
                    ref.child(itinerary.getId()).setValue(itinerary)
                            .addOnSuccessListener(aVoid -> Log.d("Firebase", "Itinerario actualizado"))
                            .addOnFailureListener(e -> Log.e("Firebase", "Fallo al actualizar el itinerario", e));
                }
                else{
                    String targetUserPath = colaborator.replace(".", ",");
                    DatabaseReference Targetref = FirebaseDatabase.getInstance().getReference("users")
                            .child(targetUserPath).child("itineraries");
                    Log.d("Firebase", "Actualizando Itinerario de " + colaborator);
                    Targetref.child(itinerary.getId()).setValue(itinerary)
                            .addOnSuccessListener(aVoid -> Log.d("Firebase", "Itinerario de " + colaborator + "actualizado" ))
                            .addOnFailureListener(e -> Log.e("Firebase", "Fallo al acutaliazar el itinerario", e));
                }


            }
        }

    }

    public void deleteItinerary(Itinerary itinerary) {
        if (itinerary.getId() != null) {
            ref.child(itinerary.getId()).removeValue()
                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Itinerario borrado"))
                    .addOnFailureListener(e -> Log.e("Firebase", "Fallo al borrar el itinerario", e));
        }
    }

    public void shareItinerary(Itinerary itinerary , String Target){
        if (itinerary.getId() != null) {
                //acutaliza la propia base de datos
                ref.child(itinerary.getId()).setValue(itinerary)
                        .addOnSuccessListener(aVoid -> Log.d("Firebase", "Itinerario actualizado"))
                        .addOnFailureListener(e -> Log.e("Firebase", "Fallo al actualizar el itinerario", e));
                //crea el itinerario para el usuario Target
                String targetUserPath = Target.replace(".", ",");
                DatabaseReference Targetref = FirebaseDatabase.getInstance().getReference("users")
                        .child(targetUserPath).child("itineraries");
                Log.d("Firebase", "Compartierndo Itinerario con " + Target + "desde:" + itinerary.getAdmin());
                String key = Targetref.push().getKey();
                Targetref.child(itinerary.getId()).setValue(itinerary)
                        .addOnSuccessListener(aVoid -> Log.d("Firebase", "Itinerario compartido con " + Target ))
                        .addOnFailureListener(e -> Log.e("Firebase", "Fallo al compartir el itinerario", e));

        }
    }
}