package com.example.movieapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Interfaces.FavorInterface;
import com.example.movieapp.data.Model.DTO.InteractionDTO.InteractionDAOExtended;
import com.example.movieapp.data.Model.MovieModel;
import com.example.movieapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Setter;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{
    Context context;
    public List<InteractionDAOExtended> movies;
    FavorInterface favor_click;
    public List<List<InteractionDAOExtended>> moviesLists;
    public List<String> history_list;

    public void setMovies(List<InteractionDAOExtended> movies){
        this.movies = movies;
        initMoviesHistory();
        notifyDataSetChanged();
    }

    public HistoryAdapter(Context context, List<InteractionDAOExtended> movies, FavorInterface favor_click){
        this.context = context;
        this.favor_click = favor_click;
        this.movies = movies;
        history_list = new ArrayList<String>();
        moviesLists = new ArrayList<List<InteractionDAOExtended>>();
        Collections.sort(movies, (o1,o2) -> o2.getHistoryDate().compareTo(o1.getHistoryDate()));
        initMoviesHistory();
    }

    public void initMoviesHistory(){
        if(!movies.isEmpty()){
            Log.i("HISTORY TASK", "movie > 1");
            List<InteractionDAOExtended> movies_l = new ArrayList<InteractionDAOExtended>();
            history_list.add(movies.get(0).getHistory());
            movies_l.add(movies.get(0));
            for(int i = 1; i<this.movies.size(); i++){
                Log.i("HISTORY TASK", "film history : "+movies.get(i).getHistory());
                if(!history_list.contains(movies.get(i).getHistory())){
                    history_list.add(movies.get(i).getHistory());
                    Log.i("HISTORY TASK", "history list size 2 : "+history_list.size());
                    moviesLists.add(movies_l);
                    movies_l = new ArrayList<InteractionDAOExtended>();
                    movies_l.add(movies.get(i));
                }else{
                    movies_l.add(movies.get(i));
                }
            }
            moviesLists.add(movies_l);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.film_group, parent, false);
        HistoryAdapter.ViewHolder viewHolder = new HistoryAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.history_title.setText(history_list.get(position));
        FavorAdapter favorAdapter = new FavorAdapter(context, moviesLists.get(position), favor_click);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        holder.recyclerView.setLayoutManager( linearLayoutManager);
        holder.recyclerView.setAdapter(favorAdapter);
        favorAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(history_list!=null)
            return history_list.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public RecyclerView recyclerView;
        public TextView history_title;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            history_title = itemView.findViewById(R.id.groupTitle);
            recyclerView = itemView.findViewById(R.id.movieGroup_recyclerview);
        }
    }
}
