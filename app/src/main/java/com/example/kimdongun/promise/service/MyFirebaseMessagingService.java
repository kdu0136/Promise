package com.example.kimdongun.promise.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.kimdongun.promise.ForegroundCheckTask;
import com.example.kimdongun.promise.LoginActivity;
import com.example.kimdongun.promise.Main.MainActivity;
import com.example.kimdongun.promise.Promise.AcceptPromiseActivity;
import com.example.kimdongun.promise.Promise.ContentActivity;
import com.example.kimdongun.promise.PushWakeLock;
import com.example.kimdongun.promise.R;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import static com.example.kimdongun.promise.R.mipmap.main_icon;

/**
 * Created by KimDongun on 2017-01-23.
 */

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";


    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        boolean foregroud = false;
        try {
            foregroud = new ForegroundCheckTask().execute(getApplicationContext()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(!foregroud) {
            //추가한것
            Log.d("foreground", "새 메세지 (어플 꺼짐)");
            SharedPreferences pref = getSharedPreferences("setting", Context.MODE_PRIVATE);
            if(pref.getBoolean("push", true)) { //푸시 허용 상태
                if(remoteMessage.getData().get("pro_no") != null) {
                    sendNotification(remoteMessage.getData().get("message"), remoteMessage.getData().get("type"), remoteMessage.getData().get("pro_no"));
                }else{
                    sendNotification(remoteMessage.getData().get("message"), remoteMessage.getData().get("type"), "");
                }
            }else{ //푸시 거부 상태

            }
        }else{
            Log.d("foreground", "새 메세지 (어플 켜짐)");
            MainActivity.mDialogThread.getMSG = true;
            MainActivity.mDialogThread.msg = remoteMessage.getData().get("message");
            MainActivity.mDialogThread.type = remoteMessage.getData().get("type");
            if(remoteMessage.getData().get("pro_no") != null)
                MainActivity.mDialogThread.pro_no = Integer.valueOf(remoteMessage.getData().get("pro_no"));
        }
    }

    private void sendNotification(String messageBody, String str_type, String pro_num) {
        PushWakeLock.requestCpuWakeLock(getApplicationContext());
        String subContent = messageBody;
        Intent intent = new Intent();
        if(str_type.equals("friend_accept")) { //친구 수락 알림
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("id", R.id.social);
            intent.putExtra("start_page", 0);
        }else if(str_type.equals("friend_request")) { //친구 요청 알림
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("id", R.id.social);
            intent.putExtra("start_page", 2);
        }else if(str_type.equals("friend_refuse")){ //친구 거절 알림
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("id", R.id.social);
            intent.putExtra("start_page", 1);
        }else if(str_type.equals("promise_request")){ //약속 요청 알림
            intent = new Intent(this, AcceptPromiseActivity.class);
            intent.putExtra("index", Integer.valueOf(pro_num));
        }else if(str_type.equals("promise")){ //약속 거절, 수락, 변경 알림
            intent = new Intent(this, ContentActivity.class);
            intent.putExtra("index", Integer.valueOf(pro_num));
        }else if(str_type.equals("promise_delete")){ //약속 제외,파기 알림
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("id", R.id.promise);
            intent.putExtra("start_page", 0);
        }else if(str_type.equals("other_client")) { //다른 기기에서 접속
            SharedPreferences pref = getSharedPreferences("login", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("name", "");
            editor.putString("mail", "");
            editor.putInt("index", -1);
            editor.putString("type", "logout");
            editor.putInt("search", 1);
            editor.putInt("location", 1);
            editor.commit();

            SharedPreferences pref2 = getSharedPreferences("setting", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor2 = pref2.edit();
            editor2.putBoolean("push", true);
            editor2.commit();

            intent = new Intent(this, LoginActivity.class);
        }else if(str_type.equals("promise_noti")){ //약속 알림
            intent = new Intent(this, ContentActivity.class);
            intent.putExtra("index", Integer.valueOf(pro_num));
            subContent = "약속 시간이 다 되어 갑니다!!";
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        /*Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_alarm_on)
                .setContentTitle("새 메세지")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);*/

        NotificationCompat.Builder mBuilder = createNotification(subContent);

        if(str_type.equals("promise_noti")){ //약속 알림
            NotificationCompat.InboxStyle inboxStyle =
                    new NotificationCompat.InboxStyle();
            ArrayList<String> arrayList = new ArrayList<>();
            StringTokenizer st = new StringTokenizer(messageBody,"\n");
            while(st.hasMoreTokens()){
                arrayList.add(st.nextToken());
            }
            inboxStyle.setBigContentTitle("새 메시지");
            inboxStyle.setSummaryText("메세지를 자세히 볼 수 있습니다");
            NotificationCompat.Action action = new NotificationCompat.Action(R.mipmap.ic_launcher, "button1", createPendingIntent());
            for (int i = 0; i < arrayList.size(); i++) {
                inboxStyle.addLine(arrayList.get(i));
            }

            //스타일 추가
            mBuilder.setStyle(inboxStyle);
        }

        mBuilder.setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, mBuilder.build());//notificationBuilder.build());
    }

    /**
     * 노티피케이션을 누르면 실행되는 기능을 가져오는 노티피케이션
     *
     * 실제 기능을 추가하는 것
     * @return
     */
    private PendingIntent createPendingIntent(){
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        return stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    /**
     * 노티피케이션 빌드
     * @return
     */
    private NotificationCompat.Builder createNotification(String subContent){
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                //.setSmallIcon(R.drawable.ic_alarm_on)
                //.setLargeIcon(icon)
                .setContentTitle("새 메세지")
                .setContentText(subContent)
                .setSmallIcon(R.mipmap.main_icon/*스와이프 전 아이콘*/)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        return builder;
    }
}

