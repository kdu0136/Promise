package com.example.kimdongun.promise.Promise;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kimdongun.promise.AddFriendListViewItem;
import com.example.kimdongun.promise.ContentFriendDialog;
import com.example.kimdongun.promise.PromiseSocialListViewAdapter;
import com.example.kimdongun.promise.PromiseSocialListViewItem;
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

public class FragmentAcceptSocial extends Fragment implements AdapterView.OnItemClickListener{

    private ListView listView;
    public TextView textView_no_friend;
    private PromiseSocialListViewAdapter adapter;

    public FragmentAcceptSocial() {
        // Required empty public constructor
    }
    @Override
    public String toString() {
        return "FragmentAcceptSocial";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_change_sponsor, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new PromiseSocialListViewAdapter(getActivity());
        listView = (ListView)view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        textView_no_friend = (TextView)view.findViewById(R.id.textView_no_friend);

        listView.setOnItemClickListener(this);

        HashMap<String, String> postData = new HashMap<>();
        postData.put("no", String.valueOf(((AcceptPromiseActivity)getActivity()).index));
        postData.put("m_no", String.valueOf(myAccount.getIndex()));

        PostDB postDB = new PostDB(getActivity());
        postDB.putData("load_promise_social.php", postData, new OnFinishDBListener() {
            @Override
            public void onSuccess(String output) {
                try {
                    JSONObject reader = new JSONObject(output);
                    JSONArray objects = reader.getJSONArray("social");
                    adapter.removeAll();
                    for (int i = 0; i < objects.length(); i++) {
                        JSONObject object = objects.getJSONObject(i);
                        PromiseSocialListViewItem item = new PromiseSocialListViewItem(); //약속 시간등 정보
                        item.no = Integer.valueOf(object.getString("no"));
                        item.mail = object.getString("mail");
                        item.name = object.getString("name");
                        item.type = object.getString("type");
                        double lat = Double.valueOf(object.getString("lat"));
                        double lon = Double.valueOf(object.getString("lon"));
                        item.locationON = Integer.valueOf(object.getString("locationON"));
                        if(item.locationON == 0){ //실시간 위치 공유 꺼진 계정? -> 출발지를 장소로
                            lat = Double.valueOf(object.getString("lat_s"));
                            lon = Double.valueOf(object.getString("lon_s"));
                        }
                        item.lat = lat;
                        item.lon = lon;
                        int isStart = Integer.valueOf(object.getString("isStart"));
                        int isAccept = Integer.valueOf(object.getString("isAccept"));
                        if(isAccept == 0) { //수락 대기
                            item.state = "수락 대기";
                        }else{
                            if(isStart == 0){ //미 출발
                                item.state = "미 출발";
                            }else{ //출발
                                item.state = "출발";
                            }
                        }
                        adapter.addItem(item);
                    }
                    adapter.notifyDataSetChanged();
                    if (adapter.getCount() == 0) {
                        textView_no_friend.setVisibility(View.VISIBLE);
                    } else {
                        textView_no_friend.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
        if(parent.equals(listView)) {
            final PromiseSocialListViewItem item = ((PromiseSocialListViewItem) adapter.getItem(index));
            HashMap<String, String> postData = new HashMap<>();
            postData.put("no", String.valueOf(myAccount.getIndex()));
            postData.put("f_no", String.valueOf(item.no));

            PostDB postDB = new PostDB(getActivity());
            postDB.prograssBar = false;
            postDB.putData("select_friend_state.php", postData, new OnFinishDBListener() {
                @Override
                public void onSuccess(String output) {
                    AddFriendListViewItem item2 = new AddFriendListViewItem(item.mail, item.name, item.type, item.no, output);
                    ContentFriendDialog contentItemDialog = new ContentFriendDialog(getActivity(), item2);
                    contentItemDialog.show();
                }
            });

        }
    }
}