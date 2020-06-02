package com.galarzaIvan.movies.requests;

import com.galarzaIvan.movies.constants.MovieDBConstants;
import com.galarzaIvan.movies.models.MovieDbResponse;
import com.galarzaIvan.movies.models.ReviewResponse;
import com.galarzaIvan.movies.models.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieRequests {

    @GET(MovieDBConstants.POPULAR_MOVIES)
    Call<MovieDbResponse> getPopularMovies(
            @Query(MovieDBConstants.KEY_PARAM) String apiKey,
            @Query(MovieDBConstants.LANGUAGE_PARAM) String language);

    @GET(MovieDBConstants.TOP_RATED_MOVIES)
    Call<MovieDbResponse> getMoviesTopRated(
            @Query(MovieDBConstants.KEY_PARAM) String apiKey,
            @Query(MovieDBConstants.LANGUAGE_PARAM) String language);

    @GET(MovieDBConstants.GET_VIDEOS)
    Call<TrailerResponse> getMoviesTrailers(
            @Path("id") int movieId,
            @Query(MovieDBConstants.KEY_PARAM) String apiKey,
            @Query(MovieDBConstants.LANGUAGE_PARAM) String language);

    @GET(MovieDBConstants.GET_REVIEWS)
    Call<ReviewResponse> getMoviesReviews(
            @Path("id") int movieId,
            @Query(MovieDBConstants.KEY_PARAM) String apiKey,
            @Query(MovieDBConstants.LANGUAGE_PARAM) String language);

}

