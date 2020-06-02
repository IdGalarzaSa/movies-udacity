package com.galarzaIvan.movies.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.util.TableInfo;

import com.galarzaIvan.movies.models.Movie;
import com.galarzaIvan.movies.models.Review;
import com.galarzaIvan.movies.models.TrailerInfo;

import java.util.List;

@Entity(tableName = "favorite")
public class Favorite {

    @PrimaryKey(autoGenerate = true)
    int id;
    private Movie movie;
    private List<TrailerInfo> trailerInfo;
    private List<Review> review;

    @Ignore
    public Favorite(Movie movie, List<TrailerInfo> trailerInfo, List<Review> review) {
        this.movie = movie;
        this.trailerInfo = trailerInfo;
        this.review = review;
    }

    public Favorite(int id, Movie movie, List<TrailerInfo> trailerInfo, List<Review> review) {
        this.id = id;
        this.movie = movie;
        this.trailerInfo = trailerInfo;
        this.review = review;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public List<TrailerInfo> getTrailerInfo() {
        return trailerInfo;
    }

    public void setTrailerInfo(List<TrailerInfo> trailerInfo) {
        this.trailerInfo = trailerInfo;
    }

    public List<Review> getReview() {
        return review;
    }

    public void setReview(List<Review> review) {
        this.review = review;
    }
}
