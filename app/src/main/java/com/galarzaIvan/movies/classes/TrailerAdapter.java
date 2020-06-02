package com.galarzaIvan.movies.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.galarzaIvan.movies.R;
import com.galarzaIvan.movies.models.TrailerInfo;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder>{

    private final String TAG = "TrailerAdapter";
    private List<TrailerInfo> mTrailerList;

    private final TrailerAdapterOnClickHandler mTrailerAdapterOnClickHandler;

    public TrailerAdapter(TrailerAdapterOnClickHandler clickHandler){
        mTrailerAdapterOnClickHandler = clickHandler;
    }

    public interface TrailerAdapterOnClickHandler{
        void trailerClickListener(TrailerInfo trailerInfo);
    }

    public void setTrailerList(List<TrailerInfo> trailers){
        mTrailerList = trailers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater trailerInflater = LayoutInflater.from(context);
        View newView = trailerInflater.inflate(R.layout.movie_trailer_item, parent, false);
        return new TrailerAdapterViewHolder(newView);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapterViewHolder holder, int position) {
        TrailerInfo trailerInfo = mTrailerList.get(position);
        holder.setTrailer(trailerInfo);
    }

    @Override
    public int getItemCount() {
        if (this.mTrailerList == null)
            return 0;
        return mTrailerList.size();
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTrailerName;

        TrailerAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mTrailerName = (TextView) itemView.findViewById(R.id.tv_trailer_name);
            itemView.setOnClickListener(this);
        }

        private void setTrailer(TrailerInfo trailer){
            mTrailerName.setText(trailer.getName());
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            TrailerInfo trailerInfo = mTrailerList.get(adapterPosition);
            mTrailerAdapterOnClickHandler.trailerClickListener(trailerInfo);

        }
    }

}
