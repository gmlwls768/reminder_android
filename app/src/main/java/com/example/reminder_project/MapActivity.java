package com.example.reminder_project;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
//        mapView.setMapViewEventListener(this);

        mapView.setShowCurrentLocationMarker(true);

        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(35.898054, 128.544296);
        mapView.setMapCenterPoint(mapPoint,true);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        MapPOIItem marker = new MapPOIItem();

//        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);



        marker.setItemName("Default Marker");
        marker.setTag(0);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker);

        simplebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DB 코드 수정
                finish();
            }
        });

    }

}