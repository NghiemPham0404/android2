package com.example.movieapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.Model.MoviesGroup;
import com.example.movieapp.R;

import java.util.List;

public class MoviesGroupAdapter extends RecyclerView.Adapter<MoviesGroupAdapter.ViewHolder>{


    Context context;
    List<MoviesGroup> moviesGroups;

    String userId;


    public MoviesGroupAdapter(Context context, List<MoviesGroup> moviesGroups) {
        this.context = context;
        this.moviesGroups= moviesGroups;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    @NonNull
    @Override
    public MoviesGroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.film_group, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesGroupAdapter.ViewHolder holder, int position) {
        holder.groupTitle.setText(moviesGroups.get(position).getTitle());
        if(moviesGroups.get(position).getTitle().equals("Popular")){
            initMovieGroupMovie(holder.moviesGroupRecycler, moviesGroups.get(position).getMovies(), 1);
        }else{
            initMovieGroupMovie(holder.moviesGroupRecycler, moviesGroups.get(position).getMovies());
        }

    }

    @Override
    public int getItemCount() {
       return  moviesGroups.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
            TextView groupTitle;
            RecyclerView moviesGroupRecycler;

            public ViewHolder(@NonNull View itemView){
                    super(itemView);
                    this.groupTitle = itemView.findViewById(R.id.groupTitle);
                    this.moviesGroupRecycler = itemView.findViewById(R.id.movieGroup_recyclerview);
            }
    }

    private void initMovieGroupMovie(RecyclerView moviesRecyclerView, List<MovieModel> movies){
        if(this.moviesGroups.size() > 1){
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.context , RecyclerView.HORIZONTAL, false);
           moviesRecyclerView.setLayoutManager(linearLayoutManager);
        }else{
            moviesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }
        FilmAdapter filmAdapter = new FilmAdapter(movies, context);
        filmAdapter.setUserId(userId);
        moviesRecyclerView.setAdapter(filmAdapter);
    }

    private void initMovieGroupMovie(RecyclerView moviesRecyclerView, List<MovieModel> movies, int view_type){
        if(this.moviesGroups.size() > 1){
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.context , RecyclerView.HORIZONTAL, false);
            moviesRecyclerView.setLayoutManager(linearLayoutManager);
        }else{
            moviesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }

        FilmAdapter filmAdapter = new FilmAdapter(movies, context, view_type);
        filmAdapter.setUserId(userId);
        moviesRecyclerView.setAdapter(filmAdapter);
    }
}
