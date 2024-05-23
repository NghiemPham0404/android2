package com.example.movieapp.View.LoginPackage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.LoginModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.MyService2;
import com.example.movieapp.View.HomeActivity;
import com.example.movieapp.ViewModel.UserViewModel;
import com.example.movieapp.utils.Credentials;
import com.example.movieapp.utils.ManagerApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreLoginView extends AppCompatActivity {

    private Button signInBtn;
    private TextView welcomeTextView;
    private ConstraintLayout loadingScreen;
    private String TAG = "LOGIN TASK";
    private UserViewModel userViewModel;
    private LoginModel regisAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_login_view);

        initComponents();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        regisAccount = userViewModel.getAccount().getValue();
        initFeature();
        loadingScreen.setVisibility(View.GONE);
    }
    private void initComponents() {
        welcomeTextView = findViewById(R.id.welcome_pre_login);
        loadingScreen = findViewById(R.id.loadingLayout);

        signInBtn = findViewById(R.id.signin_presignin_btn);
    }

    private void initFeature() {
        welcomeTextView.setText("Welcome " + regisAccount.getUsername());
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PreLoginView.this, "Loading", Toast.LENGTH_SHORT).show();
               signIn();
            }
        });
    }

    public void signIn(){
        navigateToHomeActivity(regisAccount);
    }
    public void navigateToHomeActivity(LoginModel loginModel){
        AccountModel loginAccount = (AccountModel) loginModel;
        userViewModel.storeAccount(this, loginAccount);
        Intent loginSuccessIntent = new Intent(PreLoginView.this, HomeActivity.class);
        loginSuccessIntent.putExtra("loginAccount", (Parcelable) loginAccount);
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