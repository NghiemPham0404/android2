package com.example.movieapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.NotificationModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;
import com.example.movieapp.View.Movie_infomation;
import com.example.movieapp.View.UserPackage.Notification.NotificationsView;

import java.util.List;
import java.util.zip.Inflater;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    Context context;
    List<NotificationModel> notifications;
    AccountModel loginAccount;

    public NotificationAdapter(Context context, AccountModel loginAccount){
        this.context = context;
        this.loginAccount = loginAccount;
    }
    public void setNotifications(List<NotificationModel> notifications){
        this.notifications = notifications;
        for(int i =0; i<notifications.size(); i++){
            if(!notifications.get(i).getUserId().equalsIgnoreCase(loginAccount.getUser_id())){
                notifications.remove(i);
                i--;
            }
        }
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel notification = notifications.get(position);
        holder.title.setText(notification.getTitle());
        holder.body.setText(notification.getBody());
        holder.time.setText(notification.getTime());
        new ImageLoader().loadImageIntoImageView(context, notification.getLargeImage(), holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent click_noti_intent;
                if(notification.getContent()==null){
                    click_noti_intent = new Intent(context, Movie_infomation.class);
                    click_noti_intent.putExtra("film_id", notification.getMovieId());
                    click_noti_intent.putExtra("loginAccount", (Parcelable) loginAccount);
                }else{
                    click_noti_intent = new Intent(context, NotificationsView.class);
                    click_noti_intent.putExtra("notification", notification);
                    click_noti_intent.putExtra("loginAccount", (Parcelable) loginAccount);
                }
                context.startActivity(click_noti_intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(notifications!=null){
            return notifications.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, body, time;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_noti);
            body = itemView.findViewById(R.id.body_noti);
            time = itemView.findViewById(R.id.time_noti);
            imageView = itemView.findViewById(R.id.image_noti);
        }
    }
}
