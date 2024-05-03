package com.example.movieapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.R;
import com.example.movieapp.Request.SearchRecommendRequest;

import java.util.List;

public class SearchRecommendAdapter extends RecyclerView.Adapter< SearchRecommendAdapter.ViewHolder> {

    Context context;
    List<String> searchRecommends;

    OnItemClick onItemClick;
    public interface OnItemClick{
        void itemClick(String search_txt);
    }

    public SearchRecommendAdapter(Context context, List<String> searchRecommends, OnItemClick onItemClick){
        this.context = context;
        this.searchRecommends = searchRecommends;
        this.onItemClick = onItemClick;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_recommend_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String recommend = searchRecommends.get(position).toString();
        int i = position;
        holder.recommend_text.setText(searchRecommends.get(position).toString());
        holder.delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchRecommendRequest.deleteSearchTextFromFile(recommend ,context);
                searchRecommends.remove(i);
                notifyDataSetChanged();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.itemClick(searchRecommends.get(i).toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        if(searchRecommends!=null){
            return searchRecommends.size();
        }
        return 0;
    }

    public void setRecommends(List<String> searchTexts) {
        this.searchRecommends = searchTexts;
        notifyDataSetChanged();
    }

    public List<String> getRecommends(){
        return this.searchRecommends;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageButton delete_button;
        TextView recommend_text;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            delete_button = itemView.findViewById(R.id.delete_recomment_btn);
            recommend_text = itemView.findViewById(R.id.recommend_text);
        }
    }
}
