package com.example.kimdongun.promise;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kimdongun.promise.Dday.DdayClass;

import java.util.ArrayList;

import static com.example.kimdongun.promise.R.id.textView_d_day;

/**
 * Created by KimDongun on 2016-10-03.
 */

public class ListViewAdapter extends BaseAdapter {

    public ArrayList<ListViewItem> myListItem; //아이템 리스트
    private Context myContext = null;
    private LayoutInflater myInflater;

    public int total_data;

    public ListViewAdapter(Context myContext){
        super();
        this.myContext = myContext;
        this.myListItem = new ArrayList<ListViewItem>();
        total_data = 0;

        //this.ddayThreads = new ArrayList<DdayThread>();
        //this.handlers = new ArrayList<Handler>();
    }
    @Override
    public int getCount() {
        return myListItem.size();
    }

    @Override
    public Object getItem(int index) {
        return myListItem.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }


    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder viewHolder;

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if(view == null){
            // 레이아웃을 inflate시켜 새로운 view 생성.
            myInflater = (LayoutInflater)myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = myInflater.inflate(R.layout.listview_item, parent, false);
            // Holder pattern을 위한 wrapper 초기화 (row를 base 클래스로 지정)
            viewHolder = new ViewHolder(view);
            // row에 viewWrapper object를 tag함 (나중에 row를 재활용 할때 필요)
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        // getView() 호출시 인자로 전달된 position을 이용해 현재 사용중인 RowModel의 객체 model 얻기.
        final ListViewItem item = myListItem.get(index);

        // convertView(row)내부의 TextView의 text를 객체가 저장하고 있는 String으로  설정.
        //Dday 계산
        item.D_day = DdayClass.calDday(item.dYear, item.dMonth, item.dDay);
        String st_Dday;
        { //디데이 구간
            int d_day = item.D_day;
            if(d_day > 0) { //약속 날 이전
                st_Dday = "D-" + d_day;
            }else{ //약속 당일, 지남
                int d_hour = DdayClass.calDhour(item.dYear, item.dMonth, item.dDay, item.dHour, item.dMin);
                if(d_hour < 0) { //약속 지남
                    st_Dday = "지남";
                }else{ //약속 당일
                    st_Dday = "D-day";
                }
            }
        }
        viewHolder.getD_day().setText(st_Dday); //디데이

        if(item.isStart == 0){ //약속 미 출발
            viewHolder.getIsStart().setText("미 출발");
            viewHolder.getIsStart().setTextColor(Color.BLACK);//argb(255, 255, 100, 100));
        }else if(item.isStart == 1){ //출발
            viewHolder.getIsStart().setText("출발");
            viewHolder.getIsStart().setTextColor(myContext.getResources().getColor(R.color.colorAccent));
        }else{ //미수락
            viewHolder.getIsStart().setVisibility(View.INVISIBLE);
        }

        if(item.alram_time < 0) { //미수락 아이템
            viewHolder.getAlram_time().setVisibility(View.INVISIBLE);
            viewHolder.getImageView_alram().setVisibility(View.INVISIBLE);
        }else{
            viewHolder.getAlram_time().setText(item.alram_time+"분 전");
        }

        Drawable drawable_alarm;
        if(item.isAlarm == 1) { //이미 울린 약속
            drawable_alarm = myContext.getResources().getDrawable(R.drawable.ic_alarm);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable_alarm.setTint(Color.WHITE);
            }
        }else{
            drawable_alarm = myContext.getResources().getDrawable(R.drawable.ic_alarm);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable_alarm.setTint(myContext.getResources().getColor(R.color.colorAccent));
            }
        }
        viewHolder.getImageView_alram().setBackgroundDrawable(drawable_alarm);

        if(item.content.length() != 0) //약속 내용 있는 경우
            viewHolder.getContent().setText(item.content);
        else //약속 내용 없는 경우
            viewHolder.getContent().setText("내용 없음");

        viewHolder.getPlace().setText(item.place); //약속 장소
        viewHolder.getSponsor().setText(item.spon_name); //주최자 이름
        String dDate = item.dYear + "/";
        if(item.dMonth < 9){
            dDate += "0"+ (item.dMonth+1);
        }else{
            dDate += (item.dMonth+1);
        }
        dDate += "/";
        if(item.dDay < 10){
            dDate += "0"+ item.dDay;
        }else{
            dDate += item.dDay;
        }
        viewHolder.getDate().setText(dDate); //약속 날짜
        String dTime = "";
        if(item.dHour < 10){
            dTime += "0"+ item.dHour;
        }else{
            dTime += item.dHour;
        }
        dTime += ":";
        if(item.dMin < 10){
            dTime += "0"+ item.dMin;
        }else{
            dTime += item.dMin;
        }
        viewHolder.getTime().setText(dTime); //약속 시간

        return view;
    }

    public void addTopItem(ListViewItem item){
        myListItem.add(0, new ListViewItem(item));
    }

    public void addItem(ListViewItem item){
        myListItem.add(new ListViewItem(item));
    }

    public void editItem(int index, ListViewItem item){
        for(int i=0; i<myListItem.size(); i++){
            int pro_index = myListItem.get(i).index;
            if(pro_index == index){
                Log.d("edit", "no: "+i);
                ListViewItem tempItem = myListItem.get(i);
                tempItem.content = item.content; //약속 내용
                tempItem.place = item.place; //약속 장소
                tempItem.dYear = item.dYear; //디데이 날짜와 시간
                tempItem.dMonth = item.dMonth;
                tempItem.dDay = item.dDay;
                tempItem.dHour = item.dHour;
                tempItem.dMin = item.dMin;
                tempItem.isStart = item.isStart;
                tempItem.alram_time = item.alram_time;
                tempItem.isAlarm = item.isAlarm;
                return;
            }
        }
    }

    public void removeItem(int index){
        for(int i=0; i<myListItem.size(); i++){
            int pro_index = myListItem.get(i).index;
            if(pro_index == index){
                myListItem.remove(i);
                return;
            }
        }
        //ddayThreads.get(index).stop = true;
    }

    public void removeAll(){
        myListItem.clear();
    }

    class ViewHolder{
        private View base;
        private TextView d_day; //디데이
        private TextView place; //약속 장소
        private TextView content; //약속 내용
        private TextView date; //약속 날짜
        private TextView time; //약속 시간
        private TextView sponsor; //주최자
        private TextView isStart; //출발 유무
        private TextView alram_time; //알람 시간
        private ImageView imageView_alram; //알람 이미지

        ViewHolder(View base){
            this.base = base;
        }

        TextView getD_day() {
            if (d_day == null) {
                d_day = (TextView)base.findViewById(textView_d_day);
            }
            return d_day;
        }

        TextView getPlace() {
            if (place == null) {
                place = (TextView)base.findViewById(R.id.textView_place);
            }
            return place;
        }

        TextView getContent() {
            if(content == null) {
                content = (TextView)base.findViewById(R.id.textView_content);
            }
            return content;
        }

        TextView getDate() {
            if (date == null) {
                date = (TextView)base.findViewById(R.id.textView_date);
            }
            return date;
        }

        TextView getTime() {
            if (time == null) {
                time = (TextView)base.findViewById(R.id.textView_time);
            }
            return time;
        }

        TextView getSponsor(){
            if(sponsor == null){
                sponsor = (TextView)base.findViewById(R.id.textView_sponsor);
            }
            return sponsor;
        }

        TextView getIsStart(){
            if(isStart == null){
                isStart = (TextView)base.findViewById(R.id.textView_isStart);
            }
            return isStart;
        }

        TextView getAlram_time(){
            if(alram_time == null){
                alram_time = (TextView)base.findViewById(R.id.textView_alram);
            }
            return alram_time;
        }

        ImageView getImageView_alram(){
            if(imageView_alram == null){
                imageView_alram = (ImageView)base.findViewById(R.id.imageView_alram);
            }
            return imageView_alram;
        }
    }

    public void setMyContext(Context contect){
        this.myContext = contect;
    }
}
