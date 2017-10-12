package com.example.kimdongun.promise.Tutorial;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.kimdongun.promise.Main.MainActivity;
import com.example.kimdongun.promise.Promise.AddActivity;
import com.example.kimdongun.promise.Promise.ContentActivity;
import com.example.kimdongun.promise.R;
import com.example.kimdongun.promise.Social.AddFriendActivity;
import com.example.kimdongun.promise.ViewPagerTutorialAdapter;
import com.example.kimdongun.promise.serverDB.OnFinishDBListener;
import com.example.kimdongun.promise.serverDB.PostDB;

import java.util.HashMap;

import static com.example.kimdongun.promise.Account.myAccount;

public class TutorialActivity extends AppCompatActivity {

    public String type_tutorial; //어떤 튜토리얼 화면인지
    public int index; //약속 확인 경우 다시 돌아갈 인덱스

    private ViewPager view_pager;
    private ViewPagerTutorialAdapter adapter;

    private LinearLayout layout_circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        Intent intent = getIntent();
        type_tutorial = intent.getStringExtra("type");
        Log.d("type", type_tutorial);

        view_pager = (ViewPager) findViewById(R.id.view_pager);
        adapter = new ViewPagerTutorialAdapter(getSupportFragmentManager());

        layout_circle = (LinearLayout)findViewById(R.id.layout_circle);

        if(type_tutorial.equals("tuto_promise")){ //약속 목록 튜토리얼
            FragmentTutorialStart tutorial_start = new FragmentTutorialStart();
            tutorial_start.type_tutorial = "약속 목록";
            tutorial_start.drawable = getResources().getDrawable(R.drawable.tuto_promise_s);
            adapter.addFragment(tutorial_start);

            FragmentTutorial tutorial1 = new FragmentTutorial();
            tutorial1.drawable = getResources().getDrawable(R.drawable.tuto_promise_1);
            adapter.addFragment(tutorial1);

            FragmentTutorial tutorial2 = new FragmentTutorial();
            tutorial2.drawable = getResources().getDrawable(R.drawable.tuto_promise_2);
            adapter.addFragment(tutorial2);

            FragmentTutorial tutorial3 = new FragmentTutorial();
            tutorial3.drawable = getResources().getDrawable(R.drawable.tuto_promise_3);
            adapter.addFragment(tutorial3);

            FragmentTutorialEnd tutorial_end = new FragmentTutorialEnd();
            tutorial_end.type_tutorial = "약속 목록";
            tutorial_end.drawable = getResources().getDrawable(R.drawable.tuto_promise_e);
            adapter.addFragment(tutorial_end);

        }else if(type_tutorial.equals("tuto_social")){ //친구 화면 튜토리얼
            FragmentTutorialStart tutorial_start = new FragmentTutorialStart();
            tutorial_start.type_tutorial = "친구 목록";
            tutorial_start.drawable = getResources().getDrawable(R.drawable.tuto_social_s);
            adapter.addFragment(tutorial_start);

            FragmentTutorial tutorial1 = new FragmentTutorial();
            tutorial1.drawable = getResources().getDrawable(R.drawable.tuto_social_1);
            adapter.addFragment(tutorial1);

            FragmentTutorial tutorial2 = new FragmentTutorial();
            tutorial2.drawable = getResources().getDrawable(R.drawable.tuto_social_2);
            adapter.addFragment(tutorial2);

            FragmentTutorialEnd tutorial_end = new FragmentTutorialEnd();
            tutorial_end.type_tutorial = "친구 목록";
            tutorial_end.drawable = getResources().getDrawable(R.drawable.tuto_social_e);
            adapter.addFragment(tutorial_end);

        }else if(type_tutorial.equals("tuto_add_social")){ //친구 추가 튜토리얼
            FragmentTutorialStart tutorial_start = new FragmentTutorialStart();
            tutorial_start.type_tutorial = "친구 추가";
            tutorial_start.drawable = getResources().getDrawable(R.drawable.tuto_add_social_s);
            adapter.addFragment(tutorial_start);

            FragmentTutorial tutorial1 = new FragmentTutorial();
            tutorial1.drawable = getResources().getDrawable(R.drawable.tuto_add_social_1);
            adapter.addFragment(tutorial1);

            FragmentTutorial tutorial2 = new FragmentTutorial();
            tutorial2.drawable = getResources().getDrawable(R.drawable.tuto_add_social_2);
            adapter.addFragment(tutorial2);

            FragmentTutorial tutorial3 = new FragmentTutorial();
            tutorial3.drawable = getResources().getDrawable(R.drawable.tuto_add_social_3);
            adapter.addFragment(tutorial3);

            FragmentTutorial tutorial4 = new FragmentTutorial();
            tutorial4.drawable = getResources().getDrawable(R.drawable.tuto_add_social_4);
            adapter.addFragment(tutorial4);

            FragmentTutorialEnd tutorial_end = new FragmentTutorialEnd();
            tutorial_end.type_tutorial = "친구 추가";
            tutorial_end.drawable = getResources().getDrawable(R.drawable.tuto_add_social_e);
            adapter.addFragment(tutorial_end);

        }else if(type_tutorial.equals("tuto_add_promise")){ //약속 추가 튜토리얼
            FragmentTutorialStart tutorial_start = new FragmentTutorialStart();
            tutorial_start.type_tutorial = "약속 추가";
            tutorial_start.drawable = getResources().getDrawable(R.drawable.tuto_add_promise_s);
            adapter.addFragment(tutorial_start);

            FragmentTutorial tutorial1 = new FragmentTutorial();
            tutorial1.drawable = getResources().getDrawable(R.drawable.tuto_add_promise_1);
            adapter.addFragment(tutorial1);

            FragmentTutorial tutorial2 = new FragmentTutorial();
            tutorial2.drawable = getResources().getDrawable(R.drawable.tuto_add_promise_2);
            adapter.addFragment(tutorial2);

            FragmentTutorial tutorial3 = new FragmentTutorial();
            tutorial3.drawable = getResources().getDrawable(R.drawable.tuto_add_promise_3);
            adapter.addFragment(tutorial3);

            FragmentTutorial tutorial4 = new FragmentTutorial();
            tutorial4.drawable = getResources().getDrawable(R.drawable.tuto_add_promise_4);
            adapter.addFragment(tutorial4);

            FragmentTutorial tutorial5 = new FragmentTutorial();
            tutorial5.drawable = getResources().getDrawable(R.drawable.tuto_add_promise_5);
            adapter.addFragment(tutorial5);

            FragmentTutorial tutorial6 = new FragmentTutorial();
            tutorial6.drawable = getResources().getDrawable(R.drawable.tuto_add_promise_6);
            adapter.addFragment(tutorial6);

            FragmentTutorial tutorial7 = new FragmentTutorial();
            tutorial7.drawable = getResources().getDrawable(R.drawable.tuto_add_promise_7);
            adapter.addFragment(tutorial7);

            FragmentTutorialEnd tutorial_end = new FragmentTutorialEnd();
            tutorial_end.type_tutorial = "약속 추가";
            tutorial_end.drawable = getResources().getDrawable(R.drawable.tuto_add_promise_e);
            adapter.addFragment(tutorial_end);

        }else if(type_tutorial.equals("tuto_content_promise")){ //약속 확인 튜토리얼
            index = intent.getIntExtra("index", 0);

            FragmentTutorialStart tutorial_start = new FragmentTutorialStart();
            tutorial_start.type_tutorial = "약속 확인";
            tutorial_start.drawable = getResources().getDrawable(R.drawable.tuto_content_promise_s);
            adapter.addFragment(tutorial_start);

            FragmentTutorial tutorial1 = new FragmentTutorial();
            tutorial1.drawable = getResources().getDrawable(R.drawable.tuto_content_promise_1);
            adapter.addFragment(tutorial1);

            FragmentTutorial tutorial2 = new FragmentTutorial();
            tutorial2.drawable = getResources().getDrawable(R.drawable.tuto_content_promise_2);
            adapter.addFragment(tutorial2);

            FragmentTutorial tutorial3 = new FragmentTutorial();
            tutorial3.drawable = getResources().getDrawable(R.drawable.tuto_content_promise_3);
            adapter.addFragment(tutorial3);

            FragmentTutorial tutorial4 = new FragmentTutorial();
            tutorial4.drawable = getResources().getDrawable(R.drawable.tuto_content_promise_4);
            adapter.addFragment(tutorial4);

            FragmentTutorial tutorial5 = new FragmentTutorial();
            tutorial5.drawable = getResources().getDrawable(R.drawable.tuto_content_promise_5);
            adapter.addFragment(tutorial5);

            FragmentTutorial tutorial6 = new FragmentTutorial();
            tutorial6.drawable = getResources().getDrawable(R.drawable.tuto_content_promise_6);
            adapter.addFragment(tutorial6);

            FragmentTutorial tutorial7 = new FragmentTutorial();
            tutorial7.drawable = getResources().getDrawable(R.drawable.tuto_content_promise_7);
            adapter.addFragment(tutorial7);

            FragmentTutorial tutorial8 = new FragmentTutorial();
            tutorial8.drawable = getResources().getDrawable(R.drawable.tuto_content_promise_8);
            adapter.addFragment(tutorial8);

            FragmentTutorialEnd tutorial_end = new FragmentTutorialEnd();
            tutorial_end.type_tutorial = "약속 확인";
            tutorial_end.drawable = getResources().getDrawable(R.drawable.tuto_content_promise_e);
            adapter.addFragment(tutorial_end);

        }
        view_pager.setAdapter(adapter);

        for(int i = 0; i < adapter.getCount(); i++){
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            if(i == 0){
                iv.setBackgroundResource(R.drawable.page_on);
            }else{
                iv.setBackgroundResource(R.drawable.page_off);
            }
            layout_circle.addView(iv);
        }

        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for(int i = 0; i < layout_circle.getChildCount(); i++){
                    if(i == position){
                        layout_circle.getChildAt(i).setBackgroundResource(R.drawable.page_on);
                    }else{
                        layout_circle.getChildAt(i).setBackgroundResource(R.drawable.page_off);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onBackPressed() { //뒤로 버튼
    }

    public void endTutorial(){
        HashMap<String, String> postData = new HashMap<>();
        postData.put("no", String.valueOf(myAccount.getIndex()));
        postData.put("type_tutorial", type_tutorial);

        PostDB postDB = new PostDB();
        postDB.putData("update_tutorial.php", postData, new OnFinishDBListener() {
            @Override
            public void onSuccess(String output) {
                Intent intent = null;
                if(type_tutorial.equals("tuto_promise")) { //약속 목록 튜토리얼
                    myAccount.tuto_promise = 1;
                    intent = new Intent(TutorialActivity.this, MainActivity.class);
                    intent.putExtra("id", R.id.promise);
                    intent.putExtra("start_page", 0);
                }else if(type_tutorial.equals("tuto_social")){ //친구 목록 튜토리얼
                    myAccount.tuto_social = 1;
                    intent = new Intent(TutorialActivity.this, MainActivity.class);
                    intent.putExtra("id", R.id.social);
                    intent.putExtra("start_page", 0);
                }else if(type_tutorial.equals("tuto_add_social")){ //친구 추가 튜토리얼
                    myAccount.tuto_add_social = 1;
                    intent = new Intent(TutorialActivity.this, AddFriendActivity.class);
                }else if(type_tutorial.equals("tuto_add_promise")){ //약속 추가 튜토리얼
                    myAccount.tuto_add_promise = 1;
                    intent = new Intent(TutorialActivity.this, AddActivity.class);
                }else if(type_tutorial.equals("tuto_content_promise")){ //약속 확인 튜토리얼
                    myAccount.tuto_content_promise = 1;
                    intent = new Intent(TutorialActivity.this, ContentActivity.class);
                    intent.putExtra("index", index);
                }
                SharedPreferences pref = getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt(type_tutorial, 1);
                editor.commit();

                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }
}

