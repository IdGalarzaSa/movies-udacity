package com.galarzaIvan.movies.database;

import androidx.room.TypeConverter;

import com.galarzaIvan.movies.models.TrailerInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class TypeConvertTrailers {

    @TypeConverter
    public String fromTrailerList(List<TrailerInfo> trailerInfoList) {
        if (trailerInfoList == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<TrailerInfo>>() {}.getType();
        String json = gson.toJson(trailerInfoList, type);
        return json;
    }

    @TypeConverter
    public List<TrailerInfo> toTrailerList(String trailerListString) {
        if (trailerListString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<TrailerInfo>>() {}.getType();
        List<TrailerInfo> trailerInfoList = gson.fromJson(trailerListString, type);
        return trailerInfoList;
    }
}
