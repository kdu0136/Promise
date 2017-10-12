package com.example.kimdongun.promise;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by KimDongun on 2016-10-03.
 */

public class PublicTrafficListViewAdapter extends BaseAdapter{

    public ArrayList<PublicTrafficListViewItem> myListItem; //아이템 리스트
    private Context myContext = null;
    private LayoutInflater myInflater;


    public PublicTrafficListViewAdapter(Context myContext){
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
            view = myInflater.inflate(R.layout.public_traffic_listview_item, parent, false);
            // Holder pattern을 위한 wrapper 초기화 (row를 base 클래스로 지정)
            viewHolder = new ViewHolder(view);
            // row에 viewWrapper object를 tag함 (나중에 row를 재활용 할때 필요)
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        // getView() 호출시 인자로 전달된 position을 이용해 현재 사용중인 RowModel의 객체 model 얻기.
        final PublicTrafficListViewItem item = myListItem.get(index);

        // convertView(row)내부의 TextView의 text를 객체가 저장하고 있는 String으로  설정.
        //소요 시간
        String st_t = "약 ";
        int minite = item.total_time / 60; //분
        int sec = item.total_time % 60; //초
        if(sec >= 30){ //30초 이상이면 +1분
            minite++;
        }
        int hour = minite / 60; //시
        if(hour > 0){ //60분 이상이면 x시를 빼고 분 다시 구함
            minite = minite % 60;
            st_t += hour + "시간 ";
        }
        st_t += minite + "분";
        viewHolder.getTextView_time().setText(st_t); //쇼요 시간

        //거리
        float distance = item.total_distance/1000f;
        viewHolder.getTextView_distance().setText(String.valueOf(distance) + "km"); //거리

        //도보 시간
        String st_t_w = "도보 약 ";
        int minite_w = item.total_walk_time / 60; //분
        int sec_w = item.total_walk_time % 60; //초
        if(sec_w >= 30){ //30초 이상이면 +1분
            minite_w++;
        }
        int hour_w = minite_w / 60; //시
        if(hour_w > 0){ //60분 이상이면 x시를 빼고 분 다시 구함
            minite_w = minite_w % 60;
            st_t_w += hour_w + "시간 ";
        }
        st_t_w += minite_w + "분";
        viewHolder.getTextView_walkTime().setText(st_t_w); //쇼요 시간

        //가격
        viewHolder.getTextView_price().setText("카드 " + String.valueOf(item.total_price) + "원"); //가격

        //경로 라인, 경로 정보
        int use_width = 0; //경로정보 사용 width
        Log.d("test", "arrayList_distance: " + item.arrayList_distance.size());
        viewHolder.getLayout_route_line().removeAllViews();
        viewHolder.getLayout_route().removeAllViews();
        viewHolder.getAddView().removeAllViews();
        for(int i = 0; i < item.arrayList_distance.size(); i++){
            View line_view = new View(myContext);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            float view_weight = item.arrayList_distance.get(i);
            float cut_line = 0.5f;
            if(view_weight < cut_line){
                view_weight = cut_line;
            }
            param.weight = view_weight;
            line_view.setLayoutParams(param);
            line_view.setBackgroundColor(setLineColor(item.arrayList_vehicle_type.get(i), item.arrayList_line_type.get(i)));
            viewHolder.getLayout_route_line().addView(line_view); //경로 색상 라인 추가

            TextView route_text = new TextView(myContext);
            ViewGroup.LayoutParams text_params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            route_text.setLayoutParams(text_params);
            //route_text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            route_text.setText(item.arrayList_route_name.get(i));
            route_text.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

            ImageView route_image = new ImageView(myContext);
            ViewGroup.LayoutParams image_params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            image_params.width = route_text.getMeasuredHeight();
            image_params.height = route_text.getMeasuredHeight();
            route_image.setLayoutParams(image_params);
            //route_image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            route_image.setBackgroundResource(setTypeIcon(item.arrayList_vehicle_type.get(i)));
            route_image.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

            use_width += route_text.getMeasuredWidth() + route_image.getMeasuredWidth();

            Display display = ((AppCompatActivity)myContext).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            //viewHolder.getLayout_route_line().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            Log.d("test", "width: " + size.x * 0.95);//viewHolder.getLayout_route_line().getMeasuredWidth());
            if(use_width >= size.x * 0.95){ //최대 라인 넘었을 경우 밑에다가 텍스트 추가
                LinearLayout layout = new LinearLayout(myContext);
                layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                viewHolder.getLayout_route().addView(layout);
                use_width = route_text.getMeasuredWidth() + route_image.getMeasuredWidth();
            }
            viewHolder.getAddView().addView(route_image);
            viewHolder.getAddView().addView(route_text);
        }

        if(item.isSelect){
            viewHolder.getLayout_background().setBackgroundColor(Color.LTGRAY);
        }else{
            viewHolder.getLayout_background().setBackgroundColor(Color.WHITE);
        }

        return view;
    }

    public void addItem(PublicTrafficListViewItem item){
        myListItem.add(new PublicTrafficListViewItem(item.total_time, item.total_distance, item.total_walk_time, item.total_price,
                item.arrayList_distance, item.arrayList_route_name, item.arrayList_vehicle_type, item.arrayList_line_type));
    }

    public void removeAll(){
        myListItem.clear();
    }

    public void selectItem(int index) { //-1넣으면 무조건 false
        for(int i = 0; i < myListItem.size(); i++){
            PublicTrafficListViewItem item = myListItem.get(i);
            if(index == i)
                item.isSelect = true;
            else
                item.isSelect = false;
        }
        notifyDataSetChanged();
    }

    class ViewHolder{
        private View base;
        private TextView textView_time;
        private TextView textView_distance;
        private TextView textView_walkTime;
        private TextView textView_price;

        private LinearLayout layout_route_line;
        private LinearLayout layout_route;
        private LinearLayout addView;

        private RelativeLayout layout_background; //배경

        ViewHolder(View base){
            this.base = base;
        }

        TextView getTextView_time(){
            if(textView_time == null){
                textView_time = (TextView)base.findViewById(R.id.textView_time);
            }
            return textView_time;
        }

        TextView getTextView_distance(){
            if(textView_distance == null){
                textView_distance = (TextView)base.findViewById(R.id.textView_distance);
            }
            return textView_distance;
        }

        TextView getTextView_walkTime(){
            if(textView_walkTime == null){
                textView_walkTime = (TextView)base.findViewById(R.id.textView_walkTime);
            }
            return textView_walkTime;
        }

        TextView getTextView_price(){
            if(textView_price == null){
                textView_price = (TextView)base.findViewById(R.id.textView_price);
            }
            return textView_price;
        }

        LinearLayout getLayout_route_line(){
            if(layout_route_line == null){
                layout_route_line = (LinearLayout)base.findViewById(R.id.layout_route_line);
            }
            return layout_route_line;
        }

        LinearLayout getLayout_route(){
            if(layout_route == null){
                layout_route = (LinearLayout)base.findViewById(R.id.layout_route);
            }
            return layout_route;
        }

        LinearLayout getAddView(){
            if(getLayout_route().getChildCount() == 0){
                LinearLayout layout = new LinearLayout(myContext);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.bottomMargin = 5;
                layout.setLayoutParams(params);
                getLayout_route().addView(layout);
            }
            return (LinearLayout)getLayout_route().getChildAt(getLayout_route().getChildCount() - 1);
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

    public int setLineColor(String vehicle_type, String line_type){
        Log.d("Num", "vehicle_type: " + vehicle_type);
        Log.d("Num", "line_type: " + line_type);
        switch (vehicle_type){
            case "WALKING": //도보
                return Color.argb(255, 188, 188, 188);
            case "RAIL": //철도
                return Color.argb(255, 102, 145, 201);
            case "METRO_RAIL": //경전철 대중교통
                break;
            case "SUBWAY": //지하 경전철.
                switch (line_type){
                    case "1호선":
                        return Color.argb(255, 0, 52, 153);
                    case "2호선":
                        return Color.argb(255, 55, 180, 45);
                    case "3호선":
                        return Color.argb(255, 239, 91, 42);
                    case "4호선":
                        return Color.argb(255, 49, 113, 211);
                    case "5호선":
                        return Color.argb(255, 137, 59, 182);
                    case "6호선":
                        return Color.argb(255, 154, 79, 17);
                    case "7호선":
                        return Color.argb(255, 96, 109, 0);
                    case "8호선":
                        return Color.argb(255, 231, 27, 114);
                    case "9호선":
                        return Color.argb(255, 192, 161, 35);
                    case "인천1호선":
                        return Color.argb(255, 102, 145, 201);
                    case "인천2호선":
                        return Color.argb(255, 255, 184, 80);
                    case "분당선":
                        return Color.argb(255, 237, 178, 23);
                    case "신분당":
                        return Color.argb(255, 168, 2, 45);
                    case "경의중앙선":
                        return Color.argb(255, 125, 196, 165);
                    case "경춘선":
                        return Color.argb(255, 38, 169, 127);
                    case "공항":
                        return Color.argb(255, 113, 184, 229);
                    case "의정부":
                        return Color.argb(255, 255, 142, 0);
                    case "수인선":
                        return Color.argb(255, 237, 178, 23);
                    case "에버라인":
                        return Color.argb(255, 119, 195, 113);
                    case "자기부상":
                        return Color.argb(255, 255, 157, 90);
                    case "경강선":
                        return Color.argb(255, 38, 115, 242);
                    default:
                        return Color.argb(255, 0, 52, 153);
                }
            case "TRAM": //지상 경전철.
                return Color.argb(255, 102, 145, 201);
            case "MONORAIL": //모노레일
                break;
            case "HEAVY_RAIL": //일반 열차.
                break;
            case "COMMUTER_TRAIN": //통근 열차.
                break;
            case "HIGH_SPEED_TRAIN": //고속 전철.
                break;
            case "BUS": //버스
                return Color.argb(255, 170, 48, 255);
            case "INTERCITY_BUS": //시외 버스.
                break;
            case "TROLLEYBUS": //트롤리 버스.
                break;
            case "SHARE_TAXI": //합승 택시
                break;
            case "FERRY": //페리.
                break;
            case "CABLE_CAR": //지상에서 케이블로 운영되는 차량
                break;
            case "GONDOLA_LIFT": //공중 케이블카.
                break;
            case "FUNICULAR": //케이블로 당겨서 가파른 경사를 오르는 차량
                break;
            case "OTHER": //기타 모든 차량
                break;
            default:
                break;
        }
        return Color.argb(255, 18, 163, 73);
    }

    public int setTypeIcon(String vehicle_type){
        Log.d("Num", "vehicle_type: " + vehicle_type);
        switch (vehicle_type){
            case "WALKING": //도보
                return R.drawable.public_walk;
            case "HIGH_SPEED_TRAIN": //고속 전철.
            case "RAIL": //철도
            case "HEAVY_RAIL": //일반 열차.
                return R.drawable.public_train;
            case "METRO_RAIL": //경전철 대중교통
            case "SUBWAY": //지하 경전철.
            case "TRAM": //지상 경전철.
            case "MONORAIL": //모노레일
            case "COMMUTER_TRAIN": //통근 열차.
                return R.drawable.public_subway;
            case "BUS": //버스
            case "INTERCITY_BUS": //시외 버스.
            case "TROLLEYBUS": //트롤리 버스.
                return R.drawable.public_bus;
            default:
                return R.drawable.public_etc;
        }
    }
}
