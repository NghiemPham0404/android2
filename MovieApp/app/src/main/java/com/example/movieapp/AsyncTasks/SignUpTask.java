package com.example.movieapp.AsyncTasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.movieapp.View.LoginPackage.PreLoginView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SignUpTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = "LOGIN TASK";

    private String url;
    private String username;
    private String password;
    private String email;
    private Context context;

    private ConstraintLayout constraintLayout;
    public SignUpTask(Context context, String url, String username, String password, String email) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.email = email;
        this.context = context;
    }

    public SignUpTask(Context context, String url, String username, String password, String email, ConstraintLayout constraintLayout) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.email = email;
        this.context = context;
        this.constraintLayout = constraintLayout;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(constraintLayout!=null){
            constraintLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            return postToUrl(url, username, password, email);
        } catch (IOException e) {
            Log.e(TAG, "Error posting data", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if(constraintLayout!=null){
            constraintLayout.setVisibility(View.GONE);
        }
        // Handle the result here
        if (result != null) {
            Log.d(TAG, "Response: " + result);
            try {
                // Convert the response into a JSON object.
                JSONObject jsonObject = new JSONObject(result);
                String signup_result = jsonObject.getString("success");

                if(signup_result.equalsIgnoreCase("true")) {
                        Intent pre_login_intent = new Intent(context, PreLoginView.class);
                        pre_login_intent.putExtra("username", username);
                        pre_login_intent.putExtra("email", email);
                        pre_login_intent.putExtra("password", password);
                        context.startActivity(pre_login_intent);
                }else{
                    if(jsonObject.getString("noti").equalsIgnoreCase("Email đã được dùng để đăng ký!")){
                        Toast.makeText(context, "Email had been used to sign up", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(context, "Sign up failed, try later", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {

            }
        } else {
            Log.e(TAG, "Failed to get response");
        }
    }

    private String postToUrl(String urlString, String username, String password, String email) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        // Construct data to send
        String data = "email=" + URLEncoder.encode(email, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8") + "&username=" + URLEncoder.encode(username, "UTF-8");

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
