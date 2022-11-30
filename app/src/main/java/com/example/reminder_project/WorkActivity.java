package com.example.reminder_project;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
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

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


@RequiresApi(api = Build.VERSION_CODES.O)
public class WorkActivity extends AppCompatActivity {
    ToDoTable todo;
    SQLiteDatabase sqlDB;
    Cursor cursor;
    int alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinute; // 알림 시간
    boolean isModifyActivity; // 수정 or 추가 페이지 판별

    EditText placeEdtTxt, titleEdtTxt, contentEdtTxt;
    RadioGroup priorityGroup;
    RadioButton highPriorityBtn, mediumPriorityBtn, lowPriorityBtn, nonePriorityBtn;
    ImageButton alarmSetBtn;
    TextView AlarmTimeView;
    Button saveBtn;


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        alarmYear = 0; alarmMonth = 0; alarmDay = 0; alarmHour = 0; alarmMinute = 0;
        todo = new ToDoTable(this);

        titleEdtTxt = (EditText) findViewById(R.id.titleEdtTxt);
        contentEdtTxt = (EditText) findViewById(R.id.contentEdtTxt);

        priorityGroup = (RadioGroup) findViewById(R.id.priorityGroup);
        highPriorityBtn = (RadioButton) findViewById(R.id.highPriorityBtn);
        mediumPriorityBtn = (RadioButton) findViewById(R.id.mediumPriorityBtn);
        lowPriorityBtn = (RadioButton) findViewById(R.id.lowPriorityBtn);
        nonePriorityBtn = (RadioButton) findViewById(R.id.nonePriorityBtn);

        alarmSetBtn = (ImageButton) findViewById(R.id.alarmSetBtn);
        AlarmTimeView = (TextView) findViewById(R.id.timeText);
        placeEdtTxt = (EditText) findViewById(R.id.placeEdtTxt);
        saveBtn = (Button) findViewById(R.id.saveBtn);

        Button moveGui = (Button) findViewById(R.id.moveGui);
        // 수정 or 추가 페이지 판별
        Intent intent = getIntent();
        String tableRowId = intent.getStringExtra("id");
        if(tableRowId == null) isModifyActivity = false;
        else isModifyActivity = true;

        if (isModifyActivity) {
            sqlDB = todo.getReadableDatabase(); //Create and/or open a database
            cursor = sqlDB.rawQuery("SELECT * FROM toDo WHERE ON_CREATE ='" + tableRowId + "';", null); //id에 해당하는 컬럼의 값을 가져옴
            cursor.moveToFirst(); //커서를 첫번째로 이동

//            String result = "";
//            for (int i = 0; i < 8; i++) {
//                result += cursor.getString(i) + " ";
//            }
//            System.out.println(result);

            titleEdtTxt.setText(cursor.getString(ToDoTable.FIELD_NAME_TITLE)); //타이틀 설정

            if(cursor.getString(ToDoTable.FIELD_NAME_CONTENTS) != null){ //할일 내용 설절
                contentEdtTxt.setText(cursor.getString(ToDoTable.FIELD_NAME_CONTENTS));
            }

            int userPriority = Integer.parseInt(cursor.getString(ToDoTable.FIELD_NAME_PRIORITY));
            switch (userPriority){ //우선순위 설정
                case 0:
                    priorityGroup.check(R.id.nonePriorityBtn);
                    break;
                case 1:
                    priorityGroup.check(R.id.lowPriorityBtn);
                    break;
                case 2:
                    priorityGroup.check(R.id.mediumPriorityBtn);
                    break;
                case 3:
                    priorityGroup.check(R.id.highPriorityBtn);
                    break;
            }

            if(cursor.getString(ToDoTable.FIELD_NAME_ALARM) != null){ //알림시간 설정
                String alarmSetTime [] = cursor.getString(ToDoTable.FIELD_NAME_ALARM).split("/");
                alarmYear = Integer.parseInt(alarmSetTime[0]);
                alarmMonth = Integer.parseInt(alarmSetTime[1]);
                alarmDay = Integer.parseInt(alarmSetTime[2]);
                alarmHour = Integer.parseInt(alarmSetTime[3]);
                alarmMinute = Integer.parseInt(alarmSetTime[4]);
                AlarmTimeView.setText(alarmYear + "년" + alarmMonth + "월" + alarmDay + "일" + alarmHour + "시" + alarmMinute + "분");
            }

            if(cursor.getString(ToDoTable.FIELD_NAME_LOCATION) != null){ //장소 설정
                placeEdtTxt.setText(cursor.getString(ToDoTable.FIELD_NAME_LOCATION));
            }
        }

        // 알림시간 설정
        alarmSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LocalDateTime nowTime = LocalDateTime.now();
                String formatNowTime = nowTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm"));
                String [] splitedNowTime = formatNowTime.split("/");

                showTime(splitedNowTime[3], splitedNowTime[4]);
                showDate(splitedNowTime[0], splitedNowTime[1], splitedNowTime[2]);
            }
        });

        // 목록 저장 OR 수정
        saveBtn.setOnClickListener(new View.OnClickListener() {
            final LocalDateTime nowTime = LocalDateTime.now();
            final String formatNowTime = nowTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            @Override
            public void onClick(View view) {
                String _Title = titleEdtTxt.getText().toString();
                String _Contents = contentEdtTxt.getText().toString();
                String _priority = "0";
                switch (priorityGroup.getCheckedRadioButtonId()){
                    case R.id.nonePriorityBtn:
                        _priority = "0";
                        break;
                    case R.id.lowPriorityBtn:
                        _priority = "1";
                        break;
                    case R.id.mediumPriorityBtn:
                        _priority = "2";
                        break;
                    case R.id.highPriorityBtn:
                        _priority = "3";
                        break;
                }

                String myLocation = placeEdtTxt.getText().toString();
                sqlDB = todo.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("TITLE", _Title);
                cv.put("CONTENTS", _Contents);
                cv.put("PRIORITY", _priority);
                cv.put("LOCATION", myLocation);

                if(alarmDay == 0){ //날짜가 지정되지 않은 경우
                    if (isModifyActivity) {
                        sqlDB.update("toDo", cv, "ON_CREATE='" + tableRowId + "'", null);
                    }else{
                        cv.put("ON_CREATE", formatNowTime);
                        sqlDB.insert("toDo", null, cv);
                    }
                }else{
                    Calendar alarmCalendar = setAlarmTime();
                    String alarmTitle = titleEdtTxt.getText().toString();
                    String alarmSummary = contentEdtTxt.getText().toString();

                    Date alarmTime = alarmCalendar.getTime();
                    String _alarm = new SimpleDateFormat("yyyy/MM/dd/HH/mm").format(alarmTime);
                    cv.put("ALARM", _alarm);

                    if (isModifyActivity) {
                        sqlDB.update("toDo", cv, "ON_CREATE='" + tableRowId + "'", null);
                        makeAlarm(alarmCalendar, alarmTitle, alarmSummary, tableRowId);
                    }else{
                        cv.put("ON_CREATE", formatNowTime);
                        sqlDB.insert("toDo", null, cv);
                        makeAlarm(alarmCalendar, alarmTitle, alarmSummary, formatNowTime);
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

        moveGui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);//인텐트 효과 없애기

            }
        });
    }

    public void showDate(String sYear, String sMonth, String sDay) {
        DatePickerDialog dialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                alarmYear = year;
                alarmMonth = monthOfYear + 1;
                alarmDay = dayOfMonth;
            }
        },Integer.parseInt(sYear),Integer.parseInt(sMonth) -1,Integer.parseInt(sDay));
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setMessage("날짜를 선택하시오");
        dialog.show();
    }
    public void showTime(String sHour, String sMinute) {
        TimePickerDialog dialog = new TimePickerDialog(this,android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                alarmHour = hourOfDay;
                alarmMinute = minute;
                AlarmTimeView.setText(alarmYear + "년" + alarmMonth + "월" + alarmDay + "일" + alarmHour + "시" + alarmMinute + "분");
            }
        }, Integer.parseInt(sHour), Integer.parseInt(sMinute), true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setMessage("시간을 선택하시오");
        dialog.show();
    }

    public Calendar setAlarmTime() { //날짜 설정 및 Toast 메시지 출력 후 Calender 객체 반환
        int YEAR = alarmYear; int MONTH = alarmMonth - 1; int DAY = alarmDay; int HOUR = alarmHour; int MINUTE = alarmMinute;

        // 현재 지정된 시간으로 알람 시간 설정
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.YEAR, YEAR);
        calendar.set(Calendar.MONTH, MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, DAY);
        calendar.set(Calendar.HOUR_OF_DAY, HOUR);
        calendar.set(Calendar.MINUTE, MINUTE);
        calendar.set(Calendar.SECOND, 0);

        //설정한 날짜를 Toast 메시지로 보여줌
        Date currentDateTime = calendar.getTime();
        String date = new SimpleDateFormat("yyyy년 MM월 dd일 EE a hh시 mm분", Locale.getDefault()).format(currentDateTime);
        Toast.makeText(getApplicationContext(), date + "으로 알림이 설정되었습니다!", Toast.LENGTH_SHORT).show();
        return calendar;
    }

    public void makeAlarm(Calendar alarmTime, String alarmTitle, String alarmSummary, String tableRowId){
        Intent alarmIntent = new Intent(getApplicationContext(), AlarmNotification.class);
        alarmIntent.putExtra("alarmTitle", alarmTitle);
        alarmIntent.putExtra("alarmSummary", alarmSummary);
        alarmIntent.putExtra("tableRowId", tableRowId);
        // PendingIntent 는, 가지고 있는 Intent 를 당장 수행하진 않고 특정 시점에 수행하도록 하는 특징을 갖고 있다
        // 특정시점에 alarmIntent(AlarmNotification)가 실행 됨
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) (System.currentTimeMillis() / 1000), alarmIntent, FLAG_IMMUTABLE);
        // 특정시점 설정
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent); //지정된 시간에 휴대폰 화면을 깨우고 매일 반복하여 알림을 보여줌

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Android Marshmallow 이상에서만 실행되는 코드
                // 사용자가 전원을 충전하지 않고 화면이 꺼진 채로 기기를 일정 기간 정지 상태로 두면 기기는 Doze 모드를 시작하게 됩니다.
                // Doze 모드에서도 실행되는 알람을 설정해야 하는 경우 setAndAllowWhileIdle() 또는 setExactAndAllowWhileIdle()를 사용합니다.
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
            }
        }
    }
}