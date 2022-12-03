package com.example.reminder_project;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedList;


public class MainActivity extends AppCompatActivity {
    ToDoTable todo; //A helper class to manage database creation and version management.
    SQLiteDatabase sqlDB;
    Cursor cursor;
    LinearLayout listContainer;
    Button completeBtn;
    ImageButton listAddBtn;
    EditText quickAddEdt;
    LinkedList<LinearLayout> highPriorityItem;
    LinkedList<LinearLayout> mediumPriorityItem;
    LinkedList<LinearLayout> lowPriorityItem;
    LinkedList<LinearLayout> nonePriorityItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        getWindow().setWindowAnimations(0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);


        todo = new ToDoTable(this);
        sqlDB = todo.getWritableDatabase(); //Create and/or open a database
        cursor = sqlDB.rawQuery("SELECT * FROM toDo WHERE IS_COMPLETE = 0;", null); //데이터베이스의 모든 값을 가져옴
        getHashKey();

        listContainer = (LinearLayout) findViewById(R.id.listContainer);
        completeBtn = (Button) findViewById(R.id.completeBtn);
        listAddBtn = (ImageButton) findViewById(R.id.addBtn);
        quickAddEdt = (EditText) findViewById(R.id.quickAddText);

        highPriorityItem = new LinkedList<>(); // 높은 우선순위 목록
        mediumPriorityItem = new LinkedList<>(); // 중간 우선순위 목록
        lowPriorityItem = new LinkedList<>(); // 낮은 우선순위 목록
        nonePriorityItem = new LinkedList<>(); //우선순위 없은 목록

        while (cursor.moveToNext()) {
            String result = "";
            for (int i = 0; i < 10; i++) {
                result += cursor.getString(i) + " ";
            }
            System.out.println("DB DATA: " + result);

            String tableRowId = cursor.getString(ToDoTable.FIELD_NAME_ON_CREATE); //DB 개인키

            //너비, 높이 설정 단축어
            LinearLayout.LayoutParams MP_WC = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams WC_MP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);

            LinearLayout listItem = new LinearLayout(getApplicationContext()); // 각각의 할일 목록을 감싸는 레이아웃
            LinearLayout listItemPriorityContainer = new LinearLayout(getApplicationContext()); // 우선순위를 감싸는 레이아웃
            CheckBox completeChkBox = new CheckBox(getApplicationContext()); // 완료버튼
            TextView listItemTitle = new TextView(getApplicationContext()); // 제목
            TextView listItemPriority = new TextView(getApplicationContext()); // 우선순위

            listItem.setLayoutParams(MP_WC);
            listItem.setOrientation(LinearLayout.HORIZONTAL);

            listItemPriorityContainer.setLayoutParams(MP_WC);
            listItemPriorityContainer.setGravity(Gravity.RIGHT);

            completeChkBox.setLayoutParams(WC_MP);

            listItemTitle.setLayoutParams(WC_MP);
            listItemTitle.setText(cursor.getString(ToDoTable.FIELD_NAME_TITLE)); // View의 텍스트 값을 toDoTable의 TITLE 컬럼의 값으로 설정

            int prioritySize = Integer.parseInt(cursor.getString(ToDoTable.FIELD_NAME_PRIORITY)); //우선순위만큼 별 생성
            String priorityRank = "";
            for (int i = 0; i < prioritySize; i++) {
                priorityRank += "★";
            }

            listItemPriority.setLayoutParams(WC_MP);
            listItemPriority.setText(priorityRank);
            listItemPriority.setTextColor(Color.RED);

            //자식 위젯 설정
            listItem.addView(completeChkBox);
            listItem.addView(listItemTitle);
            listItemPriorityContainer.addView(listItemPriority);
            listItem.addView(listItemPriorityContainer);

            //우선순위에 따라 리스트에 삽입
            switch (prioritySize) {
                case 0:
                    nonePriorityItem.add(listItem);
                    break;
                case 1:
                    lowPriorityItem.add(listItem);
                    break;
                case 2:
                    mediumPriorityItem.add(listItem);
                    break;
                case 3:
                    highPriorityItem.add(listItem);
                    break;
            }

            // 클릭 시 목록 수정페이지로 이동
            listItemTitle.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), WorkActivity.class);
                    intent.putExtra("id", tableRowId); //해당 목록의 개인키를 넘겨준다
                    startActivity(intent);
                    overridePendingTransition(0, 0);//인텐트 효과 없애기

                }
            });

            // 클릭 시 해당 목록이 완료된 항목으로 이동
            completeChkBox.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View view) {
                    LocalDateTime now = LocalDateTime.now();
                    String formatNow = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss")); //현재날짜 설정

                    ContentValues cv = new ContentValues();
                    cv.put("IS_COMPLETE", "1"); //These Fields should be your String values of actual column names
                    cv.put("ALARM", "0000/00/00/00/00");
                    cv.put("ON_COMPLETE", formatNow);
                    sqlDB.update("toDo", cv, "ON_CREATE='" + tableRowId + "'", null);
                    Toast.makeText(getApplicationContext(), "할 일 완료!", Toast.LENGTH_SHORT).show();

                    finish();//인텐트 종료
                    overridePendingTransition(0, 0);//인텐트 효과 없애기
                    Intent intent = getIntent(); //인텐트
                    startActivity(intent); //액티비티 열기
                    overridePendingTransition(0, 0);//인텐트 효과 없애기
                }
            });
        }
        cursor.close();

        //높음, 중간, 낮음, 없음순으로 목록을 정렬한다
        //우선순위 내에서는 목록을 저장한 순서대로 정렬한다.
        Iterator<LinearLayout> ir;
        ir = highPriorityItem.iterator();
        while (ir.hasNext()) {
            LinearLayout childLayout = ir.next();
            listContainer.addView(childLayout);
        }

        ir = mediumPriorityItem.iterator();
        while (ir.hasNext()) {
            LinearLayout childLayout = ir.next();
            listContainer.addView(childLayout);
        }

        ir = lowPriorityItem.iterator();
        while (ir.hasNext()) {
            LinearLayout childLayout = ir.next();
            listContainer.addView(childLayout);
        }

        ir = nonePriorityItem.iterator();
        while (ir.hasNext()) {
            LinearLayout childLayout = ir.next();
            listContainer.addView(childLayout);
        }

        //완료한 목록 페이지로 이동
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CompleteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);//인텐트 효과 없애기
                Toast.makeText(getApplicationContext(), "완료한 페이지 목록으로 이동하였습니다!", Toast.LENGTH_SHORT).show();
            }
        });

        // 빠른목록 추가 입력위젯에 키보드 이벤트가 발생한 경우
        quickAddEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { // 입력하기 전에 조치

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { //입력난에 변화가 있을 시 조치
                if (quickAddEdt.getText().toString().equals("")) { //입력된 내용에 따라 이미지 변경
                    listAddBtn.setImageResource(R.drawable.add_icon);
                } else {
                    listAddBtn.setImageResource(R.drawable.check_icon);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { // 입력이 끝났을 때 조치

            }
        });

        // 빠른 추가 OR 목록 추가페이지 이동
        listAddBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O) //API 경고 무시
            @Override
            public void onClick(View view) {
                if (quickAddEdt.getText().toString().equals("")) { //quickAddText가 공백일 경우 목록 추가(수정) 페이지로 이동
                    Intent intent = new Intent(getApplicationContext(), WorkActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);//인텐트 효과 없애기

                } else { //그렇지 않을 경우 toDoTable에 입력받은 값을 추가
                    LocalDateTime now = LocalDateTime.now();
                    sqlDB = todo.getWritableDatabase();
                    String title = quickAddEdt.getText().toString();
                    String formatNow = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")); //현재날짜 설정

                    ContentValues cv = new ContentValues();
                    cv.put("TITLE", title); //These Fields should be your String values of actual column names
                    cv.put("ON_CREATE", formatNow);
                    sqlDB.insert("toDo", null, cv);
                    sqlDB.close();
                    quickAddEdt.setText("");
                    Toast.makeText(getApplicationContext(), title + "을 추가하였습니다", Toast.LENGTH_SHORT).show();

                    finish();//인텐트 종료
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class); //인텐트
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0, 0);//인텐트 효과 없애기
                    Toast.makeText(getApplicationContext(), "완료한 페이지 목록으로 이동하였습니다!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }
}

