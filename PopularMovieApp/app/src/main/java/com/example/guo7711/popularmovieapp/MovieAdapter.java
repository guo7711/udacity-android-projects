package com.example.guo7711.popularmovieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by guo7711 on 10/15/2015.
 */
public class MovieAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Movie> movies;
    private LayoutInflater inflater;

    public void setMovies(ArrayList<Movie> result)
    {
        movies = result;

    }

    public MovieAdapter(Context c, ArrayList<Movie> list) {
        mContext = c;
        movies = list;
        inflater = LayoutInflater.from(mContext);
    }

    public int getCount() {
        return movies.size();
    }

    public Object getItem(int position) {
        return movies.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(500, 750));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

       // MovieFragment.FetchMovieTask.execute();

        getImageURLs("popularity.desc");

        if (position<=1)
        Picasso.with(mContext).load(movies.get(position).posterURL).into(imageView);
        return imageView;


    }

    public void getImageURLs(String sort_by) {
        MovieFragment.FetchMovieTask task = new MovieFragment().new FetchMovieTask();
        task.execute(sort_by);

    }


}
