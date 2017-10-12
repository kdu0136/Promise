package com.example.kimdongun.promise.Main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kimdongun.promise.AddFriendListViewItem;
import com.example.kimdongun.promise.ContentFriendDialog;
import com.example.kimdongun.promise.R;

import static com.example.kimdongun.promise.Account.myAccount;

/**
 * Created by KimDongun on 2016-12-30.
 */

public class FragmentSocialList extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener{

    private ListView listView;
    public TextView textView_noFind;
    private ContentFriendDialog contentItemDialog;

    public FragmentSocialList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_social_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((FragmentSocial)getParentFragment()).mSocialPagerAdapter.setPageTitle(0, "친구 목록" + myAccount.main_adapter.getCount());

        //초기화
        listView = (ListView)view.findViewById(R.id.listView);
        textView_noFind = (TextView)view.findViewById(R.id.textView_noFind);

        listView.setAdapter(myAccount.main_adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
        if(parent.equals(listView)) {
            int no = ((AddFriendListViewItem) myAccount.main_adapter.getItem(index)).no;
            Log.d("Click_NO", "no: "+no);
            contentItemDialog = new ContentFriendDialog(getActivity(), myAccount.main_adapter.getItemAt(no));
            contentItemDialog.show();
        }
    }
}