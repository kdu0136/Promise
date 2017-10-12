package com.example.kimdongun.promise.Promise;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.kimdongun.promise.Account;
import com.example.kimdongun.promise.AddPromiseSocialListViewItem;
import com.example.kimdongun.promise.Dday.DdayClass;
import com.example.kimdongun.promise.DialogHandler;
import com.example.kimdongun.promise.DialogThread;
import com.example.kimdongun.promise.ListViewItem;
import com.example.kimdongun.promise.Main.MainActivity;
import com.example.kimdongun.promise.Network.Network;
import com.example.kimdongun.promise.R;
import com.example.kimdongun.promise.search.ItemPlace;
import com.example.kimdongun.promise.serverDB.OnFinishDBListener;
import com.example.kimdongun.promise.serverDB.PostDB;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.kimdongun.promise.Account.myAccount;
import static com.example.kimdongun.promise.Main.MainActivity.mDialogHandler;
import static com.example.kimdongun.promise.Main.MainActivity.mDialogThread;

public class AcceptPromiseActivity extends AppCompatActivity{
    private ListViewItem listViewItem;
    private ItemPlace arrivePlace; //도착 장소
    private ItemPlace startPlace; //출발 장소
    public ArrayList<AddPromiseSocialListViewItem> friend; //약속 친구

    private Fragment targetFragment; //현재 프래그먼트

    public int index; //약속 인덱스

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        if(myAccount == null){
            myAccount = new Account(this);
        }
        myAccount.activityNow = "NotMain";

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(mDialogHandler == null) {
            mDialogHandler = new DialogHandler(this);
        }
        if(mDialogThread == null){
            mDialogThread = new DialogThread(mDialogHandler);
            mDialogThread.setDaemon(true);
            mDialogThread.start();
        }
        mDialogHandler.context = this;
        //인덱스 받기
        Intent intent = getIntent();
        index = intent.getIntExtra("index", 0);

        //DB에 보낼 데이타
        HashMap<String, String> postData = new HashMap<String, String>();
        postData.put("no", String.valueOf(index)); //약속 인덱스
        postData.put("m_no", String.valueOf(myAccount.getIndex())); //약속 작성 계정 번호

        //DB에서 약속 불러옴
        PostDB postDB = new PostDB(this);
        postDB.putData("select_promise.php", postData, new OnFinishDBListener() {
            @Override
            public void onSuccess(String output) {
                if(output.equals("null")){
                    Toast.makeText(getApplicationContext(),"존재하지 않는 약속입니다.",Toast.LENGTH_LONG).show();
                    myAccount.load_Data();
                    Intent intent = new Intent(AcceptPromiseActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    //변수 초기화
                    listViewItem = new ListViewItem();
                    arrivePlace = new ItemPlace();
                    startPlace = new ItemPlace();
                    targetFragment = new FragmentAcceptMain();
                    friend = new ArrayList<>();

                    initData(output);

                    //프레그먼트 초기화
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.add(R.id.fragment, targetFragment);
                    fragmentTransaction.commit();
                    myAccount.load_Data();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        String str_frag = targetFragment.toString();
        if(!str_frag.equals("FragmentAcceptMain")){ //현재 화면이 약속 화면이 아니면 약속 화면으로 전환
            if(str_frag.equals("FragmentAcceptMap") && ((FragmentAcceptMap)targetFragment).isShowSearchList()){
                ((FragmentAcceptMap)targetFragment).hideSearchList();
            }else {
                switchFragment("FragmentAcceptMain");
            }
        }else { //약속 화면이면 어플 종료 물어봄
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(AcceptPromiseActivity.this);
            alert_confirm.setMessage("작성을 취소 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent n_intent = new Intent(AcceptPromiseActivity.this, MainActivity.class);
                            startActivity(n_intent);
                            finish();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String str_frag = targetFragment.toString();
        switch (item.getItemId()){
            case android.R.id.home:
                if(!str_frag.equals("FragmentAcceptMain")) { //현재 화면이 약속 화면이 아니면 약속 화면으로 전환
                    switchFragment("FragmentAcceptMain");
                }
                else{
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(AcceptPromiseActivity.this);
                    alert_confirm.setMessage("작성을 취소 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent n_intent = new Intent(AcceptPromiseActivity.this, MainActivity.class);
                                    startActivity(n_intent);
                                    finish();
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
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public ListViewItem getListViewItem() { return this.listViewItem; }
    public ItemPlace getArrivePlace() { return this.arrivePlace; }
    public ItemPlace getStartPlace() { return this.startPlace; }

    public void setArrivePlace(ItemPlace itemPlace) {
        arrivePlace.title = itemPlace.title;
        arrivePlace.address = itemPlace.address;
        arrivePlace.longitude = itemPlace.longitude;
        arrivePlace.latitude = itemPlace.latitude;
        arrivePlace.id = itemPlace.id;
    }
    public void setStartPlace(ItemPlace itemPlace) {
        startPlace.title = itemPlace.title;
        startPlace.address = itemPlace.address;
        startPlace.longitude = itemPlace.longitude;
        startPlace.latitude = itemPlace.latitude;
        startPlace.id = itemPlace.id;
    }

    public void switchFragment(String str_frag){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if(str_frag.equals("FragmentAcceptMain")) {
            targetFragment = new FragmentAcceptMain();
        }else if(str_frag.equals("FragmentAcceptMapA")){ //도착 장소 선택 프래그먼트
            targetFragment = new FragmentAcceptMap();
            ((FragmentAcceptMap)targetFragment).TYPE_OF_MAP = FragmentAddMap.ARRIVE;
        }else if(str_frag.equals("FragmentAcceptMapS")){ //출발 장소 선택 프래그먼트
            targetFragment = new FragmentAcceptMap();
            ((FragmentAcceptMap)targetFragment).TYPE_OF_MAP = FragmentAddMap.START;
        }else if(str_frag.equals("FragmentAcceptSocial")){ //친구 등록 프래그먼트
            targetFragment = new FragmentAcceptSocial();
        }
        fragmentTransaction.replace(R.id.fragment, targetFragment);
        fragmentTransaction.commit();
    }

    public void acceptPromise(){
        String netState = Network.getWhatKindOfNetwork(this);
        if(netState.equals(Network.NONE_STATE)){
            Network.connectNetwork(this);
        }else {
            //DB에 보낼 데이타
            HashMap<String, String> postData = new HashMap<String, String>();
            postData.put("no", String.valueOf(listViewItem.index)); //약속 고유 번호
            postData.put("m_no", String.valueOf(myAccount.getIndex())); //약속 작성 계정 번호
            postData.put("place_s", startPlace.title); //약속 출발 장소
            postData.put("lat_s", String.valueOf(startPlace.latitude)); //약속 출발 장소 위도
            postData.put("lon_s", String.valueOf(startPlace.longitude)); //약속 출발 장소 경도
            postData.put("content", String.valueOf(listViewItem.content)); //약속 내용
            postData.put("mail", myAccount.getMail()); //계정 메일

            //DB에 약속 추가
            PostDB postDB = new PostDB(this);
            postDB.putData("update_accept_promise.php", postData, new OnFinishDBListener() {
                @Override
                public void onSuccess(String output) {
                    if(output.equals("null")) {
                        Toast.makeText(getApplicationContext(), "존재하지 않는 약속입니다.", Toast.LENGTH_LONG).show();
                        myAccount.load_request();
                    }else {
                        Log.d("update_accept_promise", output);
                        myAccount.listViewAdapter.addTopItem(listViewItem); //약속 추가
                        myAccount.listViewAdapter.notifyDataSetChanged();
                        myAccount.listViewAcceptAdapter.removeItem(listViewItem.index);
                        myAccount.listViewAcceptAdapter.notifyDataSetChanged();
                    }

                    Intent intent = new Intent(AcceptPromiseActivity.this, ContentActivity.class);
                    intent.putExtra("index", listViewItem.index);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    public void refusePromise(){
        HashMap<String, String> postData = new HashMap<>();
        postData.put("no", String.valueOf(listViewItem.index)); //약속 고유 번호
        postData.put("m_no", String.valueOf(myAccount.getIndex())); //약속 작성 계정 번호
        postData.put("mail", myAccount.getMail()); //약속 고유 번호

        PostDB postDB = new PostDB(this);
        postDB.putData("refuse_promise.php", postData, new OnFinishDBListener() {
            @Override
            public void onSuccess(String output) {
                if(output.equals("null")) {
                    Toast.makeText(getApplicationContext(), "존재하지 않는 약속입니다.", Toast.LENGTH_LONG).show();
                    myAccount.load_request();
                }else {
                    myAccount.listViewAcceptAdapter.removeItem(listViewItem.index);
                    myAccount.listViewAcceptAdapter.notifyDataSetChanged();
                }
                Intent intent = new Intent(AcceptPromiseActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initData(String output){
        //Log.i("test", "output: " + output);
        try {
            JSONObject reader = new JSONObject(output);
            JSONArray objects = reader.getJSONArray("promise");
            JSONArray objects2 = reader.getJSONArray("social");
            arrivePlace = new ItemPlace(); //도착 장소
            startPlace = new ItemPlace(); //출발 장소
            listViewItem = new ListViewItem(); //약속 시간등 정보
            for (int i = 0; i < objects.length(); i++) {
                JSONObject object = objects.getJSONObject(i);
                arrivePlace.title = object.getString("place_a"); //도착 장소 이름
                arrivePlace.latitude = Double.valueOf(object.getString("lat_a")); //도착 장소 위도
                arrivePlace.longitude = Double.valueOf(object.getString("lon_a")); //도착 장소 경도
                listViewItem.dYear = Integer.valueOf(object.getString("year")); //약속 년
                listViewItem.dMonth = Integer.valueOf(object.getString("month")); //약속 월
                listViewItem.dDay = Integer.valueOf(object.getString("day")); //약속 일
                listViewItem.dHour = Integer.valueOf(object.getString("hour")); //약속 시
                listViewItem.dMin = Integer.valueOf(object.getString("minute")); //약속 분
                listViewItem.content = object.getString("content"); //약속 내용
                listViewItem.place = arrivePlace.title; //약속 장소
                listViewItem.index = Integer.valueOf(object.getString("no")); //약속 고유 번호
                listViewItem.spon_name = object.getString("spon_name");
                listViewItem.spon_no = Integer.valueOf(object.getString("spon_no")); //약속 고유 번호
                listViewItem.D_day = DdayClass.calDday(listViewItem.dYear, listViewItem.dMonth, listViewItem.dDay);
            }
            for (int i = 0; i < objects2.length(); i++) {
                JSONObject object = objects2.getJSONObject(i);
                AddPromiseSocialListViewItem item = new AddPromiseSocialListViewItem();
                item.no = Integer.valueOf(object.getString("no"));
                item.mail = object.getString("mail");
                item.name = object.getString("name");
                item.type = object.getString("type");
                friend.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
