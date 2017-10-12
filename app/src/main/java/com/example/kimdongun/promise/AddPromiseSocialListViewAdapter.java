package com.example.kimdongun.promise;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by KimDongun on 2016-10-03.
 */

public class AddPromiseSocialListViewAdapter extends BaseAdapter implements View.OnClickListener{

    public ArrayList<AddPromiseSocialListViewItem> myListItem; //아이템 리스트
    public ArrayList<AddPromiseSocialListViewItem> appearListItem; //아이템 리스트
    private Context myContext = null;
    private LayoutInflater myInflater;


    public AddPromiseSocialListViewAdapter(Context myContext){
        super();
        this.myContext = myContext;
        this.myListItem = new ArrayList<>();
        this.appearListItem = new ArrayList<>();
    }
    @Override
    public int getCount() {
        return appearListItem.size();
    }

    public int getRealCount() { return myListItem.size(); }

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
        CheckBox checkbox;

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if(view == null){
            // 레이아웃을 inflate시켜 새로운 view 생성.
            myInflater = (LayoutInflater)myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = myInflater.inflate(R.layout.add_promise_social_listview_item, parent, false);
            // Holder pattern을 위한 wrapper 초기화 (row를 base 클래스로 지정)
            viewHolder = new ViewHolder(view);
            // row에 viewWrapper object를 tag함 (나중에 row를 재활용 할때 필요)
            view.setTag(viewHolder);
            checkbox = viewHolder.getCheckBox();

            CheckBox.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Integer index = (Integer) buttonView.getTag();
                    AddPromiseSocialListViewItem item = appearListItem.get(index);

                    item.isCheck = isChecked;
                }
            };

            checkbox.setOnCheckedChangeListener(listener);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
            checkbox = viewHolder.getCheckBox();
        }

        // getView() 호출시 인자로 전달된 position을 이용해 현재 사용중인 RowModel의 객체 model 얻기.
        final AddPromiseSocialListViewItem item = appearListItem.get(index);

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

        checkbox.setTag(new Integer(index));
        checkbox.setChecked(item.isCheck);

        return view;
    }

    public void addItem(AddPromiseSocialListViewItem item){
        myListItem.add(new AddPromiseSocialListViewItem(item.mail, item.name, item.type, item.no, item.isCheck));
    }

    public void removeAt(AddFriendListViewItem item){
        for(int i = 0; i < myListItem.size(); i++){
            AddPromiseSocialListViewItem temp = myListItem.get(i);
            if(temp.no == item.no){
                myListItem.remove(i);
                return;
            }
        }
    }

    public void removeAll(){
        myListItem.clear();
    }

    public void filterItem(String searchText){
        appearListItem.clear();
        searchText = searchText.toLowerCase(Locale.getDefault());
        if(searchText.length() == 0){
            appearListItem.addAll(myListItem);
        }else{
            for(AddPromiseSocialListViewItem item : myListItem){
                String contentStr =item.name;
                if(contentStr.toLowerCase().contains(searchText)){
                    appearListItem.add(item);
                }
            }
        }

        this.notifyDataSetChanged();
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
        private ImageView type; //계정 종류
        private CheckBox checkbox;

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

        ImageView getType() {
            if(type == null){
                type = (ImageView)base.findViewById(R.id.imageView_type);
            }
            return type;
        }

        CheckBox getCheckBox(){
            if(checkbox == null){
                checkbox = (CheckBox)base.findViewById(R.id.checkbox);
            }
            return checkbox;
        }
    }

    public void setMyContext(Context contect){
        this.myContext = contect;
    }
}
