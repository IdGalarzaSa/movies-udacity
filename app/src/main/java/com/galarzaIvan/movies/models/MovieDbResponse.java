package com.galarzaIvan.movies.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieDbResponse {
    public Integer page;
    @SerializedName("total_results")
    public Integer totalResults;
    @SerializedName("total_pages")
    public Integer totalPages;
    public List<Movie> results;
}