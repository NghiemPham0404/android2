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

import com.example.movieapp.Interfaces.Form_validate;
import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.LoginModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.MyService2;
import com.example.movieapp.View.HomeActivity;
import com.example.movieapp.utils.Credentials;
import com.example.movieapp.utils.ManagerApi;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginViewActivity extends AppCompatActivity implements Form_validate {
    TextInputEditText email_txt, password_txt;
    TextView email_err_signin, password_err_signin;
    Button loginBtn, loginBtn_gg, loginBtn_face, forgot_passBtn, loginBtn_sms, signupBtn;
    ConstraintLayout loadingScreen;

    private SignInClient onTapClient;
    private BeginSignInRequest signInRequest;

    private static final int REQ_ONE_TAP = 100;
    private static final String TAG = "LOGIN TASK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);

        initComponents();


        initFeature();
    }

    public void initComponents() {
        email_txt = findViewById(R.id.username_login_txt);
        password_txt = findViewById(R.id.password_login_txt);
        email_err_signin = findViewById(R.id.email_err_signin);
        password_err_signin = findViewById(R.id.password_err_signin);


        loginBtn = findViewById(R.id.sign_in_btn);
        loginBtn_gg = findViewById(R.id.login_google);
        signupBtn = findViewById(R.id.sign_up_btn_login);
        loadingScreen = findViewById(R.id.loadingLayout);
    }

    public void initFeature() {
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

        String email = email_txt.getText().toString().trim();
        String password = password_txt.getText().toString().trim();
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
                               Intent intent = new Intent(LoginViewActivity.this, HomeActivity.class);
                               AccountModel accountModel = (AccountModel) result;
                               intent.putExtra("loginAccount",accountModel);
                               LoginViewActivity.this.startActivity(intent);
                           } else {
                               String error = result.getError();
                               if (error.equalsIgnoreCase("Wrong Password") && password_err_signin != null) {
                                   password_err_signin.setText(error);
                                   password_err_signin.setVisibility(View.VISIBLE);
                                   email_err_signin.setVisibility(View.GONE);
                               } else if (email_err_signin != null) {
                                   email_err_signin.setText(error);
                                   password_err_signin.setVisibility(View.GONE);
                                   email_err_signin.setVisibility(View.VISIBLE);
                               }
                           }
                       }
                       loadingScreen.setVisibility(View.GONE);
                   }

                }

                @Override
                public void onFailure(Call<LoginModel> call, Throwable t) {
                    Log.i(TAG, "Fail"+t.toString());
                    if(loadingScreen!=null){
                        loadingScreen.setVisibility(View.GONE);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error posting data", e);
        }
    }

    public void signInWithGoogle() {

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
                    startIntentSenderForResult(beginSignInResult.getPendingIntent().getIntentSender(), REQ_ONE_TAP, null, 0, 0, 0);

                } catch (Exception e) {
                    Log.e("TAG", "COULD NOT START ONE TAP UI" + e.getLocalizedMessage());
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                    SignInCredential credential = onTapClient.getSignInCredentialFromIntent(data);
                    String google_id = credential.getId();

                    if (google_id != null) {
                        loadingScreen.setVisibility(View.VISIBLE);
                        Call<LoginModel> loginModelCall = MyService2.getApi().loginWithGoogle(Credentials.functionname_login,google_id);
                        loginModelCall.enqueue(new Callback<LoginModel>() {
                            @Override
                            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                                if(response.code() == 200){
                                    LoginModel loginModel = response.body();
                                    if(loginModel.getSuccess().equalsIgnoreCase("true")){
                                        AccountModel loginAccount = (AccountModel) loginModel;
                                        Intent loginSuccessIntent = new Intent(LoginViewActivity.this, HomeActivity.class);
                                        loginSuccessIntent.putExtra("loginAccount", loginAccount);
                                        loadingScreen.setVisibility(View.GONE);
                                        startActivity(loginSuccessIntent);
                                    }else{
                                        Toast.makeText(LoginViewActivity.this, loginModel.getError(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<LoginModel> call, Throwable t) {
                                Log.e("LOGIN TASK", "Fail to login with google");
                            }
                        });
                    }
                } catch (Exception e) {
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