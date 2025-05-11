package com.example.movieapp.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.movieapp.data.Model.user.UserDTO.*;
import com.example.movieapp.data.Repositories.UserRepo;

public class UserViewModel extends AndroidViewModel {
    private static UserRepo userRepo;

    public UserViewModel(Application application){
        super(application);
        userRepo = UserRepo.getInstance(application.getApplicationContext());
    }

    public void requestLoginedAccount(){
        userRepo.requestCurrentUser();
    }

    public void requestUpdateUser(String name, String email, String password, String sms, String avatar){
        UserUpdate userUpdate = new UserUpdate();
        userUpdate.setName(name);
        userUpdate.setEmail(email);
        userUpdate.setPassword(password);
        userUpdate.setAvatar(avatar);
        userUpdate.setSms(sms);
        userRepo.requestUpdateUser(userUpdate);
    }

    public void requestUpdateUser(UserInfo userInfo){
        UserUpdate userUpdate = new UserUpdate();
        userUpdate.setName(userInfo.getName());
        userUpdate.setEmail(userInfo.getEmail());
        userUpdate.setAvatar(userInfo.getAvatar());
        userUpdate.setSms(userInfo.getSms());
        userRepo.requestUpdateUser(userUpdate);
    }

    public LiveData<UserInfo> getLoginedAccount(){
        return userRepo.getCurrentUser();
    }

    public void requestLogOut(){
        userRepo.logOut();
    }
}
