package com.example.movieapp.views.home.homePages.userTab;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.movieapp.data.Model.AccountModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;
import com.example.movieapp.Request.LoginAccountRequest;
import com.example.movieapp.data.Model.user.UserDTO;
import com.example.movieapp.views.login.LoginViewActivity;
import com.example.movieapp.views.home.homePages.userTab.Notification.NotificationsView;
import com.example.movieapp.views.home.homePages.userTab.UserInfomation.UserInfomation;
import com.example.movieapp.ViewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserPage extends Fragment {
    // TODO : GHI LẠI TÀI KHOẢN ĐÃ ĐĂNG NHẬP
    UserDTO.UserInfo loginAccount;
    public static final String getAccount = "loginAccount";
    ImageView user_avatar;
    TextView user_name;
    private UserViewModel userViewModel;
    private Button  noti_btn;

    public UserPage() {
        // Required empty public constructor
    }
    Button user_infomation_btn, logout_btn;

    public static UserPage newInstance() {
        return new UserPage();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getLoginedAccount().observe(this, currentUser -> {
            if(currentUser !=null){
                loginAccount = currentUser;
                initFeature();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_page, container, false);
        initComponents(view);
        return  view;
    }

    public void initComponents(View view){
        user_name = view.findViewById(R.id.user_name);
        user_avatar = view.findViewById(R.id.user_avatar);
        user_infomation_btn = view.findViewById(R.id.user_info_button);
        noti_btn = view.findViewById(R.id.noti_button);
        logout_btn = view.findViewById(R.id.logout_btn);
    }

    public void initFeature(){
        loadAvatar();
        user_name.setText(loginAccount.getName());
        user_infomation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserInfomation.class);
                startActivity(intent);
            }
        });

        noti_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NotificationsView.class);
                startActivity(intent);
            }
        });
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                userViewModel.requestLogOut();
                Intent intent = new Intent(getContext(), LoginViewActivity.class);
                startActivity(intent);
            }
        });
    }


    public void loadAvatar(){
        new ImageLoader().loadAvatar(getContext(), loginAccount.getAvatar(), user_avatar, loginAccount.getName());
    }
}