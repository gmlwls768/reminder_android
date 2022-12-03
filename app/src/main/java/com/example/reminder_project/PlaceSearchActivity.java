package com.example.reminder_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

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
    private static double lat;
    private static double lng;
    private static String name;
    private static String address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_search);

        Intent intent = getIntent();

        String lat_string = intent.getStringExtra("lat");
        String lng_string = intent.getStringExtra("lng");
        name = intent.getStringExtra(name);

        if(lat_string != null) {
            lat = Double.parseDouble(lat_string);
            lng = Double.parseDouble(lng_string);
        }

        System.out.println();
        RelativeLayout map_view = (RelativeLayout) findViewById(R.id.map_view);
        ListView listView = (ListView) findViewById(R.id.listView);
        Button mapSaveBtn = (Button) findViewById(R.id.mapSaveBtn);


        listView.setVisibility(View.GONE);

        net.daum.mf.map.api.MapView mapView = new net.daum.mf.map.api.MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView, 0);
        System.out.println("ADaaaaaaaaaaaaa" +lat);

        if(lat_string == null) {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
            mapView.setShowCurrentLocationMarker(true);
        }

        else{

            listView.setVisibility(View.GONE);
            map_view.setVisibility(View.VISIBLE);

//            mapView.removeAllPOIItems();

            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            mapView.setShowCurrentLocationMarker(false);
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(lat, lng);
            mapView.setMapCenterPoint(mapPoint,true);


            MapPOIItem marker = new MapPOIItem();
            marker.setItemName(name);
            marker.setTag(0);
            marker.setMapPoint(mapPoint);
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            mapView.addPOIItem(marker);
//            mapView.setShowCurrentLocationMarker(true);


        }



        resultText = "";
        items = new ArrayList<>();
        placeSearchEdtTxt = (EditText) findViewById(R.id.placeSearchEdtTxt);
        placeSearchBtn = (Button) findViewById(R.id.placeSearchBtn);

        Integer alarmYear = intent.getIntExtra("alarmYear", -1);
        Integer alarmMonth = intent.getIntExtra("alarmMonth", -1);
        Integer alarmDay = intent.getIntExtra("alarmDay", -1);
        Integer alarmHour = intent.getIntExtra("alarmHour", -1);
        Integer alarmMinute = intent.getIntExtra("alarmMinute", -1);

        String titleEdtTxt = intent.getStringExtra("titleEdtTxt");
        String contentEdtTxt = intent.getStringExtra("contentEdtTxt");
        String priority = intent.getStringExtra("priority");
        String isReloadState = intent.getStringExtra("isReloadState");
        String tableRowId = intent.getStringExtra("tableRowId");

        placeSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map_view.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                items.clear();
                name = placeSearchEdtTxt.getText().toString();
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

                        lat = pAdapter.getItem(position).getLat();
                        lng = pAdapter.getItem(position).getLng();
                        name = pAdapter.getItem(position).getName();
                        address = pAdapter.getItem(position).getAddress();

                        listView.setVisibility(View.GONE);
                        map_view.setVisibility(View.VISIBLE);

                        mapView.removeAllPOIItems();

                        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                        mapView.setShowCurrentLocationMarker(false);
                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(lat, lng);
                        mapView.setMapCenterPoint(mapPoint,true);


                        MapPOIItem marker = new MapPOIItem();
                        marker.setItemName(name);
                        marker.setTag(0);
                        marker.setMapPoint(mapPoint);
                        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                        mapView.addPOIItem(marker);
                        mapView.setShowCurrentLocationMarker(true);

                    }
                });
            }
        });

        mapSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WorkActivity.class);
                intent.putExtra("lat", Double.toString(lat));
                intent.putExtra("lng", Double.toString(lng));
                intent.putExtra("name", name);
                intent.putExtra("address", address);
                intent.putExtra("tableRowId", intent.getStringExtra("tableRowId"));
                intent.putExtra("isReloadState", intent.getStringExtra("isReloadState"));


                intent.putExtra("titleEdtTxt",titleEdtTxt);
                intent.putExtra("contentEdtTxt",contentEdtTxt);
                intent.putExtra("priority",priority);
                intent.putExtra("tableRowId", tableRowId);
                intent.putExtra("isReloadState", isReloadState);

                intent.putExtra("alarmYear",alarmYear);
                intent.putExtra("alarmMonth",alarmMonth);
                intent.putExtra("alarmDay",alarmDay);
                intent.putExtra("alarmHour",alarmHour);
                intent.putExtra("alarmMinute",alarmMinute);

                mapViewContainer.removeView(mapView);

                startActivity(intent);
                overridePendingTransition(0, 0);//인텐트 효과 없애기
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