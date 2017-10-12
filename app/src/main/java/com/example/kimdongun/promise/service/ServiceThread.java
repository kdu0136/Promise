package com.example.kimdongun.promise.service;

import android.os.Handler;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by KimDongun on 2017-01-24.
 */

public class ServiceThread extends Thread {
    Handler handler;
    boolean isRun = true;

    public ServiceThread(Handler handler){
        this.handler = handler;
    }

    public void stopForever(){
        synchronized (this){
            this.isRun = false;
        }
    }

    public void run(){
        //반복으로 수행할 작업
        while (isRun){
            handler.sendEmptyMessage(0); //핸들러에게 메세지 보냄
            try {
                Thread.sleep(10000);
            }catch (Exception e){}
        }
    }
}
