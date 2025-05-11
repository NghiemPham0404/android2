package com.example.movieapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Interfaces.FavorInterface;
import com.example.movieapp.Request.MyService;
import com.example.movieapp.data.Model.DTO.InteractionDTO.*;
import com.example.movieapp.data.Model.MovieModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;
import com.example.movieapp.utils.Credentials;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.text.DateFormat;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavorAdapter extends RecyclerView.Adapter<FavorAdapter.ViewHolder>{

    Context context;
    List<InteractionDAOExtended> favHisMovies;
    FavorInterface favor_click;

    public FavorAdapter(Context context, List<InteractionDAOExtended> favHisMovies, FavorInterface favor_click){
        this.context = context;
        this.favHisMovies = favHisMovies;
        this.favor_click = favor_click;
    }

    @NonNull
    @Override
    public FavorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favor_history_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FavorAdapter.ViewHolder holder, int position) {
            Call<MovieModel> movieModelCall = MyService.getMovieApi().searchMovieDetail(favHisMovies.get(position).getMovie_id(), Credentials.API_KEY);
            movieModelCall.enqueue(new Callback<MovieModel>() {
                @Override
                public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                    if(response.isSuccessful()){
                        MovieModel movie = response.body();
                        InteractionDAOExtended interaction = favHisMovies.get(holder.getAdapterPosition());
                        movie.setDuration(interaction.getPlay_progress());
                        movie.setFavorTime(interaction.getTime_favor());
                        movie.setVideoUrl(interaction.getUrl());

                        holder.title_film_item_fv_history.setText(movie.getTitle());
                        holder.gerne_favor.setText(movie.getGenresString());
                        holder.removeBtn.setChecked(true);
                        holder.publishYear.setText("("+movie.getPublishDate()+")");

                        holder.movie_rating.setText(movie.getRating());

                        holder.runtime.setText(movie.getMaxDurationTime());

                        if(movie.getPlayBackPositition()>0){
                            holder.current_duration.setText(movie.getPlayBackPositionString());
                        }else{
                            holder.current_duration.setText("");
                        }
                        new ImageLoader().loadImageIntoImageView(context, Credentials.BASE_IMAGE_URL + movie.getPoster_path(), holder.movie_poster, holder.shimmerFrameLayout);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                favor_click.openMovie(movie.getId());
                            }
                        });

                        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                favor_click.changeFavorite(movie.getId());
                            }
                        });

                        favor_click.playMovie(movie,holder.playBtn);
                    }
                }

                @Override
                public void onFailure(Call<MovieModel> call, Throwable t) {
                    Log.i("Movie favor "+favHisMovies.get(holder.getAdapterPosition()).getMovie_id(), "fail");
                }
            });
    }

    @Override
    public int getItemCount() {
        if(favHisMovies!=null){return favHisMovies.size();
        }
        return 0;
    }

    static class ViewHolder extends  RecyclerView.ViewHolder{
        ImageView movie_poster;
        ShimmerFrameLayout shimmerFrameLayout;
        TextView title_film_item_fv_history;
        TextView gerne_favor;
        TextView publishYear;
        TextView movie_rating;

        TextView runtime;
        TextView current_duration;
        Button playBtn; ToggleButton removeBtn;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmer_layout);
            movie_poster = itemView.findViewById(R.id.movie_poster_favor);
            title_film_item_fv_history = itemView.findViewById(R.id.title_film_favor_history);
            gerne_favor = itemView.findViewById(R.id.gerne_favor_history);
            publishYear = itemView.findViewById(R.id.publishYear_favor_history);
            runtime = itemView.findViewById(R.id.runtime_favor);
            movie_rating = itemView.findViewById(R.id.movie_rating);
            current_duration = itemView.findViewById(R.id.current_duration);
            playBtn = itemView.findViewById(R.id.playBtn_favor);
            removeBtn = itemView.findViewById(R.id.removeBtn_favor);
        }
    }
}
