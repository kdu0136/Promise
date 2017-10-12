package com.example.kimdongun.promise.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kimdongun.promise.Network.Network;
import com.example.kimdongun.promise.Promise.AddActivity;
import com.example.kimdongun.promise.R;
import com.example.kimdongun.promise.ViewPagerAdapter;

import static com.example.kimdongun.promise.Account.myAccount;

/**
 * Created by KimDongun on 2016-12-30.
 */

public class FragmentPromise extends Fragment implements View.OnClickListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    public ViewPagerAdapter mSocialPagerAdapter;

    private FloatingActionButton add_btn; //추가 버튼

    private PromiseTextThread thread;
    private PromiseTextHandler handler;

    public int start_page;

    public FragmentPromise() {
        // Required empty public constructor
        start_page = 0;
    }
    @Override
    public String toString() {
        return "FragmentPromise";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_promise, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        Log.i("test", "Fragment Created");

        getActivity().setTitle("약속 목록");

        add_btn = (FloatingActionButton)view.findViewById(R.id.add_btn);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mSocialPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        mSocialPagerAdapter.addFragment(new FragmentPromiseList(), "약속 목록");
        mSocialPagerAdapter.addFragment(new FragmentPromiseAccept(), "받은 요청");
        viewPager.setAdapter(mSocialPagerAdapter);

        tabLayout = (TabLayout)view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setCustomView(R.layout.tab_wiget);
        View view_tab0 = tabLayout.getTabAt(0).getCustomView();
        TextView textView_tab0 = (TextView)view_tab0.findViewById(R.id.textView_tab_text);
        textView_tab0.setText("약속 목록");

        tabLayout.getTabAt(1).setCustomView(R.layout.tab_wiget);
        View view_tab = tabLayout.getTabAt(1).getCustomView();
        TextView textView_tab = (TextView)view_tab.findViewById(R.id.textView_tab_text);
        textView_tab.setText("받은 요청");

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(position == 1){
                    String netState = Network.getWhatKindOfNetwork(getActivity());
                    if(netState.equals(Network.NONE_STATE)){
                        Network.connectNetwork(getActivity());
                    }else {
                        myAccount.load_request();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        viewPager.setCurrentItem(start_page, true);

        handler = new PromiseTextHandler(getActivity());
        thread = new PromiseTextThread(handler);
        thread.start();

        //터치 이벤트
        add_btn.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        thread.stopForever();
        super.onDestroyView();
        Log.i("test", "Fragment Destroy");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:
                Intent t_intent = new Intent(getActivity(), AddActivity.class);
                startActivity(t_intent);
                getActivity().finish();
                //Toast.makeText(getApplicationContext(),"터치",Toast.LENGTH_LONG).show();
                //Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                break;
        }
    }

    class PromiseTextThread extends Thread {
        public Handler handler;
        boolean isRun = true;

        public PromiseTextThread(Handler handler){
            this.handler = handler;
        }

        public void stopForever(){
            synchronized (this){
                this.isRun = false;
            }
        }

        public void run(){
            //반복으로 수행할 작업
            Log.d("PromiseTextThread", "시작");
            while (isRun){
                handler.sendEmptyMessage(0);
                try {
                    Thread.sleep(200);
                }catch (Exception e){}
            }
            Log.d("PromiseTextThread", "종료");
        }
    }
    class PromiseTextHandler extends Handler {
        Context context;
        public PromiseTextHandler(Context context){
            this.context = context;
        }
        @Override
        public void handleMessage(Message msg) {
            if(((FragmentPromiseList)mSocialPagerAdapter.getItem(0)).textView_noFind != null) {
                if (myAccount.listViewAdapter.getCount() > 0) {
                    ((FragmentPromiseList) mSocialPagerAdapter.getItem(0)).textView_noFind.setVisibility(View.INVISIBLE);
                } else {
                    ((FragmentPromiseList) mSocialPagerAdapter.getItem(0)).textView_noFind.setVisibility(View.VISIBLE);
                }
            }

            if(((FragmentPromiseAccept)mSocialPagerAdapter.getItem(1)).textView_no_request != null) {
                if (myAccount.listViewAcceptAdapter.getCount() > 0) { //받은 요청 있는 경우
                    View view_tab = tabLayout.getTabAt(1).getCustomView();
                    ImageView imageView_tab = (ImageView)view_tab.findViewById(R.id.textView_tab_img);
                    imageView_tab.setVisibility(View.VISIBLE);
                    ((FragmentPromiseAccept) mSocialPagerAdapter.getItem(1)).textView_no_request.setVisibility(View.INVISIBLE);
                } else { //받은 요청 없 경우
                    View view_tab = tabLayout.getTabAt(1).getCustomView();
                    ImageView imageView_tab = (ImageView)view_tab.findViewById(R.id.textView_tab_img);
                    imageView_tab.setVisibility(View.INVISIBLE);
                    ((FragmentPromiseAccept) mSocialPagerAdapter.getItem(1)).textView_no_request.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}