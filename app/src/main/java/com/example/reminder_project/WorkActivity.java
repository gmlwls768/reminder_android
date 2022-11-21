package com.example.reminder_project;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
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
import android.widget.RadioGroup;
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
    public static final int FIELD_NAME_ON_COMPLETE = 7;

//    AlarmManager alarmManager;
//    PendingIntent pendingIntent;

    int y = 0, m = 0, d = 0, h = 0, mi = 0; // 사용자가 설정한 시간
    ToDoTable todo; //A helper class to manage database creation and version management.
    SQLiteDatabase sqlDB;
    Cursor cursor;
    RadioGroup priorityGroup;
    Button saveBtn;
    ImageButton timeBtn, chkBtn;
    RadioButton highPriority, mediumPriority, lowPriority, nonePriority;
    EditText placeInfo, title, content;
    TextView timeText;
    LocalDateTime now= LocalDateTime.now();
    String formatedNow = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm"));
    String timeNow [] = formatedNow.split("/");


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

//        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        final Calendar calendar = Calendar.getInstance();


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        todo = new ToDoTable(this);
        title = (EditText) findViewById(R.id.title);
        content = (EditText) findViewById(R.id.content);
        priorityGroup = (RadioGroup) findViewById(R.id.priorityGroup);
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
            int userPriority = Integer.parseInt(cursor.getString(FIELD_NAME_PRIORITY));
            switch (userPriority){ //우선순위 설정
                case 0:
                    priorityGroup.check(R.id.nonePriority);
                    break;
                case 1:
                    priorityGroup.check(R.id.lowPriority);
                    break;
                case 2:
                    priorityGroup.check(R.id.mediumPriority);
                    break;
                case 3:
                    priorityGroup.check(R.id.highPriority);
                    break;
            }

            title.setText(cursor.getString(FIELD_NAME_TITLE)); //타이틀 설정

            if(cursor.getString(FIELD_NAME_CONTENTS) != null){ //할일 내용 설절
                content.setText(cursor.getString(FIELD_NAME_CONTENTS));
            }

            if(cursor.getString(FIELD_NAME_ON_CREATE) != null){ //알림시간 설정
                String userAlertTime [] = cursor.getString(FIELD_NAME_ON_CREATE).split("/");
                y = Integer.parseInt(userAlertTime[0]);
                m = Integer.parseInt(userAlertTime[1]);
                d = Integer.parseInt(userAlertTime[2]);
                h = Integer.parseInt(userAlertTime[3]);
                mi = Integer.parseInt(userAlertTime[4]);
                timeText.setText(y + "." + m + "." + d + "\n" + h + ":" + mi);

//                calendar.set(y,m,d,h,mi);
//                alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
//                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_HOUR,pendingIntent);
            }

            if(cursor.getString(FIELD_NAME_LOCATION) != null){ //장소 설정

                placeInfo.setText(cursor.getString(FIELD_NAME_LOCATION));
            }

        }


        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTime();
                showDate();
            }
        });


        chkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeText.setText("Date: " +y + "." + m + "." + d + "\n" + "Time: " + h + ":" + mi);
            }
        });



        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myTitle = title.getText().toString();
                String myContents = content.getText().toString();
                String myPriority = "0";
                switch (priorityGroup.getCheckedRadioButtonId()){
                    case R.id.nonePriority:
                        myPriority = "0";
                        break;
                    case R.id.lowPriority:
                        myPriority = "1";
                        break;
                    case R.id.mediumPriority:
                        myPriority = "2";
                        break;
                    case R.id.highPriority:
                        myPriority = "3";
                        break;
                }
                String myLocation = placeInfo.getText().toString();
                sqlDB = todo.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("TITLE", myTitle); //These Fields should be your String values of actual column names
                cv.put("CONTENTS", myContents);
                cv.put("PRIORITY", myPriority);
                cv.put("LOCATION", myLocation);

                if(y != 0){ //날짜가 지정되지 않은 경우
                    String myAlert = Integer.toString(y) + "/" + Integer.toString(m) + "/" + Integer.toString(d) + "/" + Integer.toString(h) + "/" + Integer.toString(mi);
                    cv.put("ALERT", myAlert);
                    if (id != -1) { //id 컬럼 값이 있을 경우(수정 페이지)
                        sqlDB.update("toDo", cv, "id=" + id, null);
                    }else{
                        cv.put("ON_CREATE", formatedNow);
                        sqlDB.insert("toDo", null, cv);
                    }

                }else {
                    if (id != -1) { //id 컬럼 값이 있을 경우(수정 페이지)
                        sqlDB.update("toDo", cv, "id=" + id, null);
                    }else{
                        cv.put("ON_CREATE", formatedNow);
                        sqlDB.insert("toDo", null, cv);
                    }
                }
                sqlDB.close();
                Toast.makeText(getApplicationContext(), "할일목록을 저장하였습니다!", Toast.LENGTH_SHORT).show();
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);//인텐트 효과 없애기

            }
        });
    }


    void showDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                y = year;
                m = month + 1;
                d = dayOfMonth;

            }
        },Integer.parseInt(timeNow[0]),Integer.parseInt(timeNow[1]),Integer.parseInt(timeNow[2]));
        datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        datePickerDialog.setMessage("날짜를 선택하시오");
        datePickerDialog.show();
    }

    void showTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                h = hourOfDay;
                mi = minute;
            }
        }, Integer.parseInt(timeNow[3]), Integer.parseInt(timeNow[4]), true);
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.setMessage("시간을 선택하시오");
        timePickerDialog.show();
    }
}