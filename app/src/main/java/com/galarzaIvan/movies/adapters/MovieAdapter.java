package com.galarzaIvan.movies.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.galarzaIvan.movies.R;
import com.galarzaIvan.movies.classes.Movie;
import com.squareup.picasso.Picasso;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder>{

    private Movie[] mMovieList;

    public void setMovieList(Movie[] movies){
        mMovieList = movies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater movieInflater = LayoutInflater.from(context);

        View newView = movieInflater.inflate(R.layout.movie_item, parent, false);

        return new MovieAdapterViewHolder(newView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapterViewHolder holder, int position) {
        Movie movie = mMovieList[position];
        holder.setMovie(movie, position);
    }

    @Override
    public int getItemCount() {

        if (this.mMovieList == null)
            return 0;

        return mMovieList.length;
    }

    static class MovieAdapterViewHolder extends RecyclerView.ViewHolder {

        private ImageView mMoviePoster;
        private Movie mMovie;

        MovieAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            // Init view
            mMoviePoster= (ImageView) itemView.findViewById(R.id.iv_moviePoster);
        }

        private void setMovie(Movie movie, int position){
            this.mMovie = movie;
            Picasso.get().load("https://picsum.photos/id/"+position+"/400/700").into(mMoviePoster);
        }
    }

}
