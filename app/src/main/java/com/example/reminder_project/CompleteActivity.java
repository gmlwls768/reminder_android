package com.example.reminder_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class CompleteActivity extends AppCompatActivity {
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
    Intent intent;
    LinearLayout listLayoutParent;
    Button todoBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);
        todo = new ToDoTable(this);
        sqlDB = todo.getWritableDatabase(); //Create and/or open a database
        cursor = sqlDB.rawQuery("SELECT * FROM toDo WHERE IS_COMPLETE = 1;", null); //데이터베이스의 모든 값을 가져옴
        todoBtn = findViewById(R.id.todoBtn);
        listLayoutParent = (LinearLayout) findViewById(R.id.list_layout_parent);

        while (cursor.moveToNext()) {
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT); //너비, 높이 설정 단축어
            LinearLayout.LayoutParams w = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT); //너비, 높이 설정 단축어
            LinearLayout listItemBox= new LinearLayout(getApplicationContext()); // 각각의 할일 목록을 감싸는 레이아웃
            listItemBox.setLayoutParams(p); //너비, 높이 설정
            listItemBox.setOrientation(LinearLayout.HORIZONTAL); // 수평 정렬
            int listId = Integer.parseInt(cursor.getString(FIELD_NAME_ID));
            CheckBox cb = new CheckBox(getApplicationContext()); // listItemBox 내 사용될 CheckBox
            cb.setLayoutParams(w);

            TextView tv= new TextView(getApplicationContext()); // listItemBox 내 사용될 TextView
            tv.setLayoutParams(w);
            tv.setText(cursor.getString(FIELD_NAME_TITLE)); // View의 텍스트 값을 toDoTable의 TITLE 컬럼의 값으로 설정

            TextView dbIdView= new TextView(getApplicationContext()); //listItemBox 내 사용될 TextView
            dbIdView.setLayoutParams(w);
            dbIdView.setVisibility(View.GONE);
            dbIdView.setText(cursor.getString(FIELD_NAME_ID)); // View의 텍스트 값을 toDoTable의 ID 컬럼의 값으로 설정

            cb.setOnClickListener(new View.OnClickListener() { // 클릭 시 해당 목록이 완료된 항목으로 이동
                @Override
                public void onClick(View view) {
                    sqlDB.execSQL("DELETE FROM toDo WHERE ID = " + listId + ";");
                    finish();//인텐트 종료
                    overridePendingTransition(0, 0);//인텐트 효과 없애기
                    intent = getIntent(); //인텐트
                    startActivity(intent); //액티비티 열기
                    overridePendingTransition(0, 0);//인텐트 효과 없애기
                }
            });

            listItemBox.addView(cb); //자식 위젯 설절
            listItemBox.addView(tv); //자식 위젯 설절
            listLayoutParent.addView(listItemBox); //자식 위젯 설절
        }
        cursor.close();

        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker); //타임피커 연결

        todoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Toast.makeText(getApplicationContext(), "할일 목록으로 이동하였습니다!",Toast.LENGTH_SHORT).show();
            }

        });
    }
}