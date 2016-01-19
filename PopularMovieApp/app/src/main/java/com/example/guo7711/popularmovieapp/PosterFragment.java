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
import java.util.HashSet;
import java.util.Set;

/**
 * Created by guo7711 on 10/15/2015.
 */
public class PosterFragment extends Fragment {

    private PosterAdapter posterAdapter;
    private View rootView;
    ArrayList<Movie> movies = new ArrayList<>();


    public PosterFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelableArrayList("movie", movies);
        super.onSaveInstanceState(outState);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
           Intent intent = new Intent(getActivity(), SettingsActivity.class);startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    public void updateMovies(View rootView){
        FetchPosterTask movieTask = new FetchPosterTask(getActivity(), rootView);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String order_by = prefs.getString(getString(R.string.pref_order_key), "popularity.desc");
        movieTask.execute(order_by);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        posterAdapter = new PosterAdapter(getActivity(), movies);
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

    /*    ConnectivityManager cm =
                (ConnectivityManager)(getActivity().getSystemService(Context.CONNECTIVITY_SERVICE));

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();*/

//        if (!isConnected) {
//            Toast toast = Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT);
//            toast.show();
//        }
//        else {

            if (savedInstanceState != null) {
                movies = savedInstanceState.getParcelableArrayList("movie");

                // update UI
                posterAdapter.setMovies(movies);
                posterAdapter.notifyDataSetChanged();

                if (movies != null) {
                    GridView gridView = (GridView) rootView.findViewById(R.id.movie_grid);
                    gridView.setAdapter(posterAdapter);

                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {
                            // Toast.makeText(getActivity(), "" + position,
                            //       Toast.LENGTH_SHORT).show();
                            String selectedMovieID = (movies.get(position)).id;
                            Intent intent = new Intent(getActivity(), DetailActivity.class);
                            intent.putExtra("movie", movies.get(position));
                            startActivity(intent);

                        }
                    });

                }
            } else {
                updateMovies(rootView);
            }
       // }


        return rootView;
    }


     class FetchPosterTask extends AsyncTask<String, Void, ArrayList<Movie>> {

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

            if (order_by.equals("favourite")) {

                movies = new ArrayList<>();
                SharedPreferences sp = mContext.getSharedPreferences("pref_general", Context.MODE_PRIVATE);
                Set<String> retrive_set = new HashSet<String>();
                retrive_set = sp.getStringSet("favourite", null);

                String[] array = new String[retrive_set.size()];
                retrive_set.toArray(array);

                for (int i = 0; i < array.length; i++) {
                    Log.d("FetchPoster", array[i]);
                }

                for (int i = 0; i < array.length; i++) {
                    try {
                        // Construct the URL for the OpenWeatherMap query
                        // Possible parameters are avaiable at OWM's forecast API page, at
                        // http://openweathermap.org/API#forecast
                        //
                        //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=[YOUR API KEY]


                        URL url = new URL("http://api.themoviedb.org/3/movie/"+array[i]+"?api_key=" + mContext.getResources().getString(R.string.apikey));

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

                        Movie m = MovieDataParser.getMovieByID(array[0], responseJsonStr);
                        movies.add(m);

                    } catch (IOException e) {
                        Log.e("PosterFragment", "Error " + e.getMessage());
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
                                Log.e("PosterFragment", "Error closing stream", e);
                            }
                        }
                    }
                }
            }
            else {
                try {
                    // Construct the URL for the OpenWeatherMap query
                    // Possible parameters are avaiable at OWM's forecast API page, at
                    // http://openweathermap.org/API#forecast
                    //
                    //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=[YOUR API KEY]


                    URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=" + order_by + "&api_key=" + mContext.getResources().getString(R.string.apikey));

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

                    movies = MovieDataParser.getMovies(responseJsonStr);

                } catch (IOException e) {
                    Log.e("PosterFragment", "Error " + e.getMessage());
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
                            Log.e("PosterFragment", "Error closing stream", e);
                        }
                    }
                }
            }
            return movies;
        }



        @Override
        protected void onPostExecute(ArrayList<Movie> result) {

            if (result != null) {
                posterAdapter.setMovies(result);
                posterAdapter.notifyDataSetChanged();
                movies = result;
            }


            if (movies != null)
            {
                GridView gridView = (GridView) rootView.findViewById(R.id.movie_grid);
                gridView.setAdapter(posterAdapter);

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        // Toast.makeText(getActivity(), "" + position,
                        //       Toast.LENGTH_SHORT).show();
                        String selectedMovieID = (movies.get(position)).id;
                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                        intent.putExtra("movie", movies.get(position));
                        startActivity(intent);

                    }
                });

                //  Log.e("onPostExecute", String.valueOf(movies.size()));
            }


            super.onPostExecute(result);
        }


    }


}
