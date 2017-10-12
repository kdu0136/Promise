package com.example.kimdongun.promise.Promise;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.kimdongun.promise.AddPromiseSocialListViewItem;
import com.example.kimdongun.promise.AlramDialog;
import com.example.kimdongun.promise.DaumMapAPI.MapApiConst;
import com.example.kimdongun.promise.Dday.DdayClass;
import com.example.kimdongun.promise.R;
import com.example.kimdongun.promise.search.ItemPlace;

import net.daum.mf.map.api.CameraPosition;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.CancelableCallback;
import net.daum.mf.map.api.MapLayout;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import java.util.HashMap;

import static com.example.kimdongun.promise.Account.myAccount;
import static com.example.kimdongun.promise.R.id.textView_alram;

/**
 * Created by KimDongun on 2017-01-11.
 */

public class FragmentContentMain extends Fragment implements View.OnClickListener, DialogInterface.OnDismissListener{
    private ScrollView scrollView; //스크롤뷰

    private TextView textView_D_day; //디데이
    public  TextView textView_isStart; //출발 유무 텍스트
    private TextView textView_content; //약속 내용

    private TextView textView_place; //약속 장소
    private TextView textView_date; //약속 날짜
    private TextView textView_time; //약속 시간
    private TextView textView_start; //출발 장소

    private LinearLayout layout_place; //약속 장소 선택가능 레이아웃
    private LinearLayout layout_date; //약속 날짜 선택가능 레이아웃
    private LinearLayout layout_time; //약속 시간 선택가능 레이아웃
    private LinearLayout layout_start; //출발 장소 선택가능 레이아웃

    private MapView mMapView; //맵
    private RelativeLayout layout_map;//맵 레이아웃
    private LinearLayout layout_route_info; //경로 정보 버튼

    private TextView textView_social; //약속 친구
    private LinearLayout layout_social; //약속 친구 선택가능 레이아웃
    private ImageView imageView_social;//약속 친구 이미지

    private ItemPlace arrivePlace; //도착 장소
    private ItemPlace startPlace; //출발 장소

    private LinearLayout layout_alarm; //알람 터치
    private ImageView imageView_alarm; //알람 그림
    private TextView textView_alarm; //알람 텍스트

    private HashMap<Integer, ItemPlace> mTagItemMap = new HashMap<Integer, ItemPlace>();

    private CameraPosition cameraPositionArrive; //도착 지점 카메라 에니메이션
    private CameraPosition cameraPositionStart; //출발 지점 카메라 에니메이션

    private AlramDialog alramDialog;

    public Menu mMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content_main, container, false);
    }

    @Override
    public String toString() {
        return "FragmentContentMain";
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        //초기화
        scrollView = (ScrollView)view.findViewById(R.id.scrollView); //스크롤뷰

        textView_D_day = (TextView)view.findViewById(R.id.textView_D_day); //디데이
        textView_isStart = (TextView)view.findViewById(R.id.textView_isStart); //출발 유무 텍스트
        textView_content = (TextView)view.findViewById(R.id.textView_content); //약속 내용

        textView_place = (TextView)view.findViewById(R.id.textView_place); //약속 장소
        textView_date = (TextView)view.findViewById(R.id.textView_date); //약속 날짜
        textView_time = (TextView)view.findViewById(R.id.textView_time); //약속 시간
        textView_start = (TextView)view.findViewById(R.id.textView_start); //출발 장소

        layout_place = (LinearLayout) view.findViewById(R.id.layout_place); //약속 장소 선택가능 레이아웃
        layout_date = (LinearLayout) view.findViewById(R.id.layout_date); //약속 날짜 선택가능 레이아웃
        layout_time = (LinearLayout) view.findViewById(R.id.layout_time); //약속 시간 선택가능 레이아웃
        layout_start = (LinearLayout) view.findViewById(R.id.layout_start); //출발 장소 선택가능 레이아웃

        layout_map = (RelativeLayout)view.findViewById(R.id.layout_map); //맵 레이아웃
        layout_route_info  = (LinearLayout) view.findViewById(R.id.layout_route_info); //경로 정보 버튼

        textView_social = (TextView)view.findViewById(R.id.textView_social); //약속 친구
        layout_social  = (LinearLayout) view.findViewById(R.id.layout_social); //약속 친구 선택가능 레이아웃
        imageView_social = (ImageView)view.findViewById(R.id.imageView_social);

        arrivePlace = ((ContentActivity)getActivity()).getArrivePlace(); //도착 장소
        startPlace = ((ContentActivity)getActivity()).getStartPlace(); //출발 장소

        layout_alarm = (LinearLayout)view.findViewById(R.id.layout_alram); //알람 터치
        imageView_alarm = (ImageView) view.findViewById(R.id.imageView_alram); //알람 그림
        textView_alarm = (TextView)view.findViewById(textView_alram); //알람 텍스트

        cameraPositionArrive = new CameraPosition(MapPoint.mapPointWithGeoCoord(arrivePlace.latitude, arrivePlace.longitude), 2);
        cameraPositionStart = new CameraPosition(MapPoint.mapPointWithGeoCoord(startPlace.latitude, startPlace.longitude), 2);

        //맵
        MapLayout mapLayout = new MapLayout(getActivity());
        mMapView = mapLayout.getMapView();
        mMapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY); //api-key 설정

        mMapView.setMapViewEventListener(mapViewEventListener);
        mMapView.setOpenAPIKeyAuthenticationResultListener(openAPIKeyAuthenticationResultListener);
        mMapView.setPOIItemEventListener(poiItemEventListener);

        mMapView.setMapType(MapView.MapType.Standard);

        RelativeLayout mapViewContainer = (RelativeLayout)view.findViewById(R.id.map_view);
        mapViewContainer.addView(mapLayout);

        //값 대입
        ((ContentActivity)getActivity()).getListViewItem().D_day = DdayClass.calDday(((ContentActivity)getActivity()).getListViewItem().dYear,
                ((ContentActivity)getActivity()).getListViewItem().dMonth, ((ContentActivity)getActivity()).getListViewItem().dDay);
        String st_Dday;
        { //디데이 구간
            int d_day = ((ContentActivity)getActivity()).getListViewItem().D_day;
            if(d_day > 0) { //약속 날 이전
                st_Dday = "D-" + d_day;
            }else{ //약속 당일, 지남
                int d_hour = DdayClass.calDhour(((ContentActivity)getActivity()).getListViewItem().dYear,
                        ((ContentActivity)getActivity()).getListViewItem().dMonth,
                        ((ContentActivity)getActivity()).getListViewItem().dDay,
                        ((ContentActivity)getActivity()).getListViewItem().dHour,
                        ((ContentActivity)getActivity()).getListViewItem().dMin);
                if(d_hour < 0) { //약속 지남
                    st_Dday = "지남";
                }else{ //약속 당일
                    st_Dday = "D-day";
                }
            }
        }
        textView_D_day.setText(st_Dday); //디데이
        if(((ContentActivity)getActivity()).getListViewItem().isStart == 0) {
            textView_isStart.setText("미 출발");
            textView_isStart.setTextColor(Color.GRAY);
        }else if(((ContentActivity)getActivity()).getListViewItem().isStart == 1){
            textView_isStart.setText("출발");
            textView_isStart.setTextColor(getResources().getColor(R.color.colorAccent));
        }

        textView_content.setText(((ContentActivity)getActivity()).getListViewItem().content); //약속 내용
        textView_place.setText(((ContentActivity)getActivity()).getArrivePlace().title); //약속 장소
        //친구 목록 초기화
        if(((ContentActivity)getActivity()).friend.size() != 0){ //기존 등록한 친구가 있으면 세팅
            String str_firends = "";
            for(int i = 0; i < ((ContentActivity)getActivity()).friend.size(); i++){
                AddPromiseSocialListViewItem item = ((ContentActivity)getActivity()).friend.get(i);
                str_firends += item.name;
                if(i < ((ContentActivity)getActivity()).friend.size()-1)
                    str_firends += ", ";
            }
            textView_social.setText(str_firends);
            Drawable drawable = getActivity().getResources().getDrawable(R.drawable.ic_social2_1);
            imageView_social.setBackgroundDrawable(drawable);
        }

        int m_year = ((ContentActivity)getActivity()).getListViewItem().dYear;
        int m_month = ((ContentActivity)getActivity()).getListViewItem().dMonth;
        int m_day = ((ContentActivity)getActivity()).getListViewItem().dDay;
        textView_date.setText(String.format("%d년 %d월 %d일", m_year,m_month + 1, m_day)); //약속 날짜
        int m_hour = ((ContentActivity)getActivity()).getListViewItem().dHour;
        int m_minute = ((ContentActivity)getActivity()).getListViewItem().dMin;
        textView_time.setText(String.format("%d시 %d분", m_hour, m_minute)); //약속 시간
        textView_start.setText(((ContentActivity)getActivity()).getStartPlace().title); //출발 장소

        Drawable drawable_alarm;
        if(((ContentActivity)getActivity()).getListViewItem().isAlarm == 1) { //이미 울린 약속
            drawable_alarm = getResources().getDrawable(R.drawable.ic_alarm_off);
        }else{
            drawable_alarm = getResources().getDrawable(R.drawable.ic_alarm_on);
        }
        imageView_alarm.setBackgroundDrawable(drawable_alarm);

        String str_alram = ((ContentActivity)getActivity()).getListViewItem().alram_time+"분 전";
        textView_alarm.setText(str_alram);

        //터치 이벤트
        layout_place.setOnClickListener(this); //약속 장소 선택가능 레이아웃
        layout_start.setOnClickListener(this); //출발 장소 선택가능 레이아웃
        layout_route_info.setOnClickListener(this); //경로 정보 버튼
        layout_social.setOnClickListener(this); //약속 친구 선택가능 레이아웃
        layout_alarm.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_content, menu);
        mMenu = menu;
        if(((ContentActivity)getActivity()).getListViewItem().isStart == 0) {//미출발 상태
            mMenu.getItem(0).setTitle("출발");
        }else{ //출발 상태
            mMenu.getItem(0).setTitle("출발 취소");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_start: //출발 버튼
                String str_msg;
                int isStarted; //미출발
                if(((ContentActivity)getActivity()).getListViewItem().isStart == 0) { //미출발 상태
                    str_msg = "약속을 출발 하시겠습니까?";
                    isStarted = 1;
                }else{ //출발 상태
                    str_msg = "약속 출발을 취소 하시겠습니까?";
                    isStarted = 0;
                }
                AlertDialog.Builder alert_confirm0 = new AlertDialog.Builder(getActivity());
                final int finalIsStarted = isStarted;
                alert_confirm0.setMessage(str_msg).setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //출발
                                ((ContentActivity)getActivity()).startPromise(finalIsStarted);
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert0 = alert_confirm0.create();
                alert0.show();
                break;

            case R.id.action_edit: //수정 버튼
                ((ContentActivity)getActivity()).editData();
                break;

            case R.id.action_delete: //삭제 버튼
                if(myAccount.getIndex() == ((ContentActivity)getActivity()).getListViewItem().spon_no){ //주최자인 경우
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                    alert_confirm.setMessage("약속을 완전히 파기하시겠습니까?\n주최자를 다른사람에게 양도하고\n본인만 약속에서 나가시겠습니까?").setCancelable(false).setPositiveButton("파기",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((ContentActivity)getActivity()).deleteAll();
                                }
                            }).setNegativeButton("양도",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                                    alert_confirm.setMessage("약속 인원이 2명 미만일 경우\n" +
                                            "주죄자를 양도 할 수 없습니다").setCancelable(false).setPositiveButton("확인",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    ((ContentActivity) getActivity()).switchFragment("FragmentContentChangeSponsor");
                                                }
                                            });
                                    AlertDialog alert = alert_confirm.create();
                                    alert.show();
                                }
                            }).setNeutralButton("취소",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            });
                    AlertDialog alert = alert_confirm.create();
                    alert.show();
                }else{
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                    alert_confirm.setMessage("정말로 삭제 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((ContentActivity)getActivity()).deleteData();
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
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    MapView.MapViewEventListener mapViewEventListener = new MapView.MapViewEventListener() {
        @Override
        public void onMapViewInitialized(MapView mapView) { //맵 초기화
            ItemPlace[] itemPlaces = {arrivePlace, startPlace};
            int[] img = {R.drawable.ic_map_arrive, R.drawable.ic_map_start};
            int[] img1 = {R.drawable.ic_map_arrive_over, R.drawable.ic_map_start_over};
            MapPointBounds mapPointBounds = new MapPointBounds();

            for (int i = 0; i < itemPlaces.length; i++) {
                ItemPlace item = itemPlaces[i];

                MapPOIItem poiItem = new MapPOIItem();
                poiItem.setItemName(item.title);
                poiItem.setTag(i);
                MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.latitude, item.longitude);
                poiItem.setMapPoint(mapPoint);
                mapPointBounds.add(mapPoint);
                poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomImageResourceId(img[i]);
                poiItem.setCustomSelectedImageResourceId(img1[i]);
                poiItem.setCustomImageAutoscale(false);
                poiItem.setCustomImageAnchor(0.5f, 1.0f);

                mMapView.addPOIItem(poiItem);
                mTagItemMap.put(poiItem.getTag(), item);
            }

            //mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));
            //mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(arrivePlace.latitude, arrivePlace.longitude), true);
            mMapView.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositionArrive), 500, new CancelableCallback() {
                @Override
                public void onFinish() {
                }
                @Override
                public void onCancel() {
                }
            });

            MapPOIItem[] poiItems = mMapView.getPOIItems();
            if (poiItems.length > 0) {
                mMapView.selectPOIItem(poiItems[0], false);
            }

            Log.e("log", "onMapViewInitialized");
        }

        @Override
        public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) { //지도 중심 좌표가 이동한 경우 호출
            Log.e("log", "onMapViewCenterPointMoved");

        }

        @Override
        public void onMapViewZoomLevelChanged(MapView mapView, int i) { //지도 확대/축소 레벨이 변경된 경우 호출
            Log.e("log", "onMapViewZoomLevelChanged");

        }

        @Override
        public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) { //사용자가 지도 위를 터치한 경우 호출
            Log.e("log", "onMapViewSingleTapped");

        }

        @Override
        public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) { //사용자가 지도 위 한 지점을 더블 터치한 경우 호출
            Log.e("log", "onMapViewDoubleTapped");

        }

        @Override
        public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) { //사용자가 지도 위 한 지점을 길게 누른 경우(long press) 호출
            Log.e("log", "onMapViewLongPressed");

        }

        @Override
        public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) { //사용자가 지도 드래그를 시작한 경우 호출
            Log.e("log", "onMapViewDragStarted");

        }

        @Override
        public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) { //사용자가 지도 드래그를 끝낸 경우 호출
            Log.e("log", "onMapViewDragEnded");

        }

        @Override
        public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) { //지도의 이동이 완료된 경우 호출
            Log.e("log", "onMapViewMoveFinished");

        }
    };

    MapView.OpenAPIKeyAuthenticationResultListener openAPIKeyAuthenticationResultListener = new MapView.OpenAPIKeyAuthenticationResultListener() {
        @Override
        public void onDaumMapOpenAPIKeyAuthenticationResult(MapView mapView, int i, String s) {

        }
    };

    MapView.POIItemEventListener poiItemEventListener = new MapView.POIItemEventListener() {
        @Override
        //단말 사용자가 POI Item을 선택한 경우 호출 , 사용자가 MapView 에 등록된 POI Item 아이콘(마커)를 터치한 경우 호출
        public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
            Log.e("log", "onPOIItemSelected");
        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
            Log.e("log", "onCalloutBalloonOfPOIItemTouched");

        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
            Log.e("log", "onCalloutBalloonOfPOIItemTouched");
        }

        @Override
        //단말 사용자가 길게 누른 후(long press) 끌어서(dragging) 위치 이동이 가능한 POI Item의 위치를 이동시킨 경우 호출
        public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
            Log.e("log", "onDraggablePOIItemMoved");

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_place: //약속 장소 선택가능 레이아웃
                mMapView.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositionArrive), 500, new CancelableCallback() {
                    @Override
                    public void onFinish() {
                        MapPOIItem[] poiItems = mMapView.getPOIItems();
                        if (poiItems.length > 0) {
                            mMapView.selectPOIItem(poiItems[0], false);
                        }
                    }
                    @Override
                    public void onCancel() {
                    }
                });
                scrollView.requestChildFocus(null, layout_map);
                break;

            case R.id.layout_start: //출발 장소 선택가능 레이아웃
                mMapView.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositionStart), 500, new CancelableCallback() {
                    @Override
                    public void onFinish() {
                        MapPOIItem[] poiItems = mMapView.getPOIItems();
                        if (poiItems.length > 0) {
                            mMapView.selectPOIItem(poiItems[1], false);
                        }
                    }
                    @Override
                    public void onCancel() {
                    }
                });
                scrollView.requestChildFocus(null, layout_map);
                break;

            case R.id.layout_route_info: //경로 정보 버튼
                ((ContentActivity)getActivity()).switchFragment("FragmentContentMap");
                break;

            case R.id.layout_social: //약속 친구 선택가능 레이아웃
                ((ContentActivity)getActivity()).switchFragment("FragmentContentSocial");
                break;

            case R.id.layout_alram:
                alramDialog = new AlramDialog(getActivity(), ((ContentActivity)getActivity()).getListViewItem().alram_time);
                alramDialog.setOnDismissListener(this);
                alramDialog.show();
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        ((ContentActivity)getActivity()).getListViewItem().alram_time = ((AlramDialog)dialog).time;
        ((ContentActivity)getActivity()).getListViewItem().isAlarm = 0;
        textView_alarm.setText(((ContentActivity)getActivity()).getListViewItem().alram_time+"분 전");
        myAccount.listViewAdapter.editItem(((ContentActivity)getActivity()).getListViewItem().index, ((ContentActivity)getActivity()).getListViewItem());
        Drawable drawable_alarm = getResources().getDrawable(R.drawable.ic_alarm_on);
        imageView_alarm.setBackgroundDrawable(drawable_alarm);
    }
}
