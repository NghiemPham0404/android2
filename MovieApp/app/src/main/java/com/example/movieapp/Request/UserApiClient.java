package com.example.movieapp.Request;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.AppExecutors;
import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.LoginModel;
import com.example.movieapp.utils.Credentials;
import com.facebook.login.Login;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class UserApiClient {
    private static UserApiClient instance;
    private MutableLiveData<LoginModel> loginAccount;
    private MutableLiveData<ForgotPassModel> forgotPassModel;
    private RetrieveAccountRunnable loginRetrieveAccountRunnable;
    private RetrieveAccountRunnable regisRetrieveAccountRunnable;
    private RetrieveAccountRunnable forgotPassRetriveRunnable;

    private RetrieveAccountRunnable updateRetrieveAccountRunnable;
    public static UserApiClient getInstance(){
        if(instance == null){
            instance = new UserApiClient();
        }
        return instance;
    }
    public UserApiClient(){
        loginAccount = new MutableLiveData<>();
        forgotPassModel = new MutableLiveData<>();
    }
    public LiveData<LoginModel> getAccount(){
        return loginAccount;
    }
    public void setAccount(AccountModel accountModel){
        LoginModel loginModel = new LoginModel(accountModel.getUser_id(),
                accountModel.getUsername(),
                accountModel.getEmail(),
                accountModel.getPassword(),
                accountModel.getSms(),
                accountModel.getGoogle_id(),
                accountModel.getFacebook_id(),
                accountModel.getAvatar(),
                "true",
                null
                );
        loginAccount.postValue(loginModel);
    }
    public LiveData<ForgotPassModel> getForgotPassModel(){
        return forgotPassModel;
    }
    public void logOut() {
        loginAccount.postValue(null);
    }
    public void loginDefault(String email, String password){
        if(loginRetrieveAccountRunnable!=null){
            loginRetrieveAccountRunnable = null;
        }
        loginRetrieveAccountRunnable = new RetrieveAccountRunnable(email, password);
        final Future myHandler = AppExecutors.getInstance().networkIO().submit(loginRetrieveAccountRunnable);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS);
    }
    public void regisWithInfo(String email, String username, String password) {
        if(regisRetrieveAccountRunnable!=null){
            regisRetrieveAccountRunnable = null;
        }
        regisRetrieveAccountRunnable = new RetrieveAccountRunnable(email, username, password);
        final Future myHandler = AppExecutors.getInstance().networkIO().submit(regisRetrieveAccountRunnable);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS);
    }
    public void loginWithGoogle(String email, String username, String google_id, String avatar){
        if(loginRetrieveAccountRunnable!=null){
            loginRetrieveAccountRunnable = null;
        }
        loginRetrieveAccountRunnable = new RetrieveAccountRunnable(email, username,google_id, avatar);
        final Future myHandler = AppExecutors.getInstance().networkIO().submit( loginRetrieveAccountRunnable);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS);
    }
    public void loginWithFacebook(String username,  String facebook_id, String avatar){
        if(loginRetrieveAccountRunnable!=null){
            loginRetrieveAccountRunnable = null;
        }
        loginRetrieveAccountRunnable = new RetrieveAccountRunnable(username, facebook_id, avatar, true);
        final Future myHandler = AppExecutors.getInstance().networkIO().submit( loginRetrieveAccountRunnable);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS);
    }
    public void updateAccount(AccountModel accountModel){
        if(updateRetrieveAccountRunnable!=null){
            updateRetrieveAccountRunnable = new RetrieveAccountRunnable(accountModel);
        }
        updateRetrieveAccountRunnable = new RetrieveAccountRunnable(accountModel);
        final Future myHandler = AppExecutors.getInstance().networkIO().submit( updateRetrieveAccountRunnable);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS);
    }
    public void sendForgotLink(String email){
        if(forgotPassRetriveRunnable != null){
            forgotPassRetriveRunnable = null;
        }
        forgotPassRetriveRunnable = new RetrieveAccountRunnable(email);
        final Future myHandler = AppExecutors.getInstance().networkIO().submit(forgotPassRetriveRunnable);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Hủy lời gọi Retrofit
                myHandler.cancel(true);
            }
        }, 10, TimeUnit.SECONDS);
    }


    private class RetrieveAccountRunnable implements Runnable {
        boolean cancelRequest;
        AccountModel accountModel;
        String username, email, password, facebook_id, google_id, avatar;
        String functionname;
        private void cancelRequest() {
            Log.v("QUERY TASK", "Canceled request");
        }
        // login
        public RetrieveAccountRunnable(String email, String password){
            this.functionname = Credentials.functionname_login;
            this.email = email;
            this.password = password;
        }
        public RetrieveAccountRunnable(String email){
            this.email = email;
            this.functionname = Credentials.functionname_forgotPassword;
        }

        //login with google
        public RetrieveAccountRunnable(String email, String username,  String google_id, String avatar){
            this.email = email;
            this.username = username;
            this.google_id = google_id;
            this.avatar = avatar;
            this.functionname = Credentials.functionname_login;
        }

        //login with facebook
        public RetrieveAccountRunnable(String username, String facebook_id, String avatar, boolean fb){
            this.username = username;
            this.facebook_id = facebook_id;
            this.avatar = avatar;
            this.functionname = Credentials.functionname_login;
        }

        //sign up
        public RetrieveAccountRunnable(String email,String username,String password){
            this.username = username;
            this.email = email;
            this.password = password;
            this.functionname = Credentials.functionname_regis;
        }
        // update user info
        public RetrieveAccountRunnable(AccountModel accountModel){
            this.accountModel = accountModel;
            this.functionname = Credentials.functionname_updateUser;
        }

        @Override
        public void run() {
            try {
                switch (functionname){
                    case Credentials.functionname_login:
                            if(google_id == null && facebook_id == null){
                                // default login
                                Log.i("LOGIN","login default");
                                Response response = loginDefault().execute();
                                if(response.isSuccessful()){
                                    Log.i("LOGIN","login default sucess");
                                    LoginModel loginModel = ((Response<LoginModel>) response).body();
                                    loginAccount.postValue(loginModel);
                                }else{
                                    Log.i("LOGIN DEFAULT",  "fail");
                                }
                            }else{
                                if(google_id!=null){
                                    // login with gg
                                    Log.i("LOGIN GG","login google");
                                    Response response = loginWithGoogle().execute();
                                    if(response.isSuccessful()){
                                        LoginModel loginModel = ((Response<LoginModel>) response).body();
                                        if(loginModel.getSuccess().equalsIgnoreCase("true")){
                                            Log.i("LOGIN GG","login google success");
                                            loginAccount.postValue(loginModel);
                                        }else{
                                            Log.i("LOGIN GG","login google fail, regis google");
                                            response = regisWithGoogle().execute();
                                            if(response.isSuccessful()){
                                                Log.i("LOGIN GG","regis google sucess");
                                                loginModel = ((Response<LoginModel>) response).body();
                                                loginAccount.postValue(loginModel);
                                            }
                                        }
                                    }
                                }else if(facebook_id!=null){
                                    // login with fb
                                    Log.i("LOGIN FB","login google");
                                    Response response = loginWithFB().execute();
                                    if(response.isSuccessful()){
                                        LoginModel loginModel = ((Response<LoginModel>) response).body();
                                        if(loginModel.getSuccess().equalsIgnoreCase("true")){
                                            Log.i("LOGIN FB","login facebook success");
                                            loginAccount.postValue(loginModel);
                                        }else{
                                            Log.i("LOGIN FB","login facebook fail, regis facebook");
                                            response = regisWithFB().execute();
                                            if(response.isSuccessful()){
                                                Log.i("LOGIN FB","regis facebook sucess");
                                                loginModel = ((Response<LoginModel>) response).body();
                                                loginAccount.postValue(loginModel);
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                    case Credentials.functionname_regis:
                            Log.i("REGIS","regis default");
                            Response response = regisDefault().execute();
                            if(response.isSuccessful()){
                                LoginModel loginModel = ((Response<LoginModel>) response).body();
                                loginAccount.postValue(loginModel);
                            }
                             break;
                    case Credentials.functionname_updateUser:
                            Log.i("Update Account","update");
                            if(accountModel!=null){
                                LoginModel loginModel = loginAccount.getValue();
                                loginModel.setUsername(accountModel.getUsername());
                                loginModel.setEmail(accountModel.getEmail());
                                loginModel.setPassword(accountModel.getPassword());
                                loginModel.setAvatar(accountModel.getAvatar());
                                loginModel.setGoogle_id(accountModel.getGoogle_id());
                                loginModel.setFacebook_id(accountModel.getFacebook_id());
                                loginModel.setSms(accountModel.getSms());
                                loginAccount.postValue(loginModel);
                                response = updateAccount().execute();
                                if(response.isSuccessful()){
                                   Log.i("Updated Account", " Success");
                                }else{
                                    Log.i("Updated Account", "Fail");
                                }
                            }
                            break;
                    case Credentials.functionname_forgotPassword:
                        Log.i("forgot password","forgotPassword");
                        response = getForgotPassword().execute();
                        if(response.isSuccessful()){
                            ForgotPassModel forgot_pass_model = ((Response<ForgotPassModel>) response).body();
                            forgotPassModel.postValue(forgot_pass_model);
                        }
                        break;
                }

                if(cancelRequest){
                    cancelRequest();
                }
            }catch (Exception e){
                Log.i("Retrive User Runnable",  "fail"+e);
            }
        }
        public Call<LoginModel> loginDefault(){
            return  MyService2.getApi().loginWithAccount(Credentials.functionname_login, email, password);
        }
        public Call<LoginModel> loginWithGoogle(){
            return  MyService2.getApi().loginWithGoogle(Credentials.functionname_login, google_id);
        }
        public Call<LoginModel> loginWithFB(){
            return  MyService2.getApi().loginWithFacebook(Credentials.functionname_login, facebook_id);
        }
        public Call<LoginModel> regisDefault(){
            return MyService2.getApi().signUpWithInfo(Credentials.functionname_regis, email, password, username);
        }
        public Call<LoginModel> regisWithGoogle(){
            return MyService2.getApi().regisWithGoogle(Credentials.functionname_regis, google_id, email, username, avatar);
        }
        public Call<LoginModel> regisWithFB(){
            return MyService2.getApi().regisWithFacebook(Credentials.functionname_regis, facebook_id, username, avatar);
        }
        public Call<AccountModel> updateAccount(){
            String userId = accountModel.getUser_id();
            String username = accountModel.getUsername();
            String email = accountModel.getEmail();
            String password = accountModel.getPassword();
            String sms = accountModel.getSms();
            String google_id = accountModel.getGoogle_id();
            String facebook_id = accountModel.getFacebook_id();
            String avatar = accountModel.getAvatar();
            return MyService2.getApi().configureUserInfo(Credentials.functionname_updateUser,
                    userId,
                    username,
                    email,
                    password,
                    sms,
                    google_id,
                    facebook_id,
                    avatar);
        }
        public Call<ForgotPassModel> getForgotPassword(){
            return MyService2.getApi().getForgotPass(Credentials.functionname_forgotPassword, email);
        }
    }
    public class ForgotPassModel{
        public String getSuccess() {
            return success;
        }

        public void setSuccess(String success) {
            this.success = success;
        }

        public String getNoti() {
            return noti;
        }

        public void setNoti(String noti) {
            this.noti = noti;
        }

        String success;
        String noti;

        public ForgotPassModel(String success, String noti) {
            this.success = success;
            this.noti = noti;
        }
    }
}
