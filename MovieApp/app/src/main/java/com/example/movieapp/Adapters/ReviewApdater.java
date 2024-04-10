package com.example.movieapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Model.DetailModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;

import java.util.Collections;
import java.util.List;

public class ReviewApdater extends RecyclerView.Adapter<ReviewApdater.ViewHolder> {
    List<DetailModel> detailModels;
    Context context;

    public ReviewApdater(Context context,  List<DetailModel> detailModels){
        this.context = context;
        this.detailModels = detailModels;
        Collections.sort(this.detailModels, (o1, o2) -> o2.getTime().compareTo(o1.getTime()));
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
            holder.rating.setRating(Float.parseFloat(detailModel.getRating()));
            new ImageLoader().loadAvatar(context,detailModel.getAvatar(),holder.avatar, holder.avatar_text, detailModel.getUsername());
    }

    @Override
    public int getItemCount() {
        if(detailModels!=null)
            return detailModels.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView username, date, review, avatar_text;
        RatingBar rating;
        ImageView avatar;
        public ViewHolder(@NonNull View viewItem){
            super(viewItem);
            this.avatar_text = viewItem.findViewById(R.id.avatarText);
            this.username = viewItem.findViewById(R.id.user_name_cm_view);
            this.date = viewItem.findViewById(R.id.date_comment_view);
            this.review = viewItem.findViewById(R.id.user_comment_view);
            this.rating = viewItem.findViewById(R.id.movie_rating);
            this.avatar = viewItem.findViewById(R.id.imageAvatar);
        }
    }
}
