package com.example.movieapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Model.CreditModel;
import com.example.movieapp.R;
import com.example.movieapp.View.Movie_infomation;

import java.time.Year;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CareerAdapter extends RecyclerView.Adapter<CareerAdapter.ViewHolder> {

    Context context;
    List<CreditModel> creditModelList;
    List<Integer> yearList= new ArrayList<Integer>();


    public CareerAdapter(Context context, List<CreditModel> creditModelList){
        this.context = context;
        this.creditModelList = creditModelList;
        this.creditModelList.sort(Comparator.reverseOrder());
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.career_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.movie_name.setText(creditModelList.get(position).getTitle());
        holder.role.setText(creditModelList.get(position).getCharacter());
        int rating =  (int)(creditModelList.get(position).getVote_average()*10);
        if(rating < 70){
            if(rating<50 && rating!=0){
                holder.rating.setTextColor(context.getResources().getColor(R.color.neon_pink));
            }else{
                holder.rating.setTextColor(context.getResources().getColor(R.color.yellow));
            }
        }else{
            holder.rating.setTextColor(context.getResources().getColor(R.color.lime_green));
        }
        holder.rating.setText( (int)(creditModelList.get(position).getVote_average()*10) + " %");

        if(!yearList.contains(creditModelList.get(position).getYear())){
            yearList.add(creditModelList.get(position).getYear());
            holder.year.setText(creditModelList.get(position).getYear()+"");
        }else{
            holder.year.setText("");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent movie_intent = new Intent(context, Movie_infomation.class);
                movie_intent.putExtra("film_id",creditModelList.get(position).getId());
                context.startActivity(movie_intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(creditModelList!=null){
            return creditModelList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView year, movie_name, role,rating;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            year = itemView.findViewById(R.id.year_career);
            movie_name = itemView.findViewById(R.id.movie_name_career);
            role = itemView.findViewById(R.id.role_career);
            rating = itemView.findViewById(R.id.rating_career);
        }
    }


}