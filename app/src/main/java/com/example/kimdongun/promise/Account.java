package com.example.kimdongun.promise;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.kimdongun.promise.serverDB.OnFinishDBListener;
import com.example.kimdongun.promise.serverDB.PostDB;
import com.example.kimdongun.promise.service.LocationService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by KimDongun on 2016-12-20.
 */

public class Account {
    public static Account myAccount;

    //private
    private int index; //식별자
    private String mail; //로그인 아이디
    private String type; //구글인지 페이스북 계정인지

    //public
    public String name; //이름
    public int search_on; //계정 검색 허용 여부 (1이면 허용)
    public int location_on; //실시간 위치 공유 허용 여부 (1이면 허용)

    public int tuto_promise; //약속 목록 튜토리얼 확인 유무
    public int tuto_social; //친구 목록 튜토리얼 확인 유무
    public int tuto_add_social; //친구 추가 튜토리얼 확인 유무
    public int tuto_add_promise; //약속 추가 튜토리얼 확인 유무
    public int tuto_content_promise; //약속 확인 튜토리얼 확인 유무

    public ListViewAdapter listViewAdapter; //약속 어댑터
    public ListViewAdapter listViewAcceptAdapter; //약속 요청 어댑터
    public Context mContext;

    public AddFriendListViewAdapter main_adapter; //메인 프래그먼트 어댑터
    public AddFriendListViewAdapter request_adapter; //요청 한 프래그먼트 어댑터
    public AddFriendListViewAdapter accept_adapter; //요청 받은 프래그먼트 어댑터

    public String activityNow; //현재 액티비티

    public Account(Context mContext){
        this.mContext = mContext;

        SharedPreferences pref = mContext.getSharedPreferences("login", Context.MODE_PRIVATE);
        this.name = pref.getString("name", "");
        this.mail = pref.getString("mail", "");
        this.index = pref.getInt("index", -1);
        this.type = pref.getString("type", "logout");
        this.search_on = pref.getInt("search", 1);
        this.location_on = pref.getInt("location", 1);

        this.tuto_promise = pref.getInt("tuto_promise", 0);
        this.tuto_social = pref.getInt("tuto_social", 0);
        this.tuto_add_social = pref.getInt("tuto_add_social", 0);
        this.tuto_add_promise = pref.getInt("tuto_add_promise", 0);
        this.tuto_content_promise = pref.getInt("tuto_content_promise", 0);

        listViewAdapter = new ListViewAdapter(this.mContext);
        listViewAcceptAdapter = new ListViewAdapter(this.mContext);
        main_adapter = new AddFriendListViewAdapter(this.mContext);
        request_adapter = new AddFriendListViewAdapter(this.mContext);
        accept_adapter = new AddFriendListViewAdapter(this.mContext);
        activityNow = "";

        Intent intent = new Intent(this.mContext, LocationService.class);
        this.mContext.startService(intent);
    }

    public void setAdapterContext(Context context){
        mContext = context;
        listViewAdapter.setMyContext(mContext);
        listViewAcceptAdapter.setMyContext(mContext);
        main_adapter.setMyContext(mContext);
        request_adapter.setMyContext(mContext);
        accept_adapter.setMyContext(mContext);
    }

    public void loginAccount(String name, String mail, int index, String type, int search_on, int location_on,
                             int tuto_promise, int tuto_social, int tuto_add_social, int tuto_add_promise, int tuto_content_promise){
        this.name = name;
        this.mail = mail;
        this.index = index;
        this.type = type;
        this.search_on = search_on;
        this.location_on = location_on;

        this.tuto_promise = tuto_promise;
        this.tuto_social = tuto_social;
        this.tuto_add_social = tuto_add_social;
        this.tuto_add_promise = tuto_add_promise;
        this.tuto_content_promise = tuto_content_promise;
        saveAccountData();
    }

    public void logoutAccount(){
        this.name = "";
        this.mail = "";
        this.index = -1;
        this.type = "logout";
        this.search_on = 1;
        this.location_on = 1;
        saveAccountData();
        Intent intent = new Intent(this.mContext, LocationService.class);
        this.mContext.stopService(intent);
    }

    public void saveAccountData(){
        SharedPreferences pref = mContext.getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("name", this.name);
        editor.putString("mail", this.mail);
        editor.putInt("index", this.index);
        editor.putString("type", this.type);
        editor.putInt("search", this.search_on);
        editor.putInt("location", this.location_on);

        editor.putInt("tuto_promise", this.tuto_promise);
        editor.putInt("tuto_social", this.tuto_social);
        editor.putInt("tuto_add_socail", this.tuto_add_social);
        editor.putInt("tuto_add_promise", this.tuto_add_promise);
        editor.putInt("tuto_content_promise", this.tuto_content_promise);
        editor.commit();
    }

    public void load_Data(){
        load_promise();
        load_request();
        load_friend();
    }

    //약속 목록 불러오기
    public void load_promise(){
        Log.i("load_promise", "m_no: " + index);
        HashMap<String, String> postData = new HashMap<String, String>();
        postData.put("m_no", String.valueOf(index));
        postData.put("listEndNum", String.valueOf(0)); //0번째 부터
        postData.put("searchType", "place_a"); //도착 장소 이름으로 검색
        postData.put("findText", "");

        PostDB postDB = new PostDB(mContext);
        postDB.prograssBar = false;
        postDB.putData("load_promise.php", postData, new OnFinishDBListener() {
            @Override
            public void onSuccess(String output) {
                Log.i("load_promise", "output: " + output);
                try {
                    JSONObject reader = new JSONObject(output);
                    JSONArray objects = reader.getJSONArray("promise");
                    myAccount.listViewAdapter.removeAll();
                    for (int i = 0; i < objects.length(); i++) {
                        JSONObject object = objects.getJSONObject(i);
                        if(i < objects.length() - 1) {
                            ListViewItem listViewItem = new ListViewItem(); //약속 시간등 정보
                            listViewItem.place = object.getString("place_a"); //도착 장소 이름
                            listViewItem.dYear = Integer.valueOf(object.getString("year")); //약속 년
                            listViewItem.dMonth = Integer.valueOf(object.getString("month")); //약속 월
                            listViewItem.dDay = Integer.valueOf(object.getString("day")); //약속 일\
                            listViewItem.dHour = Integer.valueOf(object.getString("hour")); //약속 시
                            listViewItem.dMin = Integer.valueOf(object.getString("minute")); //약속 분
                            listViewItem.content = object.getString("content"); //약속 내용
                            listViewItem.index = Integer.valueOf(object.getString("no")); //약속 식별자
                            listViewItem.spon_name = object.getString("spon_name");
                            listViewItem.spon_no = Integer.valueOf(object.getString("spon_no"));
                            listViewItem.isStart = Integer.valueOf(object.getString("isStart"));
                            listViewItem.alram_time = Integer.valueOf(object.getString("t_alarm"));
                            listViewItem.isAlarm = Integer.valueOf(object.getString("isAlarm"));
                            myAccount.listViewAdapter.addItem(listViewItem);
                        }else{
                            myAccount.listViewAdapter.total_data = Integer.valueOf(object.getString("total_count")); //총 데이터 갯수
                        }
                    }
                    myAccount.listViewAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //약속 요청 목록 불러오기
    public void load_request(){
        Log.d("load_request", "load_request");
        HashMap<String, String> postData = new HashMap<String, String>();
        postData.put("m_no", String.valueOf(myAccount.getIndex()));

        PostDB postDB = new PostDB(mContext);
        postDB.prograssBar = false;
        postDB.putData("load_promise_accept.php", postData, new OnFinishDBListener() {
            @Override
            public void onSuccess(String output) {
                Log.i("load_promise_accept", "output: " + output);
                try {
                    JSONObject reader = new JSONObject(output);
                    JSONArray friend_obj = reader.getJSONArray("promise");
                    myAccount.listViewAcceptAdapter.removeAll();
                    //친구목록
                    for (int i = 0; i < friend_obj.length(); i++) {
                        JSONObject object = friend_obj.getJSONObject(i);
                        ListViewItem listViewItem = new ListViewItem(); //약속 시간등 정보
                        listViewItem.place = object.getString("place_a"); //도착 장소 이름
                        listViewItem.dYear = Integer.valueOf(object.getString("year")); //약속 년
                        listViewItem.dMonth = Integer.valueOf(object.getString("month")); //약속 월
                        listViewItem.dDay = Integer.valueOf(object.getString("day")); //약속 일\
                        listViewItem.dHour = Integer.valueOf(object.getString("hour")); //약속 시
                        listViewItem.dMin = Integer.valueOf(object.getString("minute")); //약속 분
                        listViewItem.content = object.getString("content"); //약속 내용
                        listViewItem.index = Integer.valueOf(object.getString("no")); //약속 식별자
                        listViewItem.spon_name = object.getString("spon_name");
                        listViewItem.spon_no = Integer.valueOf(object.getString("spon_no"));
                        listViewItem.alram_time = -1;
                        listViewItem.isAlarm = 0;
                        listViewItem.isStart = 2;

                        myAccount.listViewAcceptAdapter.addItem(listViewItem);
                    }
                    myAccount.listViewAcceptAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //친구 목록 갱신하기
    public void load_friend(){
        HashMap<String, String> postData = new HashMap<String, String>();
        postData.put("no", String.valueOf(index));

        PostDB postDB = new PostDB(mContext);
        postDB.prograssBar = false;
        postDB.putData("load_friend.php", postData, new OnFinishDBListener() {
            @Override
            public void onSuccess(String output) {
                Log.i("load_friend", "output: " + output);
                try {
                    JSONObject reader = new JSONObject(output);
                    JSONArray friend_obj = reader.getJSONArray("friend");
                    myAccount.main_adapter.removeAll();
                    myAccount.request_adapter.removeAll();
                    myAccount.accept_adapter.removeAll();
                    //친구목록
                    for (int i = 0; i < friend_obj.length(); i++) {
                        JSONObject object = friend_obj.getJSONObject(i);
                        AddFriendListViewItem item = new AddFriendListViewItem();
                        item.no = Integer.valueOf(object.getString("no")); //친구 계정 번호
                        item.mail = object.getString("mail"); //메일
                        item.name = object.getString("name"); //이름
                        item.type = object.getString("type"); //계정 유형
                        item.state = object.getString("state"); //친구 상태(친구, 요청, 수락, 남남)
                        if(item.state.equals("friend")) {
                            myAccount.main_adapter.addItem(item);
                        }else if(item.state.equals("request")) {
                            myAccount.request_adapter.addItem(item);
                        }else if(item.state.equals("accept")) {
                            myAccount.accept_adapter.addItem(item);
                        }
                        myAccount.main_adapter.notifyDataSetChanged();
                        myAccount.request_adapter.notifyDataSetChanged();
                        myAccount.accept_adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //Getter
    public int getIndex() { return index; }
    public String getMail() { return mail; }
    public String getType() { return type; }
}
