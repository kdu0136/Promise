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

public class PromiseSocialListViewAdapter extends BaseAdapter implements View.OnClickListener{

    public ArrayList<PromiseSocialListViewItem> myListItem; //아이템 리스트
    private Context myContext = null;
    private LayoutInflater myInflater;


    public PromiseSocialListViewAdapter(Context myContext){
        super();
        this.myContext = myContext;
        this.myListItem = new ArrayList<>();
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
            view = myInflater.inflate(R.layout.promise_social_listview_item, parent, false);
            // Holder pattern을 위한 wrapper 초기화 (row를 base 클래스로 지정)
            viewHolder = new ViewHolder(view);
            // row에 viewWrapper object를 tag함 (나중에 row를 재활용 할때 필요)
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        // getView() 호출시 인자로 전달된 position을 이용해 현재 사용중인 RowModel의 객체 model 얻기.
        final PromiseSocialListViewItem item = myListItem.get(index);

        // convertView(row)내부의 TextView의 text를 객체가 저장하고 있는 String으로  설정.
        //메일
        viewHolder.getMail().setText(item.mail);
        //이름
        viewHolder.getName().setText(item.name);
        //계정 종류
        if(item.type.equals("google")){
            Drawable drawable_t = myContext.getResources().getDrawable(R.drawable.ic_google);
            viewHolder.getType().setBackgroundDrawable(drawable_t);
        }else if(item.type.equals("facebook")){
            Drawable drawable_t = myContext.getResources().getDrawable(R.drawable.ic_facebook);
            viewHolder.getType().setBackgroundDrawable(drawable_t);
        }
        viewHolder.getState().setText(item.state);
        if(item.state.equals("출발")){
            viewHolder.getState().setTextColor(myContext.getResources().getColor(R.color.colorAccent));
        }else if(item.state.equals("미 출발")){
            viewHolder.getState().setTextColor(Color.GRAY);
       }

        if(item.isSelect){
            viewHolder.getLayout_background().setBackgroundColor(Color.LTGRAY);
        }else{
            viewHolder.getLayout_background().setBackgroundColor(Color.WHITE);
        }

        return view;
    }

    public void addItem(PromiseSocialListViewItem item){
        myListItem.add(new PromiseSocialListViewItem(item.mail, item.name, item.type, item.no, item.state, item.lat, item.lon, item.locationON, item.isSelect));
    }

    public void removeAt(AddFriendListViewItem item){
        for(int i = 0; i < myListItem.size(); i++){
            PromiseSocialListViewItem temp = myListItem.get(i);
            if(temp.no == item.no){
                myListItem.remove(i);
                return;
            }
        }
    }

    public void selectItem(int index) { //-1넣으면 무조건 false
        for(int i = 0; i < myListItem.size(); i++){
            PromiseSocialListViewItem item = myListItem.get(i);
            if(index == i)
                item.isSelect = true;
            else
                item.isSelect = false;
        }
        notifyDataSetChanged();
    }

    public void removeAll(){
        myListItem.clear();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()){
        }
    }

    class ViewHolder{
        private View base;
        private TextView mail; //메일
        private TextView name; //이름
        private TextView state; //상태
        private ImageView type; //계정 종류
        private RelativeLayout layout_background; //배경

        ViewHolder(View base){
            this.base = base;
        }

        TextView getMail() {
            if (mail == null) {
                mail = (TextView)base.findViewById(R.id.textView_mail);
            }
            return mail;
        }

        TextView getName() {
            if (name == null) {
                name = (TextView)base.findViewById(R.id.textView_name);
            }
            return name;
        }

        TextView getState(){
            if(state == null){
                state = (TextView)base.findViewById(R.id.textView_state);
            }
            return state;
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
