package com.galarzaIvan.movies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galarzaIvan.movies.classes.MovieAdapter;
import com.galarzaIvan.movies.classes.RetrofitController;
import com.galarzaIvan.movies.database.AppDatabase;
import com.galarzaIvan.movies.models.Movie;
import com.galarzaIvan.movies.requests.MovieRequests;
import com.galarzaIvan.movies.constants.MovieDBConstants;
import com.galarzaIvan.movies.constants.AppConstants;
import com.galarzaIvan.movies.models.MovieDbResponse;
import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private String TAG = "MainActivity";

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private MovieRequests mMovieRequests;
    private Call<MovieDbResponse> mCurrentCall;
    private LinearLayout mLinearLayoutError;
    private TextView mErrorMessage;
    private Context mContext;
    private Boolean isOnline;
    private LinearLayout mLinearLayoutLoading;

    //Database
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        mContext = this;

        // Init Database
        mDb = AppDatabase.getInstance(this);

        netWorkConnection();
        initViews();            // In this method I will init all the views of this activity
        configRecyclerView();   // RecyclerView configs
        initRetrofit();         // Init Retrofit
        getData(createTopRatedMoviesCall()); // Set a default top rated movies call
    }

    private void netWorkConnection() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkRequest.Builder builder = new NetworkRequest.Builder();

            if (connectivityManager != null) {
                connectivityManager.registerNetworkCallback(builder.build(), new ConnectivityManager.NetworkCallback() {
                            @Override
                            public void onAvailable(Network network) {
                                isOnline = true;
                            }

                            @Override
                            public void onLost(Network network) {
                                isOnline = false;
                            }
                        }
                );
            }
            isOnline = false;
        } catch (Exception e) {
            isOnline = false;
        }
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);

        mLinearLayoutError = (LinearLayout) findViewById(R.id.ly_error);
        mLinearLayoutLoading = (LinearLayout) findViewById(R.id.ly_progress);
        mErrorMessage = (TextView) findViewById(R.id.tv_errorMessage);

        // Retry buttton
        Button mRetryButton = (Button) findViewById(R.id.bt_retry);
        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(mCurrentCall);
            }
        });

    }

    private void configRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                this,
                AppConstants.GRID_COLLUMNS
        );
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);
    }

    private void initRetrofit() {
        Retrofit retrofit = RetrofitController.getInstance();
        mMovieRequests = retrofit.create(MovieRequests.class);
    }

    // For default gets the popular movies
    private void getData(final Call<MovieDbResponse> myCall) {
        if (isOnline) {
            hideError();
            showLoading();
            getMovies(myCall);
        } else {
            showError(getString(R.string.no_network_connection));
        }
    }

    private void getMovies(Call<MovieDbResponse> myCall) {
        /*
         * I save this call into a variable if I have any network problem and the user needs to use the "retry" button
         */
        mCurrentCall = myCall.clone();

        myCall.enqueue(new Callback<MovieDbResponse>() {
            @Override
            public void onResponse(Call<MovieDbResponse> call, Response<MovieDbResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        hideError();
                        mMovieAdapter.setMovieList(response.body().results);
                    } else {
                        showError(getString(R.string.default_error_message));
                        mMovieAdapter.setMovieList(null);
                    }
                } else {
                    showError(getString(R.string.default_error_message));
                    Log.e(TAG, "Unsuccessful call: " + response.toString());
                }
            }

            @Override
            public void onFailure(Call<MovieDbResponse> call, Throwable t) {
                showError(getString(R.string.default_error_message));
                Log.e(TAG, "onFailure: " + t.toString());
            }
        });
    }

    private void showError(String errorMessage) {
        mErrorMessage.setText(errorMessage);
        mLinearLayoutError.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        hideLoading();
    }

    private void hideError() {
        mLinearLayoutError.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        hideLoading();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_by, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuItemThatWasSelected = item.getItemId();
        Call<MovieDbResponse> callResponse;
        switch (menuItemThatWasSelected) {
            case R.id.favorites:
                getFavorites();
                break;
            case R.id.order_by_top_rated_movies:
                callResponse = createTopRatedMoviesCall();
                getData(callResponse);
                break;
            case R.id.order_by_popular_movies:
            default:
                callResponse = createPopularMoviesCall();
                getData(callResponse);
        }
        return super.onOptionsItemSelected(item);
    }

    private Call<MovieDbResponse> createPopularMoviesCall() {
        setTitle(getString(R.string.popular));
        return mMovieRequests.getPopularMovies(
                MovieDBConstants.API_KEY,
                MovieDBConstants.LANGUAGE);
    }

    private Call<MovieDbResponse> createTopRatedMoviesCall() {
        setTitle(getString(R.string.top_rated));
        return mMovieRequests.getMoviesTopRated(
                MovieDBConstants.API_KEY,
                MovieDBConstants.LANGUAGE);
    }

    private void getFavorites() {
        List<Movie> movies = mDb.favoriteDao().getFavoritesMovies();
        mMovieAdapter.setMovieList(movies);
    }

    @Override
    public void movieClickListener(Movie movie) {

        //Parse data from an object to json (String) to send inside an intent
        String data = new Gson().toJson(movie);
        Intent intent = new Intent(this, MovieInfoActivity.class);
        intent.putExtra(AppConstants.MOVIE_EXTRA, data);
        startActivity(intent);
    }

    private void showLoading() {
        mLinearLayoutLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        mLinearLayoutLoading.setVisibility(View.GONE);
    }
}
