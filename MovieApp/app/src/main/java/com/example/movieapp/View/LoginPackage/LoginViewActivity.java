package com.example.movieapp.View.LoginPackage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieapp.AsyncTasks.LoginAsyncTask;
import com.example.movieapp.Interfaces.Form_validate;
import com.example.movieapp.R;
import com.example.movieapp.View.HomeActivity;
import com.example.movieapp.utils.Credentials;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;


public class LoginViewActivity extends AppCompatActivity implements Form_validate {
    TextInputEditText email_txt, password_txt;
    TextView email_err_signin, password_err_signin;
    Button loginBtn, loginBtn_gg, loginBtn_face, forgot_passBtn, loginBtn_sms, signupBtn;
    ConstraintLayout loadingScreen;

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
        email_txt = findViewById(R.id.username_login_txt);
        password_txt = findViewById(R.id.password_login_txt);
        email_err_signin = findViewById(R.id.email_err_signin);
        password_err_signin = findViewById(R.id.password_err_signin);


        loginBtn = findViewById(R.id.sign_in_btn);
        loginBtn_gg = findViewById(R.id.login_google);
        signupBtn = findViewById(R.id.sign_up_btn_login);
        loadingScreen = findViewById(R.id.loadingLayout);
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

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sign_upIntent = new Intent(LoginViewActivity.this, RegisterView.class);
                startActivity(sign_upIntent);
            }
        });

    }

    public void signInDefault() {
        String login_string = Credentials.login_link;
        String username = email_txt.getText().toString().trim();
        String password = password_txt.getText().toString().trim();

        new LoginAsyncTask(this ,login_string, username, password, loadingScreen, email_err_signin, password_err_signin).execute();
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

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public void turnOffValidateViews() {

    }
}