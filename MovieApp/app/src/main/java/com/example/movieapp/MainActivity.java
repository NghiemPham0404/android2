package com.example.movieapp;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.LoginModel;
import com.example.movieapp.Request.LoginAccountRequest;
import com.example.movieapp.View.HomeActivity;
import com.example.movieapp.View.LoginPackage.LoginViewActivity;
import com.example.movieapp.ViewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class MainActivity extends AppCompatActivity {
    Button try_again_btn;
    ProgressBar progressBar;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        initFeatures();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        load();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void initComponents(){
        try_again_btn = findViewById(R.id.try_again_btn);
        progressBar = findViewById(R.id.progressBar2);
    }

    public void initFeatures(){
        try_again_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load();
            }
        });
    }

    public void load(){
        try_again_btn.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        if(isNetworkAvailable(this)){
                tryToLogin();
        }else{
            Toast.makeText(this, "Conection error", Toast.LENGTH_SHORT).show();
            try_again_btn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    public void tryToLogin(){
        // relogin
        AccountModel loginAccount = userViewModel.loginStored(this);
        if(loginAccount!=null){
            Toast.makeText(this, "logined name :"+loginAccount.getUsername() + " " +loginAccount.getFacebook_id(),  Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("loginAccount", (Parcelable) loginAccount);
            startActivity(intent);
        }else{
            // go to login view
            goToLoginIntent();
        }
    }

    private void goToLoginIntent() {
        Intent intent = new Intent(MainActivity.this, LoginViewActivity.class);
        startActivity(intent);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}