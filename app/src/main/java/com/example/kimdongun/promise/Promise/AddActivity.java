package com.example.kimdongun.promise.Promise;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.example.kimdongun.promise.Account;
import com.example.kimdongun.promise.AddPromiseSocialListViewItem;
import com.example.kimdongun.promise.DialogHandler;
import com.example.kimdongun.promise.DialogThread;
import com.example.kimdongun.promise.ListViewItem;
import com.example.kimdongun.promise.Main.MainActivity;
import com.example.kimdongun.promise.Network.Network;
import com.example.kimdongun.promise.Permission;
import com.example.kimdongun.promise.R;
import com.example.kimdongun.promise.Social.AddFriendActivity;
import com.example.kimdongun.promise.Tutorial.TutorialActivity;
import com.example.kimdongun.promise.search.ItemPlace;
import com.example.kimdongun.promise.serverDB.OnFinishDBListener;
import com.example.kimdongun.promise.serverDB.PostDB;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.kimdongun.promise.Account.myAccount;
import static com.example.kimdongun.promise.Main.MainActivity.mDialogHandler;
import static com.example.kimdongun.promise.Main.MainActivity.mDialogThread;

public class AddActivity extends AppCompatActivity{
    private ListViewItem listViewItem;
    private ItemPlace arrivePlace; //도착 장소
    private ItemPlace startPlace; //출발 장소
    public ArrayList<AddPromiseSocialListViewItem> friend; //약속 친구

    private Fragment targetFragment; //현재 프래그먼트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Log.d("AddActivity", "onCreate");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (myAccount == null) {
            myAccount = new Account(this);
            myAccount.load_Data();
        }
        myAccount.activityNow = "NotMain";
        if (mDialogHandler == null) {
            mDialogHandler = new DialogHandler(this);
        }
        if (mDialogThread == null) {
            mDialogThread = new DialogThread(mDialogHandler);
            mDialogThread.setDaemon(true);
            mDialogThread.start();
        }
        mDialogHandler.context = this;

        //변수 초기화
        listViewItem = new ListViewItem();
        arrivePlace = new ItemPlace();
        startPlace = new ItemPlace();
        targetFragment = new FragmentAddMain();
        friend = new ArrayList<>();

        //터치 이벤트

        //프레그먼트 초기화
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fragment, targetFragment);
        fragmentTransaction.commit();
        switchFragment("FragmentAddMain");

        if (myAccount.tuto_add_promise == 0) { //약속 추가 튜토리얼 아직 안봄
            Intent intent_tuto = new Intent(AddActivity.this, TutorialActivity.class);
            intent_tuto.putExtra("type", "tuto_add_promise");
            startActivity(intent_tuto);
            overridePendingTransition(0, 0);
            finish();
        }
    }

    @Override
    protected void onPause() {
        Log.d("AddActivity", "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("AddActivity", "onStop");
        super.onStop();
    }

    @Override
    protected void onResume() {
        Permission.permissionLocation(this);
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                Log.d("Permission", "onRequestPermissionsResult");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 허가
                    // 해당 권한을 사용해서 작업을 진행할 수 있습니다
                } else {
                    // 권한 거부
                    // 사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다
                    Intent n_intent = new Intent(AddActivity.this, MainActivity.class);
                    startActivity(n_intent);
                    finish();
                }
                return;
        }
    }

    @Override
    public void onBackPressed() {
        String str_frag = targetFragment.toString();
        if(!str_frag.equals("FragmentAddMain")){ //현재 화면이 약속 화면이 아니면 약속 화면으로 전환
            if(str_frag.equals("FragmentAddMap") && ((FragmentAddMap)targetFragment).isShowSearchList()){
                ((FragmentAddMap)targetFragment).hideSearchList();
            }else {
                switchFragment("FragmentAddMain");
            }
        }else { //약속 화면이면 어플 종료 물어봄
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(AddActivity.this);
            alert_confirm.setMessage("작성을 취소 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent n_intent = new Intent(AddActivity.this, MainActivity.class);
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
                if(!str_frag.equals("FragmentAddMain")) { //현재 화면이 약속 화면이 아니면 약속 화면으로 전환
                    switchFragment("FragmentAddMain");
                }
                else{
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(AddActivity.this);
                    alert_confirm.setMessage("작성을 취소 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent n_intent = new Intent(AddActivity.this, MainActivity.class);
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
        if(str_frag.equals("FragmentAddMain")) {
            targetFragment = new FragmentAddMain();
        }else if(str_frag.equals("FragmentAddMapA")){ //도착 장소 선택 프래그먼트
            targetFragment = new FragmentAddMap();
            ((FragmentAddMap)targetFragment).TYPE_OF_MAP = FragmentAddMap.ARRIVE;
        }else if(str_frag.equals("FragmentAddMapS")){ //출발 장소 선택 프래그먼트
            targetFragment = new FragmentAddMap();
            ((FragmentAddMap)targetFragment).TYPE_OF_MAP = FragmentAddMap.START;
        }else if(str_frag.equals("FragmentAddSocial")){ //친구 등록 프래그먼트
            targetFragment = new FragmentAddSocial();
        }
        fragmentTransaction.replace(R.id.fragment, targetFragment);
        fragmentTransaction.commit();
    }

    public void addPromise(){
        String netState = Network.getWhatKindOfNetwork(this);
        if(netState.equals(Network.NONE_STATE)){
            Network.connectNetwork(this);
        }else {
            //DB에 보낼 데이타
            HashMap<String, String> postData = new HashMap<String, String>();
            postData.put("m_no", String.valueOf(myAccount.getIndex())); //약속 작성 계정 번호
            postData.put("mail", myAccount.getMail()); //약속 작성 계정 메일
            postData.put("place_a", arrivePlace.title); //약속 도착 장소
            postData.put("lat_a", String.valueOf(arrivePlace.latitude)); //약속 도착 장소 위도
            postData.put("lon_a", String.valueOf(arrivePlace.longitude)); //약속 도착 장소 경도
            postData.put("place_s", startPlace.title); //약속 출발 장소
            postData.put("lat_s", String.valueOf(startPlace.latitude)); //약속 출발 장소 위도
            postData.put("lon_s", String.valueOf(startPlace.longitude)); //약속 출발 장소 경도
            postData.put("content", String.valueOf(listViewItem.content)); //약속 내용
            postData.put("year", String.valueOf(listViewItem.dYear)); //약속 년
            postData.put("month", String.valueOf(listViewItem.dMonth)); //약속 월
            postData.put("day", String.valueOf(listViewItem.dDay)); //약속 일
            postData.put("hour", String.valueOf(listViewItem.dHour)); //약속 시간
            postData.put("minute", String.valueOf(listViewItem.dMin)); //약속 분

            //약속 요청보낸 친구 번호 등록
            for(int i=0; i< friend.size(); i++) {
                AddPromiseSocialListViewItem item = friend.get(i);
                postData.put("f_no"+i, String.valueOf(item.no)); //약속 작성 계정 번호
                Log.d("f_no", "f_no: "+String.valueOf(item.no));
            }

            //DB에 약속 추가
            PostDB postDB = new PostDB(this);
            postDB.putData("insert_promise.php", postData, new OnFinishDBListener() {
                @Override
                public void onSuccess(String output) {
                    Log.d("insert_promise", output);
                    listViewItem.index = Integer.valueOf(output); //식별자 입력
                    listViewItem.place = arrivePlace.title;
                    listViewItem.spon_no = myAccount.getIndex();
                    listViewItem.spon_name = myAccount.name;
                    Log.d("tag", "DB식별자: " + listViewItem.index);
                    myAccount.listViewAdapter.addTopItem(listViewItem); //약속 추가

                    Intent intent = new Intent(AddActivity.this, ContentActivity.class);
                    intent.putExtra("index", listViewItem.index);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}
