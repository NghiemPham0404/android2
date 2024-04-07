package com.example.movieapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Model.DetailModel;
import com.example.movieapp.R;

import java.util.List;

public class ReviewApdater extends RecyclerView.Adapter<ReviewApdater.ViewHolder> {
    List<DetailModel> detailModels;
    Context context;

    public ReviewApdater(Context context,  List<DetailModel> detailModels){
        this.context = context;
        this.detailModels = detailModels;
    }

    @NonNull
    @Override
    public ReviewApdater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_rating_view, parent, false);
        ReviewApdater.ViewHolder viewHolder = new ReviewApdater.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewApdater.ViewHolder holder, int position) {
            DetailModel detailModel = detailModels.get(position);
            holder.username.setText(""+detailModel.getUsername());
            holder.review.setText(""+detailModel.getReview());
            holder.date.setText(""+detailModel.getTime());
            holder.rating.setText(""+detailModel.getRating());
    }

    @Override
    public int getItemCount() {
        if(detailModels!=null)
            return detailModels.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView username, date, review, rating;
        public ViewHolder(@NonNull View viewItem){
            super(viewItem);
            this.username = viewItem.findViewById(R.id.user_name_cm_view);
            this.date = viewItem.findViewById(R.id.date_comment_view);
            this.review = viewItem.findViewById(R.id.user_comment_view);
            this.rating = viewItem.findViewById(R.id.movie_rating);
        }
    }
}
