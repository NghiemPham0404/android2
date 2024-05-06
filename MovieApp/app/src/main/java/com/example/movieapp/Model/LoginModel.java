package com.example.movieapp.Model;

import java.io.Serializable;

public class LoginModel extends AccountModel implements Serializable {

    private String error;
    private String success;

    public String getError() {
        return error;
    }

    public String getSuccess() {
        return success;
    }
    public void setSuccess(String success){
        this.success = success;
    }

    public LoginModel(String user_id, String username, String email, String password, String sms, String google_id, String facebook_id, String avatar, String success, String error) {
        super(user_id, username, email, password, sms, google_id, facebook_id, avatar);
        this.error = error;
        this.success = success;
    }
}
