package com.example.movieapp.View.LoginPackage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.movieapp.AsyncTasks.SignUpTask;
import com.example.movieapp.Interfaces.Form_validate;
import com.example.movieapp.R;
import com.example.movieapp.utils.Credentials;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterView extends AppCompatActivity implements Form_validate {

    TextInputEditText username_txt, password_txt, psswrd_verify_txt, email_txt;
    TextView username_err_signup, password_err_signup, verify_psswrd_err_signup, email_err_signup;
    Button signup_btn;
    ConstraintLayout loadingScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_view);
        initComponent();
        initFunction();
    }

    public void initComponent() {
        loadingScreen = findViewById(R.id.loadingLayout);

        username_txt = findViewById(R.id.username_txt_signup);
        password_txt = findViewById(R.id.password_txt_signup);
        psswrd_verify_txt = findViewById(R.id.password_verify_signup);
        email_txt = findViewById(R.id.email_txt_signup);
        signup_btn = findViewById(R.id.sign_up_btn);

        //err
        username_err_signup = findViewById(R.id.username_err_signup);
        password_err_signup = findViewById(R.id.password_err_signup);
        verify_psswrd_err_signup = findViewById(R.id.password_verify_err_signup);
        email_err_signup = findViewById(R.id.email_err_signup);
    }

    @Override
    public boolean validate() {
        if(!validateName(username_txt.getText().toString().trim())){
            return false;
        }else if(!validateEmail(email_txt.getText().toString().trim())){
            return false;
        } else if (!validatePassword(password_txt.getText().toString().trim())){
            return false;
        }
        return true;
    }

    public boolean validateName(String name) {
        if (username_txt.getText().toString().trim().equalsIgnoreCase("")) {
            username_err_signup.setText("please enter name");
            username_err_signup.setVisibility(View.VISIBLE);
            return false;
        }else if (!name_pattern.matcher(name).matches()) {
            if (name.length() < 3) {
                username_err_signup.setText("valid name must contain 3 digits atleast, doesn't contains number digits");
            } else {
                username_err_signup.setText("valid name doesn't contains number digits");
            }
            username_err_signup.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    public boolean validatePassword(String password){
        if (password_txt.getText().toString().equalsIgnoreCase("")) {
            password_err_signup.setText("please enter password");
            password_err_signup.setVisibility(View.VISIBLE);
            return false;
        }else if(!password_pattern.matcher(password).matches()){
            password_err_signup.setText("a valid password must contains atleast 8 digits of both letter and number");
            password_err_signup.setVisibility(View.VISIBLE);
            return false;
        }

        if (psswrd_verify_txt.getText().toString().equalsIgnoreCase("")) {
            verify_psswrd_err_signup.setText("please enter verified password");
            verify_psswrd_err_signup.setVisibility(View.VISIBLE);
            return false;
        } else if(!password.equalsIgnoreCase(psswrd_verify_txt.getText().toString().trim())){
            verify_psswrd_err_signup.setText("verified password must match with the password");
            verify_psswrd_err_signup.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    public boolean validateEmail(String email_input){
        if (email_txt.getText().toString().equalsIgnoreCase("")) {
            email_err_signup.setText("please enter your valid email");
            email_err_signup.setVisibility(View.VISIBLE);
            return false;
        }else if(!email_pattern.matcher(email_input).matches()){
            email_err_signup.setText("please enter a valid email");
            email_err_signup.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    @Override
    public void turnOffValidateViews() {
        // TODO : turn off verify when filling form
        username_err_signup.setVisibility(View.GONE);
        password_err_signup.setVisibility(View.GONE);
        verify_psswrd_err_signup.setVisibility(View.GONE);
        email_err_signup.setVisibility(View.GONE);
    }

    public void initFunction() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                turnOffValidateViews();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        username_txt.addTextChangedListener(textWatcher);
        password_txt.addTextChangedListener(textWatcher);
        psswrd_verify_txt.addTextChangedListener(textWatcher);
        email_txt.addTextChangedListener(textWatcher);

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validation = validate();
                if(validation){
                    String username = username_txt.getText().toString().trim();
                    String email = email_txt.getText().toString().trim();
                    String password = password_txt.getText().toString().trim();
                    new SignUpTask( RegisterView.this, Credentials.signup_link,
                            username, password, email, loadingScreen).execute();
                }
            }
        });
    }
    public void signUp(String username, String email, String password){
        loadingScreen.setVisibility(View.VISIBLE);

    }

}