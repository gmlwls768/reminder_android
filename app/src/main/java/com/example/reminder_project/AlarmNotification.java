package com.example.reminder_project;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class AlarmNotification extends BroadcastReceiver {
    ToDoTable todo; //A helper class to manage database creation and version management.
    SQLiteDatabase sqlDB;
    Cursor cursor;

    @Override
    public void onReceive(Context context, Intent intent) {
        String alarmTitle = intent.getStringExtra("alarmTitle");
        String alarmSummary = intent.getStringExtra("alarmSummary");
        String s_dbRowId = intent.getStringExtra("dbRowId");
        System.out.println("reminder dbRowId: " + s_dbRowId);

        //사용자에게 일어나는 이벤트를 알리는 클래스. 이것이 사용자에게 백그라운드에서 무슨 일이 일어났다고 알려주는 방법입니다.
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        //특정시점에 notificationIntent(MainActivity)을 실행함
        PendingIntent pendingI = PendingIntent.getActivity(context, 1, notificationIntent, FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");


        //OREO API 26 이상에서는 채널 필요
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남

            String channelName = "알림 채널";
            String description = "특정 시간에 알림합니다.";
            int importance = NotificationManager.IMPORTANCE_HIGH; //소리와 알림메시지를 같이 보여줌

            NotificationChannel channel = new NotificationChannel("default", channelName, importance);
            channel.setDescription(description);

            if (notificationManager != null) {
                // 노티피케이션 채널을 시스템에 등록
                notificationManager.createNotificationChannel(channel);
            }
        } else
            builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        builder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("{Time to watch some cool stuff!}")
                .setContentTitle(alarmTitle)
                .setContentText(alarmSummary)
                .setContentInfo("INFO")
                .setContentIntent(pendingI);// 알림 클릭 시 설정한 Activity로 이동

        if (notificationManager != null) {
            todo = new ToDoTable(context);
            sqlDB = todo.getReadableDatabase(); //Create and/or open a database
            cursor = sqlDB.rawQuery("SELECT * FROM toDo WHERE ON_CREATE='" + s_dbRowId + "';", null); //id에 해당하는 컬럼의 값을 가져옴
            cursor.moveToFirst(); //커서를 첫번째로 이동
            System.out.println("reminder " + cursor.getString(ToDoTable.FIELD_NAME_ALERT));
            LocalDateTime now = LocalDateTime.now();
            String formatNow = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm")); //현재날짜 설정

            //알림 취소되었거나 알림이 수정되었다면
            if (cursor.getString(ToDoTable.FIELD_NAME_ALERT).equals("0000/00/00/00/00") || !cursor.getString(ToDoTable.FIELD_NAME_ALERT).equals(formatNow)) {
                System.out.println("reminder notification 작동안함");
            } else {
                // 노티피케이션 동작시킴
                // System.currentTimeMills()를 이용, 현재 시간을 받아와 대입하여 그때그때 id값을 다르게 지정
                System.out.println("reminder notification 작동");
                notificationManager.notify(1, builder.build());
            }
        }
    }
}
