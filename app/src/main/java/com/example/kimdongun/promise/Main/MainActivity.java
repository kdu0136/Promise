package com.example.kimdongun.promise.Main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kimdongun.promise.Account;
import com.example.kimdongun.promise.DialogHandler;
import com.example.kimdongun.promise.DialogThread;
import com.example.kimdongun.promise.LoginActivity;
import com.example.kimdongun.promise.Network.Network;
import com.example.kimdongun.promise.R;
import com.example.kimdongun.promise.Tutorial.TutorialActivity;
import com.example.kimdongun.promise.serverDB.OnFinishDBListener;
import com.example.kimdongun.promise.serverDB.PostDB;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

import static com.example.kimdongun.promise.Account.myAccount;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private TextView textView_name; //네비메뉴에서 보이는 계정 이름
    private TextView textView_mail; //네비메뉴에서 보이는 계정 메일
    private ImageView imageView_type; //네비메뉴에서 보이는 계정 유형

    private NavigationView navigationView;

    public static DialogThread mDialogThread; //알림 왔을때 뜨는 창 쓰레드
    public static DialogHandler mDialogHandler; //알림 왔을때 뜨는 창 핸들러

    public ImageView imageView_newAlram;

    private NewAlramThread thread; //N표시 갱신 쓰레드
    private NewAlramHandler handler; //N표시 갱신 핸들러

    private int current_fr; //현재 프래그먼트 id
    private Fragment targetFragment; //현재 프래그먼트
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseInstanceId.getInstance().getToken();

        //계정 초기화
        if(myAccount == null){
            myAccount = new Account(this);
            myAccount.load_Data();
        }
        myAccount.setAdapterContext(this);
        myAccount.activityNow = "MainActivity";

        //알림 왔을때 뜨는 창 쓰레드, 핸들러 초기화
        if(mDialogHandler == null) {
            mDialogHandler = new DialogHandler(this);
        }
        if(mDialogThread == null){
            mDialogThread = new DialogThread(mDialogHandler);
            mDialogThread.setDaemon(true);
            mDialogThread.start();
        }
        mDialogHandler.context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //인텐트 값 받아서 초기 프래그먼트 설정
        Intent intent = getIntent();
        current_fr = intent.getIntExtra("id", -123);
        if(current_fr != -123) { //받은 인텐트값 없을 경우
            current_fr = intent.getIntExtra("id", 0);
            if(current_fr == R.id.account){ //계정 프레그먼트
                targetFragment = new FragmentAccount();
            }else if(current_fr == R.id.promise){ //약속 프래그먼트
                targetFragment = new FragmentPromise();
                ((FragmentPromise)targetFragment).start_page = intent.getIntExtra("start_page", 0);
            }else if(current_fr == R.id.social){ //친구 프래그먼트
                targetFragment = new FragmentSocial();
                ((FragmentSocial)targetFragment).start_page = intent.getIntExtra("start_page", 0);
            }
        }else{ //받은 인텐트값 있을 경우 약속 프래그먼트로
                current_fr = R.id.promise;
                targetFragment = new FragmentPromise();
        }

        Log.d("Life Cycle", "onCreate current_fr: "+ current_fr);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(current_fr); //기본 프레그먼트

        textView_name = (TextView)navigationView.getHeaderView(0).findViewById(R.id.textView_name);
        textView_mail = (TextView)navigationView.getHeaderView(0).findViewById(R.id.textView_mail);
        imageView_type = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView_type);

        textView_name.setText(myAccount.name);
        textView_mail.setText(myAccount.getMail());

        Drawable drawable = null;
        if(myAccount.getType().equals("google")){
            drawable = getResources().getDrawable(R.drawable.ic_google);
        }else if(myAccount.getType().equals("facebook")){
            drawable = getResources().getDrawable(R.drawable.ic_facebook);
        }
        imageView_type.setImageDrawable(drawable);

        //프레그먼트 초기화
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fragment, targetFragment);
        fragmentTransaction.commit();

        imageView_newAlram = (ImageView)findViewById(R.id.imageView_newAlram);

        handler = new NewAlramHandler(this);
        thread = new NewAlramThread(handler);
        thread.start();

        switch(current_fr){
            case R.id.promise:
                if (myAccount.tuto_promise == 0) { //약속 화면 튜토리얼 아직 안봄
                    Intent intent_tuto = new Intent(MainActivity.this, TutorialActivity.class);
                    intent_tuto.putExtra("type", "tuto_promise");
                    startActivity(intent_tuto);
                    overridePendingTransition(0, 0);
                    finish();
                }
                break;
            case R.id.social:
                if (myAccount.tuto_social == 0) { //친구 화면 튜토리얼 아직 안봄
                    Intent intent_tuto = new Intent(MainActivity.this, TutorialActivity.class);
                    intent_tuto.putExtra("type", "tuto_social");
                    startActivity(intent_tuto);
                    overridePendingTransition(0, 0);
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onRestart() {
        Log.d("Life Cycle", "onRestart");
        super.onRestart();
    }

    @Override
    protected void onStart() {
        Log.d("Life Cycle", "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d("Life Cycle", "onResume");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d("Life Cycle", "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        Log.d("Life Cycle", "onDestroy");
        thread.stopForever();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() { //뒤로 버튼
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) { //네비메뉴 활성화 경우
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(current_fr != R.id.promise){ //현재 화면이 약속 화면이 아니면 약속 화면으로 전환
                navigationView.setCheckedItem(R.id.promise);
                switchFragment(R.id.promise);
            }else { //약속 화면이면 어플 종료 물어봄
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MainActivity.this);
                alert_confirm.setMessage("어플을 종료 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.super.onBackPressed();
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
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) { //네비게이션 메뉴
        // Handle navigation view item clicks here.
        switch(item.getItemId()){
            case R.id.account://내 정보 버튼
            case R.id.promise://약속 목록 버튼
            case R.id.social://친구 목록 버튼
                switchFragment(item.getItemId());
                break;

            case R.id.logout://로그아웃 버튼
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MainActivity.this);
                alert_confirm.setMessage("로그아웃 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
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
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void switchFragment(int id){
        current_fr = id;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if(id == R.id.account){
            targetFragment = new FragmentAccount();
        }else if(id == R.id.promise){
            if (myAccount.tuto_promise == 0) { //약속 화면 튜토리얼 아직 안봄
                Intent intent_tuto = new Intent(MainActivity.this, TutorialActivity.class);
                intent_tuto.putExtra("type", "tuto_promise");
                startActivity(intent_tuto);
                overridePendingTransition(0, 0);
                finish();
                return;
            }
            targetFragment = new FragmentPromise();
        }else if(id == R.id.social) {
            if (myAccount.tuto_social == 0) { //친구 화면 튜토리얼 아직 안봄
                Intent intent_tuto = new Intent(MainActivity.this, TutorialActivity.class);
                intent_tuto.putExtra("type", "tuto_social");
                startActivity(intent_tuto);
                overridePendingTransition(0, 0);
                finish();
                return;
            }
            targetFragment = new FragmentSocial();
        }
        fragmentTransaction.replace(R.id.fragment, targetFragment);
        fragmentTransaction.commit();
    }

    void logout(){
        String netState = Network.getWhatKindOfNetwork(this);
        if(netState.equals(Network.NONE_STATE)){
            Network.connectNetwork(this);
        }else {
            HashMap<String, String> postData = new HashMap<String, String>();
            postData.put("mail", myAccount.getMail());
            postData.put("type", myAccount.getType());

            PostDB postDB = new PostDB(this);
            postDB.putData("logout.php", postData, new OnFinishDBListener() {
                @Override
                public void onSuccess(String output) {
                    myAccount.logoutAccount();
                    SharedPreferences pref = getSharedPreferences("setting", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("push", true);
                    editor.commit();
                    Intent l_intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(l_intent);
                    finish();
                }
            });
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class NewAlramThread extends Thread {
        public Handler handler;
        boolean isRun = true;

        public NewAlramThread(Handler handler){
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
                handler.sendEmptyMessage(0);
                try {
                    Thread.sleep(500);
                }catch (Exception e){}
            }
        }
    }
    class NewAlramHandler extends Handler {
        Context context;
        public NewAlramHandler(Context context){
            this.context = context;
        }
        @Override
        public void handleMessage(Message msg) {
            int total = myAccount.listViewAcceptAdapter.getCount() + myAccount.accept_adapter.getCount();
            if(total > 0) { //새로운 요청 있을 경우
                imageView_newAlram.setVisibility(View.VISIBLE);
                /*Menu menu = navigationView.getMenu();
                if(myAccount.listViewAcceptAdapter.getCount() > 0){ //약속 요청 있을 경우
                    Drawable icon = getResources().getDrawable(R.drawable.ic_promise_new);
                    menu.getItem(1).setIcon(icon);
                }else{ //없을 경우
                    Drawable icon = getResources().getDrawable(R.drawable.ic_promise);
                    menu.getItem(1).setIcon(icon);
                }
                if(myAccount.accept_adapter.getCount() > 0){ //친구 요청 있을 경우
                    Drawable icon = getResources().getDrawable(R.drawable.ic_social_new);
                    menu.getItem(2).setIcon(icon);
                }else{ //없을 경우
                    Drawable icon = getResources().getDrawable(R.drawable.ic_social);
                    menu.getItem(2).setIcon(icon);
                }*/
            }
            else {
                imageView_newAlram.setVisibility(View.INVISIBLE);
            }
        }
    }
}
