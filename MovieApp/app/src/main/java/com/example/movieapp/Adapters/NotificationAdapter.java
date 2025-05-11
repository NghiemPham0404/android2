package com.example.movieapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.data.Model.NotificationModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;
import com.example.movieapp.views.movie.MovieInformation;
import com.example.movieapp.views.home.homePages.userTab.Notification.NotificationsView;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    Context context;
    List<NotificationModel> notifications;

    public NotificationAdapter(Context context){
        this.context = context;
    }
    public void setNotifications(List<NotificationModel> notifications){
        this.notifications = notifications;
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
                    click_noti_intent = new Intent(context, MovieInformation.class);
                    click_noti_intent.putExtra("film_id", notification.getMovieId());
                }else{
                    click_noti_intent = new Intent(context, NotificationsView.class);
                    click_noti_intent.putExtra("notification", notification);
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
