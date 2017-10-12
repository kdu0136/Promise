package com.example.kimdongun.promise.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.kimdongun.promise.Main.MainActivity;
import com.example.kimdongun.promise.R;

public class MyService extends Service {

    NotificationManager notificationManager;
    Notification notification;
    ServiceThread serviceThread;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.d("MyService", "onCreate");
        super.onCreate();
    }

    //백그라운드에서 실행되는 동작들
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MyService", "onStartCommand");
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();
        serviceThread = new ServiceThread(handler);
        serviceThread.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("MyService", "onDestroy");
        serviceThread.stopForever();
        serviceThread = null; //쓰레기 값을 만들어서 빠르게 회수하라고  null을 넣어줌.
    }

    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent(MyService.this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle("Content Title")
                    .setContentText("Content Text")
                    .setSmallIcon(R.drawable.ic_alarm_on)
                    .setTicker("알림!!")
                    .setContentIntent(pendingIntent)
                    .build();

            //소리 추가
            notification.defaults = Notification.DEFAULT_SOUND;

            //알림 소리를 한번만 내도록
            notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;

            //확인하면 자동으로 알림이 제거 되도록
            notification.flags = Notification.FLAG_AUTO_CANCEL;

            notificationManager.notify(777, notification);

            //토스트 띄우기
            Toast.makeText(MyService.this, "뜸?", Toast.LENGTH_LONG).show();
        }
    }
}
