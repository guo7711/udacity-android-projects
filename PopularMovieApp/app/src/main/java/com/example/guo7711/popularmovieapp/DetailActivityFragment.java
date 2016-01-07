package com.example.guo7711.popularmovieapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    Movie selectedMovie = new Movie();
    View rootView;
    ReviewTrailerAdapter reviewTrailerAdapter;
    SharedPreferences sp;

    public DetailActivityFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sp = getContext().getSharedPreferences("pref_general", Context.MODE_PRIVATE);

    }

    public Boolean checkFavouriteByID(String MovieID){

        Set<String> retrive_set = new HashSet<String>();
        retrive_set = sp.getStringSet("favourite", null);

        String[] array = new String[retrive_set.size()];
        retrive_set.toArray(array);

        if(retrive_set.contains(MovieID)){
            return true;
        }
        else {
            return false;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Log.e("OnCreateView", "OnCreateView");

        reviewTrailerAdapter = new ReviewTrailerAdapter(getActivity(), selectedMovie.reviews);
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();


        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String selectedMovieID = intent.getStringExtra(Intent.EXTRA_TEXT);

            FetchMovieTask fetchMovieTask = new FetchMovieTask(getActivity(), rootView);
            fetchMovieTask.execute(selectedMovieID);

            FetchReviewTask fetchReviewTask = new FetchReviewTask(getActivity(), rootView);
            fetchReviewTask.execute(selectedMovieID);

            FetchTrailerTask fetchTrailerTask = new FetchTrailerTask(getActivity(), rootView);
            fetchTrailerTask.execute(selectedMovieID);

            //Log.e("onCreateView", selectedMovie.id);
            final Button button = (Button) rootView.findViewById(R.id.button);

            if (checkFavouriteByID(selectedMovieID)) {
               // Log.e("onCreateView", "TRUE");
                button.setText("Unmark as favourite");
            }
            else
            {
                //Log.e("onCreateView", "FALSE");
                button.setText("Mark as favourite");
            }


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (checkFavouriteByID(selectedMovie.id)) {

                        //SharedPreferences sp = getContext().getSharedPreferences("pref_general", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();

                        Set<String> set = new HashSet<String>();
                        set = sp.getStringSet("favourite", null);
                        set.remove(selectedMovie.id);
                        editor.putStringSet("favourite", set);
                        editor.commit();

                        Toast.makeText(getContext(), "Unmarked as favourite!", Toast.LENGTH_SHORT).show();
                        button.setText("Mark as favourite");

                    } else {
                        //SharedPreferences sp = getContext().getSharedPreferences("pref_general", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();

                        Set<String> set = new HashSet<String>();
                        set = sp.getStringSet("favourite", null);
                        set.add(selectedMovie.id);
                        editor.putStringSet("favourite", set);
                        editor.commit();

                        Toast.makeText(getContext(), "Marked as favourite!", Toast.LENGTH_SHORT).show();
                        button.setText("Unmark as favourite");
                    }

                }
            });

        }



        return rootView;

    }




    public class FetchTrailerTask extends AsyncTask<String, Void, ArrayList<Trailer>> {
        private Context mContext;
        private View rootView;

        public FetchTrailerTask(Context context, View rootView) {
            this.mContext = context;
            this.rootView = rootView;
        }

        @Override
        protected ArrayList<Trailer> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String responseJsonStr = null;

            String movieID = params[0];

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //
                //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=[YOUR API KEY]
                URL url = new URL("http://api.themoviedb.org/3/movie/"+movieID+"/videos?api_key=" + getResources().getString(R.string.apikey));

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

                selectedMovie.trailers =  MovieDataParser.getTrailersByMovieID(movieID, responseJsonStr);


            } catch (IOException e) {
                Log.e("MovieFragment", "Error ", e);
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


            return selectedMovie.trailers;
        }

        @Override
        protected void onPostExecute(ArrayList<Trailer> result_trailers) {

            if (result_trailers != null) {
                reviewTrailerAdapter.setTrailers(result_trailers);
                reviewTrailerAdapter.notifyDataSetChanged();
                selectedMovie.trailers = result_trailers;
            }


            if (selectedMovie.reviews != null) {
                //ListView reviewListView = (ListView) rootView.findViewById(R.id.reviewList);
                //reviewListView.setAdapter(reviewTrailerAdapter);

                LinearLayout trailersLinearLayout = (LinearLayout) rootView.findViewById(R.id.trailersLinearLayout);
                for (int i = 0; i < selectedMovie.trailers.size(); i++) {
                    trailersLinearLayout.addView(reviewTrailerAdapter.createTrailer(i));
                }

                super.onPostExecute(result_trailers);
            }

            // Log.e("FetchReviewTask", String.valueOf(selectedMovie.reviews.size()));
        }
    }

    public class FetchReviewTask extends AsyncTask<String, Void, ArrayList<String>> {
        private Context mContext;
        private View rootView;


        public FetchReviewTask(Context context, View rootView) {
            this.mContext = context;
            this.rootView = rootView;
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String responseJsonStr = null;

            String movieID = params[0];

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //
                //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=[YOUR API KEY]
                URL url = new URL("http://api.themoviedb.org/3/movie/"+movieID+"/reviews?api_key=" + getResources().getString(R.string.apikey));

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

                selectedMovie.reviews =  MovieDataParser.getReviewsByMovieID(movieID, responseJsonStr);


            } catch (IOException e) {
                Log.e("MovieFragment", "Error ", e);
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


            return selectedMovie.reviews;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result_reviews) {

            if (result_reviews != null) {
                reviewTrailerAdapter.setReviews(result_reviews);
                reviewTrailerAdapter.notifyDataSetChanged();
                selectedMovie.reviews = result_reviews;
            }


            if (selectedMovie.reviews != null) {
                //ListView reviewListView = (ListView) rootView.findViewById(R.id.reviewList);
                //reviewListView.setAdapter(reviewTrailerAdapter);

                LinearLayout reviewsLinearLayout = (LinearLayout) rootView.findViewById(R.id.reviewsLinearLayout);
                for (int i = 0; i < selectedMovie.reviews.size(); i++) {
                    reviewsLinearLayout.addView(reviewTrailerAdapter.createReview(i));
                }

                super.onPostExecute(result_reviews);
            }

           // Log.e("FetchReviewTask", String.valueOf(selectedMovie.reviews.size()));
        }
    }

    public class FetchMovieTask extends AsyncTask<String, Void, Movie> {

        private Context mContext;
        private View rootView;

        public FetchMovieTask(Context context, View rootView){
            this.mContext=context;
            this.rootView=rootView;
        }

        @Override
        protected Movie doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String responseJsonStr = null;

            String movieID = params[0];

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //
                //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=[YOUR API KEY]
                URL url = new URL("http://api.themoviedb.org/3/movie/"+movieID+"?api_key=" + getResources().getString(R.string.apikey));

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

                selectedMovie =  MovieDataParser.getMovieByID(movieID, responseJsonStr);



            } catch (IOException e) {
                Log.e("MovieFragment", "Error ", e);
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

            return selectedMovie;
        }

        @Override
        protected void onPostExecute(Movie result) {

            if (result != null) {
                selectedMovie = result;
                if (selectedMovie != null) {
                    ((TextView) rootView.findViewById(R.id.titleText)).setText(selectedMovie.title);
                    ((TextView) rootView.findViewById(R.id.releasedateText)).setText(selectedMovie.release_date);
                    ((TextView) rootView.findViewById(R.id.voteText)).setText(String.valueOf(selectedMovie.vote_average + "/10"));
                    ((TextView) rootView.findViewById(R.id.overViewText)).setText(selectedMovie.overview);
                    ImageView imageView = ((ImageView) rootView.findViewById(R.id.imageView));
                    Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/"+ selectedMovie.posterURL).into(imageView);
                    //((TextView) rootView.findViewById(R.id.testText)).setText("Successful!!!");

                }
            }
            super.onPostExecute(result);

        }
    }
}
