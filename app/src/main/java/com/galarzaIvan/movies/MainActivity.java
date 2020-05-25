package com.galarzaIvan.movies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.galarzaIvan.movies.classes.MovieAdapter;
import com.galarzaIvan.movies.requests.MovieRequests;
import com.galarzaIvan.movies.constants.MovieDBConstants;
import com.galarzaIvan.movies.constants.AppConstants;
import com.galarzaIvan.movies.models.MovieDbResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    private MovieRequests mMovieRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();            // In this method I will init all the views of this activity
        configRecyclerView();   // RecyclerView configs
        initRetrofit();         // Init Retrofit
        getData();
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
    }

    private void configRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                this,
                AppConstants.GRID_COLLUMNS
        );
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mMovieAdapter = new MovieAdapter();
        mRecyclerView.setAdapter(mMovieAdapter);
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MovieDBConstants.HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mMovieRequests = retrofit.create(MovieRequests.class);
    }

    private void getData() {
        // TODO: switch
        Call<MovieDbResponse> callResponse = mMovieRequests.getPopularMovies(
                MovieDBConstants.API_KEY,
                MovieDBConstants.LANGUAGE);
        getMovies(callResponse);
    }

    private void getMovies(Call<MovieDbResponse> myCall) {
        myCall.enqueue(new Callback<MovieDbResponse>() {
            @Override
            public void onResponse(Call<MovieDbResponse> call, Response<MovieDbResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        mMovieAdapter.setMovieList(response.body().results);
                    } else {
                        mMovieAdapter.setMovieList(null);
                        showError();
                    }
                } else {
                    Log.e(TAG, "Unsuccessful call: "+  response.toString());
                }
            }

            @Override
            public void onFailure(Call<MovieDbResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString() );
                showError();
            }
        });
    }

    private void showError() {

    }
}
