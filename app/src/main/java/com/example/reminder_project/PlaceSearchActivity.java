package com.example.reminder_project;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlaceSearchActivity extends AppCompatActivity {
    private ArrayList<PlaceSearchItem> items;
    private PlaceSearchItem changedPlace; // 사용자가 선택한 장소
    EditText placeSearchEdtTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_search);
        Intent intent = getIntent();
        placeSearchEdtTxt = (EditText) findViewById(R.id.placeSearchEdtTxt);
        items = new ArrayList<>();

        //현재위치를 가져옴
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double baseLat; // 검색할 위도
        double baseLng; // 검색할 경도
        if (location != null) {
            baseLat = location.getLongitude();
            baseLng = location.getLatitude();
        } else {
            baseLat = 35.8561719;
            baseLng = 129.2247477;
        }

        Log.i("baseLatitude", Double.toString(baseLat));
        Log.i("baseLongitude", Double.toString(baseLng));


        RelativeLayout map_view = (RelativeLayout) findViewById(R.id.map_view);
        ListView listView = (ListView) findViewById(R.id.listView);
        Button mapSaveBtn = (Button) findViewById(R.id.mapSaveBtn);

        listView.setVisibility(View.GONE);

        net.daum.mf.map.api.MapView mapView = new net.daum.mf.map.api.MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView, 0);

        PlaceSearchItem currentPlace = (PlaceSearchItem) intent.getSerializableExtra("currentPlace");
        if (currentPlace == null) {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
            mapView.setShowCurrentLocationMarker(true);

        }else{
            listView.setVisibility(View.GONE);
            map_view.setVisibility(View.VISIBLE);

//            mapView.removeAllPOIItems();

            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            mapView.setShowCurrentLocationMarker(false);
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(currentPlace.getLat(), currentPlace.getLng());
            mapView.setMapCenterPoint(mapPoint, true);


            MapPOIItem marker = new MapPOIItem();
            marker.setItemName(currentPlace.getName());
            marker.setTag(0);
            marker.setMapPoint(mapPoint);
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            mapView.addPOIItem(marker);
//            mapView.setShowCurrentLocationMarker(true);
        }

        placeSearchEdtTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    items.clear();
                    String query = placeSearchEdtTxt.getText().toString();
                    String resultText = new PlaceSearchTask(query, baseLat, baseLng).execute().get();
                    jsonParsing(resultText);

                    map_view.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);

                    ListView listView = (ListView) findViewById(R.id.listView);
                    final PlaceSearchAdapter pAdapter = new PlaceSearchAdapter(getApplicationContext(), items);
                    listView.setAdapter(pAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView parent, View v, int position, long id) {
                            PlaceSearchActivity.this.changedPlace = pAdapter.getItem(position);
                            listView.setVisibility(View.GONE);
                            map_view.setVisibility(View.VISIBLE);

                            mapView.removeAllPOIItems();
                            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                            mapView.setShowCurrentLocationMarker(false);
                            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(PlaceSearchActivity.this.changedPlace.getLat(), PlaceSearchActivity.this.changedPlace.getLng());
                            mapView.setMapCenterPoint(mapPoint, true);


                            MapPOIItem marker = new MapPOIItem();
                            marker.setItemName(PlaceSearchActivity.this.changedPlace.getName());
                            marker.setTag(0);
                            marker.setMapPoint(mapPoint);
                            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                            mapView.addPOIItem(marker);
                            mapView.setShowCurrentLocationMarker(true);

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mapSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WorkActivity.class);
                intent.putExtra("changedPlace", PlaceSearchActivity.this.changedPlace);
                mapViewContainer.removeView(mapView);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);//인텐트 효과 없애기
            }
        });


    }

    // json을 파싱하는 함수
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