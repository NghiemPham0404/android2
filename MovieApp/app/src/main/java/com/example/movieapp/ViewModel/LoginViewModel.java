package com.example.movieapp.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.movieapp.data.Model.auth.AuthDTO.*;
import com.example.movieapp.data.Model.auth.AuthResponse;
import com.example.movieapp.data.Repositories.AuthRepo;

public class LoginViewModel extends AndroidViewModel {

    private final AuthRepo authRepo;

    public LoginViewModel(Application application){
        super(application);
        this.authRepo = AuthRepo.getInstance(application.getApplicationContext());
    }

    public void requestLoginWithEmail(String username, String password){
        LoginModel loginModel = new LoginModel(username, password);
        this.authRepo.loginWithEmail(loginModel);
    }

    public void requestSignUpWithEmail(String username, String email, String password){
        AccountCreate accountCreate = new AccountCreate(username, email, password);
        this.authRepo.signUpWithEmail(accountCreate);
    }

    public void requestLoginWithGoogle(String googleId, String name, String avatar){
        AccountCreateGoogle accountCreateGoogle = new AccountCreateGoogle();
        accountCreateGoogle.setGoogle_id(googleId);
        accountCreateGoogle.setName(name);
        accountCreateGoogle.setAvatar(avatar);
        this.authRepo.loginWithGoogle(accountCreateGoogle);
    }


    public void requestLoginWithFacebook(String facebookId, String name, String avatar){
       AccountCreateFacebook accountCreateFacebook = new AccountCreateFacebook();
       accountCreateFacebook.setFacebook_id(facebookId);
       accountCreateFacebook.setName(name);
       accountCreateFacebook.setAvatar(avatar);
       this.authRepo.loginWithFacebook(accountCreateFacebook);
    }

    public LiveData<AuthResponse> getAuthResponse(){
        return this.authRepo.getAuthResponseMutable();
    }
}
