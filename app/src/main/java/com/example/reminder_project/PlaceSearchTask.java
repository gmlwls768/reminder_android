package com.example.reminder_project;

import android.os.AsyncTask;
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

    String clientKey = "KakaoAK 7c1805f4786a51b62c5e74a91f1f85d6";
    private String str, receiveMsg;
    private String placeName;

    public PlaceSearchTask(String placeName){
        this.placeName = placeName;
    }

    @Override
    protected String doInBackground(String... params) {
        URL url = null;
        try {
//            url = new URL("https://dapi.kakao.com/v2/local/search/category.json?category_group_code=PM9&radius=20000"); // 서버 URL
            url = new URL("https://dapi.kakao.com/v2/local/search/keyword.json?y=35.8561719&x=129.2247477&radius=20000&query=" + placeName);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
//            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Authorization", clientKey);
//            conn.setConnectTimeout(10000);
            // Read시 연결 시간
//            conn.setReadTimeout(100000);

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
                InputStream in = conn.getErrorStream();
                Log.i("결과", conn.getResponseCode() + "Error");
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    System.out.printf("%s\n", line);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return receiveMsg;
    }
}
