package com.example.guo7711.popularmovieapp;

/**
 * Created by guo7711 on 10/15/2015.
 */
public class Movie {

    String posterURL;
    String id;
    String title;

    public Movie()
    {
        this.id = "default";
    }

    public Movie(String id){
        this.id = id;
    }

}
