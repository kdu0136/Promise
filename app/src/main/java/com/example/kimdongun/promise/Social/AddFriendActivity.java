package com.example.kimdongun.promise.Social;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.kimdongun.promise.AddFriendListViewAdapter;
import com.example.kimdongun.promise.AddFriendListViewItem;
import com.example.kimdongun.promise.ContentFriendDialog;
import com.example.kimdongun.promise.Main.MainActivity;
import com.example.kimdongun.promise.Network.Network;
import com.example.kimdongun.promise.R;
import com.example.kimdongun.promise.Tutorial.TutorialActivity;
import com.example.kimdongun.promise.serverDB.OnFinishDBListener;
import com.example.kimdongun.promise.serverDB.PostDB;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import static com.example.kimdongun.promise.Account.myAccount;
import static com.example.kimdongun.promise.Main.MainActivity.mDialogHandler;
import static com.example.kimdongun.promise.R.id.listView;

public class AddFriendActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, DialogInterface.OnDismissListener{

    private View textView_noFind; //검색 결과 없음
    private SearchView searchView; //검색 창
    private ImageView buttonSearch; //검색 버튼
    private String searchText; //검색 텍스트
    private ListView myListView; //검색 친구 리스트
    private AddFriendListViewAdapter addFriendListViewAdapter; //어댑터
    private ContentFriendDialog contentItemDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        myAccount.activityNow = "NotMain";
        mDialogHandler.context = this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //초기화
        textView_noFind = findViewById(R.id.textView_noFind); //검색 결과 없음
        searchView = (SearchView)findViewById(R.id.searchView); //검색 창
        buttonSearch = (ImageView)findViewById(R.id.buttonSearch); //검색 버튼
        searchText = ""; //검색 텍스트
        myListView = (ListView)findViewById(listView); //검색 친구 리스트
        addFriendListViewAdapter = new AddFriendListViewAdapter(this);
        myListView.setAdapter(addFriendListViewAdapter);

        //터치 이벤트
        buttonSearch.setOnClickListener(this); //검색 버튼
        myListView.setOnItemClickListener(this); //리스트

        //검색 이벤트
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchText = query;
                loadListView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                return false;
            }
        });
        hideSoftKeyboard();

        if (myAccount.tuto_add_social == 0) { //친구 추가 튜토리얼 아직 안봄
            Intent intent_tuto = new Intent(AddFriendActivity.this, TutorialActivity.class);
            intent_tuto.putExtra("type", "tuto_add_social");
            startActivity(intent_tuto);
            overridePendingTransition(0, 0);
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddFriendActivity.this, MainActivity.class);
        intent.putExtra("id", R.id.social);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(AddFriendActivity.this, MainActivity.class);
                intent.putExtra("id", R.id.social);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
        if(parent.equals(myListView)) {
            int no = ((AddFriendListViewItem)addFriendListViewAdapter.getItem(index)).no;
            Log.d("Click_NO", "no: "+no);
            contentItemDialog = new ContentFriendDialog(this, addFriendListViewAdapter.getItemAt(no));
            contentItemDialog.setOnDismissListener(this);
            contentItemDialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonSearch:
                loadListView();
                break;
        }
    }

    private void loadListView(){
        String netState = Network.getWhatKindOfNetwork(this);
        if(netState.equals(Network.NONE_STATE)){
            Network.connectNetwork(this);
        }else {
            if(searchText.length() > 1) { //검색 글자 2자 이상
                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("no", String.valueOf(myAccount.getIndex()));
                postData.put("mail", searchText);

                PostDB postDB = new PostDB(this, "불러오는중");
                postDB.putData("select_search_member.php", postData, new OnFinishDBListener() {
                    @Override
                    public void onSuccess(String output) {
                        Log.i("select_search_member", "output: " + output);
                        try {
                            JSONObject reader = new JSONObject(output);
                            JSONArray objects = reader.getJSONArray("member");
                            addFriendListViewAdapter.removeAll();
                            for (int i = 0; i < objects.length(); i++) {
                                JSONObject object = objects.getJSONObject(i);
                                AddFriendListViewItem item = new AddFriendListViewItem(); //약속 시간등 정보
                                item.no = Integer.valueOf(object.getString("no")); //계정 고유 번호
                                item.mail = object.getString("mail"); //메일
                                item.name = object.getString("name"); //이름
                                item.type = object.getString("type"); //계정 유형
                                item.state = object.getString("state"); //친구 신청 상태
                                addFriendListViewAdapter.addItem(item);
                            }
                            addFriendListViewAdapter.notifyDataSetChanged();
                            if (addFriendListViewAdapter.getCount() == 0) {
                                textView_noFind.setVisibility(View.VISIBLE);
                            } else {
                                textView_noFind.setVisibility(View.INVISIBLE);
                                myListView.setSelectionFromTop(0, 0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        hideSoftKeyboard();
                    }
                });
            }else{ //검색 글자 2자 미만
                Toast.makeText(this, "2자 이상 입력하세요", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        searchView.clearFocus();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        addFriendListViewAdapter.notifyDataSetChanged();
    }
}
