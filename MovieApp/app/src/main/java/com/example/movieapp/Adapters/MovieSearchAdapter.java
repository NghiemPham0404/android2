package com.example.movieapp.Adapters;

import android.content.Context;
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
import com.example.movieapp.View.MovieInteractive;
import com.example.movieapp.utils.Credentials;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

public class MovieSearchAdapter extends RecyclerView.Adapter<MovieSearchAdapter.ViewHolder> {

    Context context;
    List<MovieModel> movies;
    AccountModel loginAccount;

    public MovieSearchAdapter(Context context, List<MovieModel> movies, AccountModel loginAccount){
        this.context = context;
        this.movies = movies;
        this.loginAccount = loginAccount;
    }

    public void setMovies(List<MovieModel> movies){
        this.movies = movies;
    }

    @NonNull
    @Override
    public MovieSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.film_item_search, parent, false);
        return new MovieSearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieSearchAdapter.ViewHolder holder, int position) {
        MovieModel movie = movies.get(position);
        holder.title.setText(movie.getTitle());
        holder.publish_date.setText("("+movie.getPublishDate()+")");
        holder.rating.setText(movie.getRating());
        new ImageLoader().loadImageIntoImageView(context, Credentials.BASE_IMAGE_URL+ movie.getPoster_path(), holder.imageView, holder.shimmerFrameLayout);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieInteractive.openMovieInformation(context, movie.getId(), loginAccount);
            }
        });
    }

    @Override
    public int getItemCount() {
       if(movies!=null){
           return movies.size();
       }else{
           return 0;
       }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, publish_date, rating;
        ShimmerFrameLayout shimmerFrameLayout;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_film_search);
            publish_date = itemView.findViewById(R.id.publishYear_search);
            rating = itemView.findViewById(R.id.movie_rating);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmer_layout);
            imageView = itemView.findViewById(R.id.movie_poster_favor);
        }
    }
}
