package com.galarzaIvan.movies.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.galarzaIvan.movies.R;
import com.galarzaIvan.movies.models.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {
    private final String TAG = "ReviewAdapter";
    private List<Review> mReviewList;


    public void setReviewList(List<Review> reviews){
        mReviewList = reviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater reviewInflater = LayoutInflater.from(context);
        View newView = reviewInflater.inflate(R.layout.movie_review_item, parent, false);
        return new ReviewAdapterViewHolder(newView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapterViewHolder holder, int position) {
        Review review = mReviewList.get(position);
        holder.setReview(review);
    }

    @Override
    public int getItemCount() {
        if (this.mReviewList == null)
            return 0;
        return mReviewList.size();
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder{

        private TextView mReview;
        private TextView mAuthor;

        ReviewAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mReview = (TextView) itemView.findViewById(R.id.tv_review);
            mAuthor = (TextView) itemView.findViewById(R.id.tv_author);
        }

        private void setReview(Review review){
            mReview.setText(review.getContent());
            mAuthor.setText(review.getAuthor());
        }

    }

}