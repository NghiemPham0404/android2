package com.example.movieapp.views.home.homePages.userTab;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;
import com.example.movieapp.data.Model.user.UserDTO;
import com.example.movieapp.views.login.LoginViewActivity;
import com.example.movieapp.views.home.homePages.userTab.Notification.NotificationsView;
import com.example.movieapp.views.home.homePages.userTab.UserInfomation.UserInformation;
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
    ImageView userAvatar;
    TextView userName;
    private UserViewModel userViewModel;
    private Button notificationButton, userInformationBtn, logoutBtn;

    public UserPage() {
        // Required empty public constructor
    }

    public static UserPage newInstance() {
        return new UserPage();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getLoginAccount().observe(this, currentUser -> {
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
        userName = view.findViewById(R.id.user_name);
        userAvatar = view.findViewById(R.id.user_avatar);
        userInformationBtn = view.findViewById(R.id.user_info_button);
        notificationButton = view.findViewById(R.id.noti_button);
        logoutBtn = view.findViewById(R.id.logout_btn);
    }

    public void initFeature(){
        loadAvatar();
        userName.setText(loginAccount.getName());
        userInformationBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), UserInformation.class);
            startActivity(intent);
        });

        notificationButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NotificationsView.class);
            startActivity(intent);
        });
        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            userViewModel.requestLogOut();
            Intent intent = new Intent(getContext(), LoginViewActivity.class);
            startActivity(intent);
        });
    }


    public void loadAvatar(){
        ImageLoader.loadAvatar(getContext(), loginAccount.getAvatar(), userAvatar, loginAccount.getName());
    }
}