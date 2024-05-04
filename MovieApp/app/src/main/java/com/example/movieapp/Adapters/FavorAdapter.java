package com.example.movieapp.Adapters;

import android.content.Context;
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
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;
import com.example.movieapp.utils.Credentials;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.Collections;
import java.util.List;

public class FavorAdapter extends RecyclerView.Adapter<FavorAdapter.ViewHolder>{

    Context context;
    List<MovieModel> movies;
    FavorInterface favor_click;

    public FavorAdapter(Context context, List<MovieModel> movies, FavorInterface favor_click){
        this.context = context;
        this.movies = movies;
        this.favor_click = favor_click;
    }
    public void setFavorHistoryMovies(List<MovieModel> favHisMovies){
        this.movies = favHisMovies;
        notifyDataSetChanged();
    }
    public void sort_by_release_date(int sort){
        if(getItemCount() > 0){
            if(sort ==1){
                Collections.sort(movies, (o1, o2) -> o2.getRelease_date().compareTo(o1.getRelease_date()));
            }else if(sort == 2){
                Collections.sort(movies, (o1, o2) -> o1.getRelease_date().compareTo(o2.getRelease_date()));
            }else if(sort == 0){
                Collections.sort(movies, (o1, o2) -> o2.getFavorTime().compareTo(o1.getFavorTime()));
            }
        }
    }

    public void sort_by_rating(int sort){
        if(getItemCount() > 0){
            if(sort ==1){
                Collections.sort(movies, (o1, o2) -> (int)(o2.getVote_average()*10 - o1.getVote_average()*10));
            }else if(sort==2){
                Collections.sort(movies, (o1, o2) -> (int)(o1.getVote_average()*10 - o2.getVote_average()*10));
            }else if(sort == 0){
                Collections.sort(movies, (o1, o2) -> o2.getFavorTime().compareTo(o1.getFavorTime()));
            }
        }
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
            MovieModel movie = movies.get(position);
            holder.title_film_item_fv_history.setText(movie.getTitle());
            holder.gerne_favor.setText(movie.getGenresString());
            holder.removeBtn.setChecked(true);
            holder.publishYear.setText("("+movies.get(position).getPublishDate()+")");

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

    @Override
    public int getItemCount() {
        if(movies!=null){return movies.size();
        }
        return 0;
    }

    class ViewHolder extends  RecyclerView.ViewHolder{
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
