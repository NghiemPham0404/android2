package com.example.movieapp.ViewModel;

import android.accounts.Account;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.LoginModel;
import com.example.movieapp.Repositories.UserRepostitory;
import com.example.movieapp.Request.UserApiClient;

public class UserViewModel extends ViewModel {
    private static UserRepostitory userRepostitory;

    public UserViewModel(){
        userRepostitory = UserRepostitory.getInstance();
    }
    public AccountModel loginStored(Context context){
        return userRepostitory.getStoredAccount(context);
    }
    public void storeAccount(Context context, AccountModel loginAccount){
        userRepostitory.storeAccount(context, loginAccount);
    }
    public void regisWithInfo(String username, String email, String password){
        userRepostitory.regisWithInfo(email, username, password);
    }
    public void login(String email, String password){
        userRepostitory.loginWithInfo(email, password);
    }
    public void loginWithGoogle(String email, String username, String google_id, String avatar){
        userRepostitory.loginWithGoogle(email, username, google_id, avatar);
    }
    public void loginWithFacebook(String username, String facebook_id, String avatar){
        userRepostitory.loginWithFacebook(username, facebook_id, avatar);
    }
    public LiveData<LoginModel> getAccount(){
        return userRepostitory.getAccount();
    }
    public void logOut(){
        userRepostitory.logOut();
    }
    public void update(AccountModel updatedAccount){
        userRepostitory.updateAccount(updatedAccount);
    }
    public void sendForgotLink(String email){
        userRepostitory.sendForgotLink(email);
    }
    public LiveData<UserApiClient.ForgotPassModel> getForgotPassModel(){
        return userRepostitory.getForgotPassModel();
    }
}
