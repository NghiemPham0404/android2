package com.example.movieapp.Request;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.movieapp.R;
import com.example.movieapp.utils.Credentials;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ImageLoader {
    public final static String regexPattern = ".*\\.(jpg|jpeg|png|gif|bmp|webp)$";
    public final static Pattern pattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);

    public void loadImageIntoImageView(Context context, String imageUrl, ImageView imageView, ShimmerFrameLayout shimmerFrameLayout) {
        shimmerFrameLayout.startShimmerAnimation(); // Start shimmer animation
        try {
            if (!imageUrl.equalsIgnoreCase(Credentials.BASE_IMAGE_URL + "null")) {
                Glide.with(context)
                        .load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // Caching strategy
                        .into(imageView);
            } else {
                imageView.setImageDrawable(context.getDrawable(R.drawable.unknow_image));
            }
        } finally {
            shimmerFrameLayout.stopShimmerAnimation(); // Stop shimmer animation if loading fails
        }

    }

    public void loadImageIntoImageView(Context context, String imageUrl, ImageView imageView) {
        if (!imageUrl.equalsIgnoreCase(Credentials.BASE_IMAGE_URL + "null")) {
            Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Caching strategy
                    .into(imageView);
        } else {
            imageView.setImageDrawable(context.getDrawable(R.drawable.unknow_image));
        }
    }

    public void loadAvatar(Context context, String imageUrl, ImageView imageView, TextView avatarText, String username) {
        if(pattern.matcher(imageUrl).matches() || imageUrl.contains("http")){
            Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
            avatarText.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }else{
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(200), rnd.nextInt(200), rnd.nextInt(200));
            imageView.setBackgroundColor(color);
            char firstChar = username.charAt(0);
            avatarText.setText(firstChar + "");
            avatarText.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
        }
    }
}

