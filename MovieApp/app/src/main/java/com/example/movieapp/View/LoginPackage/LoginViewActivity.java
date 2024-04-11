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
import com.example.movieapp.Request.ImageLoader;
import com.example.movieapp.Request.MyService2;
import com.example.movieapp.View.HomeActivity;
import com.example.movieapp.utils.Credentials;
import com.example.movieapp.utils.ManagerApi;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.Provider;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginViewActivity extends AppCompatActivity implements Form_validate {
    TextInputEditText email_txt, password_txt;
    TextView email_err_signin, password_err_signin;
    Button loginBtn, loginBtn_gg, forgot_passBtn, loginBtn_sms, signupBtn;
    Button loginBtn_face;
    ConstraintLayout loadingScreen;

    private SignInClient onTapClient;
    private BeginSignInRequest signInRequest;

    private static final int REQ_ONE_TAP = 100;
    private static final String TAG = "LOGIN TASK";
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);

        mAuth = FirebaseAuth.getInstance();

        initComponents();
        initFeature();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) {
            for (UserInfo userInfo : currentUser.getProviderData()) {
                Toast.makeText(this, "AVAILABLE gg : " + (userInfo.getProviderId() == GoogleAuthProvider.PROVIDER_ID), Toast.LENGTH_LONG).show();
                Toast.makeText(this, "AVAILABLE fb : " + (userInfo.getProviderId() == FacebookAuthProvider.PROVIDER_ID), Toast.LENGTH_LONG).show();
                if (currentUser.getProviderId() == FacebookAuthProvider.PROVIDER_ID) {
                    loginToAccountWithFB(currentUser.getUid());
                } else if (userInfo.getProviderId() == GoogleAuthProvider.PROVIDER_ID) {
                    loginToAccountWithGoogle(currentUser.getUid());
                    Toast.makeText(this, "GOOGLE", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    public void initComponents() {
        email_txt = findViewById(R.id.username_login_txt);
        password_txt = findViewById(R.id.password_login_txt);
        email_err_signin = findViewById(R.id.email_err_signin);
        password_err_signin = findViewById(R.id.password_err_signin);


        loginBtn = findViewById(R.id.sign_in_btn);
        loginBtn_gg = findViewById(R.id.login_google);
        loginBtn_face = findViewById(R.id.loginFbButton);
        signupBtn = findViewById(R.id.sign_up_btn_login);
        loadingScreen = findViewById(R.id.loadingLayout);
    }

    public void initFeature() {
        callbackManager = CallbackManager.Factory.create();
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

        loginBtn_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginViewActivity.this, Arrays.asList("public_profile",""));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        handleFacebookAccessToken(accessToken);
                    }

                    @Override
                    public void onCancel() {
                        // Handle login cancellation
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // Handle login error
                    }
                });
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

        signInRequest =  BeginSignInRequest.builder().setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId("200497617735-a5t4rrpnglvid5i5a0d9fd6kteqfnn5t.apps.googleusercontent.com")
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
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                    SignInCredential credential = onTapClient.getSignInCredentialFromIntent(data);
                    String google_id = credential.getId();

                    if (google_id != null) {

                        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(credential.getGoogleIdToken(), null);
                        mAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "signInWithCredential:success");
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            loginToAccountWithGoogle(user.getUid(),user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString());
//                                            new ImageLoader().loadImageIntoImageView(LoginViewActivity.this, user.getPhotoUrl().toString(), findViewById(R.id.avatar_user));
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                                        }
                                    }
                                });

                    }
                } catch (Exception e) {
                    Toast.makeText(this, "FALUIRE" + e, Toast.LENGTH_LONG).show();
                    Log.e("LOGIN TASK", "Fail to login with google " + e);
                }
                break;
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            GraphRequest request = GraphRequest.newMeRequest(
                                    token,
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response) {
                                            try {
                                                String userId = mAuth.getUid();
                                                String userName = object.getString("name");
                                                // Get user avatar URL
                                                String urlAva = object.getJSONObject("picture").getJSONObject("data").getString("url");

                                                // Display user information or perform further actions
                                                Toast.makeText(LoginViewActivity.this, "Hi, " + urlAva, Toast.LENGTH_SHORT).show();
                                                Log.i("Login FB success", " true" + userId + " " + userName + " " + urlAva);

                                                loginToAccountWithFB(userId, userName, urlAva );
                                                // Sign out from Firebase
//                                                FirebaseAuth.getInstance().signOut();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                // Handle JSON parsing error
                                            }
                                        }
                                    });

                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,picture");
                            request.setParameters(parameters);

                            request.executeAsync();

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginViewActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void loginToAccountWithGoogle(String google_id){
        loadingScreen.setVisibility(View.VISIBLE);
        Call<LoginModel> loginModelCall = MyService2.getApi().loginWithGoogle(Credentials.functionname_login,google_id);
        loginModelCall.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                if(response.code() == 200){
                    LoginModel loginModel = response.body();
                    if(loginModel.getSuccess().equalsIgnoreCase("true")){
                        navigateToHomeActivity(loginModel);
                    }else{
                        Log.e("LOGIN TASK", "Fail to login with google");
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                Log.e("LOGIN TASK", "Fail to login with google");
            }
        });
    }

    public void loginToAccountWithGoogle(String google_id,String username, String email, String avatar){
        loadingScreen.setVisibility(View.VISIBLE);
        Call<LoginModel> loginModelCall = MyService2.getApi().loginWithGoogle(Credentials.functionname_login,google_id);
        loginModelCall.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                if(response.code() == 200){
                    LoginModel loginModel = response.body();
                    if(loginModel.getSuccess().equalsIgnoreCase("true")){
                        navigateToHomeActivity(loginModel);
                    }else{
                        registAccountWithGoogle(google_id, username,email,avatar);
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                Log.e("LOGIN TASK", "Fail to login with google");
            }
        });
    }

    public void registAccountWithGoogle(String google_id,String username, String email,  String avatar){
        Call<LoginModel> loginModelCall = MyService2.getApi().regisWithGoogle(Credentials.functionname_regis, google_id, email, username, avatar);
        loginModelCall.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                if(response.code() == 200){
                    LoginModel loginModel = response.body();
                    navigateToHomeActivity(loginModel);
                }else{
                    Log.e("REGIS TASK", "fail to regist and login");
                }
            }
            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                Log.e("LOGIN TASK", "fail to regist and login with fb" + t);
            }
        });
    }
    public void loginToAccountWithFB(String fb_id){
        loadingScreen.setVisibility(View.VISIBLE);
        Call<LoginModel> loginModelCall = MyService2.getApi().loginWithFacebook(Credentials.functionname_login,fb_id);
        loginModelCall.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                if(response.code() == 200){
                    LoginModel loginAccount = response.body();
                    navigateToHomeActivity(loginAccount);
                }else{
                    Log.e("LOGIN TASK", "Fail to login with fb");
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                Log.e("LOGIN TASK", "Fail to login with fb");
            }
        });
    }

    public void loginToAccountWithFB(String fb_id, String username, String avatar){
        loadingScreen.setVisibility(View.VISIBLE);
        Call<LoginModel> loginModelCall = MyService2.getApi().loginWithFacebook(Credentials.functionname_login,fb_id);
        loginModelCall.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                if(response.isSuccessful()){
                    LoginModel loginModel = response.body();
                    if(loginModel.getSuccess().equalsIgnoreCase("true"))
                        navigateToHomeActivity(loginModel);
                    else{
                        Log.e("LOGIN TASK", "Fail to login with FB");
                        regisWithFB(fb_id, username, avatar);
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                Log.e("LOGIN TASK", "Fail to login with FB");
            }
        });
    }

    public void regisWithFB(String fb_id, String username, String avatar){
        Call<LoginModel> loginModelCall = MyService2.getApi().regisWithFacebook(Credentials.functionname_regis, fb_id, username, avatar);
        loginModelCall.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                LoginModel loginModel = response.body();
                navigateToHomeActivity(loginModel);
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                Log.e("LOGIN TASK", "fail to regist  with fb" + t);
            }
        });
    }

    public void navigateToHomeActivity(LoginModel loginModel){
        loadingScreen.setVisibility(View.GONE);
        AccountModel loginAccount = (AccountModel) loginModel;
        Intent loginSuccessIntent = new Intent(LoginViewActivity.this, HomeActivity.class);
        loginSuccessIntent.putExtra("loginAccount", loginAccount);
        startActivity(loginSuccessIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Destroy", Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public void turnOffValidateViews() {

    }
}