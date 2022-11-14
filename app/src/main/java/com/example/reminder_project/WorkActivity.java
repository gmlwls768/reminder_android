package com.example.reminder_project;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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
    Button save_remind;
    Switch timeSetSwitch;
    TimePicker timeSetTimepicker;
    DatePicker timeSetDatepicker;
    RadioButton highPriority,mediumPriority,lowPriority,nonePriority;
    EditText setPlace, todoText;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        highPriority = (RadioButton) findViewById(R.id.highPriority);
        mediumPriority = (RadioButton) findViewById(R.id.mediumPriority);
        lowPriority = (RadioButton) findViewById(R.id.lowPriority);
        nonePriority = (RadioButton) findViewById(R.id.nonePriority);
        save_remind = (Button) findViewById(R.id.save_remind);
        timeSetSwitch = (Switch) findViewById(R.id.timeSetSwitch);
        timeSetTimepicker = (TimePicker) findViewById(R.id.timeSetTimepicker);
        timeSetDatepicker = (DatePicker) findViewById(R.id.timeSetDatepicker);
        setPlace = (EditText) findViewById(R.id.setPlace);
        todoText = (EditText) findViewById(R.id.todoText);
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
        save_remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Toast.makeText(getApplicationContext(),"할일목록을 저장하였습니다!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}