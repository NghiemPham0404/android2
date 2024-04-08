package com.example.movieapp.View.LoginPackage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieapp.Model.LoginModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.MyService2;
import com.example.movieapp.View.HomeActivity;
import com.example.movieapp.utils.Credentials;
import com.example.movieapp.utils.ManagerApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreLoginView extends AppCompatActivity {

    private Button signInBtn;
    private TextView welcomeTextView;

    private String username, password, email;
    private ConstraintLayout loadingScreen;
    private String TAG = "LOGIN TASK";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_login_view);

        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        username = getIntent().getStringExtra("username");

        if(email!=null && password!=null &&username != null){
            Toast.makeText(this,email+" " + password, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

        initComponents();
        initFeature();
    }

    private void initComponents() {
        welcomeTextView = findViewById(R.id.welcome_pre_login);
        welcomeTextView.setText("Welcome " + username);
        loadingScreen = findViewById(R.id.loadingLayout);

        signInBtn = findViewById(R.id.signin_presignin_btn);
    }

    private void initFeature() {
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PreLoginView.this, "Loading", Toast.LENGTH_SHORT).show();
               signIn(email, password);
            }
        });
    }

    public void signIn(String email, String password){
        loadingScreen.setVisibility(View.VISIBLE);
        try {
            ManagerApi managerApi = MyService2.getApi();
            Call<LoginModel> loginModelCall = managerApi.loginWithAccount(Credentials.functionname_login,email, password);
            loginModelCall.enqueue(new Callback<LoginModel>() {
                @Override
                public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                    if(response.code() == 200){
                        LoginModel result = response.body();
                        if(result!=null){
                            if (result.getSuccess().equalsIgnoreCase("true")) {
                                Intent intent = new Intent(PreLoginView.this, HomeActivity.class);
                                intent.putExtra("user_id", result.getUser_id());
                                intent.putExtra("username", result.getUsername());
                                startActivity(intent);
                            } else {
                                String error = result.getError();
                                Log.i(TAG, "error");
                            }
                        }
                        loadingScreen.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onFailure(Call<LoginModel> call, Throwable t) {
                    Log.i(TAG, "Fail");
                    if(loadingScreen!=null){
                        loadingScreen.setVisibility(View.GONE);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error posting data", e);
        }
    }

}