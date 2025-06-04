package com.example.movieapp.Request;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.movieapp.R;
import com.example.movieapp.utils.Credentials;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.regex.Pattern;


public class ImageLoader {
    public static final String regexPattern = "^(https?://|file://)?[\\w\\-./%]+\\.(jpg|jpeg|png|gif|bmp|webp)(\\?.*)?$";
    public final static Pattern pattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);

    public static void loadImageIntoImageView(Context context, String imageUrl, ImageView imageView, ShimmerFrameLayout shimmerFrameLayout) {
        shimmerFrameLayout.startShimmerAnimation(); // Start shimmer animation
        boolean isValidUrl = isValidImageUrl(imageUrl);
        if (isValidUrl) {
            Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                          imageView.setImageBitmap(bitmap);
                          shimmerFrameLayout.stopShimmerAnimation();
                        }
                    });
        } else {
            imageView.setImageDrawable(context.getDrawable(R.drawable.unknow_image));
            shimmerFrameLayout.stopShimmerAnimation();
        }
    }

    public static void loadImageIntoImageView(Context context, String imageUrl, ImageView imageView) {
        boolean isValidUrl = isValidImageUrl(imageUrl);
        if (isValidUrl) {
            Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Caching strategy
                    .into(imageView);
        } else {
            imageView.setImageDrawable(context.getDrawable(R.drawable.unknow_image));
        }
    }

    public static void loadAvatar(Context context, String imageUrl, ImageView imageView, String username) {
        boolean isValidUrl = imageUrl != null &&
                (pattern.matcher(imageUrl).matches() || imageUrl.contains("http"));
        Log.i("GLIDE LOAD IMAGE", imageUrl+"");
        String urlToLoad = isValidUrl
                ? imageUrl
                : getUsernameAvatarUrl(username);

        Glide.with(context)
                .load(urlToLoad)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

        imageView.setVisibility(View.VISIBLE);
    }

    private static boolean isValidImageUrl(String imageUrl) {
        return  imageUrl != null &&
                (pattern.matcher(imageUrl).matches() || imageUrl.contains("http"));
    }

    private static String getUsernameAvatarUrl(String username) {
        return "https://api.dicebear.com/9.x/initials/png?seed=" + username + "&backgroundType=gradientLinear";
    }
}

