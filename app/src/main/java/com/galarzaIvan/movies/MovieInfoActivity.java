package com.galarzaIvan.movies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.galarzaIvan.movies.classes.RetrofitController;
import com.galarzaIvan.movies.classes.ReviewAdapter;
import com.galarzaIvan.movies.classes.TrailerAdapter;
import com.galarzaIvan.movies.constants.AppConstants;
import com.galarzaIvan.movies.constants.MovieDBConstants;
import com.galarzaIvan.movies.database.AppDatabase;
import com.galarzaIvan.movies.database.Favorite;
import com.galarzaIvan.movies.models.Movie;
import com.galarzaIvan.movies.models.Review;
import com.galarzaIvan.movies.models.ReviewResponse;
import com.galarzaIvan.movies.models.TrailerInfo;
import com.galarzaIvan.movies.models.TrailerResponse;
import com.galarzaIvan.movies.requests.MovieRequests;
import com.galarzaIvan.movies.utils.AppExecutors;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MovieInfoActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler {
    private final static String TAG = "AppCompatActivity";
    private Context mContext;

    private Movie mMovieData;
    private List<TrailerInfo> mTrailerInfoList;
    private List<Review> mReviewList;

    private MovieRequests mMovieRequests;

    private Boolean isFavorite = false;
    private String mTrailerKey;
    private Boolean isOnline = false;

    // Information Views
    private ImageView mMovieBackdropImage;
    private ImageView mMoviePosterImage;
    private TextView mMovieTittle;
    private TextView mMovieVoteAverage;
    private TextView mMovieReleaseDate;
    private TextView mMovieDescription;
    private RatingBar mMovieRating;

    // Extra Views
    private TextView mTrailerSubtitle;
    private TextView mReviewSubtitle;
    private ProgressBar mProgressBar;
    private TextView mLoadingText;

    // Recyclers
    private RecyclerView mTrailersRecycler;
    private RecyclerView mReviewsRecycler;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    //DataBase
    private AppDatabase mDb;
    private Favorite mFavorite = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);
        mContext = this;
        netWorkConnection();

        Intent intent = getIntent();
        String data = null;

        if (intent == null || !intent.hasExtra(AppConstants.MOVIE_EXTRA)) {
            Toast.makeText(this, R.string.movie_without_information, Toast.LENGTH_LONG).show();
            finish();
        } else {
            data = intent.getStringExtra(AppConstants.MOVIE_EXTRA);
        }

        if (data == null)
            finish();

        mMovieData = new Gson().fromJson(data, Movie.class);

        initViews(); // Init Views
        initDB(); // Init DataBase
        checkIfIsFavorite(); // I pass a movie to this activity. So, I check if that movie is a favorite
        initRetrofit(); // Init retrofit
        configTrailersRecyclerView(); // Init RecyclerView for Trailers
        configReviewsRecyclerView(); // Init RecyclerView for Reviews
        loadViews(); // Load all movie data into the views
        getMoviesTrailers(); // Download trailers
        getMoviesReviews(); // Download Reviews
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

    private void checkIfIsFavorite() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mFavorite = mDb.favoriteDao().getFavoriteMovie(mMovieData);
                if (mFavorite != null) {
                    isFavorite = true;
                    if (!isOnline) {
                        // If is favorite and it's offline, Load data from room
                        if (mFavorite.getTrailerInfo().size() > 0) {
                            mTrailerKey = mFavorite.getTrailerInfo().get(0).getKey();
                            mTrailerAdapter.setTrailerList(mFavorite.getTrailerInfo());
                            showTrailers(true);
                        }
                        if (mFavorite.getReview().size() > 0) {
                            mReviewAdapter.setReviewList(mFavorite.getReview());
                            showReviews(true);
                        }
                    }
                } else {
                    isFavorite = false;
                }
            }
        });
    }

    private void initDB() {
        mDb = AppDatabase.getInstance(mContext);
    }

    private void initViews() {
        // Information movie
        mMovieBackdropImage = (ImageView) findViewById(R.id.iv_movieInfo_backdrop);
        mMoviePosterImage = (ImageView) findViewById(R.id.iv_movieInfo_poster);

        mMovieTittle = (TextView) findViewById(R.id.tv_movieInfo_tittle);
        mMovieVoteAverage = (TextView) findViewById(R.id.tv_movieInfo_voteAverage);
        mMovieReleaseDate = (TextView) findViewById(R.id.tv_movieInfo_releaseDate);
        mMovieDescription = (TextView) findViewById(R.id.tv_movieInfo_description);

        mMovieRating = (RatingBar) findViewById(R.id.rb_movieInfo_rating);

        //recyclers
        mTrailersRecycler = (RecyclerView) findViewById(R.id.rv_trailers);
        mReviewsRecycler = (RecyclerView) findViewById(R.id.rv_reviews);

        //Extra views
        mTrailerSubtitle = (TextView) findViewById(R.id.tv_subTitle_trailer);
        mReviewSubtitle = (TextView) findViewById(R.id.tv_subTitle_review);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_movieInformation);
        mLoadingText = (TextView) findViewById(R.id.tv_loading_movieInformation);
    }

    private void initRetrofit() {
        Retrofit retrofit = RetrofitController.getInstance();
        mMovieRequests = retrofit.create(MovieRequests.class);
    }

    private void configTrailersRecyclerView() {
        mTrailersRecycler.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                false);

        mTrailersRecycler.setLayoutManager(linearLayoutManager);
        mTrailerAdapter = new TrailerAdapter(this);
        mTrailersRecycler.setAdapter(mTrailerAdapter);
    }

    private void configReviewsRecyclerView() {
        mReviewsRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                false);
        mReviewsRecycler.setLayoutManager(linearLayoutManager);
        mReviewAdapter = new ReviewAdapter();
        mReviewsRecycler.setAdapter(mReviewAdapter);
    }

    private void loadViews() {
        String backdropUrl = MovieDBConstants.BASE_BACKDROP_IMAGE_URL + mMovieData.getBackdropPath();
        String posterUrl = MovieDBConstants.BASE_IMAGE_URL + mMovieData.getPosterPath();

        Picasso.get()
                .load(backdropUrl)
                .error(R.drawable.image_not_found)
                .placeholder(R.drawable.progress_animation)
                .into(mMovieBackdropImage);

        Picasso.get()
                .load(posterUrl)
                .error(R.drawable.image_not_found)
                .placeholder(R.drawable.progress_animation)
                .into(mMoviePosterImage);

        mMovieTittle.setText(mMovieData.getTitle());

        /*
         * Calculate the average based on 5 stars instead of the average of 10 stars that
         * I get from MovieDB
         */
        float rating = (float) (mMovieData.getVoteAverage() * 5) / 10;
        mMovieRating.setRating(rating);
        mMovieVoteAverage.setText(String.valueOf(rating));

        mMovieReleaseDate.setText(mMovieData.getReleaseDate());

        mMovieDescription.setText(mMovieData.getOverview());
    }

    private void getMoviesTrailers() {
        if (isOnline) {
            showLoading(true);
            showTrailers(false);
            Call<TrailerResponse> myCall = mMovieRequests.getMoviesTrailers(
                    mMovieData.getId(),
                    MovieDBConstants.API_KEY,
                    MovieDBConstants.LANGUAGE);

            myCall.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Log.e(TAG, "onResponseTrailer: " + response.body().getResults());

                            mTrailerInfoList = response.body().getResults();
                            showLoading(false);

                            if (mTrailerInfoList.size() > 0) {
                                showTrailers(true);
                                mTrailerKey = mTrailerInfoList.get(0).getKey();
                            } else {
                                mTrailerKey = null;
                                showTrailers(false);
                            }

                            mTrailerAdapter.setTrailerList(response.body().getResults());
                        } else {
                            //showError(getString(R.string.default_error_message));
                            mTrailerAdapter.setTrailerList(null);
                        }
                    } else {
                        //showError(getString(R.string.default_error_message));
                        Log.e(TAG, "Unsuccessful call: " + response.toString());
                    }
                }

                @Override
                public void onFailure(Call<TrailerResponse> call, Throwable t) {
                    //showError(getString(R.string.default_error_message));
                    Log.e(TAG, "onFailure: " + t.toString());
                }
            });
        } else {
            showLoading(false);
            showTrailers(false);
        }
    }

    private void getMoviesReviews() {
        if (isOnline) {
            Call<ReviewResponse> myCall = mMovieRequests.getMoviesReviews(
                    mMovieData.getId(),
                    MovieDBConstants.API_KEY,
                    MovieDBConstants.LANGUAGE);

            myCall.enqueue(new Callback<ReviewResponse>() {
                @Override
                public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Log.e(TAG, "onResponseReview: " + response.body().getResults());

                            showLoading(false);
                            mReviewList = response.body().getResults();

                            if (mReviewList.size() > 0) {
                                showReviews(true);
                            } else {
                                showReviews(false);
                            }

                            mReviewAdapter.setReviewList(response.body().getResults());
                        } else {
                            //showError(getString(R.string.default_error_message));
                            mTrailerAdapter.setTrailerList(null);
                        }
                    } else {
                        //showError(getString(R.string.default_error_message));
                        Log.e(TAG, "Unsuccessful call: " + response.toString());
                    }
                }

                @Override
                public void onFailure(Call<ReviewResponse> call, Throwable t) {
                    //showError(getString(R.string.default_error_message));
                    Log.e(TAG, "onFailure: " + t.toString());
                }
            });
        } else {
            showLoading(false);
            showReviews(false);
        }
    }

    // Favorite icon menu
    private void changeFavoriteState(MenuItem item) {
        if (isFavorite) {
            // If is favorite
            isFavorite = false;
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.favoriteDao().deleteFavorite(mFavorite.getMovie());
                }
            });
            changeFavoriteColorIcon(item);
        } else {
            // Change to accent color
            isFavorite = true;
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.favoriteDao().insertFavorite(new Favorite(mMovieData, mTrailerInfoList, mReviewList));
                }
            });
            changeFavoriteColorIcon(item);
        }
    }

    private void changeFavoriteColorIcon(MenuItem item) {
        if (isFavorite) {
            // Is Favorite
            DrawableCompat.setTint(
                    DrawableCompat.wrap(item.getIcon()),
                    ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)
            );
        } else {

            // Isn't Favorite
            DrawableCompat.setTint(
                    DrawableCompat.wrap(item.getIcon()),
                    ContextCompat.getColor(getApplicationContext(), R.color.whiteColor)
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_information_menu, menu);
        MenuItem item = (MenuItem) menu.findItem(R.id.favorite_menu);
        changeFavoriteColorIcon(item);
        return true;
    }

    // Shared Trailer
    private void shareMovie() {
        if (mTrailerKey != null) {
            String youtubeURL = AppConstants.BASE_YOUTUBE_URL + mTrailerKey;
            String message = getString(R.string.share_message_trailer) + youtubeURL;

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, message);
            sendIntent.setType("text/html");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        } else {
            Toast.makeText(mContext, R.string.no_trailer_to_share, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void trailerClickListener(TrailerInfo trailerInfo) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.BASE_YOUTUBE_PATH_URI + trailerInfo.getKey()));
        intent.putExtra(AppConstants.VIDEO_ID, trailerInfo.getKey());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuItemThatWasSelected = item.getItemId();

        switch (menuItemThatWasSelected) {
            case (R.id.favorite_menu):
                changeFavoriteState(item);
                break;
            case (R.id.share_menu):
                shareMovie();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTrailers(boolean show) {
        if (show) {
            mTrailerSubtitle.setVisibility(View.VISIBLE);
            mTrailersRecycler.setVisibility(View.VISIBLE);
        } else {
            mTrailerSubtitle.setVisibility(View.GONE);
            mTrailersRecycler.setVisibility(View.GONE);
        }
    }

    private void showReviews(boolean show) {
        if (show) {
            mReviewSubtitle.setVisibility(View.VISIBLE);
            mReviewsRecycler.setVisibility(View.VISIBLE);
        } else {
            mReviewSubtitle.setVisibility(View.GONE);
            mReviewsRecycler.setVisibility(View.GONE);
        }
    }

    private void showLoading(boolean show) {
        if (show) {
            mProgressBar.setVisibility(View.VISIBLE);
            mLoadingText.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mLoadingText.setVisibility(View.GONE);
        }
    }
}
