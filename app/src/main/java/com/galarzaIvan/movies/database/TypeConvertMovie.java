package com.galarzaIvan.movies.database;

import androidx.room.TypeConverter;

import com.galarzaIvan.movies.models.Movie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class TypeConvertMovie {

    @TypeConverter
    public String formMovie(Movie movie) {
        if (movie == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Movie>() {}.getType();
        String json = gson.toJson(movie, type);
        return json;
    }

    @TypeConverter
    public Movie toMovie(String MovieString) {
        if (MovieString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Movie>() {}.getType();
        Movie movie = gson.fromJson(MovieString, type);
        return movie;
    }

}
