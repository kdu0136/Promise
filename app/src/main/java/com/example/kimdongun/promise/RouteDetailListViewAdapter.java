package com.example.kimdongun.promise;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by KimDongun on 2016-10-03.
 */

public class RouteDetailListViewAdapter extends BaseAdapter{

    public ArrayList<RouteDetailListViewItem> myListItem; //아이템 리스트
    private Context myContext = null;
    private LayoutInflater myInflater;


    public RouteDetailListViewAdapter(Context myContext){
        super();
        this.myContext = myContext;
        this.myListItem = new ArrayList<RouteDetailListViewItem>();
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
            view = myInflater.inflate(R.layout.route_detail_listview_item, parent, false);
            // Holder pattern을 위한 wrapper 초기화 (row를 base 클래스로 지정)
            viewHolder = new ViewHolder(view);
            // row에 viewWrapper object를 tag함 (나중에 row를 재활용 할때 필요)
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        // getView() 호출시 인자로 전달된 position을 이용해 현재 사용중인 RowModel의 객체 model 얻기.
        final RouteDetailListViewItem item = myListItem.get(index);

        // convertView(row)내부의 TextView의 text를 객체가 저장하고 있는 String으로  설정.
        //경로 정보
        viewHolder.getDetail().setText(item.detail);
        //시간
        String st_t = "목적지까지 약 ";
        int minite = item.time / 60; //분
        int sec = item.time % 60; //초
        if(sec >= 30){ //30초 이상이면 +1분
            minite++;
        }
        int hour = minite / 60; //시
        if(hour > 0){ //60분 이상이면 x시를 빼고 분 다시 구함
            minite = minite % 60;
            st_t += hour + "시간 ";
        }
        st_t += minite + "분";
        viewHolder.getTime().setText(st_t);
        //경로 종류
        if(item.type.equals("car")){
            Drawable drawable_t = myContext.getResources().getDrawable(R.drawable.ic_car);
            viewHolder.getType().setBackgroundDrawable(drawable_t);
        }else if(item.type.equals("bus")){
            Drawable drawable_t = myContext.getResources().getDrawable(R.drawable.ic_bus);
            viewHolder.getType().setBackgroundDrawable(drawable_t);
        }else if(item.type.equals("walk")){
            Drawable drawable_t = myContext.getResources().getDrawable(R.drawable.ic_walk);
            viewHolder.getType().setBackgroundDrawable(drawable_t);
        }else if(item.type.equals("start")){
            Drawable drawable_t = myContext.getResources().getDrawable(R.drawable.ic_map_start);
            viewHolder.getType().setBackgroundDrawable(drawable_t);
        }else if(item.type.equals("end")){
            Drawable drawable_t = myContext.getResources().getDrawable(R.drawable.ic_map_arrive);
            viewHolder.getType().setBackgroundDrawable(drawable_t);
        }

        if(item.isSelect){
            viewHolder.getLayout_background().setBackgroundColor(Color.LTGRAY);
        }else{
            viewHolder.getLayout_background().setBackgroundColor(Color.WHITE);
        }

        return view;
    }

    public void addItem(RouteDetailListViewItem item){
        myListItem.add(new RouteDetailListViewItem(item.detail, item.time, item.type));
    }

    public void removeAll(){
        myListItem.clear();
    }

    public void selectItem(int index) { //-1넣으면 무조건 false
        for(int i = 0; i < myListItem.size(); i++){
            RouteDetailListViewItem item = myListItem.get(i);
            if(index == i)
                item.isSelect = true;
            else
                item.isSelect = false;
        }
        notifyDataSetChanged();
    }

    class ViewHolder{
        private View base;
        private TextView detail; //경로 정보
        private TextView time; //시간
        private ImageView type; //경로 종류
        private RelativeLayout layout_background; //배경

        ViewHolder(View base){
            this.base = base;
        }

        TextView getDetail() {
            if (detail == null) {
                detail = (TextView)base.findViewById(R.id.textView_detail);
            }
            return detail;
        }

        TextView getTime() {
            if (time == null) {
                time = (TextView)base.findViewById(R.id.textView_time);
            }
            return time;
        }

        ImageView getType() {
            if(type == null){
                type = (ImageView)base.findViewById(R.id.imageView_type);
            }
            return type;
        }

        RelativeLayout getLayout_background(){
            if(layout_background == null){
                layout_background = (RelativeLayout)base.findViewById(R.id.layout_background);
            }
            return layout_background;
        }
    }

    public void setMyContext(Context contect){
        this.myContext = contect;
    }
}
