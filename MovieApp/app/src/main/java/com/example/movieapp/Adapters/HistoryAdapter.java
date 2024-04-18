package com.example.movieapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Interfaces.FavorInterface;
import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.MovieModel;
import com.example.movieapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{
    Context context;

    public List<MovieModel> movies;
    FavorInterface favor_click;

    public List<List<MovieModel>> movies_lists;

    public List<String> history_list;

    public HistoryAdapter(Context context, List<MovieModel> movies, FavorInterface favor_click){
        this.context = context;
        this.favor_click = favor_click;
        this.movies = movies;
        history_list = new ArrayList<String>();
        movies_lists = new ArrayList<List<MovieModel>>();
        filterNonHistory();
        Collections.sort(movies, (o1,o2) -> o2.getHistoryDate().compareTo(o1.getHistoryDate()));
        initMoviesHistory();
    }

    public void initMoviesHistory(){
        if(movies.size()>0){
            Log.i("HISTORY TASK", "movie > 1");
            List<MovieModel> movies_l = new ArrayList<MovieModel>();
            history_list.add(movies.get(0).getHistory());
            movies_l.add(movies.get(0));
            for(int i = 1; i<this.movies.size(); i++){
                Log.i("HISTORY TASK", "film history : "+movies.get(i).getHistory());
                if(!history_list.contains(movies.get(i).getHistory())){
                    history_list.add(movies.get(i).getHistory());
                    Log.i("HISTORY TASK", "history list size 2 : "+history_list.size());
                    movies_lists.add(movies_l);
                    movies_l = new ArrayList<MovieModel>();
                    movies_l.add(movies.get(i));
                }else{
                    movies_l.add(movies.get(i));
                }
            }
            movies_lists.add(movies_l);
        }
    }

    public void filterNonHistory(){
        for(int i = 0; i<movies.size(); i++){
            if(movies.get(i).getHistoryDate() == null){
                Log.i("HISTORY TASK", "REMOVE");
                movies.remove(i);
            }
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
        FavorAdapter favorAdapter = new FavorAdapter(context, movies_lists.get(position), favor_click);
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
