package com.example.movieapp.AsyncTasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.movieapp.View.HomeActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class  LoginAsyncTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = "LOGIN TASK";

    private String url;
    private String username;
    private String password;

    private Context context;

    private ConstraintLayout loadinglayout;

    public LoginAsyncTask(Context context, String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.context = context;
    }

    public LoginAsyncTask(Context context, String url, String username, String password, ConstraintLayout loadingLayout) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.context = context;
        this.loadinglayout = loadingLayout;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(loadinglayout!=null){
            loadinglayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            return postToUrl(url, username, password);
        } catch (IOException e) {
            Log.e(TAG, "Error posting data", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if(loadinglayout!=null){
            loadinglayout.setVisibility(View.GONE);
        }
        // Handle the result here
        if (result != null) {
            Log.d(TAG, "Response: " + result);

            try {
                // Convert the response into a JSON object.
                JSONObject jsonObject = new JSONObject(result);
                String login_result =  jsonObject.getString("success");

                if(login_result.equalsIgnoreCase("true")){

                    String username = jsonObject.getString("username");
                    String user_id =  jsonObject.getString("user_id");
                    String ẹmail = jsonObject.getString("email");
                    String password = jsonObject.getString("password");



                    Intent intent = new Intent(context, HomeActivity.class);
                    intent.putExtra("user_id", user_id);
                    intent.putExtra("username", username);
                    context.startActivity(intent);
                }else{
                    Log.e(TAG, "Failed to Login");
                }
            }catch (Exception e){
                Log.e(TAG, "Failed to get JSON");

            }

        } else {
            Log.e(TAG, "Failed to get response");
        }
    }

    private String postToUrl(String urlString, String username, String password) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        // Construct data to send
        String data = "email=" + URLEncoder.encode(username, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8");

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

            return response.toString();
        } finally {
            conn.disconnect();
        }
    }
}