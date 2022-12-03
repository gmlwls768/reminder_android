package com.example.reminder_project;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class PlaceSearchTask extends AsyncTask<String, Void, String> {

    String clientKey = BuildConfig.KAKAO_REST_API_KEY;
    private String str, receiveMsg;
    private String placeName;
    private double baseLat;
    private double baseLng;

    public PlaceSearchTask(String placeName, double baseLat, double baseLng) {
        this.placeName = placeName;
        this.baseLat = baseLat;
        this.baseLng = baseLng;
    }

    @Override
    protected String doInBackground(String... params){
        URL url = null;
        try {
//            url = new URL("https://dapi.kakao.com/v2/local/search/category.json?category_group_code=PM9&radius=20000"); // 서버 URL
            url = new URL("https://dapi.kakao.com/v2/local/search/keyword.json?y=" + baseLat + "&x=" + baseLng + "&radius=20000&query=" + placeName);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Authorization", clientKey);

            if (conn.getResponseCode() == conn.HTTP_OK) {
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                receiveMsg = buffer.toString();
                Log.i("receiveMsg : ", receiveMsg);
                reader.close();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return receiveMsg;
    }
}
