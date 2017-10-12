package com.example.kimdongun.promise.Promise;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.kimdongun.promise.AddPromiseSocialListViewAdapter;
import com.example.kimdongun.promise.AddPromiseSocialListViewItem;
import com.example.kimdongun.promise.R;
import com.example.kimdongun.promise.serverDB.OnFinishDBListener;
import com.example.kimdongun.promise.serverDB.PostDB;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import static com.example.kimdongun.promise.Account.myAccount;

/**
 * Created by KimDongun on 2016-12-30.
 */

public class FragmentAddSocial extends Fragment {

    private ListView listView;
    private AddPromiseSocialListViewAdapter addPromiseSocialListViewAdapter;

    private SearchView searchView;
    private String searchText;

    public FragmentAddSocial() {
        // Required empty public constructor
    }

    @Override
    public String toString() {
        return "FragmentAddSocial";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_add_social, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        //DB에 보낼 데이타
        HashMap<String, String> postData = new HashMap<String, String>();
        postData.put("no", String.valueOf(myAccount.getIndex())); //계정 번호

        //DB에 약속 추가
        PostDB postDB = new PostDB(getActivity());
        postDB.putData("load_friend.php", postData, new OnFinishDBListener() {
            @Override
            public void onSuccess(String output) {
                try {
                    JSONObject reader = new JSONObject(output);
                    JSONArray friend_obj = reader.getJSONArray("friend");
                    addPromiseSocialListViewAdapter = new AddPromiseSocialListViewAdapter(getActivity());
                    //친구목록
                    for (int i = 0; i < friend_obj.length(); i++) {
                        JSONObject object = friend_obj.getJSONObject(i);
                        AddPromiseSocialListViewItem item = new AddPromiseSocialListViewItem();
                        item.no = Integer.valueOf(object.getString("no")); //친구 계정 번호
                        item.mail = object.getString("mail"); //메일
                        item.name = object.getString("name"); //이름
                        item.type = object.getString("type"); //계정 유형
                        for(int j = 0; j < ((AddActivity)getActivity()).friend.size(); j++){
                            AddPromiseSocialListViewItem temp_item = ((AddActivity)getActivity()).friend.get(j);
                            if(item.no == temp_item.no){
                                item.isCheck = true;
                            }
                        }
                        String state = object.getString("state"); //친구 상태(친구, 요청, 수락, 남남)
                        if(state.equals("friend")) {
                            addPromiseSocialListViewAdapter.addItem(item);
                        }
                    }
                    addPromiseSocialListViewAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                initFrag(view);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add_social, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add: //친구 등록 버튼
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                alert_confirm.setMessage("선택한 친구를 등록 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((AddActivity)getActivity()).friend.clear();
                                for(int i = 0; i < addPromiseSocialListViewAdapter.getRealCount(); i++){
                                    AddPromiseSocialListViewItem item = (AddPromiseSocialListViewItem)addPromiseSocialListViewAdapter.getItem(i);
                                    if(item.isCheck) {
                                        ((AddActivity) getActivity()).friend.add(item);
                                    }
                                }
                                ((AddActivity)getActivity()).switchFragment("FragmentAddMain");
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
        return super.onOptionsItemSelected(item);
    }

    public void initFrag(View view){
        listView = (ListView)view.findViewById(R.id.listView);

        listView.setAdapter(addPromiseSocialListViewAdapter);

        searchText = "";
        searchView = (SearchView)view.findViewById(R.id.searchView);
        addPromiseSocialListViewAdapter.filterItem(searchText);

        //검색 이벤트
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                addPromiseSocialListViewAdapter.filterItem(searchText);
                return false;
            }
        });
    }
}