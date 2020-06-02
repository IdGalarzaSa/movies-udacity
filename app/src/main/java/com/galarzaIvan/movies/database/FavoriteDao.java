package com.galarzaIvan.movies.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.galarzaIvan.movies.models.Movie;

import java.util.List;


@Dao
public interface FavoriteDao {

    @Query("SELECT movie FROM favorite")
    List<Movie> getFavoritesMovies();

    @Query("SELECT * FROM favorite")
    List<Favorite> getFavorites();

    // Return null if doesn't exist that id
    @Query("SELECT * FROM favorite WHERE id=:id")
    Favorite loadFavoriteById(int id);

    @Insert
    void insertFavorite(Favorite favorite);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFavorite(Favorite favorite);

    @Delete
    void deleteFavorite(Favorite favorite);

}
