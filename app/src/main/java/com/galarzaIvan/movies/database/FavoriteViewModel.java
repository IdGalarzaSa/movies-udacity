package com.galarzaIvan.movies.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.galarzaIvan.movies.models.Movie;

import java.util.List;

public class FavoriteViewModel extends AndroidViewModel {

    // Tipo de data
    private LiveData<List<Movie>> movies;

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database  = AppDatabase.getInstance(this.getApplication());
        movies = database.favoriteDao().getFavoritesMovies();
    }

    public LiveData<List<Movie>> getMovies(){
        return movies;
    }

}
