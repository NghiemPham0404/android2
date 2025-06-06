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

import com.example.movieapp.data.Model.PersonModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;
import com.example.movieapp.views.PersonActivity;
import com.example.movieapp.data.Model.user.UserDTO;
import com.example.movieapp.utils.Credentials;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.ViewHolder> {

    List<PersonModel> castModels;
    Context context;

    boolean isRoleShown = true;
    public CastAdapter(Context context, List<PersonModel> castModels) {
        this.context = context;
        this.castModels = castModels;
        shortenRole();
    }

    public CastAdapter(Context context) {
        this.context = context;
        isRoleShown = false;
    }


    public void setCasts(List<PersonModel> castModels){
        this.castModels = castModels;
        shortenRole();
        notifyDataSetChanged();
    }

    public void shortenRole(){
        for(int i =0; i< this.castModels.size()-1; i++){
            for(int j = i+1; j< this.castModels.size(); j++){
                if(this.castModels.get(i).getId() == this.castModels.get(j).getId()){
                    this.castModels.remove(j);
                    j--;
                }
            }
        }
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
        PersonModel castModel = castModels.get(position);
        if(isRoleShown){
            if (castModel.getCharacter() != null) {
                holder.charater_cast.setText(castModel.getCharacter());
            }else if (castModel.getKnown_for_department() != null) {
                holder.charater_cast.setText(castModel.getKnown_for_department());
            }
        }else{
            holder.charater_cast.setVisibility(View.GONE);
        }

        holder.name_cast.setText(castModel.getName());
        holder.shimmer_cast.startShimmerAnimation();
        new ImageLoader().loadImageIntoImageView(context, Credentials.BASE_IMAGE_URL + castModel.getProfile_path(), holder.image_cast, holder.shimmer_cast);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent castIntent = new Intent(context, PersonActivity.class);
                castIntent.putExtra("cast_id", castModel.getId());
                context.startActivity(castIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(castModels!=null)
            return castModels.size();
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView image_cast;
        ShimmerFrameLayout shimmer_cast;
        TextView name_cast, charater_cast;

        public ViewHolder(@NonNull View ItemView) {
            super(ItemView);
            image_cast = itemView.findViewById(R.id.image_cast);
            name_cast = itemView.findViewById(R.id.name_cast);
            charater_cast = itemView.findViewById(R.id.charater_cast);
            shimmer_cast = itemView.findViewById(R.id.shimmer_cast);
        }

    }
}
