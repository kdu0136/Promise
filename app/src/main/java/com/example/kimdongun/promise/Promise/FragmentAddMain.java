package com.example.kimdongun.promise.Promise;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.kimdongun.promise.AddPromiseSocialListViewItem;
import com.example.kimdongun.promise.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by KimDongun on 2016-12-30.
 */

public class FragmentAddMain extends Fragment implements View.OnClickListener{

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

    private LinearLayout layout_place; //약속 장소 선택가능 레이아웃
    private LinearLayout layout_date; //약속 날짜 선택가능 레이아웃
    private LinearLayout layout_time; //약속 시간 선택가능 레이아웃
    private LinearLayout layout_start; //출발 장소 선택가능 레이아웃
    private LinearLayout layout_social; //약속 친구 선택가능 레이아웃

    private int m_year, m_month, m_day, m_hour, m_minute; //선택한 년,월,일,시,분

    public FragmentAddMain() {
        // Required empty public constructor
    }

    @Override
    public String toString() {
        return "FragmentAddMain";
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

        layout_place = (LinearLayout) view.findViewById(R.id.layout_place); //약속 장소 선택가능 레이아웃
        layout_date = (LinearLayout) view.findViewById(R.id.layout_date); //약속 날짜 선택가능 레이아웃
        layout_time = (LinearLayout) view.findViewById(R.id.layout_time); //약속 시간 선택가능 레이아웃
        layout_start = (LinearLayout) view.findViewById(R.id.layout_start); //출발 장소 선택가능 레이아웃
        layout_social = (LinearLayout) view.findViewById(R.id.layout_social); //약속 친구 선택가능 레이아웃

        //내용 초기화
        if(!((AddActivity)getActivity()).getListViewItem().content.equals("")){ //입력한 내용이 있으면 그 내용으로 초기화
            editText_content.setText(((AddActivity)getActivity()).getListViewItem().content);
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
        if(((AddActivity)getActivity()).getArrivePlace().title != ""){ //기존 입력한 장소가 있으면 세팅
            textView_place.setText(((AddActivity)getActivity()).getArrivePlace().title);
            Drawable drawable = getResources().getDrawable(R.drawable.ic_map_a1);
            imageView_arrive.setBackgroundDrawable(drawable);
        }

        GregorianCalendar calendar = new GregorianCalendar();
        //날짜 초기화
        if(((AddActivity)getActivity()).getListViewItem().dYear != -1 &&
                ((AddActivity)getActivity()).getListViewItem().dMonth != -1 &&
                ((AddActivity)getActivity()).getListViewItem().dDay != -1){ //기존 입력한 날짜가 있으면 세팅
            m_year = ((AddActivity)getActivity()).getListViewItem().dYear;
            m_month = ((AddActivity)getActivity()).getListViewItem().dMonth;
            m_day = ((AddActivity)getActivity()).getListViewItem().dDay;
            textView_date.setText(String.format("%d년 %d월 %d일", m_year,m_month + 1, m_day));
            Drawable drawable = getResources().getDrawable(R.drawable.ic_calendar1);
            imageView_date.setBackgroundDrawable(drawable);
            setDate = true;
        }else{
            m_year = calendar.get(Calendar.YEAR);
            m_month = calendar.get(Calendar.MONTH);
            m_day= calendar.get(Calendar.DAY_OF_MONTH);
        }

        //시간 초기화
        if(((AddActivity)getActivity()).getListViewItem().dHour != -1 &&
                ((AddActivity)getActivity()).getListViewItem().dMin != -1){ //기존 입력한 시간이 있으면 세팅
            m_hour = ((AddActivity)getActivity()).getListViewItem().dHour;
            m_minute = ((AddActivity)getActivity()).getListViewItem().dMin;
            textView_time.setText(String.format("%d시 %d분", m_hour, m_minute));
            Drawable drawable = getResources().getDrawable(R.drawable.ic_clock1);
            imageView_time.setBackgroundDrawable(drawable);
        }else{
            m_hour = calendar.get(Calendar.HOUR_OF_DAY);
            m_minute = calendar.get(Calendar.MINUTE);
        }

        //출발 장소 초기화
        if(((AddActivity)getActivity()).getStartPlace().title != ""){ //기존 입력한 장소가 있으면 세팅
            textView_start.setText(((AddActivity)getActivity()).getStartPlace().title);
            Drawable drawable = getResources().getDrawable(R.drawable.ic_map_s1);
            imageView_start.setBackgroundDrawable(drawable);
        }

        //친구 등록 초기화
        if(((AddActivity)getActivity()).friend.size() != 0){ //기존 등록한 친구가 있으면 세팅
            String str_firends = "";
            for(int i = 0; i < ((AddActivity)getActivity()).friend.size(); i++){
                AddPromiseSocialListViewItem item = ((AddActivity)getActivity()).friend.get(i);
                str_firends += item.name;
                if(i < ((AddActivity)getActivity()).friend.size()-1)
                    str_firends += ", ";
            }
            textView_social.setText(str_firends);
            Drawable drawable = getResources().getDrawable(R.drawable.ic_social2_1);
            imageView_social.setBackgroundDrawable(drawable);
        }

        //터치 이벤트 (약속 장소, 약속 날짜, 약속 시간, 출발 장소, 약속 친구 레이아웃)
        layout_place.setOnClickListener(this);
        layout_date.setOnClickListener(this);
        layout_time.setOnClickListener(this);
        layout_start.setOnClickListener(this);
        layout_social.setOnClickListener(this);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add: //등록 버튼 터치
                if(((AddActivity)getActivity()).getArrivePlace().title == ""){ //약속 장소를 입력 안 한 경우
                    Toast.makeText(getActivity(), "약속 장소를 설정하세요", Toast.LENGTH_SHORT).show();
                    break;
                }
                if(((AddActivity)getActivity()).getListViewItem().dYear == -1 &&
                        ((AddActivity)getActivity()).getListViewItem().dMonth == -1 &&
                        ((AddActivity)getActivity()).getListViewItem().dDay == -1){ //약속 날짜 를 입력 안 한 경우
                    Toast.makeText(getActivity(), "약속 날짜를 설정하세요", Toast.LENGTH_SHORT).show();
                    break;
                }
                if(((AddActivity)getActivity()).getListViewItem().dHour == -1 &&
                        ((AddActivity)getActivity()).getListViewItem().dMin == -1){ //약속 시간 를 입력 안 한 경우
                    Toast.makeText(getActivity(), "약속 시간을 설정하세요", Toast.LENGTH_SHORT).show();
                    break;
                }
                if(((AddActivity)getActivity()).getStartPlace().title == ""){ //출발 장소를 입력 안 한 경우
                    Toast.makeText(getActivity(), "출발 장소를 설정하세요", Toast.LENGTH_SHORT).show();
                    break;
                }
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                alert_confirm.setMessage("약속을 등록 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //AddActivity.super.onBackPressed();
                                ((AddActivity)getActivity()).getListViewItem().content = (editText_content.getText().toString());
                                ((AddActivity)getActivity()).addPromise();
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_place: //약속 장소 터치
                ((AddActivity)getActivity()).getListViewItem().content = (editText_content.getText().toString());
                ((AddActivity)getActivity()).switchFragment("FragmentAddMapA");
                break;

            case R.id.layout_date: //약속 날짜 터치
                new DatePickerDialog(getActivity(), dateSetListener, m_year, m_month, m_day).show();
                break;

            case R.id.layout_time: //약속 시간 터치
                new TimePickerDialog(getActivity(), timeSetListener, m_hour, m_minute, false).show();
                break;

            case R.id.layout_start: //출발 장소 터치
                ((AddActivity)getActivity()).getListViewItem().content = (editText_content.getText().toString());
                ((AddActivity)getActivity()).switchFragment("FragmentAddMapS");
                break;

            case R.id.layout_social: //친구 등록 터치
                ((AddActivity)getActivity()).getListViewItem().content = (editText_content.getText().toString());
                ((AddActivity)getActivity()).switchFragment("FragmentAddSocial");
                break;
        }
    }

    private boolean setDate = false; //날짜 설정 유무
    //달력 선택 다이얼로그
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            boolean isOver = false;
            GregorianCalendar calendar = new GregorianCalendar();
            if(year < calendar.get(Calendar.YEAR)) {
                isOver = true;
            }else if(year == calendar.get(Calendar.YEAR)){
                if(monthOfYear < calendar.get(Calendar.MONTH)){
                    isOver = true;
                }else if(monthOfYear == calendar.get(Calendar.MONTH)){
                    if(dayOfMonth < calendar.get(Calendar.DAY_OF_MONTH)){
                        isOver = true;
                    }
                }
            }
            if(isOver) {
                Toast.makeText(getApplicationContext(), "오늘 날짜보다 이후 날짜를 선택하세요.", Toast.LENGTH_LONG).show();
                return;
            }
            m_year = year;
            m_month = monthOfYear;
            m_day = dayOfMonth;
            ((AddActivity)getActivity()).getListViewItem().dYear = m_year;
            ((AddActivity)getActivity()).getListViewItem().dMonth = m_month;
            ((AddActivity)getActivity()).getListViewItem().dDay = m_day;

            setDate = true;
            //약속 시간 초기화
            m_hour = calendar.get(Calendar.HOUR_OF_DAY);
            m_minute = calendar.get(Calendar.MINUTE);
            ((AddActivity)getActivity()).getListViewItem().dHour = -1;
            ((AddActivity)getActivity()).getListViewItem().dMin = -1;
            textView_time.setText("약속 시간");
            Drawable drawable_t = getActivity().getResources().getDrawable(R.drawable.ic_clock);
            imageView_time.setBackgroundDrawable(drawable_t);


            textView_date.setText(String.format("%d년 %d월 %d일", m_year,m_month + 1, m_day));
            Drawable drawable = getResources().getDrawable(R.drawable.ic_calendar1);
            imageView_date.setBackgroundDrawable(drawable);
        }
    };
    //시간 선택 다이얼로그
    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // TODO Auto-generated method stub
            if(!setDate) {
                Toast.makeText(getApplicationContext(), "날짜를 먼저 선택하세요.", Toast.LENGTH_LONG).show();
                return;
            }
            GregorianCalendar calendar = new GregorianCalendar();
            if(m_year == calendar.get(Calendar.YEAR)
                    && m_month == calendar.get(Calendar.MONTH)
                    && m_day == calendar.get(Calendar.DAY_OF_MONTH)){ //날짜가 오늘인 경우
                boolean isOver = false;
                if (hourOfDay < calendar.get(Calendar.HOUR_OF_DAY)) {
                    isOver = true;
                }else if(hourOfDay == calendar.get(Calendar.HOUR_OF_DAY)){
                    if (minute < calendar.get(Calendar.MINUTE)) {
                        isOver = true;
                    }
                }
                if (isOver) {
                    Toast.makeText(getApplicationContext(), "현재 시간보다 이후 시간을 선택하세요.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            m_hour = hourOfDay;
            m_minute = minute;
            ((AddActivity) getActivity()).getListViewItem().dHour = m_hour;
            ((AddActivity) getActivity()).getListViewItem().dMin = m_minute;

            textView_time.setText(String.format("%d시 %d분", m_hour, m_minute));
            Drawable drawable = getResources().getDrawable(R.drawable.ic_clock1);
            imageView_time.setBackgroundDrawable(drawable);

        }
    };
}