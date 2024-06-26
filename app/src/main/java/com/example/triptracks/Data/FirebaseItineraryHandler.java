package com.example.triptracks.Data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.triptracks.Domain.Entities.Event;
import com.example.triptracks.Domain.Entities.Itinerary;
import com.example.triptracks.Domain.Repository.ItineraryRepository;
import com.example.triptracks.Presenter.EventDecorator;
import com.example.triptracks.Presenter.ItineraryDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
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

    public FirebaseItineraryHandler(Consumer<ArrayList<Itinerary>> onItinerariesUpdated) {//Recupera los itinerarios de un usuario y los muestra en el RecyclerView
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
                    Log.w("_FIREBASEITN", "Fallo al leer el valor.", databaseError.toException());
                }
            });
        } else {
            Log.e("_FIREBASEITN", "User no loggeado.");
        }
    }


    @Override
    public void saveItinerary(Itinerary itinerary,OperationCallback callback) {//Guarda un nuevo itinerario
        String key = ref.push().getKey();
        if (key == null) return;
        itinerary.setId(key);
        ref.child(key).setValue(itinerary)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }


    @Override
    public void updateItinerary(Itinerary itinerary,OperationCallback callback) {//actualiza un itinerario
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
                                        Log.d("_FIREBASEITN","Campos actualizados");
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
                    Log.e("_FIREBASEITN", "Fallo al obtener el itinerario actual", databaseError.toException());
                }
            });
        }
    }

    private void updateItineraryFields(DatabaseReference itineraryRef, Itinerary itinerary) {

        Log.d("_FIREBASEITN","Actualizo los campos");
        Map<String, Object> updates = new HashMap<>();
        updates.put("id", itinerary.getId());
        updates.put("itineraryTitle", itinerary.getItineraryTitle());
        updates.put("country", itinerary.getCountry());
        updates.put("state", itinerary.getState());
        updates.put("city", itinerary.getCity());
        updates.put("admin",itinerary.getAdmin());
        updates.put("colaborators", itinerary.getColaborators());
        updates.put("imageUris" , itinerary.getImageUris());
        updates.put("startDate", itinerary.getStartDate());
        updates.put("endDate", itinerary.getEndDate());

        itineraryRef.updateChildren(updates)//hacerlo para todos los colaboradores
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Itinerario actualizado correctamente"))
                .addOnFailureListener(e -> Log.e("Firebase", "Fallo al actualizar el itinerario", e));
    }


    @Override
    public void deleteItinerary(Itinerary itinerary,OperationCallback callback) {//Borra un itienrario

        if (itinerary.getId() != null) {
            ArrayList<String> images =itinerary.getImageUris();
            if(images != null){
                Log.d("_FIREBASEITN", "Hay imagenes que borrar");
                for (String url:images) {
                    StorageReference ref =  FirebaseStorage.getInstance().getReferenceFromUrl(url);
                    ref.delete();
                }
            }
            else {
                Log.d("_FIREBASEITN", "NO Hay imagenes que borrar");
            }
            ref.child(itinerary.getId()).removeValue()
                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                    .addOnFailureListener(callback::onFailure);
        }
    }


    @Override
    public void shareItinerary(Itinerary itinerary , String Target,OperationCallback callback){//Comparte un itinerario
        if (itinerary.getId() != null) {
                ref.child(itinerary.getId()).setValue(itinerary)
                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                        .addOnFailureListener(callback::onFailure);

                for (Event event : getLoadedEvents() ) {
                    saveEvent(event, ref.child(itinerary.getId()).child("events"),event.getId());
                }



                String targetUserPath = Target.replace(".", ",");
                DatabaseReference Targetref = FirebaseDatabase.getInstance().getReference("users")
                        .child(targetUserPath).child("itineraries");
                Log.d("_FIREBASEITN", "Compartierndo Itinerario con " + Target + "desde:" + itinerary.getAdmin());

                Targetref.child(itinerary.getId()).setValue(itinerary)
                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                        .addOnFailureListener(callback::onFailure);

                for (Event event : getLoadedEvents()) {
                    saveEvent(event,Targetref.child(itinerary.getId()).child("events"), event.getId());
                }

        }
    }


    @Override
    public void loadEvents(String itineraryId, Consumer<List<Event>> callback) {//Carga los eventos de un itinerario
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
                Log.w("_FIREBASEITN", "loadEvents:onCancelled", databaseError.toException());
            }
        };
        eventsRef.addValueEventListener(eventListener);
    }

    @Override
    public List<Event> getLoadedEvents() {
        return new ArrayList<>(loadedEvents);
    }//Devuelve los eventos de un itinerario



    @Override
    public void updateEvent(Itinerary itinerary , Event event,OperationCallback callback) {//Actualiza un evento

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
            Log.e("_FIREBASEITN", "Error: El evento no tiene ID y no puede ser actualizado.");
        }
    }


    public void saveEvent( Event event , DatabaseReference eventsRef , String key) {//Guarda un nuevo evento

        if (key == null) return;
        eventsRef.child(key).setValue(event);
    }



    public void createEvent(CalendarDay date, String activity, ItineraryDetailActivity it) {//Crea un nuevo evento
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ","))
                .child("itineraries").child(it.itinerary.getId()).child("events");
        String id = databaseReference.push().getKey();
        it.category = it.spinner_evento.getSelectedItem().toString();
        if (id != null) {
            if (it.category.equals("Exploration")) {
                it.category = "Exploración";
            } else if (it.category.equals("Gastronomy")) {
                it.category = "Gastronomía";
            } else if (it.category.equals("Entertainment")) {
                it.category = "Entretenimiento";
            }

            Event event = new Event(id, date.getDate().toString(), it.category, activity);
            databaseReference.child(id).setValue(event)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("_FIREBASEITN", "Evento guardado correctamente.");
                        shareEvent(it.itinerary, event);
                    })
                    .addOnFailureListener(e -> Log.e("_FIREBASEITN", "Error al guardar el evento", e));
            it.eventDecorator = new EventDecorator(it, Collections.singleton(date), it.category);
            it.binding.calendarView.addDecorator(it.eventDecorator);
        }
    }

    private void shareEvent(Itinerary itinerary, Event event) {
        for (String collaborator : itinerary.getColaborators()) {
            String collaboratorPath = collaborator.replace(".", ",");
            DatabaseReference collaboratorRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(collaboratorPath).child("itineraries").child(itinerary.getId()).child("events").child(event.getId());

            collaboratorRef.setValue(event)
                    .addOnSuccessListener(aVoid -> Log.d("_FIREBASEITN", "Evento sincronizado correctamente con " + collaborator))
                    .addOnFailureListener(e -> Log.e("_FIREBASEITN", "Error al sincronizar evento con " + collaborator, e));
        }
    }

    @Override
    public void deleteOneEvent(String itineraryId, String eventId, OperationCallback callback) {
        if (itineraryId == null || eventId == null) {
            Log.e("_FIREBASEITN", "Error: Itinerary ID or Event ID is null.");
            return;
        }

        DatabaseReference eventRef = ref.child(itineraryId).child("events").child(eventId);
        eventRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d("_FIREBASEITN", "Evento eliminado correctamente.");
                    deleteOneEventFromCollaborators(itineraryId, eventId, callback);
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onFailure);
    }

    private void deleteOneEventFromCollaborators(String itineraryId, String eventId, OperationCallback callback) {
        ref.child(itineraryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Itinerary itinerary = dataSnapshot.getValue(Itinerary.class);
                if (itinerary != null) {
                    for (String collaborator : itinerary.getColaborators()) {
                        String collaboratorPath = collaborator.replace(".", ",");
                        DatabaseReference collaboratorEventRef = FirebaseDatabase.getInstance().getReference("users")
                                .child(collaboratorPath).child("itineraries").child(itineraryId).child("events").child(eventId);

                        collaboratorEventRef.removeValue()
                                .addOnSuccessListener(aVoid -> Log.d("_FIREBASEITN", "Evento eliminado correctamente de " + collaborator))
                                .addOnFailureListener(e -> Log.e("_FIREBASEITN", "Error al eliminar evento de " + collaborator, e));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("_FIREBASEITN", "Error al obtener itinerario para eliminar evento de colaboradores", databaseError.toException());
            }
        });
    }

    @Override
    public void deleteAllEvents(String itineraryId, OperationCallback callback) {
        DatabaseReference eventsRef = ref.child(itineraryId).child("events");
        eventsRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d("_FIREBASEITN", "Todos los eventos eliminados correctamente.");
                    deleteAllEventsfromcollaborators(itineraryId, callback);
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onFailure);
    }

    private void deleteAllEventsfromcollaborators(String itineraryId, OperationCallback callback) {
        ref.child(itineraryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Itinerary itinerary = dataSnapshot.getValue(Itinerary.class);
                if (itinerary != null) {
                    for (String collaborator : itinerary.getColaborators()) {
                        String collaboratorPath = collaborator.replace(".", ",");
                        DatabaseReference collaboratorEventsRef = FirebaseDatabase.getInstance().getReference("users")
                                .child(collaboratorPath).child("itineraries").child(itineraryId).child("events");

                        collaboratorEventsRef.removeValue()
                                .addOnSuccessListener(aVoid -> Log.d("_FIREBASEITN", "Todos los eventos eliminados correctamente de " + collaborator))
                                .addOnFailureListener(e -> Log.e("_FIREBASEITN", "Error al eliminar todos los eventos de " + collaborator, e));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("_FIREBASEITN", "Error al obtener itinerario para eliminar todos los eventos de colaboradores", databaseError.toException());
            }
        });
    }

    public String setId(){//Establece un id unico para cada itinerario

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").
                child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",")).child("itineraries");
        return databaseReference.push().getKey();
    }

}