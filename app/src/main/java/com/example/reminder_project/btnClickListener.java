package com.example.reminder_project;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class btnClickListener implements View.OnClickListener {
    private Context context; // Activity Context(?)
    private int hour;
    private int minutes;

    public btnClickListener(Context context, int hour, int minutes) {
        this.context = context;
        this.hour = hour;
        this.minutes = minutes;

    }

    @Override
    public void onClick(View v) {
        Calendar calendar = setTime(); //설정된 시간을 가져옴
        Intent alarmIntent = new Intent(context, AlarmNotification.class);
        // PendingIntent 는, 가지고 있는 Intent 를 당장 수행하진 않고 특정 시점에 수행하도록 하는 특징을 갖고 있다
        // 특정시점에 alarmIntent(AlarmNotification)가 실행 됨
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, FLAG_IMMUTABLE);
        // 특정시점 설정
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent); //지정된 시간에 휴대폰 화면을 깨우고 매일 반복하여 알림을 보여줌

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Android Marshmallow 이상에서만 실행되는 코드
                // 사용자가 전원을 충전하지 않고 화면이 꺼진 채로 기기를 일정 기간 정지 상태로 두면 기기는 Doze 모드를 시작하게 됩니다.
                // Doze 모드에서도 실행되는 알람을 설정해야 하는 경우 setAndAllowWhileIdle() 또는 setExactAndAllowWhileIdle()를 사용합니다.
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }

    public Calendar setTime() { //날짜 설정 및 Toast 메시지 출력 후 Calender 객체 반환
        int hour_24 = hour;
        int minute = minutes;

        // 현재 지정된 시간으로 알람 시간 설정
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour_24);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        //설정한 날짜를 Toast메시지로 보여줌
        Date currentDateTime = calendar.getTime();
        String date = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
        Toast.makeText(context, date + "으로 알림이 설정되었습니다!", Toast.LENGTH_SHORT).show();

        return calendar;
    }

}
