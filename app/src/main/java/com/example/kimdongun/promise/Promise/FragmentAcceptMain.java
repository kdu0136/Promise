package com.example.kimdongun.promise.Promise;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kimdongun.promise.AddPromiseSocialListViewItem;
import com.example.kimdongun.promise.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by KimDongun on 2016-12-30.
 */

public class FragmentAcceptMain extends Fragment implements View.OnClickListener{

    private EditText editText_content; //약속 내용

    private TextView textView_place; //약속 장소
    private TextView textView_date; //약속 날짜
    private TextView textView_time; //약속 시간
    private TextView textView_start; //출발 장소
    private TextView textView_social; //약속 친구

    private ImageView imageView_arrive;//약속 장소 이미지
    private ImageView imageView_date;//약속 날짜 이미지
    private ImageView imageView_time;//약속 시간 이미지
    private ImageView imageView_start;//출발 장소 이미지
    private ImageView imageView_social; //친구 선택 이미지

    private LinearLayout layout_start; //출발 장소 선택가능 레이아웃
    private LinearLayout layout_place; //약속 장소 선택가능 레이아웃
    private LinearLayout layout_social; //약속 친구 선택가능 레이아웃

    private int m_year, m_month, m_day, m_hour, m_minute; //선택한 년,월,일,시,분

    public FragmentAcceptMain() {
        // Required empty public constructor
    }

    @Override
    public String toString() {
        return "FragmentAcceptMain";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_add_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        //초기화
        editText_content = (EditText)view.findViewById(R.id.editText_content); //약속 내용

        textView_place = (TextView)view.findViewById(R.id.textView_place); //약속 장소
        textView_date = (TextView)view.findViewById(R.id.textView_date); //약속 날짜
        textView_time = (TextView)view.findViewById(R.id.textView_time); //약속 시간
        textView_start = (TextView)view.findViewById(R.id.textView_start); //출발 장소
        textView_social = (TextView)view.findViewById(R.id.textView_social); //약속 친구

        imageView_arrive = (ImageView)view.findViewById(R.id.imageView_arrive);//약속 장소 이미지
        imageView_date = (ImageView)view.findViewById(R.id.imageView_date);//약속 날짜 이미지
        imageView_time = (ImageView)view.findViewById(R.id.imageView_time);//약속 시간 이미지
        imageView_start = (ImageView)view.findViewById(R.id.imageView_start);//출발 장소 이미지
        imageView_social = (ImageView)view.findViewById(R.id.imageView_social); //친구 선택 이미지

        layout_start = (LinearLayout) view.findViewById(R.id.layout_start); //출발 장소 선택가능 레이아웃
        layout_place = (LinearLayout) view.findViewById(R.id.layout_place); //도착 장소 선택가능 레이아웃
        layout_social = (LinearLayout) view.findViewById(R.id.layout_social); //약속 친구 선택가능 레이아웃

        //내용 초기화
        if(!((AcceptPromiseActivity)getActivity()).getListViewItem().content.equals("")){ //입력한 내용이 있으면 그 내용으로 초기화
            editText_content.setText(((AcceptPromiseActivity)getActivity()).getListViewItem().content);
        }
        //내용 입력 텍스트 라인 제한
        editText_content.addTextChangedListener(new TextWatcher() {
            String previousString = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousString= s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editText_content.getLineCount() >= 8) //8줄로 제한
                {
                    editText_content.setText(previousString);
                    editText_content.setSelection(editText_content.length());
                }
            }
        });

        //약속 장소 초기화
        if(((AcceptPromiseActivity)getActivity()).getArrivePlace().title != ""){ //기존 입력한 장소가 있으면 세팅
            textView_place.setText(((AcceptPromiseActivity)getActivity()).getArrivePlace().title);
            Drawable drawable = getActivity().getResources().getDrawable(R.drawable.ic_map_a1);
            imageView_arrive.setBackgroundDrawable(drawable);
        }

        GregorianCalendar calendar = new GregorianCalendar();
        //날짜 초기화
        if(((AcceptPromiseActivity)getActivity()).getListViewItem().dYear != -1 &&
                ((AcceptPromiseActivity)getActivity()).getListViewItem().dMonth != -1 &&
                ((AcceptPromiseActivity)getActivity()).getListViewItem().dDay != -1){ //기존 입력한 날짜가 있으면 세팅
            m_year = ((AcceptPromiseActivity)getActivity()).getListViewItem().dYear;
            m_month = ((AcceptPromiseActivity)getActivity()).getListViewItem().dMonth;
            m_day = ((AcceptPromiseActivity)getActivity()).getListViewItem().dDay;
            textView_date.setText(String.format("%d년 %d월 %d일", m_year,m_month + 1, m_day));
            Drawable drawable = getActivity().getResources().getDrawable(R.drawable.ic_calendar1);
            imageView_date.setBackgroundDrawable(drawable);
        }else{
            m_year = calendar.get(Calendar.YEAR);
            m_month = calendar.get(Calendar.MONTH);
            m_day= calendar.get(Calendar.DAY_OF_MONTH);
        }

        //시간 초기화
        if(((AcceptPromiseActivity)getActivity()).getListViewItem().dHour != -1 &&
                ((AcceptPromiseActivity)getActivity()).getListViewItem().dMin != -1){ //기존 입력한 시간이 있으면 세팅
            m_hour = ((AcceptPromiseActivity)getActivity()).getListViewItem().dHour;
            m_minute = ((AcceptPromiseActivity)getActivity()).getListViewItem().dMin;
            textView_time.setText(String.format("%d시 %d분", m_hour, m_minute));
            Drawable drawable = getActivity().getResources().getDrawable(R.drawable.ic_clock1);
            imageView_time.setBackgroundDrawable(drawable);
        }else{
            m_hour = calendar.get(Calendar.HOUR_OF_DAY);
            m_minute = calendar.get(Calendar.MINUTE);
        }

        //출발 장소 초기화
        if(((AcceptPromiseActivity)getActivity()).getStartPlace().title != ""){ //기존 입력한 장소가 있으면 세팅
            textView_start.setText(((AcceptPromiseActivity)getActivity()).getStartPlace().title);
            Drawable drawable = getActivity().getResources().getDrawable(R.drawable.ic_map_s1);
            imageView_start.setBackgroundDrawable(drawable);
        }

        //친구 등록 초기화
        if(((AcceptPromiseActivity)getActivity()).friend.size() != 0){ //기존 등록한 친구가 있으면 세팅
            String str_firends = "";
            for(int i = 0; i < ((AcceptPromiseActivity)getActivity()).friend.size(); i++){
                AddPromiseSocialListViewItem item = ((AcceptPromiseActivity)getActivity()).friend.get(i);
                str_firends += item.name;
                if(i < ((AcceptPromiseActivity)getActivity()).friend.size()-1)
                    str_firends += ", ";
            }
            textView_social.setText(str_firends);
            Drawable drawable = getResources().getDrawable(R.drawable.ic_social2_1);
            imageView_social.setBackgroundDrawable(drawable);
        }

        //터치 이벤트 (출발 장소)
        layout_start.setOnClickListener(this);
        layout_place.setOnClickListener(this);
        layout_social.setOnClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_accept_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_refuse: //거절 버튼
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                alert_confirm.setMessage("약속을 거절 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((AcceptPromiseActivity)getActivity()).refusePromise();
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
            case R.id.action_add: //등록 버튼 터치
                if(((AcceptPromiseActivity)getActivity()).getArrivePlace().title == ""){ //약속 장소를 입력 안 한 경우
                    Toast.makeText(getActivity(), "약속 장소를 설정하세요", Toast.LENGTH_SHORT).show();
                    break;
                }
                if(((AcceptPromiseActivity)getActivity()).getListViewItem().dYear == -1 &&
                        ((AcceptPromiseActivity)getActivity()).getListViewItem().dMonth == -1 &&
                        ((AcceptPromiseActivity)getActivity()).getListViewItem().dDay == -1){ //약속 날짜 를 입력 안 한 경우
                    Toast.makeText(getActivity(), "약속 날짜를 설정하세요", Toast.LENGTH_SHORT).show();
                    break;
                }
                if(((AcceptPromiseActivity)getActivity()).getListViewItem().dHour == -1 &&
                        ((AcceptPromiseActivity)getActivity()).getListViewItem().dMin == -1){ //약속 시간 를 입력 안 한 경우
                    Toast.makeText(getActivity(), "약속 시간을 설정하세요", Toast.LENGTH_SHORT).show();
                    break;
                }
                if(((AcceptPromiseActivity)getActivity()).getStartPlace().title == ""){ //출발 장소를 입력 안 한 경우
                    Toast.makeText(getActivity(), "출발 장소를 설정하세요", Toast.LENGTH_SHORT).show();
                    break;
                }
                AlertDialog.Builder alert_confirm2 = new AlertDialog.Builder(getActivity());
                alert_confirm2.setMessage("약속을 수락 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((AcceptPromiseActivity)getActivity()).getListViewItem().content = (editText_content.getText().toString());
                                ((AcceptPromiseActivity)getActivity()).acceptPromise();
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert2 = alert_confirm2.create();
                alert2.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_place: //도착 장소 터치
                ((AcceptPromiseActivity)getActivity()).getListViewItem().content = (editText_content.getText().toString());
                ((AcceptPromiseActivity)getActivity()).switchFragment("FragmentAcceptMapA");
                break;
            case R.id.layout_start: //출발 장소 터치
                ((AcceptPromiseActivity)getActivity()).getListViewItem().content = (editText_content.getText().toString());
                ((AcceptPromiseActivity)getActivity()).switchFragment("FragmentAcceptMapS");
                break;
            case R.id.layout_social: //약속 친구 선택가능 레이아웃
                ((AcceptPromiseActivity)getActivity()).switchFragment("FragmentAcceptSocial");
                break;
        }
    }
}