package com.example.reminder_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class PlaceSearchActivity extends AppCompatActivity {
    private ArrayList<PlaceSearchItem> items;
    EditText placeSearchEdtTxt;
    Button placeSearchBtn;
    String resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_search);
        resultText = "";
        items = new ArrayList<>();
        placeSearchEdtTxt = (EditText) findViewById(R.id.placeSearchEdtTxt);
        placeSearchBtn = (Button) findViewById(R.id.placeSearchBtn);
        Intent intent = getIntent();

        Integer alarmYear = intent.getIntExtra("alarmYear", -1);
        Integer alarmMonth = intent.getIntExtra("alarmMonth", -1);
        Integer alarmDay = intent.getIntExtra("alarmDay", -1);
        Integer alarmHour = intent.getIntExtra("alarmHour", -1);
        Integer alarmMinute = intent.getIntExtra("alarmMinute", -1);

        String titleEdtTxt = intent.getStringExtra("titleEdtTxt");
        String contentEdtTxt = intent.getStringExtra("contentEdtTxt");
        String priority = intent.getStringExtra("priority");

        placeSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                items.clear();
                String name = placeSearchEdtTxt.getText().toString();
                try {
                    resultText = new PlaceSearchTask(name).execute().get();
                    jsonParsing(resultText);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                ListView listView = (ListView)findViewById(R.id.listView);
                final PlaceSearchAdapter pAdapter = new PlaceSearchAdapter(getApplicationContext(),items);
                listView.setAdapter(pAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView parent, View v, int position, long id){
//                Toast.makeText(getApplicationContext(),
//                        pAdapter.getItem(position).getName(),
//                        Toast.LENGTH_LONG).show();
                        double lat = pAdapter.getItem(position).getLat();
                        double lng = pAdapter.getItem(position).getLng();
                        String name = pAdapter.getItem(position).getName();
                        String address = pAdapter.getItem(position).getAddress();
                        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                        intent.putExtra("lat", lat);
                        intent.putExtra("lng", lng);
                        intent.putExtra("name", name);
                        intent.putExtra("address", address);
                        startActivity(intent);
                        overridePendingTransition(0, 0);//인텐트 효과 없애기
                    }
                });
            }
        });

    }

    //리스트 뷰를 이용하여 목록으로 보여줄 예정
    private void jsonParsing(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray placeArray = jsonObject.getJSONArray("documents");
            for (int i = 0; i < placeArray.length(); i++) {
                JSONObject object = placeArray.getJSONObject(i);
                String name = object.getString("place_name");
                String category = object.getString("category_group_name");
                String address = object.getString("road_address_name");
                String lat = object.getString("y");
                String lng = object.getString("x");
                items.add(new PlaceSearchItem(name, category, address, Double.parseDouble(lat), Double.parseDouble(lng)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}