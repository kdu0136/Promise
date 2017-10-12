package com.example.kimdongun.promise.Promise;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.kimdongun.promise.AddPromiseSocialListViewItem;
import com.example.kimdongun.promise.ListViewItem;
import com.example.kimdongun.promise.Main.MainActivity;
import com.example.kimdongun.promise.Network.Network;
import com.example.kimdongun.promise.R;
import com.example.kimdongun.promise.search.ItemPlace;
import com.example.kimdongun.promise.serverDB.OnFinishDBListener;
import com.example.kimdongun.promise.serverDB.PostDB;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.kimdongun.promise.Account.myAccount;
import static com.example.kimdongun.promise.Main.MainActivity.mDialogHandler;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    private ListViewItem listViewItem;
    private ItemPlace arrivePlace; //도착 장소
    private ItemPlace startPlace; //출발 장소

    private Fragment targetFragment; //현재 프래그먼트

    public ArrayList<AddPromiseSocialListViewItem> friend; //약속 친구
    public ArrayList<AddPromiseSocialListViewItem> friend_old; //이미 추가한 약속 친구
    public ArrayList<AddPromiseSocialListViewItem> friend_new; //새로 추가한 약속 친구
    public ArrayList<AddPromiseSocialListViewItem> friend_delete; //제외된 약속 친구

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        myAccount.activityNow = "NotMain";
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDialogHandler.context = this;
        //인텐트 값 받기
        Intent intent = getIntent();

        //변수 초기화
        listViewItem = (ListViewItem)intent.getSerializableExtra("listViewItem");
        arrivePlace = (ItemPlace)intent.getSerializableExtra("arrivePlace");
        startPlace = (ItemPlace)intent.getSerializableExtra("startPlace");
        friend = (ArrayList<AddPromiseSocialListViewItem>) intent.getSerializableExtra("friend");
        friend_old = new ArrayList<>(); //이미 추가한 약속 친구
        friend_new = new ArrayList<>(); //새로 추가한 약속 친구
        friend_delete = new ArrayList<>(); //제외된 약속 친구

        //이미 추가한 약속 친구 초기화
        for(int i = 0; i < friend.size(); i++){
            AddPromiseSocialListViewItem item = friend.get(i);
            friend_old.add(item);
            friend_delete.add(item);
        }
        targetFragment = new FragmentEditMain();

        //터치 이벤트

        //프레그먼트 초기화
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fragment, targetFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
        }
    }

    @Override
    public void onBackPressed() {
        String str_frag = targetFragment.toString();
        if(!str_frag.equals("FragmentEditMain")){ //현재 화면이 약속 화면이 아니면 약속 화면으로 전환
            if(str_frag.equals("FragmentEditMap") && ((FragmentEditMap)targetFragment).isShowSearchList()){
                ((FragmentEditMap)targetFragment).hideSearchList();
            }else {
                switchFragment("FragmentEditMain");
            }
        }else { //메인 화면이면 수정 취소 물어봄
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(EditActivity.this);
            alert_confirm.setMessage("수정을 취소 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent n_intent = new Intent(EditActivity.this, ContentActivity.class);
                            n_intent.putExtra("index", listViewItem.index);
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
                if(!str_frag.equals("FragmentEditMain")) { //현재 화면이 메인이 아니면 메인으로 전환
                    switchFragment("FragmentEditMain");
                }
                else{//메인 화면이면 수정 취소 물어봄
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(EditActivity.this);
                    alert_confirm.setMessage("수정을 취소 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent n_intent = new Intent(EditActivity.this, ContentActivity.class);
                                    n_intent.putExtra("index", listViewItem.index);
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
        if(str_frag.equals("FragmentEditMain")) {
            targetFragment = new FragmentEditMain();
        }else if(str_frag.equals("FragmentEditMapA")){ //도착 장소 선택 프래그먼트
            targetFragment = new FragmentEditMap();
            ((FragmentEditMap)targetFragment).TYPE_OF_MAP = FragmentAddMap.ARRIVE;
        }else if(str_frag.equals("FragmentEditMapS")){ //출발 장소 선택 프래그먼트
            targetFragment = new FragmentEditMap();
            ((FragmentEditMap)targetFragment).TYPE_OF_MAP = FragmentAddMap.START;
        }else if(str_frag.equals("FragmentEditSocialSpon")){ //친구 선택 프래그먼트 (주최자)
            targetFragment = new FragmentEditSocialSpon();
        }else if(str_frag.equals("FragmentEditSocial")){ //친구 선택 프래그먼트 (일반)
            targetFragment = new FragmentEditSocial();
        }
        fragmentTransaction.replace(R.id.fragment, targetFragment);
        fragmentTransaction.commit();
    }

    public void editPromise(){
        String netState = Network.getWhatKindOfNetwork(this);
        if(netState.equals(Network.NONE_STATE)){
            Network.connectNetwork(this);
        }else {
            //제외된 친구 갱신
            for(int i = friend_delete.size() - 1; i >= 0; i--){
                AddPromiseSocialListViewItem item_delete = friend_delete.get(i);
                boolean exist = false;
                for(int j = 0; j < friend.size(); j++){
                    AddPromiseSocialListViewItem item = friend_delete.get(i);
                    if(item_delete.no == item.no){
                        exist = true;
                        break;
                    }
                }
                if(exist){
                    friend_delete.remove(i);
                }
            }
            //DB에 보낼 데이타
            HashMap<String, String> postData = new HashMap<String, String>();
            postData.put("no", String.valueOf(listViewItem.index)); //약속 고유 번호
            postData.put("m_no", String.valueOf(myAccount.getIndex())); //약속 작성 계정 번호
            postData.put("place_a", arrivePlace.title); //약속 도착 장소
            postData.put("lat_a", String.valueOf(arrivePlace.latitude)); //약속 도착 장소 위도
            postData.put("lon_a", String.valueOf(arrivePlace.longitude)); //약속 도착 장소 경도
            postData.put("place_s", startPlace.title); //약속 출발 장소
            postData.put("lat_s", String.valueOf(startPlace.latitude)); //약속 출발 장소 위도
            postData.put("lon_s", String.valueOf(startPlace.longitude)); //약속 출발 장소 경도
            postData.put("year", String.valueOf(listViewItem.dYear)); //약속 년
            postData.put("month", String.valueOf(listViewItem.dMonth)); //약속 월
            postData.put("day", String.valueOf(listViewItem.dDay)); //약속 일
            postData.put("hour", String.valueOf(listViewItem.dHour)); //약속 시간
            postData.put("minute", String.valueOf(listViewItem.dMin)); //약속 분
            postData.put("content", String.valueOf(listViewItem.content)); //약속 내용

            if(myAccount.getIndex() == listViewItem.spon_no){ //주최자인 경우
                postData.put("mail", myAccount.getMail()); //메일
                //약속 요청보낸 친구 번호 등록
                for(int i=0; i< friend_old.size(); i++) { //기존 친구
                    AddPromiseSocialListViewItem item = friend_old.get(i);
                    postData.put("f_no_old"+i, String.valueOf(item.no)); //계정 번호
                }
                for(int i=0; i< friend_new.size(); i++) { //새 친구
                    AddPromiseSocialListViewItem item = friend_new.get(i);
                    postData.put("f_no_new"+i, String.valueOf(item.no)); //계정 번호
                }
                for(int i=0; i< friend_delete.size(); i++) { //제외된 친구
                    AddPromiseSocialListViewItem item = friend_delete.get(i);
                    postData.put("f_no_delete"+i, String.valueOf(item.no)); //계정 번호
                }

                //DB에서 약속 수정
                PostDB postDB = new PostDB(this);
                postDB.putData("update_promise_all.php", postData, new OnFinishDBListener() {
                    @Override
                    public void onSuccess(String output) {
                        if(output.equals("null")){
                            Toast.makeText(getApplicationContext(),"존재하지 않는 약속입니다.",Toast.LENGTH_LONG).show();
                            myAccount.load_Data();
                            Intent intent = new Intent(EditActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            listViewItem.place = arrivePlace.title;
                            myAccount.listViewAdapter.editItem(listViewItem.index, listViewItem);
                            myAccount.listViewAdapter.notifyDataSetChanged();
                            Intent intent = new Intent(EditActivity.this, ContentActivity.class);
                            intent.putExtra("index", listViewItem.index);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }else{ //약속 맴버인 경우
                //DB에서 약속 수정
                PostDB postDB = new PostDB(this);
                postDB.putData("update_promise.php", postData, new OnFinishDBListener() {
                    @Override
                    public void onSuccess(String output) {
                        if(output.equals("null")){
                            Toast.makeText(getApplicationContext(),"존재하지 않는 약속입니다.",Toast.LENGTH_LONG).show();
                            myAccount.load_Data();
                            Intent intent = new Intent(EditActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            listViewItem.place = arrivePlace.title;
                            myAccount.listViewAdapter.editItem(listViewItem.index, listViewItem);
                            myAccount.listViewAdapter.notifyDataSetChanged();
                            Intent intent = new Intent(EditActivity.this, ContentActivity.class);
                            intent.putExtra("index", listViewItem.index);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        }
    }
}
