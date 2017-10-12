package com.example.kimdongun.promise.Main;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kimdongun.promise.Promise.ContentActivity;
import com.example.kimdongun.promise.ListViewItem;
import com.example.kimdongun.promise.Network.Network;
import com.example.kimdongun.promise.R;
import com.example.kimdongun.promise.serverDB.OnFinishDBListener;
import com.example.kimdongun.promise.serverDB.PostDB;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import static com.example.kimdongun.promise.Account.myAccount;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by KimDongun on 2016-12-30.
 */

public class FragmentPromiseList extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private View layout_search; //root view
    public View textView_noFind; //검색 결과 없음

    private ListView myListView; //약속 리스트

    private SearchView searchView; //장소,내용 검색 창
    private LinearLayout searchView_date; //날짜 검색 창

    private TextView textView_sTime; //검색 시작 날짜
    private TextView textView_eTime; //검색 끝 날짜
    private int sYear, sMonth, sDay; //검색 시작 날짜
    private int eYear, eMonth, eDay; //검색 끝 날짜

    private ImageView buttonSearch; //검색 버튼
    private String searchText; //검색 텍스트
    private String searchType; //검색 옵션

    private boolean mRenewBySearch;
    private boolean mLockListView;

    public FragmentPromiseList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_promise_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        Log.i("test", "Fragment Created");

        //초기화
        layout_search = view.findViewById(R.id.layout_search);
        textView_noFind = view.findViewById(R.id.textView_noFind);
        myListView = (ListView)view.findViewById(R.id.listView);
        mLockListView = false;
        mRenewBySearch = false;
        searchView = (SearchView)view.findViewById(R.id.searchView); //장소,내용 검색 창
        searchView.setQueryHint("장소로 검색");
        searchView_date = (LinearLayout)view.findViewById(R.id.searchView_date); //날짜 검색 창
        searchView_date.setVisibility(View.INVISIBLE);
        textView_sTime = (TextView)view.findViewById(R.id.textView_sTime) ; //검색 시작 날짜
        textView_eTime = (TextView)view.findViewById(R.id.textView_eTime) ; //검색 끝 날짜
        GregorianCalendar calendar = new GregorianCalendar();
        sYear = calendar.get(Calendar.YEAR);
        sMonth = calendar.get(Calendar.MONTH);
        sDay= calendar.get(Calendar.DAY_OF_MONTH);
        eYear = calendar.get(Calendar.YEAR);
        eMonth = calendar.get(Calendar.MONTH);
        eDay= calendar.get(Calendar.DAY_OF_MONTH);
        textView_sTime.setText(dateForm(sYear, sMonth, sDay));
        textView_eTime.setText(dateForm(eYear, eMonth, eDay));

        searchText = "";  //검색 텍스트
        searchType = "place_a"; //검색 옵션
        buttonSearch = (ImageView)view.findViewById(R.id.buttonSearch); //검색 버튼

        //터치 이벤트
        buttonSearch.setOnClickListener(this);
        textView_sTime.setOnClickListener(this);
        textView_eTime.setOnClickListener(this);
        myListView.setOnItemClickListener(this);

        myListView.setOnScrollListener(this);

        //검색 이벤트
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchText = query;
                myAccount.listViewAdapter.removeAll();
                myAccount.listViewAdapter.total_data = 1;
                mLockListView = false;
                mRenewBySearch = true;
                loadListView(true, 200);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                myAccount.listViewAdapter.removeAll();
                myAccount.listViewAdapter.total_data = 1;
                mLockListView = false;
                mRenewBySearch = true;
                loadListView(false, 0);
                return false;
            }
        });

        //어뎁터 설정
        myListView.setAdapter(myAccount.listViewAdapter);

        layout_search.requestFocus();
        hideSoftKeyboard();

        if (myAccount.listViewAdapter.getCount() == 0) {
            textView_noFind.setVisibility(View.VISIBLE);
        } else {
            textView_noFind.setVisibility(View.INVISIBLE);
            if(mRenewBySearch) {
                myListView.setSelectionFromTop(0, 0);
                mRenewBySearch = false;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideSoftKeyboard();
        //listRenewThread.stopThread(); //리스트 갱신 쓰레드 종료
        Log.i("test", "Fragment Destroy");
    }

    MenuItem search_place;
    MenuItem search_content;
    MenuItem search_date;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_promise, menu);

        search_place = menu.findItem(R.id.search_place);
        search_content = menu.findItem(R.id.search_content);
        search_date = menu.findItem(R.id.search_date);

        search_place.setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search_place: //장소 검색
                searchView.setQueryHint("장소로 검색");
                searchType = "place_a";
                searchView.setVisibility(View.VISIBLE);
                searchView_date.setVisibility(View.INVISIBLE);

                search_place.setChecked(true);
                search_content.setChecked(false);
                search_date.setChecked(false);
                break;

            case R.id.search_content: //내용 검색
                searchView.setQueryHint("내용으로 검색");
                searchType = "content";
                searchView.setVisibility(View.VISIBLE);
                searchView_date.setVisibility(View.INVISIBLE);

                search_place.setChecked(false);
                search_content.setChecked(true);
                search_date.setChecked(false);
                break;

            case R.id.search_date: //날짜 검색
                searchType = "date";
                searchView.setVisibility(View.INVISIBLE);
                searchView_date.setVisibility(View.VISIBLE);

                search_place.setChecked(false);
                search_content.setChecked(false);
                search_date.setChecked(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textView_sTime: //검색 시작 날짜
                new DatePickerDialog(getActivity(), startDateSetListener, sYear, sMonth, sDay).show();
                break;

            case R.id.textView_eTime: //검색 끝 날짜
                new DatePickerDialog(getActivity(), endDateSetListener, eYear, eMonth, eDay).show();
                break;


            case R.id.buttonSearch: //검색 버튼
                if (searchText == null || searchText.length() == 0)
                    searchText = "";
                myAccount.listViewAdapter.removeAll();
                myAccount.listViewAdapter.total_data = 1;
                mLockListView = false;
                mRenewBySearch = true;
                loadListView(true, 200);
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
        String netState = Network.getWhatKindOfNetwork(getActivity());
        if(netState.equals(Network.NONE_STATE)){
            Network.connectNetwork(getActivity());
        }else {
            ListViewItem item = (ListViewItem) myAccount.listViewAdapter.getItem(index);
            Intent intent = new Intent(getActivity(), ContentActivity.class);
            intent.putExtra("index", item.index);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        Log.d("scroll", "onScrollStateChanged");
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {   // 현재 가장 처음에 보이는 셀번호와 보여지는 셀번호를 더한값이
        // 전체의 숫자와 동일해지면 가장 아래로 스크롤 되었다고 가정합니다.
        int count = totalItemCount - visibleItemCount;

        if(firstVisibleItem >= count && totalItemCount != 0 && mLockListView == false)
        {
            loadListView(true, 200);
        }
    }

    private void loadListView(boolean prograssBar, int delay){
        String netState = Network.getWhatKindOfNetwork(getActivity());
        if(netState.equals(Network.NONE_STATE)){
            Network.connectNetwork(getActivity());
        }else {
            // 아이템을 추가하는 동안 중복 요청을 방지하기 위해 락을 걸어둡니다.
            mLockListView = true;
            if (myAccount.listViewAdapter.total_data > myAccount.listViewAdapter.getCount()) {
                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("m_no", String.valueOf(myAccount.getIndex()));
                postData.put("listEndNum", String.valueOf(myAccount.listViewAdapter.getCount()));
                postData.put("searchType", searchType);
                if (searchType.equals("date")) {
                    postData.put("sYear", String.valueOf(sYear));
                    postData.put("sMonth", String.valueOf(sMonth));
                    postData.put("sDay", String.valueOf(sDay));
                    postData.put("eYear", String.valueOf(eYear));
                    postData.put("eMonth", String.valueOf(eMonth));
                    postData.put("eDay", String.valueOf(eDay));
                } else {
                    postData.put("findText", searchText);
                }

                PostDB postDB = new PostDB(getActivity(), "불러오는중", delay);
                postDB.prograssBar = prograssBar;
                if(prograssBar) {
                    hideSoftKeyboard();
                }
                postDB.putData("load_promise.php", postData, new OnFinishDBListener() {
                    @Override
                    public void onSuccess(String output) {
                        Log.i("load_promise", "output: " + output);
                        try {
                            JSONObject reader = new JSONObject(output);
                            JSONArray objects = reader.getJSONArray("promise");
                            for (int i = 0; i < objects.length(); i++) {
                                JSONObject object = objects.getJSONObject(i);
                                if (i < objects.length() - 1) {
                                    ListViewItem listViewItem = new ListViewItem(); //약속 시간등 정보
                                    listViewItem.place = object.getString("place_a"); //도착 장소 이름
                                    listViewItem.dYear = Integer.valueOf(object.getString("year")); //약속 년
                                    listViewItem.dMonth = Integer.valueOf(object.getString("month")); //약속 월
                                    listViewItem.dDay = Integer.valueOf(object.getString("day")); //약속 일\
                                    listViewItem.dHour = Integer.valueOf(object.getString("hour")); //약속 시
                                    listViewItem.dMin = Integer.valueOf(object.getString("minute")); //약속 분
                                    listViewItem.content = object.getString("content"); //약속 내용
                                    listViewItem.index = Integer.valueOf(object.getString("no")); //약속 식별자
                                    listViewItem.spon_name = object.getString("spon_name");
                                    listViewItem.spon_no = Integer.valueOf(object.getString("spon_no"));
                                    listViewItem.isStart = Integer.valueOf(object.getString("isStart"));
                                    listViewItem.alram_time = Integer.valueOf(object.getString("t_alarm"));
                                    listViewItem.isAlarm = Integer.valueOf(object.getString("isAlarm"));
                                    myAccount.listViewAdapter.addItem(listViewItem);
                                } else {
                                    myAccount.listViewAdapter.total_data = Integer.valueOf(object.getString("total_count")); //총 데이터 갯수
                                    Log.i("total_count", "total_count: " + myAccount.listViewAdapter.total_data);
                                }
                            }
                            myAccount.listViewAdapter.notifyDataSetChanged();
                            if (myAccount.listViewAdapter.getCount() == 0) {
                                textView_noFind.setVisibility(View.VISIBLE);
                            } else {
                                textView_noFind.setVisibility(View.INVISIBLE);
                                if(mRenewBySearch) {
                                    myListView.setSelectionFromTop(0, 0);
                                    mRenewBySearch = false;
                                }
                            }
                            mLockListView = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    //달력 선택 다이얼로그
    private DatePickerDialog.OnDateSetListener startDateSetListener = new DatePickerDialog.OnDateSetListener() {
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
            if(eYear < year) {
                isOver = true;
            }else if(eYear == year){
                if(eMonth < monthOfYear){
                    isOver = true;
                }else if(eMonth == monthOfYear){
                    if(eDay < dayOfMonth){
                        isOver = true;
                    }
                }
            }
            if(isOver){
                Toast.makeText(getApplicationContext(), "마지막 날짜보다 이전 날짜를 선택하세요.", Toast.LENGTH_LONG).show();
            }else {
                sYear = year;
                sMonth = monthOfYear;
                sDay = dayOfMonth;

                textView_sTime.setText(dateForm(sYear, sMonth, sDay));
            }
        }
    };
    private DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            //Toast.makeText(getApplicationContext(), "선택 날짜: "+year+"/"+monthOfYear+"/"+dayOfMonth + " 시작 날짜: "+sYear+"/"+sMonth+"/"+sDay, Toast.LENGTH_LONG).show();
            // TODO Auto-generated method stub
            boolean isOver = false;
            GregorianCalendar calendar = new GregorianCalendar();
            if(year < sYear) {
                isOver = true;
            }else if(year == sYear){
                if(monthOfYear < sMonth){
                    isOver = true;
                }else if(monthOfYear == sMonth){
                    if(dayOfMonth < sDay){
                        isOver = true;
                    }
                }
            }
            if(isOver){
                Toast.makeText(getApplicationContext(), "처음 날짜보다 이후 날짜를 선택하세요.", Toast.LENGTH_LONG).show();
            }else {
                eYear = year;
                eMonth = monthOfYear;
                eDay = dayOfMonth;

                textView_eTime.setText(dateForm(eYear, eMonth, eDay));
            }
        }
    };

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        searchView.setFocusable(false);
    }

    public String dateForm(int year, int month, int day) {
        String str = year + "/";
        if(month < 9){
            str += "0"+ (month+1);
        }else{
            str += (month+1);
        }
        str += "/";
        if(day < 10){
            str += "0"+ day;
        }else{
            str += day;
        }
        return str;
    }
}