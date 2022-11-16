package com.example.reminder_project;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@RequiresApi(api = Build.VERSION_CODES.O)
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


    int y = 0, m = 0, d = 0, h = 0, mi = 0; // 사용자가 설정한 시간
    ToDoTable todo; //A helper class to manage database creation and version management.
    SQLiteDatabase sqlDB;
    Cursor cursor;
    Button saveBtn;
    ImageButton timeBtn, chkBtn;
    RadioButton highPriority, mediumPriority, lowPriority, nonePriority;
    EditText placeInfo, title, content;
    TextView timeText;
    LocalDateTime now= LocalDateTime.now();
    String formatedNow = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/"));
    String timeNow [] = formatedNow.split("/");
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
        placeInfo = (EditText) findViewById(R.id.placeInfo);
        timeBtn = (ImageButton) findViewById(R.id.timeBtn);
        chkBtn = (ImageButton) findViewById(R.id.chkBtn);
        timeText = (TextView) findViewById(R.id.timeText);



        Intent intent = getIntent(); //액티비티간에 인수와 리턴값을 전달
        int id = intent.getIntExtra("id", -1); //toDoList의 id 컬럼 값
        if (id != -1) { //id 컬럼 값이 있을 경우(수정 페이지)
            sqlDB = todo.getReadableDatabase(); //Create and/or open a database
            cursor = sqlDB.rawQuery("SELECT * FROM toDo WHERE ID =" + Integer.toString(id) + ";", null); //id에 해당하는 컬럼의 값을 가져옴
            cursor.moveToFirst(); //커서를 첫번째로 이동
//            if(cursor.getString(FIELD_NAME_ON_CREATE) != null){
//
//            }
        }


        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTime();
                showDate();
                Toast.makeText(getApplicationContext(), Integer.toString(y) + Integer.toString(m) + Integer.toString(d), Toast.LENGTH_SHORT).show();
            }
        });


        chkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeText.setText(y + "." + m + "." + d + "\n" + h + ":" + mi);

            }
        });



        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Toast.makeText(getApplicationContext(), "할일목록을 저장하였습니다!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    void showDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                y = year;
                m = month + 1;
                d = dayOfMonth;

            }
        },Integer.parseInt(timeNow[0]),Integer.parseInt(timeNow[1]),Integer.parseInt(timeNow[2]));

        datePickerDialog.setMessage("날짜를 선택하시오");
        datePickerDialog.show();
    }

    void showTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                h = hourOfDay;
                mi = minute;

            }
        }, Integer.parseInt(timeNow[3]), Integer.parseInt(timeNow[4]), true);
        timePickerDialog.setMessage("시간을 선택하시오");
        timePickerDialog.show();
//        timePickerDialog.onTimeChanged(timePickerDialog.setContentView(this););
    }
}