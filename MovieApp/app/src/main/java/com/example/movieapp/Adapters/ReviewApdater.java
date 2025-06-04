package com.example.movieapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.data.Model.DTO.InteractionDTO.*;
import com.example.movieapp.data.Model.DetailModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;

import java.util.Collections;
import java.util.List;

public class ReviewApdater extends RecyclerView.Adapter<ReviewApdater.ViewHolder> {
    List<InteractionDAOReview> detailModels;
    Context context;
    int userId;
    int movie_id;

    DeleteInterface deleteInterface;

    public interface DeleteInterface {
        public void delete();
    }

    public ReviewApdater(Context context, List<InteractionDAOReview> detailModels, int userId, DeleteInterface deleteInterface) {
        this.context = context;
        this.detailModels = detailModels;
        this.userId = userId;
        this.deleteInterface = deleteInterface;
        removeEmpty();
    }

    public void removeEmpty() {
        for (int i = 0; i < this.detailModels.size(); i++) {
            InteractionDAOReview detailModel = detailModels.get(i);
            if (detailModel.getReview() == null){
                detailModels.remove(i);
                i--;
            }
        }
    }

    @NonNull
    @Override
    public ReviewApdater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_rating_view, parent, false);
            ReviewApdater.ViewHolder viewHolder = new ReviewApdater.ViewHolder(view);
            return viewHolder;
        } else if(viewType == 1){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_rating_view_user, parent, false);
            ReviewApdater.ViewHolder viewHolder = new ReviewApdater.ViewHolder(view);
            return viewHolder;
        }else{
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewApdater.ViewHolder holder, int position) {
        int i = position;
        InteractionDAOReview detailModel = detailModels.get(i);
        holder.rating.setRating(detailModel.getRating());
        holder.username.setText("" + detailModel.getName());
        holder.review.setText("" + detailModel.getReview());
        holder.date.setText("" + detailModel.getTime());

        new ImageLoader().loadAvatar(context, detailModel.getAvatar(), holder.avatar, detailModel.getName());
        
        if (detailModels.get(position).getUser_id() == this.userId) {
            Button cancelBtn = holder.itemView.findViewById(R.id.cancel_btn);
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detailModels.remove(i);
                    notifyDataSetChanged();
                    deleteInterface.delete();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (detailModels.get(position).getUser_id() == this.userId) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        if (detailModels != null)
            return detailModels.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username, date, review;
        RatingBar rating;
        ImageView avatar;

        public ViewHolder(@NonNull View viewItem) {
            super(viewItem);
            this.username = viewItem.findViewById(R.id.user_name_cm_view);
            this.date = viewItem.findViewById(R.id.date_comment_view);
            this.review = viewItem.findViewById(R.id.user_comment_view);
            this.rating = viewItem.findViewById(R.id.movie_rating);
            this.avatar = viewItem.findViewById(R.id.imageAvatar);
        }
    }
}
