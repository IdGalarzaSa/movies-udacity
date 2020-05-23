package com.galarzaIvan.movies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;

import com.galarzaIvan.movies.adapters.MovieAdapter;
import com.galarzaIvan.movies.classes.Movie;
import com.galarzaIvan.movies.constants.AppConstants;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    private Movie[] mMoviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // In this method I will init all the views of this activity
        initViews();
        // RecyclerView configs
        configRecyclerView();
        // GetData from MoviesDB
        mMoviesList = getData();
        mMovieAdapter.setMovieList(null);
        mMovieAdapter.setMovieList(mMoviesList);
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
    }

    private void configRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                this,
                AppConstants.GRID_COLLUMNS
        );
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter();
        mRecyclerView.setAdapter(mMovieAdapter);
    }

    private Movie[] getData() {
        Movie[] data = new Movie[15];
        for (int i = 0; i < 10; i++) {
            data[i] = new Movie();
        }
        return data;
    }
}
