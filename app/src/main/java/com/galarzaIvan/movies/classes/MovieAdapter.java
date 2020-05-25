package com.galarzaIvan.movies.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.galarzaIvan.movies.R;
import com.galarzaIvan.movies.constants.MovieDBConstants;
import com.galarzaIvan.movies.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder>{

    private final static String TAG = "MovieAdapter";
    private List<Movie> mMovieList;


    public void setMovieList(List<Movie> movies){
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
        Movie movie = mMovieList.get(position);
        holder.setMovie(movie);
    }

    @Override
    public int getItemCount() {
        if (this.mMovieList == null)
            return 0;
        return mMovieList.size();
    }

    static class MovieAdapterViewHolder extends RecyclerView.ViewHolder {
        private ImageView mMoviePoster;
        MovieAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            // Init ImageView
            mMoviePoster= (ImageView) itemView.findViewById(R.id.iv_moviePoster);
        }

        private void setMovie(Movie movie){
            String url = MovieDBConstants.BASE_IMAGE_URL +movie.getPosterPath();
            Picasso.get()
                    .load(url)
                    .error(R.drawable.image_not_found)
                    .placeholder(R.drawable.progress_animation)
                    .into(mMoviePoster);
        }
    }

}
