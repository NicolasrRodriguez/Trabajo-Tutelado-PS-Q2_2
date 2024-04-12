
package com.example.triptracks;

import android.os.Parcel;
import android.os.Parcelable;

public class Itinerary implements Parcelable {

    private String element;
    private String itineraryTitle;
    private String country;
    private String state;
    private String city;

    public Itinerary(String element, String itineraryTitle, String country, String state,String city) {
        this.element = element;
        this.itineraryTitle = itineraryTitle;
        this.country = country;
        this.state = state;
        this.city = city;
    }

    protected Itinerary(Parcel in) {
        element = in.readString();
        itineraryTitle = in.readString();
        country = in.readString();
        state = in.readString();
        city = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(element);
        dest.writeString(itineraryTitle);
        dest.writeString(country);
        dest.writeString(state);
        dest.writeString(city);
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

    public String getElement() { return element; }

    public void setElement(String element) {this.element = element; }

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
}