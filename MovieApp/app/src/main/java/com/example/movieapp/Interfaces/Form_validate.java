package com.example.movieapp.Interfaces;

import java.util.regex.Pattern;

public interface Form_validate {
    public static String email_regex ="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
            "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static String password_regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
    public static final String name_regex = "^[\\p{L} ]{3,}$";
    public static final String phone_regex = "^\\d{10,11}$";
    public static final Pattern email_pattern = Pattern.compile(email_regex);
    public static final Pattern password_pattern = Pattern.compile(password_regex);
    public static final Pattern name_pattern = Pattern.compile(name_regex);
    public static final  Pattern phone_pattern= Pattern.compile(phone_regex);

    public boolean validate();

    public void turnOffValidateViews();

}
