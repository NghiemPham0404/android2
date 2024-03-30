package com.example.movieapp.View.LoginPackage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieapp.AsyncTasks.LoginAsyncTask;
import com.example.movieapp.R;
import com.example.movieapp.utils.Credentials;

public class PreLoginView extends AppCompatActivity {

    private Button signInBtn;
    private TextView welcomeTextView;

    private String username, password, email;

    private ConstraintLayout layout;
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
        layout = findViewById(R.id.loadingLayout);

        signInBtn = findViewById(R.id.signin_presignin_btn);
    }

    private void initFeature() {
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PreLoginView.this, "Loading", Toast.LENGTH_SHORT).show();
                new LoginAsyncTask(PreLoginView.this, Credentials.login_link, email, password, layout).execute();
            }
        });
    }

}