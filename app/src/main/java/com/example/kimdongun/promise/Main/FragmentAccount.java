package com.example.kimdongun.promise.Main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kimdongun.promise.Network.Network;
import com.example.kimdongun.promise.R;
import com.example.kimdongun.promise.serverDB.OnFinishDBListener;
import com.example.kimdongun.promise.serverDB.PostDB;

import java.util.HashMap;

import static com.example.kimdongun.promise.Account.myAccount;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by KimDongun on 2016-12-30.
 */

public class FragmentAccount extends Fragment implements View.OnClickListener{

    private TextView textView_type;
    private TextView textView_email;
    private EditText editText_name;
    private Button button_finish;

    private Switch switch_search;
    private TextView textView_search;

    private Switch switch_location;
    private TextView textView_location;

    private Switch switch_push;
    private TextView textView_push;

    public FragmentAccount() {
        // Required empty public constructor
    }
    @Override
    public String toString() {
        return "FragmentAccount";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("계정 설정");

        //초기화
        textView_type = (TextView)view.findViewById(R.id.textView_type);
        textView_email = (TextView)view.findViewById(R.id.textView_email);
        editText_name = (EditText)view.findViewById(R.id.editText_name);
        button_finish = (Button)view.findViewById(R.id.button_finish);

        switch_search = (Switch)view.findViewById(R.id.switch_search);
        textView_search = (TextView)view.findViewById(R.id.textView_search);

        switch_location = (Switch)view.findViewById(R.id.switch_location);
        textView_location = (TextView)view.findViewById(R.id.textView_location);

        SharedPreferences pref = getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE);

        switch_push = (Switch)view.findViewById(R.id.switch_push);
        textView_push = (TextView)view.findViewById(R.id.textView_push);

        boolean setting_push = pref.getBoolean("push", true);
        switch_push.setChecked(setting_push);
        if(setting_push){
            textView_push.setText("설정");
        }else{
            textView_push.setText("해제");
        }

        textView_type.setText(myAccount.getType());
        textView_email.setText(myAccount.getMail());
        editText_name.setText(myAccount.name);

        if(myAccount.search_on == 1){
            switch_search.setChecked(true);
            textView_search.setText("설정");
        }else{
            switch_search.setChecked(false);
            textView_search.setText("해제");
        }

        if(myAccount.location_on == 1){
            switch_location.setChecked(true);
            textView_location.setText("설정");
        }else{
            switch_location.setChecked(false);
            textView_location.setText("해제");
        }

        switch_search.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                String netState = Network.getWhatKindOfNetwork(getActivity());
                if(netState.equals(Network.NONE_STATE)){
                    Network.connectNetwork(getActivity());
                    switch_search.setChecked(!isChecked);
                }else {
                    //yes
                    if(isChecked){
                        myAccount.search_on = 1;
                    }else{
                        myAccount.search_on = 0;
                    }
                    HashMap<String, String> postData = new HashMap<String, String>();
                    postData.put("no", String.valueOf(myAccount.getIndex()));
                    postData.put("name", editText_name.getText().toString());
                    postData.put("search", String.valueOf(myAccount.search_on));
                    postData.put("location", String.valueOf(myAccount.location_on));

                    PostDB postDB = new PostDB(getActivity());
                    postDB.putData("update_account.php", postData, new OnFinishDBListener() {
                        @Override
                        public void onSuccess(String output) {
                            Toast.makeText(getApplicationContext(),"수정되었습니다.",Toast.LENGTH_LONG).show();
                            myAccount.saveAccountData();
                            if(isChecked){
                                textView_search.setText("설정");
                            }else{
                                textView_search.setText("해제");
                            }
                        }
                    });
                }
            }
        });

        switch_location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                String netState = Network.getWhatKindOfNetwork(getActivity());
                if(netState.equals(Network.NONE_STATE)){
                    Network.connectNetwork(getActivity());
                    switch_location.setChecked(!isChecked);
                }else {
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                    String str;
                    if(isChecked) {
                        str = "실시간 위치 공유를 허용하시면\n" +
                                "어플을 종료하셔도\n" +
                                "1분당 약 1KB의 데이터가 소모됩니다\n" +
                                "약속이 없을 경우 데이터 절약을 위해 해제를 권장합니다";
                    }else{
                        str = "실시간 위치 공유를 해제하시면\n" +
                                "약속 목록에서 상대방이\n" +
                                "본인의 위치를 실시간으로 확인 할 수 없습니다";
                    }
                    alert_confirm.setMessage(str)
                            .setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //yes
                                    if(isChecked) {
                                        myAccount.location_on = 1;
                                    }else{
                                        myAccount.location_on = 0;
                                    }
                                    HashMap<String, String> postData = new HashMap<String, String>();
                                    postData.put("no", String.valueOf(myAccount.getIndex()));
                                    postData.put("name", editText_name.getText().toString());
                                    postData.put("search", String.valueOf(myAccount.search_on));
                                    postData.put("location", String.valueOf(myAccount.location_on));

                                    PostDB postDB = new PostDB(getActivity());
                                    postDB.putData("update_account.php", postData, new OnFinishDBListener() {
                                        @Override
                                        public void onSuccess(String output) {
                                            Toast.makeText(getApplicationContext(),"수정되었습니다.",Toast.LENGTH_LONG).show();
                                            myAccount.saveAccountData();
                                            if(isChecked) {
                                                textView_location.setText("설정");
                                            }else{
                                                textView_location.setText("해제");
                                            }
                                        }
                                    });
                                }
                            });
                    AlertDialog alert = alert_confirm.create();
                    alert.show();
                }
            }
        });

        switch_push.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                    alert_confirm.setMessage("푸시를 해제하시면\n어플이 종료되었을 때\n모든 알림을 받을 수 없습니다")
                            .setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //yes
                                }
                            });
                    AlertDialog alert = alert_confirm.create();
                    alert.show();
                }
                SharedPreferences pref = getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("push", isChecked);
                editor.commit();

                if(isChecked) {
                    textView_push.setText("설정");
                }else{
                    textView_push.setText("해제");
                }
            }
        });

        //터치 이벤트
        button_finish.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText_name.getWindowToken(), 0);
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_finish:
                if(editText_name.getText().toString().getBytes().length <= 0){
                    Toast.makeText(getActivity(), "이름을 입력하세요", Toast.LENGTH_SHORT).show();
                    break;
                }
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                alert_confirm.setMessage("'"+editText_name.getText().toString() + "'(으)로 이름을 수정 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String netState = Network.getWhatKindOfNetwork(getActivity());
                                if(netState.equals(Network.NONE_STATE)){
                                    Network.connectNetwork(getActivity());
                                }else {
                                    //yes
                                    HashMap<String, String> postData = new HashMap<String, String>();
                                    postData.put("no", String.valueOf(myAccount.getIndex()));
                                    postData.put("name", editText_name.getText().toString());
                                    postData.put("search", String.valueOf(myAccount.search_on));
                                    postData.put("location", String.valueOf(myAccount.location_on));

                                    PostDB postDB = new PostDB(getActivity());
                                    postDB.putData("update_account.php", postData, new OnFinishDBListener() {
                                        @Override
                                        public void onSuccess(String output) {
                                            Toast.makeText(getApplicationContext(),"이름이 수정되었습니다.",Toast.LENGTH_LONG).show();
                                            myAccount.name = editText_name.getText().toString();

                                            NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
                                            TextView textView_name = (TextView)navigationView.getHeaderView(0).findViewById(R.id.textView_name);
                                            textView_name.setText(myAccount.name);

                                            myAccount.saveAccountData();
                                        }
                                    });
                                }
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
    }
}