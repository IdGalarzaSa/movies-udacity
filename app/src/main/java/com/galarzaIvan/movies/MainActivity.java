package com.galarzaIvan.movies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galarzaIvan.movies.classes.MovieAdapter;
import com.galarzaIvan.movies.requests.MovieRequests;
import com.galarzaIvan.movies.constants.MovieDBConstants;
import com.galarzaIvan.movies.constants.AppConstants;
import com.galarzaIvan.movies.models.MovieDbResponse;

import java.util.Objects;

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
    private Call<MovieDbResponse> mCurrentCall;
    private LinearLayout mLinearLayoutError;
    private TextView mErrorMessage;
    private Context mContext;
    private ActionBar mActionBar;
    private Boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        mContext = this;
        mActionBar = getSupportActionBar();
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

    // For default gets the popular movies
    private void getData(final Call<MovieDbResponse> myCall) {
        if (isOnline){
            hideError();
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
                        mMovieAdapter.setMovieList(response.body().results);
                        hideError();
                    } else {
                        mMovieAdapter.setMovieList(null);
                        showError(getString(R.string.default_error_message));
                    }
                } else {
                    showError(getString(R.string.default_error_message));
                    Log.e(TAG, "Unsuccessful call: " + response.toString());
                }
            }

            @Override
            public void onFailure(Call<MovieDbResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                showError(getString(R.string.default_error_message));
            }
        });
    }

    private void showError(String errorMessage) {
        mErrorMessage.setText(errorMessage);
        mLinearLayoutError.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void hideError() {
        mLinearLayoutError.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
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
            case R.id.order_by_top_rated_movies:
                callResponse = createTopRatedMoviesCall();
                break;
            case R.id.order_by_popular_movies:
            default:
                callResponse = createPopularMoviesCall();
        }
        getData(callResponse);
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

}
