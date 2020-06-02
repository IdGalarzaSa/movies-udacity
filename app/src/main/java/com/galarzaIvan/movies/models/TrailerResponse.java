package com.galarzaIvan.movies.models;

import java.util.List;

public class TrailerResponse {
    private Integer id;
    private List<TrailerInfo> results = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<TrailerInfo> getResults() {
        return results;
    }

    public void setResults(List<TrailerInfo> results) {
        this.results = results;
    }
}
