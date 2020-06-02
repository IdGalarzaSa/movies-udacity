package com.galarzaIvan.movies.database;

import androidx.room.TypeConverter;

import com.galarzaIvan.movies.models.Review;
import com.galarzaIvan.movies.models.TrailerInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class TypeConvertReviews {

    @TypeConverter
    public String fromReviewsList(List<Review> reviewsList) {
        if (reviewsList == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Review>>() {}.getType();
        String json = gson.toJson(reviewsList, type);
        return json;
    }

    @TypeConverter
    public List<Review> toReviewsList(String reviewsString) {
        if (reviewsString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Review>>() {}.getType();
        List<Review> reviews = gson.fromJson(reviewsString, type);
        return reviews;
    }

}
