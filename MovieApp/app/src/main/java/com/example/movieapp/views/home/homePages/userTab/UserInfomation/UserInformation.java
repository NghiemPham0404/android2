package com.example.movieapp.views.home.homePages.userTab.UserInfomation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;
import com.example.movieapp.Request.MyAvatarService;
import com.example.movieapp.data.Model.user.UserDTO;
import com.example.movieapp.data.Response.AvatarResponse;
import com.example.movieapp.ViewModel.UserViewModel;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInformation extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 100;
    UserDTO.UserInfo loginAccount;
    private Button choose_avatar_btn, apply_avatar_btn;
    private ImageButton back_btn;
    private ImageView avatar_display;
    private Uri selectedImageUri;
    private LinearLayout username_btn, email_btn, password_btn,sms_btn;
    private TextView username_txt, email_txt, password_txt, sms_txt;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_infomation);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.requestLoginedAccount();
        initComponents();
        ObserveAnyChange();
        initFeature();
    }

    public void ObserveAnyChange(){
        if(userViewModel!=null){
            userViewModel.getLoginAccount().observe(this, (loginedAccount) -> {
                if(loginedAccount!=null){
                    loginAccount = loginedAccount;
                    initInfo();
                }
            });
        }
    }

    public void initComponents(){
        back_btn = findViewById(R.id.backBtn_lg_setting);
        avatar_display = findViewById(R.id.avatar_user_info);
        choose_avatar_btn = findViewById(R.id.choose_avatar_btn);
        apply_avatar_btn = findViewById(R.id.apply_avatar);

        username_btn = findViewById(R.id.user_btn);
        email_btn = findViewById(R.id.email_btn);
        password_btn = findViewById(R.id.password_btn);
        sms_btn = findViewById(R.id.sms_btn);

        username_txt = findViewById(R.id.user_txt);
        email_txt = findViewById(R.id.email_txt);
        password_txt = findViewById(R.id.password_txt);
        sms_txt = findViewById(R.id.sms_txt);
    }
    public void initInfo(){
        loadAvatar();
        username_txt.setText(loginAccount.getName());
        if(loginAccount.getEmail()!=null && loginAccount.getEmail().length() >0){
            email_txt.setText(loginAccount.getEmail().charAt(1)+"*******");
        }else{
            email_txt.setText("");
        }
        password_txt.setText("******");
        if(loginAccount.getSms()!=null && loginAccount.getSms().length()>2){
            sms_txt.setText("*****" + loginAccount.getSms().charAt(loginAccount.getSms().length()-1)  + loginAccount.getSms().charAt(loginAccount.getSms().length()-2));
        }else {
            sms_txt.setText("");
        }
    }

    public void initFeature(){
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        choose_avatar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseAvatar();
            }
        });

        apply_avatar_btn.setVisibility(View.GONE);
        apply_avatar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendImageToServer(selectedImageUri);
            }
        });

        username_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUpdate("username");
            }
        });

        password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUpdate("password");
            }
        });

        email_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUpdate("email");
            }
        });

        sms_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUpdate("sms");
            }
        });
    }
    public void goToUpdate(String updateInfo){
        Intent update_info_intent = new Intent(this, UpdateUser_Information.class);
        update_info_intent.putExtra("loginAccount", (Parcelable) loginAccount);
        update_info_intent.putExtra("updateInfo", updateInfo);
        startActivity(update_info_intent);
    }

    public void chooseAvatar(){
        if(ContextCompat.checkSelfPermission(UserInformation.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }else{
            ActivityCompat.requestPermissions(this,  new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }
    }
    // Lấy hình ảnh từ thư viện
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            apply_avatar_btn.setVisibility(View.VISIBLE);
            avatar_display.setImageURI(selectedImageUri);
        }
    }

    public void sendImageToServer(Uri imageUri){
        String realPath = getRealPathFromURI(imageUri);
        Toast.makeText(this, realPath, Toast.LENGTH_SHORT).show();
        File imageFile = new File(realPath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestBody);

        Call<AvatarResponse> call = MyAvatarService.getApi().uploadImage(MyAvatarService.key,filePart);
        call.enqueue(new Callback<AvatarResponse>() {
            @Override
            public void onResponse(Call<AvatarResponse> call, Response<AvatarResponse> response) {
                if(response.isSuccessful()){
//                        Toast.makeText(User_Infomation.this, "Đường link hình nè :" +response.body().getAvatarModel().getUrl() ,  Toast.LENGTH_SHORT).show();
                        loginAccount.setAvatar(response.body().getAvatarModel().getUrl());
                        userViewModel.requestUpdateUser(loginAccount);
                        apply_avatar_btn.setVisibility(View.GONE);
                }else{
                    try {
                        Log.i("Failure",response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<AvatarResponse> call, Throwable t) {
                Log.e("Upload", "Failed to upload image", t);
                Toast.makeText(UserInformation.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
        return null;
    }

    public void loadAvatar(){
        ImageLoader.loadAvatar(UserInformation.this, loginAccount.getAvatar(), avatar_display, loginAccount.getName());
    }
}