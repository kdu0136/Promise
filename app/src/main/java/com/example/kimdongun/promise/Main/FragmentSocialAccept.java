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

public class FragmentSocialAccept extends Fragment implements AdapterView.OnItemClickListener{

    private ListView listView;
    public TextView textView_noFind;
    private ContentFriendDialog contentItemDialog;

    public FragmentSocialAccept() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_social_accept, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((FragmentSocial)getParentFragment()).mSocialPagerAdapter.setPageTitle(2, "1213");

        listView = (ListView)view.findViewById(R.id.listView);
        listView.setAdapter(myAccount.accept_adapter);
        listView.setOnItemClickListener(this);
        textView_noFind = (TextView)view.findViewById(R.id.textView_noFind);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
        if(parent.equals(listView)) {
            int no = ((AddFriendListViewItem) myAccount.accept_adapter.getItem(index)).no;
            Log.d("Click_NO", "no: "+no);
            contentItemDialog = new ContentFriendDialog(getActivity(), myAccount.accept_adapter.getItemAt(no));
            contentItemDialog.show();
        }
    }
}