package com.galarzaIvan.movies.classes;

import com.galarzaIvan.movies.constants.MovieDBConstants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitController {

    private static Retrofit mRetrofitInstance;

    public static Retrofit getInstance() {
        if (mRetrofitInstance == null) {
            mRetrofitInstance = new Retrofit.Builder()
                    .baseUrl(MovieDBConstants.HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            return mRetrofitInstance;
        } else {
            return mRetrofitInstance;
        }
    }

}
