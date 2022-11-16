package com.example.reminder_project;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;


public class WorkActivity extends AppCompatActivity {
    //Database Filed
    public static final int FIELD_NAME_ID = 0;
    public static final int FIELD_NAME_TITLE = 1;
    public static final int FIELD_NAME_CONTENTS = 2;
    public static final int FIELD_NAME_PRIORITY = 3;
    public static final int FIELD_NAME_LOCATION = 4;
    public static final int FIELD_NAME_ON_CREATE = 5;
    public static final int FIELD_NAME_IS_COMPLETE = 6;
    public static final int FIELD_NAME_ID_ON_COMPLETED = 7;



    ToDoTable todo; //A helper class to manage database creation and version management.
    SQLiteDatabase sqlDB;
    Cursor cursor;
    Button saveBtn;
    Switch timeSetSwitch;
    TimePicker timeSetTimepicker;
    DatePicker timeSetDatepicker;
    RadioButton highPriority,mediumPriority,lowPriority,nonePriority;
    EditText placeInfo, title, content;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        todo = new ToDoTable(this);
        title = (EditText) findViewById(R.id.title);
        content = (EditText) findViewById(R.id.content);
        highPriority = (RadioButton) findViewById(R.id.highPriority);
        mediumPriority = (RadioButton) findViewById(R.id.mediumPriority);
        lowPriority = (RadioButton) findViewById(R.id.lowPriority);
        nonePriority = (RadioButton) findViewById(R.id.nonePriority);
        saveBtn = (Button) findViewById(R.id.saveBtn);
        timeSetSwitch = (Switch) findViewById(R.id.timeSetSwitch);
        timeSetTimepicker = (TimePicker) findViewById(R.id.timeSetTimepicker);
        timeSetDatepicker = (DatePicker) findViewById(R.id.timeSetDatepicker);
        placeInfo = (EditText) findViewById(R.id.placeInfo);


        Intent intent = getIntent(); //액티비티간에 인수와 리턴값을 전달
        int id = intent.getIntExtra("id", -1); //toDoList의 id 컬럼 값
        if(id != -1){ //id 컬럼 값이 있을 경우(수정 페이지)
            sqlDB = todo.getReadableDatabase(); //Create and/or open a database
            cursor = sqlDB.rawQuery("SELECT * FROM toDo WHERE ID =" + Integer.toString(id) + ";", null); //id에 해당하는 컬럼의 값을 가져옴
            cursor.moveToFirst(); //커서를 첫번째로 이동
//            if(cursor.getString(FIELD_NAME_ON_CREATE) != null){
//
//            }

        }


        timeSetSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timeSetSwitch.isChecked()){
                timeSetTimepicker.setVisibility(View.VISIBLE);
                timeSetDatepicker.setVisibility(View.VISIBLE);
                }
                else{
                    timeSetTimepicker.setVisibility(View.GONE);
                    timeSetDatepicker.setVisibility(View.GONE);
                }
            }
        });
        timeSetDatepicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {

            }
        });
        timeSetTimepicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int minute) {

            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Toast.makeText(getApplicationContext(),"할일목록을 저장하였습니다!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}