package com.galarzaIvan.movies.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.galarzaIvan.movies.models.Movie;

import java.util.List;


@Dao
public interface FavoriteDao {

    @Query("SELECT movie FROM favorite")
    List<Movie> getFavoritesMovies();

    // Return null if doesn't exist that id
    @Query("SELECT * FROM favorite WHERE id=:id")
    Favorite getFavoriteById(int id);

    @Query("SELECT * FROM favorite WHERE movie=:movie")
    Favorite getFavoriteMovie(Movie movie);

    @Insert
    void insertFavorite(Favorite favorite);

    @Delete
    void deleteFavorite(Favorite favorite);

}
