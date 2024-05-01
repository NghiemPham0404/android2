package com.example.movieapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Model.DetailModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;
import com.example.movieapp.Request.MyService2;
import com.example.movieapp.utils.Credentials;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewApdater extends RecyclerView.Adapter<ReviewApdater.ViewHolder> {
    List<DetailModel> detailModels;
    Context context;
    String userId;
    int movie_id;

    DeleteInterface deleteInterface;

    public interface DeleteInterface {
        public void delete();
    }

    public ReviewApdater(Context context, List<DetailModel> detailModels, String userId, DeleteInterface deleteInterface) {
        this.context = context;
        this.detailModels = detailModels;
        Collections.sort(this.detailModels, (o1, o2) -> o2.getTimeAsDate().compareTo(o1.getTimeAsDate()));
        this.userId = userId;
        this.deleteInterface = deleteInterface;
        removeEmpty();
    }

    public void removeEmpty() {
        for (int i = 0; i < this.detailModels.size(); i++) {
            DetailModel detailModel = detailModels.get(i);
            try {
                Float.parseFloat(detailModel.getRating());
                Toast.makeText(context, "rating : "+detailModel.getRating(), Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                detailModels.remove(i);
            }
        }
        Toast.makeText(context, "total rating : "+detailModels.size(), Toast.LENGTH_SHORT).show();
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
        try {
            DetailModel detailModel = detailModels.get(position);
            holder.rating.setRating(Float.parseFloat(detailModel.getRating()));
            holder.username.setText("" + detailModel.getUsername());
            holder.review.setText("" + detailModel.getReview());
            holder.date.setText("" + detailModel.getTime());
            new ImageLoader().loadAvatar(context, detailModel.getAvatar(), holder.avatar, holder.avatar_text, detailModel.getUsername());
        }catch (Exception e){

        }


        if (detailModels.get(position).getUserId().equalsIgnoreCase(this.userId)) {
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
        if (detailModels.get(position).getUserId().equalsIgnoreCase(this.userId)) {
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
        TextView username, date, review, avatar_text;
        RatingBar rating;
        ImageView avatar;

        public ViewHolder(@NonNull View viewItem) {
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
