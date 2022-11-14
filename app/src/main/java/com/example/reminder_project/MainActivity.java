package com.example.reminder_project;

import androidx.annotation.RequiresApi;
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
    TextView listItem;
    Button completeBtn;
    ImageButton addBtn;
    EditText quickAddText;
    ToDoTable todo; //todo 테이블
    SQLiteDatabase sqlDB;
    LinearLayout container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        todo = new ToDoTable(this);
        completeBtn = (Button)  findViewById(R.id.completeBtn);
        sqlDB = todo.getReadableDatabase();
        Cursor cursor;
        cursor = sqlDB.rawQuery("SELECT * FROM toDo;", null);

        container = (LinearLayout) findViewById(R.id.list_layout_parent);
        int add_layout = 0;
        while (cursor.moveToNext()) {
            LinearLayout ll= new LinearLayout(getApplicationContext());
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            ll.setLayoutParams(p);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            CheckBox cb = new CheckBox(getApplicationContext());
            LinearLayout.LayoutParams w = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            cb.setLayoutParams(w);
            ll.addView(cb);
            TextView tv= new TextView(getApplicationContext());
            tv.setLayoutParams(w);
            tv.setText(cursor.getString(1));
            tv.setSingleLine();
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), WorkActivity.class);
                    startActivity(intent);

                }
            });
            TextView tvminus= new TextView(getApplicationContext());
            tvminus.setLayoutParams(w);
            tvminus.setVisibility(View.GONE);
            tvminus.setText(cursor.getString(0));
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String content = tv.getText().toString();
                    Toast.makeText(getApplicationContext(), content,Toast.LENGTH_SHORT).show();
                }
            });
            ll.addView(tv);
            container.addView(ll);
        }


        completeBtn.setOnClickListener(new View.OnClickListener() { //완료한 목록 페이지로 이동
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),CompleteActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "완료한 페이지 목록으로 이동하였습니다!",Toast.LENGTH_SHORT).show();
            }
        });

        quickAddText = (EditText) findViewById(R.id.quickAddText);
        addBtn = (ImageButton) findViewById(R.id.addBtn);
        quickAddText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (quickAddText.getText().toString().equals("")){
                    addBtn.setImageResource(R.drawable.add_icon);
                }
                else {
                    addBtn.setImageResource(R.drawable.check_icon);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (quickAddText.getText().toString().equals("")){ //quickAddText가 공백일 경우 목록 추가(수정) 페이지로 이동
                    Intent intent = new Intent(getApplicationContext(), WorkActivity.class);
                    startActivity(intent);
                } else{
                    LocalDateTime now = LocalDateTime.now();
                    sqlDB = todo.getWritableDatabase();

                    String formatedNow = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss"));
                    sqlDB.execSQL("INSERT INTO toDo (CONTENTS,  PRIORITY, LOCATION, ALERT, ON_CREATE, IS_COMPLETE, ON_COMPLETED) VALUES('" + quickAddText.getText().toString() + "'," + " 0, 'none', 'none', '" + formatedNow +"', 0, 'none');");
                    sqlDB.close();
                    quickAddText.setText("");
                    Toast.makeText(getApplicationContext(), "INSERT INTO toDo VALUES('" + quickAddText.getText().toString() + "'," + " 0, 'none', 'none', '" + formatedNow +"');",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

