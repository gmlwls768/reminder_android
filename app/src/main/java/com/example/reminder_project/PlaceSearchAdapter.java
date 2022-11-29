package com.example.reminder_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PlaceSearchAdapter extends BaseAdapter {
    Context context = null;
    LayoutInflater mlayoutInflater = null;
    ArrayList<PlaceSearchItem> items;

    public PlaceSearchAdapter(Context context, ArrayList<PlaceSearchItem> items){
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public PlaceSearchItem getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View v, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.activity_place_search_item, parent, false);

        TextView nameView = (TextView) view.findViewById(R.id.placeName);
        TextView categoryView = (TextView) view.findViewById(R.id.placeCategory);
        TextView addressView = (TextView) view.findViewById(R.id.placeAddress);

        nameView.setText(items.get(i).getName());
        categoryView.setText(items.get(i).getCategory());
        addressView.setText(items.get(i).getAddress());
        return view;
    }
}