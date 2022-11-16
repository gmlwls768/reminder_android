package com.example.reminder_project;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {
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
    LocalDateTime now; // 날짜와 시간을 가져오는 패키지
    LinearLayout listLayoutParent;
    Button completeBtn;
    ImageButton addBtn;
    EditText quickAddText;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        todo = new ToDoTable(this);
        sqlDB = todo.getWritableDatabase(); //Create and/or open a database
        cursor = sqlDB.rawQuery("SELECT * FROM toDo WHERE IS_COMPLETE = 0;", null); //데이터베이스의 모든 값을 가져옴
        completeBtn = (Button) findViewById(R.id.completeBtn);
        quickAddText = (EditText) findViewById(R.id.quickAddText);
        addBtn = (ImageButton) findViewById(R.id.addBtn);
        listLayoutParent = (LinearLayout) findViewById(R.id.list_layout_parent);

        while (cursor.moveToNext()) {
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT); //너비, 높이 설정 단축어
            LinearLayout.LayoutParams w = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT); //너비, 높이 설정 단축어
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

            tv.setOnClickListener(new View.OnClickListener() { // 클릭 시 목록 수정페이지로 이동
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), WorkActivity.class);
                    intent.putExtra("id", listId);
                    startActivity(intent);
                }
            });

            cb.setOnClickListener(new View.OnClickListener() { // 클릭 시 해당 목록이 완료된 항목으로 이동
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View view) {
                    now = LocalDateTime.now();
                    String formatedNow = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss")); //현재날짜 설정
                    sqlDB.execSQL("UPDATE toDo SET IS_COMPLETE = 1 WHERE ID = " + listId + ";");
                    sqlDB.execSQL("UPDATE toDo SET ON_COMPLETED = " + formatedNow + " WHERE ID = " + listId + ";");
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

        completeBtn.setOnClickListener(new View.OnClickListener() { //완료한 목록 페이지로 이동
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),CompleteActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "완료한 페이지 목록으로 이동하였습니다!",Toast.LENGTH_SHORT).show();
            }
        });

        quickAddText.addTextChangedListener(new TextWatcher() { //키보드 이벤트가 발생할 경우
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { // 입력하기 전에 조치

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { //입력난에 변화가 있을 시 조치
                if (quickAddText.getText().toString().equals("")) { //입력된 내용에 따라 이미지 변경
                    addBtn.setImageResource(R.drawable.add_icon);
                }
                else {
                    addBtn.setImageResource(R.drawable.check_icon);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { // 입력이 끝났을 때 조치

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O) //API 경고 무시
            @Override
            public void onClick(View view) {
                if (quickAddText.getText().toString().equals("")){ //quickAddText가 공백일 경우 목록 추가(수정) 페이지로 이동
                    Intent intent = new Intent(getApplicationContext(), WorkActivity.class);
                    startActivity(intent);
                } else{ //그렇지 않을 경우 toDoTable에 입력받은 값을 추가
                    now = LocalDateTime.now();
                    sqlDB = todo.getWritableDatabase();
                    String title = quickAddText.getText().toString();
                    String formatedNow = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss")); //현재날짜 설정

                    sqlDB.execSQL("INSERT INTO toDo (TITLE, ON_CREATE) VALUES('" + title + "','" + formatedNow +"');"); //toDoTable 데이터 추가
                    sqlDB.close();
                    quickAddText.setText("");
                    Toast.makeText(getApplicationContext(), title + "을 추가하였습니다", Toast.LENGTH_SHORT).show();
                    finish();//인텐트 종료
                    overridePendingTransition(0, 0);//인텐트 효과 없애기
                    intent = getIntent(); //인텐트
                    startActivity(intent); //액티비티 열기
                    overridePendingTransition(0, 0);//인텐트 효과 없애기
                }
            }
        });

    }
}

