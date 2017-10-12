package com.example.kimdongun.promise;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by KimDongun on 2017-02-24.
 */

public class PushWakeLock {
    private static PowerManager.WakeLock wakeLock;

    public static void requestCpuWakeLock(Context context){
        if(wakeLock != null){
            return;
        }
        PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                                            PowerManager.ACQUIRE_CAUSES_WAKEUP |
                                            PowerManager.ON_AFTER_RELEASE, "hello");
        wakeLock.acquire();

        if(wakeLock != null){
            wakeLock.release();
            wakeLock = null;
        }
    }
}
