package com.example.movieapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Model.CastModel;
import com.example.movieapp.R;
import com.example.movieapp.utils.Credentials;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.ViewHolder>{

    List<CastModel> castModels;
    Context context;

    public CastAdapter(Context context, List<CastModel> castModels){
        this.context = context;
        this.castModels = castModels;
    }
    @NonNull
    @Override
    public CastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cast_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CastAdapter.ViewHolder holder, int position) {
        holder.charater_cast.setText(castModels.get(position).getCharacter());
        holder.name_cast.setText(castModels.get(position).getName());
        new FilmAdapter.DownloadImageTask(holder.image_cast, holder.shimmer_cast).execute(Credentials.BASE_IMAGE_URL + castModels.get(position).getProfile_path());
    }

    @Override
    public int getItemCount() {
        return castModels.size();
    }

    class ViewHolder extends  RecyclerView.ViewHolder{
        ShapeableImageView image_cast;
        ShimmerFrameLayout shimmer_cast;
        TextView name_cast, charater_cast;

        public ViewHolder(@NonNull View ItemView){
            super(ItemView);
            image_cast = itemView.findViewById(R.id.image_cast);
            name_cast = itemView.findViewById(R.id.name_cast);
            charater_cast = itemView.findViewById(R.id.charater_cast);
            shimmer_cast = itemView.findViewById(R.id.shimmer_layout);
        }

    }
}
