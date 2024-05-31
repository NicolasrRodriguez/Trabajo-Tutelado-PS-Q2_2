
package com.example.triptracks.Domain.Entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Itinerary implements Parcelable {
    private String id;
    private String itineraryTitle;
    private String country;
    private String state;
    private String city;
    private String startDate;
    private String endDate;

    private String Admin;

    private ArrayList<String> Colaborators;

    private ArrayList<String> ImageUris;




    public Itinerary() {
    }


    public Itinerary(String id, String itineraryTitle, String country, String state,String city,String admin, ArrayList<String> colaborators,String startDate, String endDate,ArrayList<String> imageUris) {
        this.id = id;

        this.itineraryTitle = itineraryTitle;
        this.country = country;
        this.state = state;
        this.city = city;
        this.startDate = startDate;
        this.endDate = endDate;
        this.Admin = admin;
        this.Colaborators = colaborators;
        this.ImageUris = imageUris;

    }


    protected Itinerary(Parcel in) {
        id = in.readString();
        itineraryTitle = in.readString();
        country = in.readString();
        state = in.readString();
        city = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        Admin = in.readString();
        Colaborators = in.createStringArrayList();
        ImageUris = in.createStringArrayList();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(itineraryTitle);
        dest.writeString(country);
        dest.writeString(state);
        dest.writeString(city);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeString(Admin);
        dest.writeStringList(Colaborators);
        dest.writeStringList(ImageUris);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Itinerary> CREATOR = new Creator<Itinerary>() {
        @Override
        public Itinerary createFromParcel(Parcel in) {
            return new Itinerary(in);
        }

        @Override
        public Itinerary[] newArray(int size) {
            return new Itinerary[size];
        }
    };


    public String getAdmin() {
        return Admin;
    }

    public void setAdmin(String admin) {
        Admin = admin;
    }

    public ArrayList<String> getColaborators() {
        return Colaborators;
    }


    public void setColaborators(ArrayList<String> colaborators) {
        Colaborators = colaborators;
    }

    public ArrayList<String> getImageUris() {
        return ImageUris;
    }

    public void setImageUris(ArrayList<String> imageUris) {
        ImageUris = imageUris;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getItineraryTitle() {
        return itineraryTitle;
    }

    public void setItineraryTitle(String itineraryTitle) {
        this.itineraryTitle = itineraryTitle;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}