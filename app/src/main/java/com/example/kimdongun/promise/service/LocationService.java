package com.example.kimdongun.promise.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.kimdongun.promise.serverDB.OnFinishDBListener;
import com.example.kimdongun.promise.serverDB.PostDB;

import java.util.HashMap;

public class LocationService extends Service {

    LocationServiceThread serviceThread;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.d("LocationService", "onCreate");
        super.onCreate();
    }

    //백그라운드에서 실행되는 동작들
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("LocationService", "onStartCommand");
        myServiceHandler handler = new myServiceHandler();
        serviceThread = new LocationServiceThread(handler);
        serviceThread.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("LocationService", "onDestroy");
        serviceThread.stopForever();
        serviceThread = null; //쓰레기 값을 만들어서 빠르게 회수하라고  null을 넣어줌.
    }

    class LocationServiceThread extends Thread {
        Handler handler;
        boolean isRun = true;

        public LocationServiceThread(Handler handler) {
            this.handler = handler;
        }

        public void stopForever() {
            synchronized (this) {
                this.isRun = false;
            }
        }

        public void run() {
            //반복으로 수행할 작업
            while (isRun) {
                SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
                if (!pref.getString("type", "logout").equals("logout") && pref.getInt("location", 1) == 1) { //계정 로그아웃 상태 체크, 실시간 위치 공유 허용상태
                    //권한 체크
                    int permissionCheck = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION);
                    if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                        // 권한 없음
                    } else {
                        // 권한 있음
                        handler.sendEmptyMessage(0); //핸들러에게 메세지 보냄
                    }
                }
                try {
                    Thread.sleep(1000); //1분마다
                } catch (Exception e) {
                }
            }
        }
    }

    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d("LocationService", "위치 갱신");
            LocationManager locationManager;
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    2000,
                    10, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
                            int index = pref.getInt("index", 0);

                            HashMap<String, String> postData = new HashMap<>();
                            postData.put("no", String.valueOf(index));
                            postData.put("lat", String.valueOf(location.getLatitude()));
                            postData.put("lon", String.valueOf(location.getLongitude()));

                            PostDB postDB = new PostDB();
                            postDB.putData("update_account_location.php", postData, new OnFinishDBListener() {
                                @Override
                                public void onSuccess(String output) {

                                }
                            });
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    });
            /*Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);// 정확도
            criteria.setPowerRequirement(Criteria.POWER_LOW); // 전원 소비량
            criteria.setAltitudeRequired(false); // 고도 사용여부
            criteria.setBearingRequired(false); //
            criteria.setSpeedRequired(false); // 속도
            criteria.setCostAllowed(true); // 금전적비용

            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);
            double latitude = location.getLatitude(); // 위도
            double longitude = location.getLongitude(); // 경도

            SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
            int index = pref.getInt("index", 0);

            HashMap<String, String> postData = new HashMap<>();
            postData.put("no", String.valueOf(index));
            postData.put("lat", String.valueOf(latitude));
            postData.put("lon", String.valueOf(longitude));

            PostDB postDB = new PostDB();
            postDB.putData("update_account_location.php", postData, new OnFinishDBListener() {
                @Override
                public void onSuccess(String output) {

                }
            });*/
        }
    }
}
