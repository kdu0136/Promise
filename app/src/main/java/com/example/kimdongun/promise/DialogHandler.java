package com.example.kimdongun.promise;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.example.kimdongun.promise.Main.MainActivity;
import com.example.kimdongun.promise.Promise.AcceptPromiseActivity;
import com.example.kimdongun.promise.Promise.ContentActivity;

import static com.example.kimdongun.promise.Account.myAccount;

/**
 * Created by KimDongun on 2017-02-18.
 */

public class DialogHandler extends Handler {
    public Context context;
    public DialogHandler(Context context){
        this.context = context;
    }
    @Override
    public void handleMessage(Message msg) {
        String str_msg = msg.getData().getString("msg");
        final String str_type = msg.getData().getString("type");
        final int pro_no = msg.getData().getInt("pro_no");

        if(str_type.equals("other_client")){//다른 기기에서 접속
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(context);
            alert_confirm.setMessage(str_msg).setCancelable(false).setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            myAccount.logoutAccount();
                            SharedPreferences pref2 = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = pref2.edit();
                            editor2.putBoolean("push", true);
                            editor2.commit();

                            Intent intent = new Intent(context, LoginActivity.class);
                            context.startActivity(intent);
                            ((AppCompatActivity) context).finish();

                        }
                    });
            AlertDialog alert = alert_confirm.create();
            alert.show();

        }else {
            myAccount.load_request();
            myAccount.load_friend();
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(context);
            alert_confirm.setMessage(str_msg).setCancelable(false).setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            if (str_type.equals("promise") || str_type.equals("promise_noti")) { //약속 거절, 수락 알림
                                myAccount.load_Data();
                                if (!myAccount.activityNow.equals("ContentActivity")) {
                                    intent = new Intent(context, ContentActivity.class);
                                    intent.putExtra("index", pro_no);
                                    context.startActivity(intent);
                                    ((AppCompatActivity) context).overridePendingTransition(0, 0);
                                    ((AppCompatActivity) context).finish();
                                }
                            }else {
                                if (str_type.equals("friend_accept")) { //친구 수락 알림
                                    intent = new Intent(context, MainActivity.class);
                                    intent.putExtra("id", R.id.social);
                                    intent.putExtra("start_page", 0);
                                } else if (str_type.equals("friend_request")) { //친구 요청 알림
                                    intent = new Intent(context, MainActivity.class);
                                    intent.putExtra("id", R.id.social);
                                    intent.putExtra("start_page", 2);
                                } else if (str_type.equals("friend_refuse")) { //친구 거절 알림
                                    intent = new Intent(context, MainActivity.class);
                                    intent.putExtra("id", R.id.social);
                                    intent.putExtra("start_page", 1);
                                } else if (str_type.equals("promise_request")) { //약속 요청 알림
                                    intent = new Intent(context, AcceptPromiseActivity.class);
                                    intent.putExtra("index", pro_no);
                                }else if (str_type.equals("promise_delete")) { //약속 제외,파기 알림
                                    myAccount.load_Data();
                                    intent = new Intent(context, MainActivity.class);
                                    intent.putExtra("id", R.id.promise);
                                    intent.putExtra("start_page", 0);
                                }
                                context.startActivity(intent);
                                ((AppCompatActivity) context).overridePendingTransition(0, 0);
                                ((AppCompatActivity) context).finish();
                            }

                        }
                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(str_type.equals("promise_delete")){ //약속 제외,파기 알림
                        myAccount.load_Data();
                    }
                    //setNewAlramVisivle();
                    return;
                }
            });
            AlertDialog alert = alert_confirm.create();
            alert.show();
        }
    }
}
