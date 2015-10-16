package com.example.guo7711.popularmovieapp;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.Arrays;

/**
 * Created by guo7711 on 10/15/2015.
 */
public class PosterFragment extends Fragment {

    private MovieAdapter movieAdapter;

    Movie[] movies = {
            new Movie("http://i.imgur.com/DvpvklR.png"),
            new Movie("http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg")
    };

    public PosterFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        movieAdapter = new MovieAdapter(getActivity(), Arrays.asList(movies));

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.movie_grid);
        gridView.setAdapter(movieAdapter);

        return rootView;
    }

    public class FetchMovieTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
        }

        private String[] getDataFromJson(String movieJsonStr)
        {
            return new String[0];
        }
    }
}
