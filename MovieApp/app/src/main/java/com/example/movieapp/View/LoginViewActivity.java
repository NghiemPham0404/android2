package com.example.movieapp.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.movieapp.MainActivity;
import com.example.movieapp.R;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.io.*;
import java.net.*;


public class LoginViewActivity extends AppCompatActivity {
    Button loginBtn, loginBtn_gg, loginBtn_face, forgot_passBtn, loginBtn_sms;

    private SignInClient onTapClient;
    private BeginSignInRequest signInRequest;

    private static final int REQ_ONE_TAP = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);

        initComponents();


        initFeature();
    }

    public void initComponents(){
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn_gg = findViewById(R.id.login_google);
    }

    public void initFeature(){
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               signInDefault();
            }
        });
        loginBtn_gg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

    }

    public void signInDefault() {
        String login_string = "https://script.google.com/macros/s/AKfycbxLJe1OSmnLSKY_v40woVDCWhHgI7KRTGl6NLl7BUazg2307dSMp-97ufAuvXzt4V2y/exec";
        String username = ((TextInputEditText)findViewById(R.id.username_login_txt)).getText().toString().trim();
        String password = ((TextInputEditText)findViewById(R.id.password_login_txt)).getText().toString().trim();

        new LoginAsyncTask(login_string, username, password).execute();
    }

    public void signInWithGoogle(){

        onTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder().setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
                        .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId("354165537912-u39fre4fugtrjdavbjked9fhvp5et0e5.apps.googleusercontent.com")
                                .setFilterByAuthorizedAccounts(false)
                                .build())
                        .setAutoSelectEnabled(true)
                        .build();

        onTapClient.beginSignIn(signInRequest).addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
            @Override
            public void onSuccess(BeginSignInResult beginSignInResult) {
                try {
                    startIntentSenderForResult(beginSignInResult.getPendingIntent().getIntentSender(), REQ_ONE_TAP, null, 0,0,0);

                }catch (Exception e){
                    Log.e("TAG", "COULD NOT START ONE TAP UI"+e.getLocalizedMessage());
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG 2", e.getLocalizedMessage());
                finish();
            }
        });
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQ_ONE_TAP:
                    try {
                        SignInCredential credential = onTapClient.getSignInCredentialFromIntent(data);
                        String idToken = credential.getGoogleIdToken();
                        String username = credential.getId();
                        String password = credential.getPassword();

                        Toast.makeText(this, username, Toast.LENGTH_SHORT).show();
                        if(idToken != null){
                            Log.d("TAG", "Got ID token");
                        }else if(password != null){
                            Log.d("TAG", "Got Password");
                        }

                        Intent loginSuccessIntent = new Intent(LoginViewActivity.this, HomeActivity.class);
                        startActivity(loginSuccessIntent);
                    }catch (Exception e){
                        Toast.makeText(this, "FALUIRE", Toast.LENGTH_SHORT).show();
                    }
                break;
        }
    }
    public class  LoginAsyncTask extends AsyncTask<Void, Void, String> {
        private static final String TAG = "LOGIN TASK";

        private String url;
        private String username;
        private String password;

        public LoginAsyncTask(String url, String username, String password) {
            this.url = url;
            this.username = username;
            this.password = password;
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
            // Handle the result here
            if (result != null) {
                Log.d(TAG, "Response: " + result);

                try {
                    // Convert the response into a JSON object.
                    JSONObject jsonObject = new JSONObject(result);
                    String login_result =  jsonObject.getString("success");
                    if(login_result.equalsIgnoreCase("true")){
                        Intent intent = new Intent(LoginViewActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }else{

                    }
                }catch (Exception e){

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
            String data = "username=" + URLEncoder.encode(username, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8");

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
}