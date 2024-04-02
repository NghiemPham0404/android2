package com.example.movieapp.AsyncTasks;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.movieapp.R;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.io.IOException;
import java.io.InputStream;

public  class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {
    ImageView imageView;
    ShimmerFrameLayout shimmerFrameLayout;

    String url;

    public DownloadImageTask(ImageView imageView, String image_url) {
        this.imageView = imageView;
        this.url = image_url;
    }

    public DownloadImageTask(ImageView imageView,  ShimmerFrameLayout shimmerFrameLayout, String image_url) {
        this.imageView = imageView;
        this.shimmerFrameLayout = shimmerFrameLayout;
        this.url = image_url;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        Bitmap bitmap = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(shimmerFrameLayout!=null){
            shimmerFrameLayout.setDuration(1000); // Shimmer animation duration in milliseconds
            shimmerFrameLayout.setRepeatCount(ValueAnimator.INFINITE); // Repeat animation indefinitely
            shimmerFrameLayout.setRepeatDelay(500); // Delay between animation cycles
            shimmerFrameLayout.startShimmerAnimation();
        }
    }



    protected void onPostExecute(Bitmap result) {
        if(result!=null){
            imageView.setBackgroundResource(R.color.black);
            imageView.setImageBitmap(result);
            if(shimmerFrameLayout!=null) {
                shimmerFrameLayout.stopShimmerAnimation();
            }
        }else{
            if(shimmerFrameLayout!=null) {
                shimmerFrameLayout.stopShimmerAnimation();
            }
            imageView.setBackgroundResource(R.color.bright_gray);
        }
    }
}
