package com.example.movieapp.Request;

import android.content.Context;
import android.util.Log;

import com.example.movieapp.utils.Credentials;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SearchRecommendRequest {
    public static void addSearchTextIntoFile(String search_text, Context context){
        try {
            List<String> searchTextList = getRecommendSearchTextFromFile(context);
            if(searchTextList == null){
                searchTextList = new ArrayList<String>();
            }
            if(searchTextList.size()== 10){
              searchTextList.remove(9);
            }
            if(!searchTextList.contains(search_text) && !search_text.trim().equalsIgnoreCase("")){
                searchTextList.add(0, search_text);
            }
            FileOutputStream fileOut = context.openFileOutput(Credentials.RECOMMEND_SEARCH_FILE_PATH, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(searchTextList);
            out.close();
            fileOut.close();
            Log.i("Save", "true");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getRecommendSearchTextFromFile(Context context){
        try {
            FileInputStream fileIn = context.openFileInput(Credentials.RECOMMEND_SEARCH_FILE_PATH);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            List<String> searchText_list = ( List<String>) in.readObject();
            in.close();
            fileIn.close();
            return searchText_list;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<String>();
        }
    }

    public static void deleteSearchTextFromFile(String search_text, Context context){
        try {
            List<String> searchTextList = getRecommendSearchTextFromFile(context);
           searchTextList.remove(search_text);
            FileOutputStream fileOut = context.openFileOutput(Credentials.RECOMMEND_SEARCH_FILE_PATH, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(searchTextList);
            out.close();
            fileOut.close();
            Log.i("Save", "true");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteSearchTextsFromFile(Context context){
        try {
            FileOutputStream fileOut = context.openFileOutput(Credentials.RECOMMEND_SEARCH_FILE_PATH, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(null);
            out.close();
            fileOut.close();
            Log.i("Save", "true");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
