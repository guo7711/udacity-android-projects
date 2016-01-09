package com.example.guo7711.popularmovieapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by guo7711 on 10/15/2015.
 */
public class Movie implements Parcelable{

    String posterURL;
    String id;
    String title;
    String release_date;
    double vote_average;
    String overview;

    ArrayList<String> reviews;
    ArrayList<Trailer> trailers;



    public Movie()
    {
        this.id = "default";
        reviews = new ArrayList<String>();
        trailers = new ArrayList<Trailer>();
    }

    public Movie(String id){

        this.id = id;
        reviews = new ArrayList<String>();
        trailers = new ArrayList<Trailer>();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(posterURL);
        dest.writeString(id);
    }
}
