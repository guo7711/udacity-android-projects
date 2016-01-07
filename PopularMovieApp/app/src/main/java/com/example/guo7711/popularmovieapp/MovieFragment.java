package com.example.guo7711.popularmovieapp;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by guo7711 on 10/15/2015.
 */
public class MovieFragment extends Fragment {

    private MovieAdapter movieAdapter;
    private View rootView;
    ArrayList<Movie> movies = new ArrayList<>();


    public MovieFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
           Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {

        updateMovies(rootView);
        super.onStart();
    }

    public void updateMovies(View rootView){
        FetchPosterTask movieTask = new FetchPosterTask(getActivity(), rootView);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String order_by = prefs.getString(getString(R.string.pref_order_key), "popularity.desc");
        movieTask.execute(order_by);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        movieAdapter = new MovieAdapter(getActivity(), movies);
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Log.e("onCreateView", "MovieFragment");

        return rootView;
    }

    public class FetchPosterTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private Context mContext;
        private View rootView;

        public FetchPosterTask(Context context, View rootView){
            this.mContext=context;
            this.rootView=rootView;
        }


        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String responseJsonStr = null;
            String order_by = params[0];

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //
                //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=[YOUR API KEY]



                URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by="+ order_by + "&api_key=" + getResources().getString(R.string.apikey));

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                responseJsonStr = buffer.toString();

                //Log.e("Doinbackground", responseJsonStr);

                movies =  MovieDataParser.getMovies(responseJsonStr);

            } catch (IOException e) {
                Log.e("MovieFragment", "Error " + e.getMessage());
                // If the code didn't successfully get the movie data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MovieFragment", "Error closing stream", e);
                    }
                }
            }
            return movies;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> result) {

            if (result != null) {
                movieAdapter.setMovies(result);
                movieAdapter.notifyDataSetChanged();
                movies = result;
            }


            if (movies != null)
            {
                GridView gridView = (GridView) rootView.findViewById(R.id.movie_grid);
                gridView.setAdapter(movieAdapter);

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        // Toast.makeText(getActivity(), "" + position,
                        //       Toast.LENGTH_SHORT).show();
                        String selectedMovieID = (movies.get(position)).id;
                        Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, selectedMovieID);
                        startActivity(intent);

                    }
                });

                //Log.e("onPostExecute", String.valueOf(movies.size()));
            }


            super.onPostExecute(result);
        }


    }
}
