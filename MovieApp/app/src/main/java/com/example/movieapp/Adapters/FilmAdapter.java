package com.example.movieapp.Adapters;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;
import com.example.movieapp.View.Movie_infomation;
import com.example.movieapp.utils.Credentials;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.ViewHolder>{

   public List<MovieModel> movies;
    private Context context;
    int view_type = 0;
   public AccountModel loginAccount;

    public FilmAdapter(List<MovieModel> movies, Context context,  AccountModel loginAccount) {
        this.movies = movies;
        this.context = context;
        this.loginAccount = loginAccount;
    }
    public FilmAdapter(Context context,  AccountModel loginAccount) {
        this.context = context;
        this.loginAccount = loginAccount;
    }

    public void setMovies(List<MovieModel> movies){
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

            // set rating number
            float rate = this.movies.get(position).getVote_average();
            float format_rate = (float) (Math.round(rate*100)*1.0/100);
            viewHolder.movie_rating.setText(format_rate+"");

            try {
                String[] date_split = this.movies.get(position).getRelease_date().split(",");
                int year = Integer.parseInt(date_split[date_split.length-1]);
                viewHolder.publish_date.setText(year+"");
            } catch (Exception e) {
                try {
                    String year =this.movies.get(position).getRelease_date().split("-")[0];
                    viewHolder.publish_date.setText(year);
                }catch (Exception ex){
                    viewHolder.publish_date.setText(this.movies.get(position).getRelease_date());
                }
            }

            // set poster for a film
            new ImageLoader().loadImageIntoImageView(context, Credentials.BASE_IMAGE_URL + this.movies.get(position).getPoster_path(), viewHolder.movie_poster, viewHolder.shimmerFrameLayout);
        int pos = position;
        //set action when click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Movie_infomation.class);
                intent.putExtra("film_id", movies.get(pos).getId());
                intent.putExtra("loginAccount", (Parcelable) loginAccount);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(movies != null)
            return movies.size();
        else
            return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder{
            TextView movie_rating, movie_title, publish_date;
            ShapeableImageView movie_poster;
            ShimmerFrameLayout shimmerFrameLayout;

            public ViewHolder(@NonNull View itemView){
                super(itemView);
                this.movie_rating = itemView.findViewById(R.id.movie_rating);
                this.movie_poster = itemView.findViewById(R.id.movie_poster);
                this.movie_title = itemView.findViewById(R.id.title_film_item_Lbl);
                this.publish_date = itemView.findViewById(R.id.publishYear_film_item);
                this.shimmerFrameLayout = itemView.findViewById(R.id.shimmer_layout);
            }
    }
}
