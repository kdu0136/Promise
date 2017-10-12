package com.example.kimdongun.promise.Location;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by KimDongun on 2017-01-15.
 */

public class LocationClass {
    public static final String DAUM_MAPS_TRANS_COORD_SEARCH_API_FORMAT = "https://apis.daum.net/local/geo/transcoord?apikey=%s&fromCoord=BESSEL&y=%f&x=%f&toCoord=WGS84&output=json";
    private static final String HEADER_NAME_X_APPID = "x-appid";
    private static final String HEADER_NAME_X_PLATFORM = "x-platform";
    private static final String HEADER_VALUE_X_PLATFORM_ANDROID = "android";

    private Context mContext;
    OnFinishTransCoordListener onFinishTransCoordListener;
    TransCoordTask transCoordTask;
    String appId;

    public static CoordiPoint getCurrentLocation(Context mContext){
        LocationManager locationManager;
        String context = Context.LOCATION_SERVICE;
        locationManager = (LocationManager)mContext.getSystemService(context);

        Criteria criteria = new Criteria();
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
        Log.d("mContext", "lat: "+latitude+"lon: "+longitude);

        return new CoordiPoint(latitude,longitude);
    }

    public static String findAddress(Context mContext, double lat, double lon){
        String currentLocationAddress = "";
        Geocoder geocoder = new Geocoder(mContext, Locale.KOREA);
        List<Address> address;
        try{
            if(geocoder != null){
                //세번째 인수는 최대 결과값인데 1개만 받으니 1로
                address = geocoder.getFromLocation(lat, lon, 1);
                //설정한 데이터로 리턴된 주소가 있으면
                if(address != null && address.size() > 0){
                    //주소
                    currentLocationAddress = address.get(0).getAddressLine(0).toString();
                    Log.d("location", "Address: "+currentLocationAddress);
                }
            }
        }catch (IOException e){
            Toast.makeText(mContext, "주소취득 실패" , Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return currentLocationAddress;
    }

    private class TransCoordTask extends AsyncTask<String, CoordiPoint, Void> {
        @Override
        protected Void doInBackground(String... urls) {
            String url = urls[0];
            Map<String, String> header = new HashMap<String, String>();
            header.put(HEADER_NAME_X_APPID, appId);
            header.put(HEADER_NAME_X_PLATFORM, HEADER_VALUE_X_PLATFORM_ANDROID);
            String json = fetchData(url, header);
            CoordiPoint itemList = parse(json);
            publishProgress(itemList);
            return null;
        }

        @Override
        protected void onProgressUpdate(CoordiPoint... values) {
            if (onFinishTransCoordListener != null) {
                if (values == null) {
                    onFinishTransCoordListener.onFail();
                } else {
                    onFinishTransCoordListener.onSuccess(values[0]);
                }
                super.onProgressUpdate(values);
            }
        }
    }

    public void transCoord(Context applicationContext, double x, double y, String apikey, OnFinishTransCoordListener onFinishTransCoordListener) {
        this.onFinishTransCoordListener = onFinishTransCoordListener;
        this.mContext = applicationContext;

        if (transCoordTask != null) {
            transCoordTask.cancel(true);
            transCoordTask = null;
        }

        if (applicationContext != null) {
            appId = applicationContext.getPackageName();
        }
        String url = transCoordApiUrlString(x, y, apikey);
        transCoordTask = new TransCoordTask();
        transCoordTask.execute(url);
    }

    private String transCoordApiUrlString(double x, double y, String apikey) {
        //return String.format(DAUM_MAPS_LOCAL_KEYWORD_SEARCH_API_FORMAT, encodedQuery, latitude, longitude, radius, page, apikey);
        return String.format(DAUM_MAPS_TRANS_COORD_SEARCH_API_FORMAT, apikey, y, x);
    }

    private String fetchData(String urlString, Map<String, String> header) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(4000 /* milliseconds */);
            conn.setConnectTimeout(7000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            if (header != null) {
                for (String key : header.keySet()) {
                    conn.addRequestProperty(key, header.get(key));
                }
            }
            conn.connect();
            InputStream is = conn.getInputStream();
            @SuppressWarnings("resource")
            Scanner s = new Scanner(is);
            s.useDelimiter("\\A");
            String data = s.hasNext() ? s.next() : "";
            is.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private CoordiPoint parse(String jsonString) {
        double x = 0;
        double y = 0;
        try {
            JSONObject reader = new JSONObject(jsonString);
            x = reader.getDouble("x");
            y = reader.getDouble("y");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return new CoordiPoint(x, y);
    }

    //GPS 설정 체크
    public static boolean chkGpsService(final Context context) {
        String gps = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        Log.d(gps, "aaaa");
        if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {
            // GPS OFF 일때 Dialog 표시
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(context);
            gsDialog.setTitle("위치 서비스 설정");
            gsDialog.setMessage("네트워크 사용, GPS 위성 사용을 모두 체크하셔야 정확한 위치 서비스가 가능합니다.\n위치 서비스 기능을 설정 하시겠습니까?");
            gsDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // GPS설정 화면으로 이동
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    context.startActivity(intent);
                }
            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            }).create().show();
            return false;

        } else {
            return true;
        }
    }

}
