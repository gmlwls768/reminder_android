package com.example.reminder_project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ToDoTable extends SQLiteOpenHelper {
    public static final int FIELD_NAME_TITLE = 0;
    public static final int FIELD_NAME_CONTENTS = 1;
    public static final int FIELD_NAME_PRIORITY = 2;
    public static final int FIELD_NAME_LOCATION = 3;
    public static final int FIELD_NAME_LAT = 4;
    public static final int FIELD_NAME_LNG = 5;
    public static final int FIELD_NAME_ALARM = 6;
    public static final int FIELD_NAME_ON_CREATE = 7;
    public static final int FIELD_NAME_IS_COMPLETE = 8;
    public static final int FIELD_NAME_ON_COMPLETE = 9;


    public ToDoTable(Context context){
        super(context, "toDo", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE toDo(TITLE TEXT, CONTENTS TEXT,  PRIORITY INTEGER DEFAULT 0, LOCATION TEXT, LAT TEXT, LNG TEXT, ALARM TEXT, ON_CREATE TEXT PRIMARY KEY, IS_COMPLETE INTEGER DEFAULT 0, ON_COMPLETE TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS toDo");
        onCreate(db);
    }
}
