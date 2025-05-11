package com.example.movieapp.data.Model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserDTO {
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class UserUpdate{
        String name;
        String email;
        String password;
        String sms;
        String avatar;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class UserOut{
        String name;
        String email;
        String avatar;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class UserInfo {
        int user_id;
        String name;
        String email;
        String sms;
        String google_id;
        String facebook_id;
        String avatar;
    }
}
