package com.example.movieapp.View.LoginPackage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.movieapp.R;
import com.example.movieapp.Request.UserApiClient;
import com.example.movieapp.ViewModel.UserViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class ForgetPassActivity extends AppCompatActivity {

    Button send_forgot_link;
    ImageButton back_btn;
    TextInputEditText email_forgot;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        initComponents();
        observeAnyChange();
        initFeatures();
    }
    private void observeAnyChange(){
        if(userViewModel!=null){
            userViewModel.getForgotPassModel().observe(this, new Observer<UserApiClient.ForgotPassModel>() {
                @Override
                public void onChanged(UserApiClient.ForgotPassModel forgotPassModel) {
                    if(forgotPassModel!=null){
                        email_forgot.setVisibility(View.GONE);
                        if(forgotPassModel.getSuccess().equalsIgnoreCase("true")){
                            findViewById(R.id.noti_forgot_pass).setVisibility(View.VISIBLE);
                            findViewById(R.id.loadingLayout).setVisibility(View.GONE);
                            send_forgot_link.setText("Send again");
                        }else{
                            Toast.makeText(ForgetPassActivity.this,forgotPassModel.getNoti(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }
    private void initFeatures() {
        send_forgot_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userViewModel.sendForgotLink(email_forgot.getText().toString().trim());
                hideKeyboard();
                findViewById(R.id.loadingLayout).setVisibility(View.VISIBLE);
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initComponents() {
        send_forgot_link = findViewById(R.id.forgot_get_link_btn);
        email_forgot = findViewById(R.id.email_forgot_pass);
        back_btn = findViewById(R.id.back_btn);
    }
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(email_forgot.getWindowToken(), 0);
        }
    }

}