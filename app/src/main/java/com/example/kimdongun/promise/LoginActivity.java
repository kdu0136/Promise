package com.example.kimdongun.promise;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kimdongun.promise.Main.MainActivity;
import com.example.kimdongun.promise.Network.Network;
import com.example.kimdongun.promise.serverDB.OnFinishDBListener;
import com.example.kimdongun.promise.serverDB.PostDB;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import static com.example.kimdongun.promise.Account.myAccount;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;

    private Button button_login_google; //구글 로그인 버튼
    private Button button_login_facebook; //페이스북 로그인 버튼

    private CallbackManager callbackManager;

    private int facebook_count;

    //구글 버튼 텍스트 변경
    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                Log.d("Permission", "onRequestPermissionsResult");
               /* if (grantResults.length > 0
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 허가
                    // 해당 권한을 사용해서 작업을 진행할 수 있습니다
                } else {
                    // 권한 거부
                    // 사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
                    alert_confirm.setMessage("위치 정보를 허용하지 않으면\n실시간 위치 공유 서비스를 사용하실 수 없습니다").setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog alert = alert_confirm.create();
                    alert.show();
                }
                return;*/
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //초기화
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        Permission.permissionAll(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        facebook_count = 0;

        String netState = Network.getWhatKindOfNetwork(this);
        if(netState.equals(Network.NONE_STATE)){
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
            alert_confirm.setMessage("네트워크 연결을 확인하세요").setCancelable(false).setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            AlertDialog alert = alert_confirm.create();
            alert.show();
        }else {
            SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
            if (pref.getString("type", "logout").equals("logout")) { //로그인 기록 없음
                setContentView(R.layout.activity_login);

                //초기화
                button_login_google = (Button)findViewById(R.id.button_login_google); //구글 로그인 버튼
                button_login_facebook = (Button)findViewById(R.id.button_login_facebook); //페이스북 로그인 버튼

                //터치 이벤트
                button_login_google.setOnClickListener(this);
                button_login_facebook.setOnClickListener(this);

                if (mGoogleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                }
                LoginManager.getInstance().logOut();

            } else { //로그인 기록 있음
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() { //뒤로 버튼
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(LoginActivity.this);
        alert_confirm.setMessage("어플을 종료 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginActivity.super.onBackPressed();
                    }
                }).setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'No'
                        return;
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.GET_ACCOUNTS)) {
                // Explain to the user why we need to write the permission.
            }

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.GET_ACCOUNTS}, 1);
            return;
        }
        String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);

        if (currentPerson != null) {
            successLogin(accountName, currentPerson.getDisplayName(), "google");
        } else {
            Log.w("Main", "error");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //구글 로그인 재접속
        if (requestCode == RC_SIGN_IN) {
            //Toast.makeText(getApplicationContext(),"재접속",Toast.LENGTH_LONG).show();
            mGoogleApiClient.connect();
        }else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),"로그인 실패",Toast.LENGTH_LONG).show();
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RC_SIGN_IN);

            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_login_google: //구글 로그인 터치
                mGoogleApiClient.connect();
                break;

            case R.id.button_login_facebook: //페이스북 로그인 터치
                Log.i("TAG", "facebook touch");
                loginFacebook();
                break;
        }
    }

    void loginFacebook(){
        Log.i("TAG", "login function");
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(final LoginResult result) {
                GraphRequest request = GraphRequest.newMeRequest(result.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse response) {
                        if (response.getError() != null) {

                        } else {
                            String user_mail = null;
                            String user_name = null;
                            try {
                                user_mail = user.getString("email");
                                user_name = user.getString("name");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.i("TAG", "user: " + user.toString());
                            Log.i("TAG", "AccessToken: " + result.getAccessToken().getToken());
                            setResult(RESULT_OK);
                            facebook_count++;
                            if(facebook_count == 1)
                                successLogin(user_mail, user_name, "facebook");
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("test", "Error: " + error);
                //finish();
            }

            @Override
            public void onCancel() {
                //finish();
            }
        });
    }

    void successLogin(final String user_mail, final String user_name, final String user_type){
        //Toast.makeText(this, "mail: "+mail+ " name: "+name+ " type: "+type, Toast.LENGTH_SHORT).show();
        Log.i("TAG", "mail: "+user_mail+ " name: "+user_name+ " type: "+user_type);

        HashMap<String, String> postData = new HashMap<String, String>();
        postData.put("mail", user_mail);
        postData.put("name", user_name);
        postData.put("type", user_type);
        String token = FirebaseInstanceId.getInstance().getToken();
        postData.put("token", token);

        PostDB postDB = new PostDB(this);
        postDB.putData("login.php", postData, new OnFinishDBListener() {
            @Override
            public void onSuccess(String output) {
                Log.d("login", output);
                success(output, user_mail, user_name, user_type);
            }
        });

    }

    public void success(String output, final String user_mail, final String user_name, final String user_type){
        if(output.equals("other")){ //다른 기기에서 접속중
            AlertDialog.Builder alert_confirm0 = new AlertDialog.Builder(this);
            alert_confirm0.setMessage("이미 다른 기기에서 접속중입니다.\n다른 기기의 접속을 해제하고 로그인 하시곘습니까?").setCancelable(false).setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            otherLogin(user_mail, user_name, user_type);
                        }
                    }).setNegativeButton("취소",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 'No'
                            if (user_type.equals("google")) { //구글 계정 로그아웃
                                if (mGoogleApiClient.isConnected()) {
                                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                                    Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
                                    mGoogleApiClient.disconnect();
                                }
                            }else if(user_type.equals("facebook")) { //페이스북 계정 로그아웃
                                LoginManager.getInstance().logOut();
                            }
                            Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                            finish();
                            return;
                        }
                    });
            AlertDialog alert0 = alert_confirm0.create();
            alert0.show();
        }else {
            try {
                    Log.d("login", output);
                    JSONObject reader = new JSONObject(output);
                    JSONArray objects = reader.getJSONArray("account");
                    //계정 초기화
                    for (int i = 0; i < objects.length(); i++) {
                        JSONObject object = objects.getJSONObject(i);
                        int index = Integer.valueOf(object.getString("no"));
                        String mail = object.getString("mail");
                        String name = object.getString("name");
                        String type = object.getString("type");
                        int search_on = Integer.valueOf(object.getString("search")); //1 이면 검색 허용
                        int location_on = Integer.valueOf(object.getString("location")); //1 이면 실시간 위치 공유 허용

                        int tuto_promise = Integer.valueOf(object.getString("tuto_promise")); //약속 목록 튜토리얼 확인 유무
                        int tuto_social = Integer.valueOf(object.getString("tuto_social")); //친구 목록 튜토리얼 확인 유무
                        int tuto_add_social = Integer.valueOf(object.getString("tuto_add_social")); //친구 추가 튜토리얼 확인 유무
                        int tuto_add_promise = Integer.valueOf(object.getString("tuto_add_promise")); //약속 추가 튜토리얼 확인 유무
                        int tuto_content_promise = Integer.valueOf(object.getString("tuto_content_promise")); //약속 확인 튜토리얼 확인 유무
                        myAccount = new Account(this);
                        myAccount.loginAccount(name, mail, index, type, search_on, location_on,
                                tuto_promise, tuto_social, tuto_add_social, tuto_add_promise, tuto_content_promise);
                        myAccount.load_Data();
                        Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            }
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void otherLogin(final String user_mail, final String user_name, final String user_type){
        Log.d("otherLogin", "user_mail: "+user_mail);
        Log.d("otherLogin", "user_name: "+user_name);
        Log.d("otherLogin", "user_type: "+user_type);
        HashMap<String, String> postData = new HashMap<String, String>();
        postData.put("mail", user_mail);
        postData.put("type", user_type);
        String token = FirebaseInstanceId.getInstance().getToken();
        postData.put("token", token);

        PostDB postDB = new PostDB(this);
        postDB.putData("login_other.php", postData, new OnFinishDBListener() {
            @Override
            public void onSuccess(String output) {
                Log.d("otherLogin", "output: "+output);
                success(output, user_mail, user_name, user_type);
            }
        });
    }
}

