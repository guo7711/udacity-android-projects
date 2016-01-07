package com.example.guo7711.popularmovieapp;

import java.util.ArrayList;

/**
 * Created by guo7711 on 10/15/2015.
 */
public class Movie {

    String posterURL;
    String id;
    String title;
    String release_date;
    double vote_average;
    String overview;

    ArrayList<String> reviews;
    ArrayList<String> trailers;



    public Movie()
    {
        this.id = "default";
        reviews = new ArrayList<String>();
        trailers = new ArrayList<String>();
    }

    public Movie(String id){
        this.id = id;
    }

}
