package com.example.movieapp.views.home.homePages.userTab.UserInfomation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieapp.Interfaces.Edittext_interface;
import com.example.movieapp.Interfaces.Form_validate;
import com.example.movieapp.R;
import com.example.movieapp.ViewModel.UserViewModel;
import com.example.movieapp.data.Model.user.UserDTO;

public class UpdateUser_Information extends AppCompatActivity implements Form_validate, Edittext_interface {
    EditText username_up, email_up, password_up, password_confirm_up, sms_up;
    Button save_btn;
    ImageButton back_btn;
    private String updateInfo;
    UserViewModel userViewModel;
    UserDTO.UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_information);
        updateInfo = getIntent().getStringExtra("updateInfo");
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getLoginAccount().observe(this, userInfo ->{
            if(userInfo!=null)
                this.userInfo = userInfo;
        });
        initComponents();
        initFeatures();
    }

    public void initComponents(){
        username_up = findViewById(R.id.username_up);
        email_up = findViewById(R.id.email_up);
        password_up = findViewById(R.id.password_up);
        password_confirm_up = findViewById(R.id.confirm_password_up);
        sms_up = findViewById(R.id.sms_up);

        save_btn = findViewById(R.id.save_btn);
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView update_info_txt = findViewById(R.id.update_info_txt);
        update_info_txt.setText(updateInfo);

        switch (updateInfo){
            case "username":
                username_up.setVisibility(View.VISIBLE);
                username_up.setText(userInfo.getName());
                break;
            case "email":
                email_up.setVisibility(View.VISIBLE);
                break;
            case "password":
                password_up.setVisibility(View.VISIBLE);
                password_confirm_up.setVisibility(View.VISIBLE);
                break;
            case "sms":
                sms_up.setVisibility(View.VISIBLE);
                break;
        }
    }
    public void initFeatures(){
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                boolean validate = validate();
                if(validate){
                    saveUserInfo();
                }
            }
        });
    }

    public void saveUserInfo(){
        userViewModel.requestUpdateUser(username_up.getText().toString().trim(),
                email_up.getText().toString().trim(),
                password_up.getText().toString().trim(), sms_up.getText().toString().trim(), userInfo.getAvatar());
        onBackPressed();
    }
    public boolean validateName(String name) {
        if (name.equalsIgnoreCase("")) {
            Toast.makeText(this,"please enter name",Toast.LENGTH_SHORT).show();
            return false;
        }else if (!name_pattern.matcher(name).matches()) {
            if (name.length() < 3) {
                Toast.makeText(this,"valid name must contain 3 digits atleast, doesn't contains number digits",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,"valid name doesn't contains number digits",Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }
    public boolean validatePassword(String password, String comfirm_password){
        if (password.equalsIgnoreCase("")) {
            Toast.makeText(this,"please enter password", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!password_pattern.matcher(password).matches()){
            Toast.makeText(this,"a valid password must contains atleast 8 digits of both letter and number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if ( comfirm_password.equalsIgnoreCase("")) {
            Toast.makeText(this,"please enter verified password", Toast.LENGTH_SHORT).show();
            return false;
        } else if(!password.equalsIgnoreCase(comfirm_password)){
            Toast.makeText(this,"verified password must match with the password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean validateEmail(String email){
        if (email.equalsIgnoreCase("")) {
            Toast.makeText(this,"please enter your valid email", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!email_pattern.matcher(email).matches()){
            Toast.makeText(this,"please enter a valid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public boolean validatePhone(String phone){
        if(!phone_pattern.matcher(phone).matches()){
            Toast.makeText(this,"please enter your valid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    @Override
    public boolean validate() {
        switch (updateInfo){
            case "username":
                return validateName(username_up.getText().toString().trim());
            case "email":
                return validateEmail(email_up.getText().toString().trim());
            case "password":
                return validatePassword(password_up.getText().toString().trim(), password_confirm_up.getText().toString().trim());
            case "sms":
                return validatePhone(sms_up.getText().toString().trim());
        }
        return false;
    }

    @Override
    public void turnOffValidateViews() {

    }

    @Override
    public void hideKeyboard() {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}