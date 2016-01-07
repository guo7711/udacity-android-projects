package com.example.guo7711.popularmovieapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by guo7711 on 1/4/2016.
 */
public class ReviewTrailerAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> reviews;
    private ArrayList<Trailer> trailers;

    private LayoutInflater inflater;

    public void setReviews(ArrayList<String> result)
    {
        reviews = result;
    }

    public void setTrailers (ArrayList<Trailer> result){
        trailers = result;
    }

    public ReviewTrailerAdapter(Context c, ArrayList<String> list) {
        mContext = c;
        reviews = list;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View createReview(int position)
    {
        TextView textView;
        textView = new TextView(mContext);
        //textView.setLayoutParams(new GridView.LayoutParams(540, 200));
        //textView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        textView.setPadding(0, 0, 0, 0);

        textView.setText(reviews.get(position));
        // Log.e("getView", String.valueOf(position));
        return textView;
    }

    public View createTrailer(final int position)
    {
        TextView textView;
        textView = new TextView(mContext);
        textView.setClickable(true);
        //textView.setLayoutParams(new GridView.LayoutParams(540, 200));
        //textView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        textView.setPadding(0, 0, 0, 0);
        textView.setTextSize(18);

        textView.setText(trailers.get(position).name);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + trailers.get(position).key));
                mContext.startActivity(intent);
            }
        });
        // Log.e("getView", String.valueOf(position));
        return textView;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            textView = new TextView(mContext);
            //textView.setLayoutParams(new GridView.LayoutParams(540, 200));
            //textView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            textView.setPadding(0, 0, 0, 0);
        } else {
            textView = (TextView) convertView;
        }

        textView.setText(reviews.get(position));
       // Log.e("getView", String.valueOf(position));
        return textView;
    }
}
