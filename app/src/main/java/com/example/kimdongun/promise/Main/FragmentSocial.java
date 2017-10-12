package com.example.kimdongun.promise.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kimdongun.promise.Social.AddFriendActivity;
import com.example.kimdongun.promise.R;
import com.example.kimdongun.promise.ViewPagerAdapter;

import static com.example.kimdongun.promise.Account.myAccount;

/**
 * Created by KimDongun on 2016-12-30.
 */

public class FragmentSocial extends Fragment{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    public ViewPagerAdapter mSocialPagerAdapter;

    private SocialTextThread thread;
    private SocialTextHandler handler;

    public int start_page;

    public FragmentSocial() {
        // Required empty public constructor
        start_page = 0;
    }
    @Override
    public String toString() {
        return "FragmentSocial";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_social, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        thread.stopForever();
        Log.i("test", "FragmentSocial Destroy");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i("test", "FragmentSocial Created");
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        getActivity().setTitle("친구 목록");

        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mSocialPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        mSocialPagerAdapter.addFragment(new FragmentSocialList(), "친구 목록");
        mSocialPagerAdapter.addFragment(new FragmentSocialRequest(), "보낸 요청");
        mSocialPagerAdapter.addFragment(new FragmentSocialAccept(), "받은 요청");
        viewPager.setAdapter(mSocialPagerAdapter);

        tabLayout = (TabLayout)view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setCustomView(R.layout.tab_wiget);
        View view_tab0 = tabLayout.getTabAt(0).getCustomView();
        TextView textView_tab0 = (TextView)view_tab0.findViewById(R.id.textView_tab_text);
        textView_tab0.setText("친구 목록");

        tabLayout.getTabAt(1).setCustomView(R.layout.tab_wiget);
        View view_ta1 = tabLayout.getTabAt(1).getCustomView();
        TextView textView_ta1 = (TextView)view_ta1.findViewById(R.id.textView_tab_text);
        textView_ta1.setText("보낸 요청");

        tabLayout.getTabAt(2).setCustomView(R.layout.tab_wiget);
        View view_tab = tabLayout.getTabAt(2).getCustomView();
        TextView textView_tab = (TextView)view_tab.findViewById(R.id.textView_tab_text);
        textView_tab.setText("받은 요청");

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                myAccount.load_friend();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        Log.d("StartPage", "PageNum: "+start_page);
        viewPager.setCurrentItem(start_page, true);

        handler = new SocialTextHandler(getActivity());
        thread = new SocialTextThread(handler);
        thread.start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_social, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent t_intent = new Intent(getActivity(), AddFriendActivity.class);
                startActivity(t_intent);
                getActivity().finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class SocialTextThread extends Thread {
        public Handler handler;
        boolean isRun = true;

        public SocialTextThread(Handler handler){
            this.handler = handler;
        }

        public void stopForever(){
            synchronized (this){
                this.isRun = false;
            }
        }

        public void run(){
            //반복으로 수행할 작업
            Log.d("SocialTextThread", "시작");
            while (isRun){
                handler.sendEmptyMessage(0);
                try {
                    Thread.sleep(200);
                }catch (Exception e){}
            }
            Log.d("SocialTextThread", "종료");
        }
    }
    class SocialTextHandler extends Handler {
        Context context;
        public SocialTextHandler(Context context){
            this.context = context;
        }
        @Override
        public void handleMessage(Message msg) {
            //친구 목록 갱신
            if(((FragmentSocialList)mSocialPagerAdapter.getItem(0)).textView_noFind != null) {
                if (myAccount.main_adapter.getCount() > 0) {
                    ((FragmentSocialList) mSocialPagerAdapter.getItem(0)).textView_noFind.setVisibility(View.INVISIBLE);
                } else {
                    ((FragmentSocialList) mSocialPagerAdapter.getItem(0)).textView_noFind.setVisibility(View.VISIBLE);
                }
            }

            //친구 요청 갱신
            if(((FragmentSocialRequest)mSocialPagerAdapter.getItem(1)).textView_noFind != null) {
                if (myAccount.request_adapter.getCount() > 0) {
                    ((FragmentSocialRequest) mSocialPagerAdapter.getItem(1)).textView_noFind.setVisibility(View.INVISIBLE);
                } else {
                    ((FragmentSocialRequest) mSocialPagerAdapter.getItem(1)).textView_noFind.setVisibility(View.VISIBLE);
                }
            }

            //친구 수락 갱신
            if(((FragmentSocialAccept)mSocialPagerAdapter.getItem(2)).textView_noFind != null) {
                if (myAccount.accept_adapter.getCount() > 0) {
                    ((FragmentSocialAccept) mSocialPagerAdapter.getItem(2)).textView_noFind.setVisibility(View.INVISIBLE);
                } else {
                    ((FragmentSocialAccept) mSocialPagerAdapter.getItem(2)).textView_noFind.setVisibility(View.VISIBLE);
                }
            }
            //받은 요청 N표시
            if (myAccount.accept_adapter.getCount() > 0) {
                View view_tab = tabLayout.getTabAt(2).getCustomView();
                ImageView imageView_tab = (ImageView)view_tab.findViewById(R.id.textView_tab_img);
                imageView_tab.setVisibility(View.VISIBLE);
            } else {
                View view_tab = tabLayout.getTabAt(2).getCustomView();
                ImageView imageView_tab = (ImageView)view_tab.findViewById(R.id.textView_tab_img);
                imageView_tab.setVisibility(View.INVISIBLE);
            }
        }
    }
}