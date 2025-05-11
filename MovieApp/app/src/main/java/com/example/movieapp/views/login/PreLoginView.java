package com.example.movieapp.views.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieapp.ViewModel.LoginViewModel;
import com.example.movieapp.data.Model.AccountModel;
import com.example.movieapp.data.Model.auth.AuthResponse;
import com.example.movieapp.R;
import com.example.movieapp.data.Model.user.UserDTO;
import com.example.movieapp.views.home.HomeActivity;
import com.example.movieapp.ViewModel.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class PreLoginView extends AppCompatActivity {

    private Button signInBtn;
    private TextView welcomeTextView;
    private ConstraintLayout loadingScreen;
    private String TAG = "LOGIN TASK";
    private LoginViewModel loginViewModel;
    private UserViewModel userViewModel;
    UserDTO.UserInfo regisAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_login_view);

        initComponents();
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        loginViewModel.getAuthResponse().observe(this, (authResponse) -> {
            if(authResponse != null){
                loadingScreen.setVisibility(View.GONE);
            }
        });
        initFeature();
    }
    private void initComponents() {
        welcomeTextView = findViewById(R.id.welcome_pre_login);
        loadingScreen = findViewById(R.id.loadingLayout);

        signInBtn = findViewById(R.id.signin_presignin_btn);
    }

    private void initFeature() {
        welcomeTextView.setText("Welcome " + regisAccount.getName());
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PreLoginView.this, "Loading", Toast.LENGTH_SHORT).show();
               signIn();
            }
        });
    }

    public void signIn(){
        loginViewModel.getAuthResponse().observe(this, (authResponse) -> {
            if(authResponse != null){
                userViewModel.requestLoginedAccount();
                userViewModel.getLoginedAccount().observe(this, (accountModel) -> {
                    if(accountModel != null){
                        regisAccount = accountModel;
                        loadingScreen.setVisibility(View.GONE);
                    }else{
                        loadingScreen.setVisibility(View.GONE);
                        Toast.makeText(getBaseContext(), "Login fail", Toast.LENGTH_SHORT).show();
                    }
                });
                loadingScreen.setVisibility(View.GONE);
                navigateToHomeActivity(authResponse);
            }
        });
    }
    public void navigateToHomeActivity(AuthResponse authResponse){
        Intent loginSuccessIntent = new Intent(PreLoginView.this, HomeActivity.class);
        subcribeToNotification();
        startActivity(loginSuccessIntent);
    }
    public void subcribeToNotification(){
        FirebaseMessaging.getInstance().subscribeToTopic("notification")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(PreLoginView.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}