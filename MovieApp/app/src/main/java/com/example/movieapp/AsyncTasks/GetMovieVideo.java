package com.example.movieapp.AsyncTasks;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import com.example.movieapp.Model.VideoModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.MyService;
import com.example.movieapp.Response.CastResponse;
import com.example.movieapp.Response.VideoResponse;
import com.example.movieapp.View.Movie_infomation;
import com.example.movieapp.View.PlayingTrailer;
import com.example.movieapp.utils.Credentials;
import com.example.movieapp.utils.MovieApi;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetMovieVideo extends AsyncTask<Void, Void, String> {

    private int id;
    private ExoPlayer player;
    private String urlString;
    String video;
    private Context context;

    private Button playButton;
    private static final String functionName="movies";
    public GetMovieVideo(String urlString,int id, ExoPlayer player) {
        this.id = id;
        this.player = player;
        this.urlString= urlString;
    }
    public GetMovieVideo(String urlString,int id, Button playButton, Context context) {
        this.id = id;
        this.urlString= urlString;
        this.playButton = playButton;
        this.context = context;
    }


    @Override
    protected String doInBackground(Void... voids) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Construct data to send
            String data = "movieId=" + URLEncoder.encode(id+"", "UTF-8") + "&functionname=" + URLEncoder.encode(functionName, "UTF-8");

            // Send data
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = data.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            // Get the response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                Log.i("video link",response.toString());
                return response.toString();
            } finally {
                conn.disconnect();
            }
        }catch (Exception e){
            Log.i("video link",e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        String videoUrl;
        try {
            // Convert the response into a JSON object.
            Log.i("video link",s);
            JSONObject jsonObject = new JSONObject(s);
            videoUrl = jsonObject.getString("url");
            if(player!=null && videoUrl!=null &&!videoUrl.equalsIgnoreCase("")){
                MediaItem mediaItem = MediaItem.fromUri(videoUrl);

                player.setMediaItem(mediaItem);
                player.prepare();
                player.setPlayWhenReady(true);
            }

            if(playButton!=null){
                if( videoUrl!=null &&!videoUrl.equalsIgnoreCase("")){
                    playButton.setText("Watch movie");
                    playButton.setBackgroundResource(R.drawable.gradient_corner_bg);
                    playButton.setEnabled(true);
                }else{
                    MovieApi movieApi = MyService.getMovieApi();
                    Call<VideoResponse> videoResponseCall = movieApi.searchVideoByFilmID(id, Credentials.API_KEY);
                    videoResponseCall.enqueue(new Callback<VideoResponse>() {
                        @Override
                        public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                            if(response.code() == 200){
                                List<VideoModel> videoModels = response.body().getVideoModels();
                                if(videoModels.size() > 0){
                                    video = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/"+videoModels.get(0).getKey()+"\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen=\"true\"></iframe>";
                                    for(int i = 0; i<videoModels.size(); i++){
                                        if(videoModels.get(i).getType().equalsIgnoreCase("Trailer")){
                                            video = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/"+videoModels.get(i).getKey()+"\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen=\"true\"></iframe>";
                                        }
                                    }
                                    playButton.setText("Trailer");
                                    playButton.setBackgroundResource(R.drawable.play_trailer_btn);
                                    playButton.setEnabled(true);
                                    playButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(context, PlayingTrailer.class);
                                            intent.putExtra("video_string", video);
                                            context.startActivity(intent);
                                        }
                                    });
                                }else{
                                    playButton.setEnabled(false);
                                    playButton.setText("Upcomming");
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<VideoResponse> call, Throwable t) {
                            playButton.setEnabled(false);
                            playButton.setText("Upcomming");
                        }
                    });
                }

            }
        }catch (Exception e){
                Log.e("TAG","Paste fail");
        }
    }
}
