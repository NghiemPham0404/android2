package com.example.movieapp.Request;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.movieapp.R;
import com.example.movieapp.utils.Credentials;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.jetbrains.annotations.Nullable;


public class ImageLoader {
    public void loadImageIntoImageView(Context context, String imageUrl, ImageView imageView, ShimmerFrameLayout shimmerFrameLayout) {

        shimmerFrameLayout.startShimmerAnimation(); // Start shimmer animation

        try {
            if (!imageUrl.equalsIgnoreCase(Credentials.BASE_IMAGE_URL+"null")) {
                Glide.with(context)
                        .load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // Caching strategy
                        .into(imageView);
            } else {
                imageView.setImageDrawable(context.getDrawable(R.drawable.unknow_image));
            }
        }finally {
            shimmerFrameLayout.stopShimmerAnimation(); // Stop shimmer animation if loading fails
        }

    }

    public void loadImageIntoImageView(Context context, String imageUrl, ImageView imageView) {
        if (!imageUrl.equalsIgnoreCase(Credentials.BASE_IMAGE_URL+"null")) {
            Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Caching strategy
                    .into(imageView);
        } else {
            imageView.setImageDrawable(context.getDrawable(R.drawable.unknow_image));
        }
    }
}

