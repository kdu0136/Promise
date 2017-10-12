package com.example.kimdongun.promise;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 * Created by KimDongun on 2017-02-22.
 */

public class Permission {
    public static void permissionAll(final Activity activity){
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.GET_ACCOUNTS)) {
                // Explain to the user why we need to write the permission.
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Explain to the user why we need to write the permission.
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(activity);
                alert_confirm.setMessage("위치정보 접근을 허용하지 않으면\n약속에 관한 모든 사용에 제한이 있습니다").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.GET_ACCOUNTS, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
            }
            return;
        }
    }
    public static void permissionAccount(Activity activity){
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.GET_ACCOUNTS)) {
                // Explain to the user why we need to write the permission.
                Toast.makeText(activity, "GET_ACCOUNTS permission", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.GET_ACCOUNTS}, 1);
            return;
        }
    }
    public static void permissionLocation(final Activity activity){
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Explain to the user why we need to write the permission.
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(activity);
                alert_confirm.setMessage("위치정보 접근을 허용하지 않으면\n약속에 관한 모든 사용에 제한이 있습니다").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
            }
        }
        return;
    }
}
