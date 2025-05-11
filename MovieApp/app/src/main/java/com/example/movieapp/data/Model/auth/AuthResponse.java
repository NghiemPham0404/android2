package com.example.movieapp.data.Model.auth;

import com.example.movieapp.data.Model.AccountModel;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse implements Serializable {

    private String error;
    private String success;
    private String access_token;
}
