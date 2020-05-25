package com.galarzaIvan.movies.requests;

import com.galarzaIvan.movies.constants.MovieDBConstants;
import com.galarzaIvan.movies.models.MovieDbResponse;

import retrofit2.Call;
import retrofit2.http.GET;
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
}

