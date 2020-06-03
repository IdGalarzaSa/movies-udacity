package com.galarzaIvan.movies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private String TAG = "MainActivity";

    private static final String MAIN_INSTANCE = "main_instance";

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private MovieRequests mMovieRequests;
    private String mCurrentMovieCall;
    private LinearLayout mLinearLayoutError;
    private TextView mErrorMessage;
    private Context mContext;
    private Boolean isOnline;
    private LinearLayout mLinearLayoutLoading;
    private List<Movie> mMovieList;

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

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MAIN_INSTANCE)) {
                hideLoading();
                hideError();
                String previousData = savedInstanceState.getString(MAIN_INSTANCE);
                List<Movie> movieList = Arrays.asList(new Gson().fromJson(previousData, Movie[].class));
                mMovieAdapter.setMovieList(movieList);
            }
        } else {
            getTopRated();
        }

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
                switch (mCurrentMovieCall) {
                    case AppConstants.POPULAR:
                        getPopular();
                        break;
                    case AppConstants.FAVORITES:
                        getFavorites();
                        break;
                    case AppConstants.TOP_RATED:
                    default:
                        getTopRated();
                }
            }
        });
    }

    private void configRecyclerView() {

        GridLayoutManager gridLayoutManager;
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(
                    this,
                    AppConstants.GRID_PORTRAIT
            );
        } else {
            gridLayoutManager = new GridLayoutManager(
                    this,
                    AppConstants.GRID_LANDSCAPE
            );
        }

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);
    }

    private void initRetrofit() {
        Retrofit retrofit = RetrofitController.getInstance();
        mMovieRequests = retrofit.create(MovieRequests.class);
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
                getTopRated();
                break;
            case R.id.order_by_popular_movies:
            default:
                getPopular();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getPopular() {
        if (isOnline) {
            hideError();
            showLoading();
            mCurrentMovieCall = AppConstants.POPULAR;
            setTitle(getString(R.string.popular));
            Call<MovieDbResponse> myCall = mMovieRequests.getPopularMovies(
                    MovieDBConstants.API_KEY,
                    MovieDBConstants.LANGUAGE);
            getMovies(myCall);
        } else {
            showError(getString(R.string.no_network_connection));
        }
    }

    private void getTopRated() {
        if (isOnline) {
            hideError();
            showLoading();
            mCurrentMovieCall = AppConstants.TOP_RATED;
            setTitle(getString(R.string.top_rated));
            Call<MovieDbResponse> myCall = mMovieRequests.getMoviesTopRated(
                    MovieDBConstants.API_KEY,
                    MovieDBConstants.LANGUAGE);

            getMovies(myCall);
        } else {
            showError(getString(R.string.no_network_connection));
        }
    }

    private void getFavorites() {
        hideError();
        showLoading();
        mCurrentMovieCall = AppConstants.FAVORITES;
        List<Movie> movies = mDb.favoriteDao().getFavoritesMovies();

        mMovieAdapter.setMovieList(movies);
        hideError();
    }

    private void getMovies(Call<MovieDbResponse> myCall) {
        myCall.enqueue(new Callback<MovieDbResponse>() {
            @Override
            public void onResponse(Call<MovieDbResponse> call, Response<MovieDbResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        hideError();
                        mMovieList = response.body().results;
                        if (mMovieList != null && mMovieList.size() > 0) {
                            mMovieAdapter.setMovieList(mMovieList);
                        } else {
                            mMovieAdapter.setMovieList(null);
                        }
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

    private void showError(String errorMessage) {
        mErrorMessage.setText(errorMessage);
        mLinearLayoutError.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        hideLoading();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String data = new Gson().toJson(mMovieList);
        outState.putString(MAIN_INSTANCE, data);
    }

}
