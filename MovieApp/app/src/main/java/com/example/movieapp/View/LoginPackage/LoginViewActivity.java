package com.example.movieapp.View.LoginPackage;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieapp.Interfaces.Edittext_interface;
import com.example.movieapp.Interfaces.Form_validate;
import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.LoginModel;
import com.example.movieapp.R;
import com.example.movieapp.View.HomeActivity;
import com.example.movieapp.ViewModel.UserViewModel;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class LoginViewActivity extends AppCompatActivity implements Form_validate, Edittext_interface {
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

    private LoginModel loginAccount;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);
        mAuth = FirebaseAuth.getInstance();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        initComponents();
        ObserveAnyChange();
        initFeature();
    }
    public void ObserveAnyChange(){
        if(userViewModel!=null){
            userViewModel.getAccount().observe(this, new Observer<LoginModel>() {
                @Override
                public void onChanged(LoginModel loginModel) {
                    loginAccount = loginModel;
                    if(loginAccount.getSuccess().equalsIgnoreCase("true")){
                        navigateToHomeActivity(loginAccount);
                    }else{
                        String error = loginAccount.getError();
                        if(error.equalsIgnoreCase("Wrong Password")){
                            password_err_signin.setText(error);
                            password_err_signin.setVisibility(View.VISIBLE);
                        }else{
                            Toast.makeText(LoginViewActivity.this, "Lỗi : "+loginAccount.getError(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    loadingScreen.setVisibility(View.GONE);
                }
            });
        }
    }
    public void initComponents() {
        email_txt = findViewById(R.id.email_forgot_pass);
        password_txt = findViewById(R.id.password_login_txt);
        email_err_signin = findViewById(R.id.email_err_signin);
        password_err_signin = findViewById(R.id.password_err_signin);


        loginBtn = findViewById(R.id.sign_in_btn);
        forgot_passBtn = findViewById(R.id.forgot_pass_btn);
        loginBtn_gg = findViewById(R.id.login_google);
        loginBtn_face = findViewById(R.id.loginFbButton);
        signupBtn = findViewById(R.id.sign_up_btn_login);
        loadingScreen = findViewById(R.id.loadingLayout);
    }

    public void initFeature() {
        callbackManager = CallbackManager.Factory.create();
        forgot_passBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginViewActivity.this, ForgetPassActivity.class);
                startActivity(intent);
            }
        });
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
        userViewModel.login(email, password);
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

    public void loginToAccountWithGoogle(String google_id,String username, String email, String avatar){
        loadingScreen.setVisibility(View.VISIBLE);
        userViewModel.loginWithGoogle(email, username, google_id, avatar);
    }

    public void loginToAccountWithFB(String fb_id, String username, String avatar){
        loadingScreen.setVisibility(View.VISIBLE);
        userViewModel.loginWithFacebook(username, fb_id, avatar);
    }

    public void navigateToHomeActivity(LoginModel loginModel){
        loadingScreen.setVisibility(View.GONE);
        AccountModel loginAccount = (AccountModel) loginModel;
        userViewModel.storeAccount(this, loginAccount);
        Intent loginSuccessIntent = new Intent(LoginViewActivity.this, HomeActivity.class);
        loginSuccessIntent.putExtra("loginAccount", (Parcelable) loginAccount);
        subcribeToNotification();
        startActivity(loginSuccessIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Destroy", Toast.LENGTH_SHORT).show();
//        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public void turnOffValidateViews() {

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
                        Toast.makeText(LoginViewActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(password_txt.getWindowToken(), 0);
        }
    }
}