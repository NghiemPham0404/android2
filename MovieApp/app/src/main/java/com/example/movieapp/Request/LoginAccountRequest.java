package com.example.movieapp.Request;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movieapp.Model.AccountModel;
import com.example.movieapp.Model.LoginModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class LoginAccountRequest {
    public static void saveUserToFile(AccountModel user, Context context) {

        try {
            FileOutputStream fileOut = context.openFileOutput("user.txt", Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(user);
            out.close();
            fileOut.close();
            Log.i("Save", "true");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static AccountModel readUserFromFile(Context context) {
        try {
            FileInputStream fileIn = context.openFileInput("user.txt");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            AccountModel user = (AccountModel) in.readObject();
            in.close();
            fileIn.close();
            return user;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}