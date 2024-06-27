package com.example.movieapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;
import com.example.movieapp.View.MovieInfomation;
import com.example.movieapp.utils.Credentials;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.ViewHolder>{

    private List<MovieModel> movies;
    private Context context;
    private AccountModel loginAccount;

    public SliderAdapter(Context context, AccountModel loginAccount){
        this.context = context;
        this.loginAccount = loginAccount;
    }

    public void setMovies(List<MovieModel> movies){
        this.movies = movies;
    }

    @org.checkerframework.checker.nullness.qual.NonNull
    @Override
    public SliderAdapter.ViewHolder onCreateViewHolder(@org.checkerframework.checker.nullness.qual.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.film_item_2, parent, false);
        return new SliderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@org.checkerframework.checker.nullness.qual.NonNull SliderAdapter.ViewHolder holder, int position) {
        MovieModel movie = movies.get(position);
        new ImageLoader().loadImageIntoImageView(context, Credentials.BASE_IMAGE_URL + movie.getBackdrop_path(), holder.imageView);
        holder.movie_name.setText(movie.getTitle());
        holder.movie_status.setText("");
        float rating = Math.round(movie.getVote_average()*10)*1.0f/10;
        holder.movie_rating.setText(rating+"");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MovieInfomation.class);
                intent.putExtra("loginAccount", (Parcelable) loginAccount);
                intent.putExtra("film_id", movie.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(movies != null){
            return movies.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView movie_name, movie_rating, movie_status;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.movie_backdrop);
            movie_name = itemView.findViewById(R.id.title_film_item_Lbl2);
            movie_status = itemView.findViewById(R.id.status_movie_lbl);
            movie_rating = itemView.findViewById(R.id.movie_rating);
        }
    }
}
