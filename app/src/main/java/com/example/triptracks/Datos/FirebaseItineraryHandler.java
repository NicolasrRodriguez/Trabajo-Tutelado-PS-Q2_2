package com.example.triptracks.Datos;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.triptracks.Domain.Entities.Event;
import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.Repository.ItineraryRepository;
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
import java.util.function.Consumer;

public class FirebaseItineraryHandler implements ItineraryRepository {
    private DatabaseReference ref;
    private ArrayList<Itinerary> mItineraryList;
    private List<Event> loadedEvents = new ArrayList<>();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private FirebaseItineraryHandler() {}

    public FirebaseItineraryHandler(Consumer<ArrayList<Itinerary>> onItinerariesUpdated) {
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


    @Override
    public void saveItinerary(Itinerary itinerary,OperationCallback callback) {
        String key = ref.push().getKey();
        if (key == null) return;
        itinerary.setId(key);
        ref.child(key).setValue(itinerary)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }


    @Override
    public void updateItinerary(Itinerary itinerary,OperationCallback callback) {
        for (String colaborator: itinerary.getColaborators()) {
            String colaboratorPath = colaborator.replace(".", ",");
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(colaboratorPath);

            DatabaseReference itineraryRef = userRef.child("itineraries").child(itinerary.getId());


            itineraryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Itinerary currentItinerary = dataSnapshot.getValue(Itinerary.class);
                    if (currentItinerary != null) {
                        boolean shouldUpdate = !currentItinerary.getCountry().equals(itinerary.getCountry()) ||
                                !currentItinerary.getState().equals(itinerary.getState()) || !currentItinerary.getCity().equals(itinerary.getCity() );


                        if (shouldUpdate) {
                            updateItineraryFields(itineraryRef, itinerary);
                            DatabaseReference eventsRef = itineraryRef.child("events");
                            eventsRef.removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        updateItineraryFields(itineraryRef, itinerary);
                                        callback.onSuccess();
                                    })
                                    .addOnFailureListener(callback::onFailure);
                        }else{
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
        updates.put("admin",itinerary.getAdmin());
        updates.put("colaborators", itinerary.getColaborators());
        updates.put("ImageUris" , itinerary.getImagesuris());
        updates.put("startDate", itinerary.getStartDate());
        updates.put("endDate", itinerary.getEndDate());

        itineraryRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Itinerario actualizado correctamente"))
                .addOnFailureListener(e -> Log.e("Firebase", "Fallo al actualizar el itinerario", e));
    }


    @Override
    public void deleteItinerary(Itinerary itinerary,OperationCallback callback) {
        if (itinerary.getId() != null) {
            ref.child(itinerary.getId()).removeValue()
                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                    .addOnFailureListener(callback::onFailure);
        }
    }


    @Override
    public void shareItinerary(Itinerary itinerary , String Target,OperationCallback callback){
        if (itinerary.getId() != null) {
                //acutaliza la propia base de datos
                ref.child(itinerary.getId()).setValue(itinerary)
                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                        .addOnFailureListener(callback::onFailure);

                for (Event event : getLoadedEvents() ) {
                    saveEvent(event, ref.child(itinerary.getId()).child("events"),event.getId());
                }


                //crea el itinerario para el usuario Target
                String targetUserPath = Target.replace(".", ",");
                DatabaseReference Targetref = FirebaseDatabase.getInstance().getReference("users")
                        .child(targetUserPath).child("itineraries");
                Log.d("Firebase", "Compartierndo Itinerario con " + Target + "desde:" + itinerary.getAdmin());

                Targetref.child(itinerary.getId()).setValue(itinerary)
                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                        .addOnFailureListener(callback::onFailure);

                for (Event event : getLoadedEvents()) {
                    saveEvent(event,Targetref.child(itinerary.getId()).child("events"), event.getId());
                }

        }
    }


    @Override
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

    @Override
    public List<Event> getLoadedEvents() {
        return new ArrayList<>(loadedEvents);
    }


    @Override
    public void deleteOneEvent(String itineraryId, String eventId,OperationCallback callback) {
        if (itineraryId == null || eventId == null) {
            Log.e("Firebase", "Error: Itinerary ID or Event ID is null.");
            return;
        }

        DatabaseReference eventRef = ref.child(itineraryId).child("events").child(eventId);
        eventRef.removeValue()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);


    }

    @Override
    public void updateEvent(Itinerary itinerary , Event event,OperationCallback callback) {

        if (event.getId() != null) {
            for (String colaborator : itinerary.getColaborators()) {
                String colaboratorPath = colaborator.replace(".", ",");
                DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("users")
                        .child(colaboratorPath).child("itineraries").child(itinerary.getId()).child("events").child(event.getId());
                eventRef.setValue(event)
                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                        .addOnFailureListener(callback::onFailure);
            }
        } else {
            Log.e("Firebase", "Error: El evento no tiene ID y no puede ser actualizado.");
        }
    }


    public void saveEvent( Event event , DatabaseReference eventsRef , String key) {

        if (key == null) return;
        eventsRef.child(key).setValue(event);
    }

    @Override
    public void deleteAllEvents(String itineraryId,OperationCallback callback) {
        DatabaseReference eventsRef = ref.child(itineraryId).child("events");
        eventsRef.removeValue()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public String setId(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").
                child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",")).child("itineraries");
        return databaseReference.push().getKey();
    }

}