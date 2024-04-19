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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class ItineraryHandler {
    private DatabaseReference ref;
    private ArrayList<Itinerary> mItineraryList;
    private List<Event> loadedEvents = new ArrayList<>();

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

            DatabaseReference itineraryRef = ref.child(itinerary.getId());

            itineraryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Itinerary currentItinerary = dataSnapshot.getValue(Itinerary.class);

                    if (currentItinerary != null) {
                        if (!currentItinerary.getCountry().equals(itinerary.getCountry()) ||
                                !currentItinerary.getState().equals(itinerary.getState())|| !currentItinerary.getCity().equals(itinerary.getCity())) {
                            DatabaseReference eventsRef = itineraryRef.child("events");
                            eventsRef.removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firebase", "Eventos eliminados correctamente");
                                        updateItineraryFields(itineraryRef, itinerary);
                                    })
                                    .addOnFailureListener(e -> Log.e("Firebase", "Fallo al eliminar eventos", e));
                        } else {
                            updateItineraryFields(itineraryRef, itinerary);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase", "Fallo al obtener el itinerario actual", databaseError.toException());
                }
            });
        }
    }

    private void updateItineraryFields(DatabaseReference itineraryRef, Itinerary itinerary) {

        Map<String, Object> updates = new HashMap<>();
        updates.put("id", itinerary.getId());
        updates.put("itineraryTitle", itinerary.getItineraryTitle());
        updates.put("country", itinerary.getCountry());
        updates.put("state", itinerary.getState());
        updates.put("city", itinerary.getCity());
        updates.put("startDate", itinerary.getStartDate());
        updates.put("endDate", itinerary.getEndDate());

        itineraryRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Itinerario actualizado correctamente"))
                .addOnFailureListener(e -> Log.e("Firebase", "Fallo al actualizar el itinerario", e));
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

    public void loadEvents(String itineraryId, Consumer<List<Event>> callback) {
        DatabaseReference eventsRef = ref.child(itineraryId).child("events");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadedEvents.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);
                    if (event != null) {
                        loadedEvents.add(event);
                    }
                }
                callback.accept(new ArrayList<>(loadedEvents));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Firebase", "loadEvents:onCancelled", databaseError.toException());
            }
        };
        eventsRef.addValueEventListener(eventListener);
    }

    public List<Event> getLoadedEvents() {
        return new ArrayList<>(loadedEvents);
    }

    public void deleteEvent(String itineraryId, String eventId) {
        if (itineraryId == null || eventId == null) {
            Log.e("Firebase", "Error: Itinerary ID or Event ID is null.");
            return;
        }

        DatabaseReference eventRef = ref.child(itineraryId).child("events").child(eventId);
        eventRef.removeValue()
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Evento eliminado correctamente."))
                .addOnFailureListener(e -> Log.e("Firebase", "Error al eliminar el evento.", e));


    }

    public void updateEvent(String itineraryId, Event event) {
        if (event.getId() != null) {
            DatabaseReference eventRef = ref.child(itineraryId).child("events").child(event.getId());
            eventRef.setValue(event)
                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Evento actualizado correctamente."))
                    .addOnFailureListener(e -> Log.e("Firebase", "Error al actualizar el evento.", e));
        } else {
            Log.e("Firebase", "Error: El evento no tiene ID y no puede ser actualizado.");
        }
    }


    public void saveEvent(String itineraryId, Event event) {
        DatabaseReference eventsRef = ref.child(itineraryId).child("Events");
        String eventId = eventsRef.push().getKey();
        if (eventId == null) return;
        eventsRef.child(eventId).setValue(event);
    }

    public void deleteEvents(String itineraryId) {
        DatabaseReference eventsRef = ref.child(itineraryId).child("events");
        eventsRef.removeValue()
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Eventos eliminados correctamente"))
                .addOnFailureListener(e -> Log.e("Firebase", "Fallo al eliminar eventos", e));
    }

}