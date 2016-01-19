package com.example.guo7711.popularmovieapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends AppCompatActivity implements OnMovieSelectedCallback{

    Boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (findViewById(R.id.movie_detail_container) != null){
            mTwoPane = true;
            Log.e("onCreate", "Tablet found");

            if (savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailActivityFragment())
                        .commit();
            }
        }
        else {
            Log.e("onCreate", "Phone found");
            mTwoPane = false;

        }
        setContentView(R.layout.activity_main);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
}

    @Override
    public void onMovieSelected() {

    }
}


