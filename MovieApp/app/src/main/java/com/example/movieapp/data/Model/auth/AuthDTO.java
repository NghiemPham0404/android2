package com.example.movieapp.data.Model.auth;

import lombok.*;

public class AuthDTO {
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class AccountCreate{
        String name;
        String password;
        String email;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class AccountCreateGoogle{
        String google_id;
        String name;
        String avatar;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class AccountCreateFacebook{
        String facebook_id;
        String name;
        String avatar;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class AccountUpdate{
        String name;
        String avatar;
        String email;
        String password;
        String sms;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class LoginModel{
        String email;
        String password;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class LoginModelGoogle{

        String google_id;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class LoginModelFacebook{

        String facebook_id;
    }
}

