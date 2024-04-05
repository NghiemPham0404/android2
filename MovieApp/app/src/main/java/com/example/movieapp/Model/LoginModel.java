package com.example.movieapp.Model;

public class LoginModel extends AccountModel{

    private String error;
    private String success;

    public String getError() {
        return error;
    }

    public String getSuccess() {
        return success;
    }

    public LoginModel(String user_id, String username, String email, String password, String sms, String google_id, String facebook_id, String avatar, String success, String error) {
        super(user_id, username, email, password, sms, google_id, facebook_id, avatar);
        this.error = error;
        this.success = success;
    }
}
