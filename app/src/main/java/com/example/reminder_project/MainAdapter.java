package com.example.reminder_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MainAdapter extends BaseAdapter {
    Context context = null;
    LayoutInflater mlayoutInflater = null;
    ArrayList<ToDoListItem> items;

    public MainAdapter(Context context, ArrayList<ToDoListItem> items){
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ToDoListItem getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View v, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.activity_main_item, parent, false);

        TextView listItemTitle = (TextView) view.findViewById(R.id.listItemTitle);
        TextView listItemPriority = (TextView) view.findViewById(R.id.listItemPriority);

        listItemTitle.setText(items.get(i).getTitle());
        listItemPriority.setText(items.get(i).getPriority());
        return view;
    }
}
