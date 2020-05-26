package com.galarzaIvan.movies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.galarzaIvan.movies.constants.AppConstants;
import com.galarzaIvan.movies.constants.MovieDBConstants;
import com.galarzaIvan.movies.models.Movie;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class MovieInfoActivity extends AppCompatActivity {
    private final static String TAG = "AppCompatActivity";

    private Movie mMovieData;

    private ImageView mMovieBackdropImage;
    private ImageView mMoviePosterImage;
    private TextView mMovieTittle;
    private TextView mMovieVoteAverage;
    private TextView mMovieReleaseDate;
    private TextView mMovieDescription;
    private RatingBar mMovieRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(AppConstants.MOVIE_EXTRA)) {
            String data = intent.getStringExtra(AppConstants.MOVIE_EXTRA);
            mMovieData = new Gson().fromJson(data,Movie.class);
            initViews();
            loadViews();
        } else {
            Toast.makeText(this, "This movie hasn't information", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initViews() {
        mMovieBackdropImage = (ImageView) findViewById(R.id.iv_movieInfo_backdrop);
        mMoviePosterImage = (ImageView) findViewById(R.id.iv_movieInfo_poster);

        mMovieTittle = (TextView) findViewById(R.id.tv_movieInfo_tittle);
        mMovieVoteAverage = (TextView) findViewById(R.id.tv_movieInfo_voteAverage);
        mMovieReleaseDate = (TextView) findViewById(R.id.tv_movieInfo_releaseDate);
        mMovieDescription = (TextView) findViewById(R.id.tv_movieInfo_description);

        mMovieRating = (RatingBar) findViewById(R.id.rb_movieInfo_rating);
    }

    private void loadViews() {

        String backdropUrl = MovieDBConstants.BASE_BACKDROP_IMAGE_URL+mMovieData.getBackdropPath();
        String posterUrl = MovieDBConstants.BASE_IMAGE_URL+mMovieData.getPosterPath();

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

}
