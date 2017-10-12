package com.example.kimdongun.promise;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by KimDongun on 2017-01-31.
 */

public class DialogThread extends Thread {
    public Handler handler;
    boolean isRun = true;

    public  boolean getMSG = false;
    public String type = "";
    public String msg = "";
    public int pro_no = 0;

    public DialogThread(Handler handler){
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
            if(getMSG) {
                Bundle data = new Bundle();
                data.putString("type", type);
                data.putString("msg", msg);
                data.putInt("pro_no", pro_no);
                Message message = handler.obtainMessage();
                message.setData(data);
                handler.sendMessage(message); //핸들러에게 메세지 보냄
                getMSG = false;
            }
            try {
                Thread.sleep(500);
            }catch (Exception e){}
        }
    }
}