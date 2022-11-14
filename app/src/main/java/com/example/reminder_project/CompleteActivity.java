package com.example.reminder_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CompleteActivity extends AppCompatActivity {
    Button todoBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);
        todoBtn = findViewById(R.id.todoBtn);
        todoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Toast.makeText(getApplicationContext(), "할일 목록으로 이동하였습니다!",Toast.LENGTH_SHORT).show();
            }

        });
    }
}