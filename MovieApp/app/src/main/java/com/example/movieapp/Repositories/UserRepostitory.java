package com.example.movieapp.Repositories;

import android.accounts.Account;
import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.LoginModel;
import com.example.movieapp.Request.LoginAccountRequest;
import com.example.movieapp.Request.UserApiClient;

public class UserRepostitory {
    private static UserRepostitory instance;
    private UserApiClient userApiClient;
    private Context context;
    public static UserRepostitory getInstance(){
        if(instance == null){
            instance = new UserRepostitory();
        }
        return instance;
    }
    public UserRepostitory(){
        userApiClient = UserApiClient.getInstance();
    }
    public void storeAccount(Context context, AccountModel accountModel){
        LoginAccountRequest.saveUserToFile(accountModel, context);
    }
    public AccountModel getStoredAccount(Context context){
        this.context = context;
        AccountModel storedAccount =  LoginAccountRequest.readUserFromFile(context);
        if(storedAccount!=null){
            userApiClient.setAccount(storedAccount);
        }
        return storedAccount;
    }
    public void loginWithInfo(String email, String password){
        userApiClient.loginDefault(email, password);
    }
    public void regisWithInfo(String email, String username, String password){
        userApiClient.regisWithInfo(email, username, password);
    }
    public void loginWithGoogle(String email, String username, String google_id, String avatar){
        userApiClient.loginWithGoogle(email,username,google_id,avatar);
    }
    public void loginWithFacebook(String username, String facebook_id, String avatar){
        userApiClient.loginWithFacebook(username, facebook_id, avatar);
    }
    public LiveData<LoginModel> getAccount(){
        return userApiClient.getAccount();
    }
    public LiveData<LoginModel> getRegisAccount(){
        return  userApiClient.getRegisAccount();
    }
    public LiveData<UserApiClient.ForgotPassModel> getForgotPassModel(){
        return userApiClient.getForgotPassModel();
    }
    public void logOut(){
        storeAccount(context, null);
        userApiClient.logOut();
    }
    public void updateAccount(AccountModel accountModel){
        userApiClient.updateAccount(accountModel);
        storeAccount(context, accountModel);
    }
    public void sendForgotLink(String email){
        userApiClient.sendForgotLink(email);
    }

}
