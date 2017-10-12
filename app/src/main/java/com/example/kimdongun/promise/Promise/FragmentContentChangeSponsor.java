package com.example.kimdongun.promise.Promise;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kimdongun.promise.Main.MainActivity;
import com.example.kimdongun.promise.PromiseSocialListViewAdapter;
import com.example.kimdongun.promise.PromiseSocialListViewItem;
import com.example.kimdongun.promise.R;
import com.example.kimdongun.promise.serverDB.OnFinishDBListener;
import com.example.kimdongun.promise.serverDB.PostDB;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import static com.example.kimdongun.promise.Account.myAccount;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by KimDongun on 2016-12-30.
 */

public class FragmentContentChangeSponsor extends Fragment implements AdapterView.OnItemClickListener{

    private ListView listView;
    private PromiseSocialListViewAdapter adapter;

    public FragmentContentChangeSponsor() {
        // Required empty public constructor
    }
    @Override
    public String toString() {
        return "FragmentContentChangeSponsor";
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

        listView.setOnItemClickListener(this);

        HashMap<String, String> postData = new HashMap<>();
        postData.put("no", String.valueOf(((ContentActivity)getActivity()).index));
        postData.put("m_no", String.valueOf(myAccount.getIndex()));

        PostDB postDB = new PostDB(getActivity());
        postDB.putData("load_promise_social.php", postData, new OnFinishDBListener() {
            @Override
            public void onSuccess(String output) {
                if(output.equals("null")){
                    Toast.makeText(getApplicationContext(),"잘못된 접근입니다.",Toast.LENGTH_LONG).show();
                    myAccount.load_Data();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }else {
                    try {
                        JSONObject reader = new JSONObject(output);
                        JSONArray objects = reader.getJSONArray("social");
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
                            if (item.locationON == 0) { //실시간 위치 공유 꺼진 계정? -> 출발지를 장소로
                                lat = Double.valueOf(object.getString("lat_s"));
                                lon = Double.valueOf(object.getString("lon_s"));
                            }
                            item.lat = lat;
                            item.lon = lon;

                            int isStart = Integer.valueOf(object.getString("isStart"));
                            int isAccept = Integer.valueOf(object.getString("isAccept"));
                            if (isAccept == 0) { //수락 대기
                                item.state = "수락 대기";
                            } else {
                                if (isStart == 0) { //미 출발
                                    item.state = "미 출발";
                                } else { //출발
                                    item.state = "출발";
                                }
                            }
                            adapter.addItem(item);
                        }
                        adapter.notifyDataSetChanged();

                        if (adapter.getCount() < 2) { //약속 인원이 2명 미만
                            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                            alert_confirm.setMessage("약속 인원이 2명 미만입니다\n" +
                                    "약속을 파기 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //확인
                                            ((ContentActivity)getActivity()).deleteAll();
                                        }
                                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //취소
                                    ((ContentActivity) getActivity()).switchFragment("FragmentContentMain");
                                }
                            });
                            AlertDialog alert = alert_confirm.create();
                            alert.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
        if(parent.equals(listView)) {
            final PromiseSocialListViewItem item = ((PromiseSocialListViewItem) adapter.getItem(index));

            if(item.state.equals("수락 대기")){ //수락 대기 인원
                Toast.makeText(getActivity(), "약속을 수락한 인원에게만 주최자를 양도할 수 있습니다.", Toast.LENGTH_LONG).show();
            }else{ //수락한 인원
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                alert_confirm.setMessage("'"+item.mail+"'님에게 주최자를\n양도 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((ContentActivity)getActivity()).changeSponsor(item);
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
}