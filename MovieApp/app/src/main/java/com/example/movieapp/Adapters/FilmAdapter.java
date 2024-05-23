package com.example.movieapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;
import com.example.movieapp.View.MovieInfomation;
import com.example.movieapp.utils.Credentials;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.ViewHolder> {

    public List<MovieModel> movies;
    private Context context;
    int view_type = 0;
    public AccountModel loginAccount;

    public FilmAdapter(List<MovieModel> movies, Context context, AccountModel loginAccount) {
        this.movies = movies;
        this.context = context;
        this.loginAccount = loginAccount;
    }

    public FilmAdapter(Context context, AccountModel loginAccount) {
        this.context = context;
        this.loginAccount = loginAccount;
    }

    public void setMovies(List<MovieModel> movies) {
        this.movies = movies;
    }

    @Override
    public int getItemViewType(int position) {
        return view_type;
    }

    @NonNull
    @Override
    public FilmAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.film_item, parent, false);
        return new FilmAdapter.ViewHolder(view);
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull FilmAdapter.ViewHolder holder, int position) {
        FilmAdapter.ViewHolder viewHolder = (FilmAdapter.ViewHolder) holder;
        viewHolder.movie_title.setText(this.movies.get(position).getTitle());

       viewHolder.movie_rating.setText(movies.get(position).getRating());

        viewHolder.publish_date.setText(this.movies.get(position).getPublishDate());

        // set poster for a film
        new ImageLoader().loadImageIntoImageView(context, Credentials.BASE_IMAGE_URL + this.movies.get(position).getPoster_path(), viewHolder.movie_poster, viewHolder.shimmerFrameLayout);
        int pos = position;
        //set action when click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MovieInfomation.class);
                intent.putExtra("film_id", movies.get(pos).getId());
                intent.putExtra("loginAccount", (Parcelable) loginAccount);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (movies != null)
            return movies.size();
        else
            return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView movie_rating, movie_title, publish_date;
        ShapeableImageView movie_poster;
        ShimmerFrameLayout shimmerFrameLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.movie_rating = itemView.findViewById(R.id.movie_rating);
            this.movie_poster = itemView.findViewById(R.id.movie_poster);
            this.movie_title = itemView.findViewById(R.id.title_film_item_Lbl);
            this.publish_date = itemView.findViewById(R.id.publishYear_film_item);
            this.shimmerFrameLayout = itemView.findViewById(R.id.shimmer_layout);
        }
    }
}
