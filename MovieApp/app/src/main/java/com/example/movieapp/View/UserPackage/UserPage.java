package com.example.movieapp.View.UserPackage;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.R;
import com.example.movieapp.Request.ImageLoader;
import com.example.movieapp.Request.LoginAccountRequest;
import com.example.movieapp.View.LoginPackage.LoginViewActivity;
import com.example.movieapp.View.UserPackage.UserInfomation.User_Infomation;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserPage extends Fragment {
    // TODO : GHI LẠI TÀI KHOẢN ĐÃ ĐĂNG NHẬP
    AccountModel loginAccount;
    public static final String getAccount = "loginAccount";
    ImageView user_avatar;
    TextView avatar_text, user_name;

    public UserPage() {
        // Required empty public constructor
    }
    Button user_infomation_btn, logout_btn;

    public static UserPage newInstance(AccountModel loginAccount) {
        UserPage fragment = new UserPage();
        Bundle args = new Bundle();
        args.putParcelable(getAccount, loginAccount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           loginAccount = (AccountModel) getArguments().getParcelable(getAccount);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_page, container, false);
        initComponents(view);
        initFeature();
        return  view;
    }

    public void initComponents(View view){
        user_name = view.findViewById(R.id.user_name);
        user_avatar = view.findViewById(R.id.user_avatar);
        avatar_text = view.findViewById(R.id.avatarText);
        user_infomation_btn = view.findViewById(R.id.user_info_button);
        logout_btn = view.findViewById(R.id.logout_btn);
    }

    public void initFeature(){
        loadAvatar();
        user_name.setText(loginAccount.getUsername());
        user_infomation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), User_Infomation.class);
                intent.putExtra("loginAccount", (Parcelable) loginAccount);
                startActivity(intent);
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginViewActivity.class);
                startActivity(intent);
                LoginAccountRequest.saveUserToFile(null,getContext());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loadAvatar();
    }

    public void loadAvatar(){
        new ImageLoader().loadAvatar(getContext(), loginAccount.getAvatar(), user_avatar, avatar_text ,loginAccount.getUsername());
    }
}