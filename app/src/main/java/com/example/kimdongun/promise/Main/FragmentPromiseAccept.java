package com.example.kimdongun.promise.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kimdongun.promise.Promise.AcceptPromiseActivity;
import com.example.kimdongun.promise.ListViewItem;
import com.example.kimdongun.promise.Network.Network;
import com.example.kimdongun.promise.R;

import static com.example.kimdongun.promise.Account.myAccount;

/**
 * Created by KimDongun on 2016-12-30.
 */

public class FragmentPromiseAccept extends Fragment implements AdapterView.OnItemClickListener{

    private ListView listView;
    public TextView textView_no_request;

    public FragmentPromiseAccept() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_promise_accept, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (ListView)view.findViewById(R.id.listView);
        listView.setAdapter(myAccount.listViewAcceptAdapter);
        textView_no_request = (TextView)view.findViewById(R.id.textView_no_request);

        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
        String netState = Network.getWhatKindOfNetwork(getActivity());
        if(netState.equals(Network.NONE_STATE)){
            Network.connectNetwork(getActivity());
        }else {
            ListViewItem item = (ListViewItem) myAccount.listViewAcceptAdapter.getItem(index);
            Intent intent = new Intent(getActivity(), AcceptPromiseActivity.class);
            intent.putExtra("index", item.index);
            startActivity(intent);
            getActivity().finish();
        }
    }
}