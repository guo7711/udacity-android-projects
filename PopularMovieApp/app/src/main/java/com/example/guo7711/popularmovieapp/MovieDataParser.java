package com.example.guo7711.popularmovieapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by guo7711 on 12/10/2015.
 */
public class MovieDataParser {


    public static Movie getMovieByID(String ID, String movieJsonStr)
    {
        Movie m = new Movie(ID);
        try {
            JSONObject onemovie = new JSONObject(movieJsonStr);
            String posterpath = onemovie.getString("poster_path");
            m.posterURL = posterpath;
            String releasedate = onemovie.getString("release_date");
            m.release_date = releasedate;
            String title = onemovie.getString("title");
            m.title = title;
            double vote = onemovie.getDouble("vote_average");
            m.vote_average = vote;
            String overview = onemovie.getString("overview");
            m.overview = overview;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Log.e("getMovieByID", m.overview);
        return m;
    }

    public static ArrayList<String> getReviewsByMovieID(String MovieID, String responseJsonStr){

        ArrayList<String> reviews = new ArrayList<>();

        //only get results in page 1
        try {
            JSONArray pageOneResults = new JSONObject(responseJsonStr).getJSONArray("results");

            for (int i = 0; i < pageOneResults.length(); i++){
                JSONObject onereviewcontent = pageOneResults.getJSONObject(i);
                String onecontent = onereviewcontent.getString("content");
                reviews.add(onecontent);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reviews;

    }

    public static String[] getPosterPaths(String movieJsonStr)throws JSONException{
        JSONArray movies = new JSONObject(movieJsonStr).getJSONArray("results");
        ArrayList<String> posters = new ArrayList<String>();

        for (int i = 0; i < movies.length(); i++){
            JSONObject onemovie = movies.getJSONObject(i);
            String posterpath = onemovie.getString("poster_path");
            posters.add(posterpath);
        }

        String[] posterArr = new String[posters.size()];
               posterArr =  posters.toArray(posterArr);
        return posterArr;
    }

    public static ArrayList<Movie> getMovies(String movieJsonStr){

        ArrayList<Movie> movieArray = new ArrayList<Movie>();

        try {

            JSONArray movies = new JSONObject(movieJsonStr).getJSONArray("results");

            for (int i = 0; i < movies.length(); i++) {
                JSONObject onemovie = null;
                onemovie = movies.getJSONObject(i);
                String posterpath = "http://image.tmdb.org/t/p/w185/" + onemovie.getString("poster_path");
                String id = onemovie.getString("id");

                Movie movieObj = new Movie(id);
                movieObj.posterURL = posterpath;

                movieArray.add(movieObj);
                //Log.d("PosterURL", posterpath);
            }


        }

        catch (JSONException e) {
            e.printStackTrace();
        }

        return movieArray;
    }

}
