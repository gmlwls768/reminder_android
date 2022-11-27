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

    LinearLayout listContainer;
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
        listContainer = (LinearLayout) findViewById(R.id.listContainer);

        while (cursor.moveToNext()) {
//            String result = "";
//            for (int i = 0; i < 8; i++) {
//                result += cursor.getString(i) + " ";
//            }
//            System.out.println("DB DATA: " + result);

            String tableRowId = cursor.getString(ToDoTable.FIELD_NAME_ON_CREATE); //DB 개인키

            //너비, 높이 설정 단축어
            LinearLayout.LayoutParams MP_WC = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams WC_MP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);

            LinearLayout listItem = new LinearLayout(getApplicationContext()); // 각각의 할일 목록을 감싸는 레이아웃
            CheckBox DeleteChkBox = new CheckBox(getApplicationContext()); // 완료버튼
            TextView listItemTitle = new TextView(getApplicationContext()); // 제목

            listItem.setLayoutParams(MP_WC);
            listItem.setOrientation(LinearLayout.HORIZONTAL);

            DeleteChkBox.setLayoutParams(WC_MP);

            listItemTitle.setLayoutParams(WC_MP);
            listItemTitle.setText(cursor.getString(ToDoTable.FIELD_NAME_TITLE)); // View의 텍스트 값을 toDoTable의 TITLE 컬럼의 값으로 설정

            listItem.addView(DeleteChkBox);
            listItem.addView(listItemTitle);
            listContainer.addView(listItem);

            // 클릭 시 해당목록 완전삭제
            DeleteChkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sqlDB.execSQL("DELETE FROM toDo WHERE ON_CREATE='" + tableRowId + "';");
                    finish();//인텐트 종료
                    overridePendingTransition(0, 0);//인텐트 효과 없애기
                    Intent intent = getIntent(); //인텐트
                    startActivity(intent); //액티비티 열기
                    overridePendingTransition(0, 0);//인텐트 효과 없애기
                    Toast.makeText(getApplicationContext(), "할일 목록이 삭제 되었습니다",Toast.LENGTH_SHORT).show();
                }
            });
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