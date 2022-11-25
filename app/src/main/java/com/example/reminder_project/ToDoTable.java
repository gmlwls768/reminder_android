package com.example.reminder_project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ToDoTable extends SQLiteOpenHelper {
    public ToDoTable(Context context){
        super(context, "toDo", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE toDo(ID INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT, CONTENTS TEXT,  PRIORITY INTEGER DEFAULT 0, LOCATION TEXT, ALERT TEXT, ON_CREATE TEXT, IS_COMPLETE INTEGER DEFAULT 0, ON_COMPLETE TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS toDo");
        onCreate(db);
    }
}
