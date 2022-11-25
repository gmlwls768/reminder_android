package com.example.reminder_project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CompleteActivity extends AppCompatActivity {
    ToDoTable todo; //A helper class to manage database creation and version management.
    SQLiteDatabase sqlDB;
    Cursor cursor;
    Intent intent;
    LinearLayout listLayoutParent;
    Button todoBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setWindowAnimations(0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        todo = new ToDoTable(this);
        sqlDB = todo.getWritableDatabase(); //Create and/or open a database
        cursor = sqlDB.rawQuery("SELECT * FROM toDo WHERE IS_COMPLETE = 1;", null); //데이터베이스의 모든 값을 가져옴
        todoBtn = findViewById(R.id.todoBtn);
        listLayoutParent = (LinearLayout) findViewById(R.id.list_layout_parent);

        while (cursor.moveToNext()) {
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT); //너비, 높이 설정 단축어
            LinearLayout.LayoutParams w = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT); //너비, 높이 설정 단축어
            LinearLayout listItemBox= new LinearLayout(getApplicationContext()); // 각각의 할일 목록을 감싸는 레이아웃
            LinearLayout oneBox = new LinearLayout(getApplicationContext());
            oneBox.setLayoutParams(p);
            oneBox.setGravity(Gravity.RIGHT);
            listItemBox.setLayoutParams(p); //너비, 높이 설정
            listItemBox.setOrientation(LinearLayout.HORIZONTAL); // 수평 정렬

            String listId = cursor.getString(ToDoTable.FIELD_NAME_ON_CREATE);
            CheckBox cb = new CheckBox(getApplicationContext()); // listItemBox 내 사용될 CheckBox
            cb.setLayoutParams(w);

            TextView tv= new TextView(getApplicationContext()); // listItemBox 내 사용될 TextView
            tv.setLayoutParams(w);
            tv.setText(cursor.getString(ToDoTable.FIELD_NAME_TITLE)); // View의 텍스트 값을 toDoTable의 TITLE 컬럼의 값으로 설정

            TextView tvPriority= new TextView(getApplicationContext()); // listItemBox 내 사용될 TextView
            tvPriority.setLayoutParams(w);
            int tempPriority = Integer.parseInt(cursor.getString(ToDoTable.FIELD_NAME_PRIORITY));
            String tempPriorityText = "";
            if(tempPriority>=1) {
                for (int i = 0; i < tempPriority; i++) {
                    tempPriorityText += "★";
                }
            }
            tvPriority.setText(tempPriorityText);
            tvPriority.setTextColor(Color.RED);

            cb.setOnClickListener(new View.OnClickListener() { // 클릭 시 해당 목록이 완료된 항목으로 이동
                @Override
                public void onClick(View view) {
                    sqlDB.execSQL("DELETE FROM toDo WHERE ON_CREATE='" + listId + "';");
                    finish();//인텐트 종료
                    overridePendingTransition(0, 0);//인텐트 효과 없애기
                    intent = getIntent(); //인텐트
                    startActivity(intent); //액티비티 열기
                    overridePendingTransition(0, 0);//인텐트 효과 없애기
                    Toast.makeText(getApplicationContext(), "할일 목록이 삭제 되었습니다",Toast.LENGTH_SHORT).show();
                }
            });
            oneBox.addView(tvPriority);
            listItemBox.addView(cb); //자식 위젯 설절
            listItemBox.addView(tv); //자식 위젯 설절
            listLayoutParent.addView(listItemBox); //자식 위젯 설절
            listItemBox.addView(oneBox);

        }
        cursor.close();


        todoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);//인텐트 효과 없애기
                Toast.makeText(getApplicationContext(), "할일 목록으로 이동하였습니다!",Toast.LENGTH_SHORT).show();
            }

        });
    }
}