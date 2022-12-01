package com.example.reminder_project;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.android.map.MapViewTouchEventListener;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;


public class MapActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        Button simplebtn = findViewById(R.id.simplebtn);
        net.daum.mf.map.api.MapView mapView = new net.daum.mf.map.api.MapView(this);
        Intent intent = getIntent();

        Integer alarmYear = intent.getIntExtra("alarmYear", -1);
        Integer alarmMonth = intent.getIntExtra("alarmMonth", -1);
        Integer alarmDay = intent.getIntExtra("alarmDay", -1);
        Integer alarmHour = intent.getIntExtra("alarmHour", -1);
        Integer alarmMinute = intent.getIntExtra("alarmMinute", -1);

        String titleEdtTxt = intent.getStringExtra("titleEdtTxt");
        String contentEdtTxt = intent.getStringExtra("contentEdtTxt");
        String priority = intent.getStringExtra("priority");

        double lat = intent.getDoubleExtra("lat", -9999);
        double lng = intent.getDoubleExtra("lng", -9999);
        String name = intent.getStringExtra("name");
        String address = intent.getStringExtra("address");


//        mapView.setMapViewEventListener(this);

        mapView.setShowCurrentLocationMarker(true);

        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(lat, lng);
        mapView.setMapCenterPoint(mapPoint,true);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        MapPOIItem marker = new MapPOIItem();

//        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);



        marker.setItemName(name);
        marker.setTag(0);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
//        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker);

        simplebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DB 코드 수정
                Intent intent = new Intent(getApplicationContext(), WorkActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("address", address);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);



                intent.putExtra("titleEdtTxt",titleEdtTxt);
                intent.putExtra("contentEdtTxt",contentEdtTxt);
                intent.putExtra("priority",priority);
                intent.putExtra("alarmYear",alarmYear);
                intent.putExtra("alarmMonth",alarmMonth);
                intent.putExtra("alarmDay",alarmDay);
                intent.putExtra("alarmHour",alarmHour);
                intent.putExtra("alarmMinute",alarmMinute);


                System.out.println(titleEdtTxt);
                System.out.println(contentEdtTxt);
                System.out.println(priority);

                overridePendingTransition(0, 0);//인텐트 효과 없애기
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

    }

}