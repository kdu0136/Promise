package com.example.kimdongun.promise.Promise;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
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
import com.example.kimdongun.promise.PromiseSocialListViewItem;
import com.example.kimdongun.promise.R;
import com.example.kimdongun.promise.Tutorial.TutorialActivity;
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

public class ContentActivity extends AppCompatActivity {
    public int index; //약속 인덱스

    private ItemPlace arrivePlace; //도착 장소
    private ItemPlace startPlace; //출발 장소
    private ListViewItem listViewItem; //약속 시간등 정보
    public ArrayList<AddPromiseSocialListViewItem> friend; //약속 친구

    private Fragment targetFragment; //현재 프래그먼트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(myAccount == null){
            myAccount = new Account(this);
            myAccount.load_Data();
        }
        myAccount.activityNow = "ContentActivity";
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
                    Intent intent = new Intent(ContentActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    initData(output);

                    if (myAccount.tuto_content_promise == 0) { //약속 확인 튜토리얼 아직 안봄
                        Intent intent_tuto = new Intent(ContentActivity.this, TutorialActivity.class);
                        intent_tuto.putExtra("type", "tuto_content_promise");
                        intent_tuto.putExtra("index", index);
                        startActivity(intent_tuto);
                        overridePendingTransition(0, 0);
                        finish();
                    }

                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_content, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        String str_frag = targetFragment.toString();
        if(!str_frag.equals("FragmentContentMain")){ //현재 화면이 내용 메인화면이 아니면 메인 화면으로 전환
            if(str_frag.equals("FragmentContentMap") && ((FragmentContentMap)targetFragment).isShowDetailRouteList()){
                ((FragmentContentMap)targetFragment).hideDetailRouteList();
                ((FragmentContentMap)targetFragment).cameraCenter();
            }else {
                switchFragment("FragmentContentMain");
            }
        }else { //메인 엑티비티로
            Intent n_intent = new Intent(ContentActivity.this, MainActivity.class);
            startActivity(n_intent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: //뒤로가기 버튼
                String str_frag = targetFragment.toString();
                if(!str_frag.equals("FragmentContentMain")){ //현재 화면이 내용 메인화면이 아니면 메인 화면으로 전환
                    if(str_frag.equals("FragmentContentMap") && ((FragmentContentMap)targetFragment).isShowDetailRouteList()){
                        ((FragmentContentMap)targetFragment).hideDetailRouteList();
                        ((FragmentContentMap)targetFragment).cameraCenter();
                    }else {
                        switchFragment("FragmentContentMain");
                    }
                }else { //메인 엑티비티로
                    Intent n_intent = new Intent(ContentActivity.this, MainActivity.class);
                    startActivity(n_intent);
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
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
            friend = new ArrayList<>(); //약속 친구
            for (int i = 0; i < objects.length(); i++) {
                JSONObject object = objects.getJSONObject(i);
                arrivePlace.title = object.getString("place_a"); //도착 장소 이름
                arrivePlace.latitude = Double.valueOf(object.getString("lat_a")); //도착 장소 위도
                arrivePlace.longitude = Double.valueOf(object.getString("lon_a")); //도착 장소 경도
                startPlace.title = object.getString("place_s"); //출발 장소 이름
                startPlace.latitude = Double.valueOf(object.getString("lat_s")); //출발 장소 위도
                startPlace.longitude = Double.valueOf(object.getString("lon_s")); //출발 장소 경도
                listViewItem.dYear = Integer.valueOf(object.getString("year")); //약속 년
                listViewItem.dMonth = Integer.valueOf(object.getString("month")); //약속 월
                listViewItem.dDay = Integer.valueOf(object.getString("day")); //약속 일
                listViewItem.dHour = Integer.valueOf(object.getString("hour")); //약속 시
                listViewItem.dMin = Integer.valueOf(object.getString("minute")); //약속 분
                listViewItem.content = object.getString("content"); //약속 내용
                listViewItem.place = arrivePlace.title; //약속 장소
                listViewItem.index = Integer.valueOf(object.getString("no")); //약속 고유 번호
                listViewItem.spon_name = object.getString("spon_name");
                listViewItem.spon_no = Integer.valueOf(object.getString("spon_no"));
                listViewItem.D_day = DdayClass.calDday(listViewItem.dYear, listViewItem.dMonth, listViewItem.dDay);
                listViewItem.isStart = Integer.valueOf(object.getString("isStart"));
                listViewItem.alram_time = Integer.valueOf(object.getString("t_alarm"));
                listViewItem.isAlarm = Integer.valueOf(object.getString("isAlarm"));
            }
            for(int i = 0; i < objects2.length(); i++){
                JSONObject object = objects2.getJSONObject(i);
                AddPromiseSocialListViewItem item = new AddPromiseSocialListViewItem();
                item.no = Integer.valueOf(object.getString("no"));
                item.mail = object.getString("mail");
                item.name = object.getString("name");
                item.type = object.getString("type");
                friend.add(item);
            }
            iniActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void iniActivity(){
        setContentView(R.layout.activity_content);

        //초기화
        targetFragment = new FragmentContentMain();

        //프레그먼트 초기화
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fragment, targetFragment);
        fragmentTransaction.commit();
    }

    public void editData(){
        HashMap<String, String> postData = new HashMap<String, String>();
        postData.put("no", String.valueOf(index));
        postData.put("m_no", String.valueOf(myAccount.getIndex()));

        PostDB postDB = new PostDB(this);
        postDB.putData("start_promise.php", postData, new OnFinishDBListener() {
            @Override
            public void onSuccess(String output) {
                if(output.equals("null")){
                    Toast.makeText(getApplicationContext(),"존재하지 않는 약속입니다.",Toast.LENGTH_LONG).show();
                    myAccount.load_Data();
                    Intent intent = new Intent(ContentActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(ContentActivity.this, EditActivity.class);
                    intent.putExtra("listViewItem", listViewItem);
                    intent.putExtra("arrivePlace", arrivePlace);
                    intent.putExtra("startPlace", startPlace);
                    intent.putExtra("friend", friend);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public void changeSponsor(final PromiseSocialListViewItem item){
        HashMap<String, String> postData = new HashMap<>();
        postData.put("pro_no", String.valueOf(listViewItem.index)); //약속 번호
        postData.put("no", String.valueOf(myAccount.getIndex())); //기존 주최자
        postData.put("new_spon_no", String.valueOf(item.no)); //새로운 주최자
        postData.put("mail", myAccount.getMail()); //메일

        PostDB postDB = new PostDB(this);
        postDB.prograssBar = false;
        postDB.putData("change_sponsor.php", postData, new OnFinishDBListener() {
            @Override
            public void onSuccess(String output) {
                Toast.makeText(ContentActivity.this, "'"+item.mail + "'님에게 주최자를 양도하였습니다", Toast.LENGTH_LONG).show();
                myAccount.listViewAdapter.removeItem(index);
                myAccount.listViewAdapter.notifyDataSetChanged();
                Intent intent = new Intent(ContentActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void deleteData(){
        String netState = Network.getWhatKindOfNetwork(this);
        if(netState.equals(Network.NONE_STATE)){
            Network.connectNetwork(this);
        }else {
            //DB에 보낼 데이타
            HashMap<String, String> postData = new HashMap<String, String>();
            postData.put("no", String.valueOf(index)); //약속 인덱스
            postData.put("m_no", String.valueOf(myAccount.getIndex())); //약속 작성 계정 번호
            postData.put("mail", myAccount.getMail());

            PostDB postDB = new PostDB(this, "삭제중...");
            postDB.putData("delete_promise.php", postData, new OnFinishDBListener() {
                @Override
                public void onSuccess(String output) {
                    if(output.equals("null")){
                        Toast.makeText(getApplicationContext(),"존재하지 않는 약속입니다.",Toast.LENGTH_LONG).show();
                        myAccount.load_Data();
                    }else {
                        myAccount.listViewAdapter.removeItem(index);
                        myAccount.listViewAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(getApplicationContext(),"약속을 삭제하였습니다.",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ContentActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    public void deleteAll(){
        String netState = Network.getWhatKindOfNetwork(this);
        if(netState.equals(Network.NONE_STATE)){
            Network.connectNetwork(this);
        }else {
            //DB에 보낼 데이타
            HashMap<String, String> postData = new HashMap<String, String>();
            postData.put("no", String.valueOf(index)); //약속 인덱스(약속 root index)
            postData.put("m_no", String.valueOf(myAccount.getIndex())); //약속 작성 계정 번호(주최자 번호)
            postData.put("mail", myAccount.getMail());

            PostDB postDB = new PostDB();
            postDB.putData("delete_promise_all.php", postData, new OnFinishDBListener() {
                @Override
                public void onSuccess(String output) {
                    if(output.equals("null")){
                        Toast.makeText(getApplicationContext(),"존재하지 않는 약속입니다.",Toast.LENGTH_LONG).show();
                        myAccount.load_Data();
                    }else {
                        myAccount.listViewAdapter.removeItem(index);
                        myAccount.listViewAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(getApplicationContext(),"약속을 파기 하였습니다.",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ContentActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    public void startPromise(int isStarted){ //1을 받으면 출발상태로 변경
        listViewItem.isStart = isStarted;
        HashMap<String, String> postData = new HashMap<String, String>();
        postData.put("no", String.valueOf(index));
        postData.put("m_no", String.valueOf(myAccount.getIndex()));
        postData.put("isStart", String.valueOf(listViewItem.isStart));

        PostDB postDB = new PostDB(this);
        postDB.putData("start_promise.php", postData, new OnFinishDBListener() {
            @Override
            public void onSuccess(String output) {
                if(output.equals("null")){
                    Toast.makeText(getApplicationContext(),"존재하지 않는 약속입니다.",Toast.LENGTH_LONG).show();
                    myAccount.load_Data();
                    Intent intent = new Intent(ContentActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    if (listViewItem.isStart == 1) { //약속 출발 상태
                        ((FragmentContentMain) targetFragment).mMenu.getItem(0).setTitle("출발 취소");
                        ((FragmentContentMain) targetFragment).textView_isStart.setText("출발");
                        ((FragmentContentMain) targetFragment).textView_isStart.setTextColor(getResources().getColor(R.color.colorAccent));
                    } else { //약속 미출발상태
                        ((FragmentContentMain) targetFragment).mMenu.getItem(0).setTitle("출발");
                        ((FragmentContentMain) targetFragment).textView_isStart.setText("미 출발");
                        ((FragmentContentMain) targetFragment).textView_isStart.setTextColor(Color.GRAY);
                    }
                    myAccount.listViewAdapter.editItem(listViewItem.index, listViewItem);
                }
            }
        });
    }

    public void switchFragment(String str_frag){
        String netState = Network.getWhatKindOfNetwork(this);
        if(netState.equals(Network.NONE_STATE)){
            Network.connectNetwork(this);
        }else {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            if (str_frag.equals("FragmentContentMain")) {
                targetFragment = new FragmentContentMain();
            } else if (str_frag.equals("FragmentContentMap")) { //도착 장소 선택 프래그먼트
                targetFragment = new FragmentContentMap();
            }else if (str_frag.equals("FragmentContentSocial")) { //친구 목록 프래그먼트
                targetFragment = new FragmentContentSocial();
            }else if(str_frag.equals("FragmentContentChangeSponsor")) { //주최자 임명 프래그먼트
                targetFragment = new FragmentContentChangeSponsor();
            }
            fragmentTransaction.replace(R.id.fragment, targetFragment);
            fragmentTransaction.commit();
        }
    }

    public ListViewItem getListViewItem() { return this.listViewItem; }
    public ItemPlace getArrivePlace() { return this.arrivePlace; }
    public ItemPlace getStartPlace() { return this.startPlace; }
}
