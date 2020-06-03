package com.galarzaIvan.movies.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.galarzaIvan.movies.models.Movie;

import java.util.List;


@Dao
public interface FavoriteDao {

    @Query("SELECT movie FROM favorite")
    LiveData<List<Movie>> getFavoritesMovies();

    @Query("SELECT * FROM favorite WHERE movie=:movie")
    Favorite getFavoriteMovie(Movie movie);


    // I prefer use this query because I try to delete a register by Movie object
    @Query("DELETE FROM favorite WHERE movie=:movie")
    void deleteFavorite(Movie movie);

    @Insert
    void insertFavorite(Favorite favorite);
}
