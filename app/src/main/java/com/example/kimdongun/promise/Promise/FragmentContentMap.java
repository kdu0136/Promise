package com.example.kimdongun.promise.Promise;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.kimdongun.promise.DaumMapAPI.MapApiConst;
import com.example.kimdongun.promise.Location.CoordiPoint;
import com.example.kimdongun.promise.Location.LocationClass;
import com.example.kimdongun.promise.Location.RoutePoint;
import com.example.kimdongun.promise.Network.Network;
import com.example.kimdongun.promise.PublicTrafficListViewAdapter;
import com.example.kimdongun.promise.PublicTrafficListViewItem;
import com.example.kimdongun.promise.R;
import com.example.kimdongun.promise.RouteDetailListViewAdapter;
import com.example.kimdongun.promise.RouteDetailListViewItem;
import com.example.kimdongun.promise.serverDB.OnFinishDBListener;
import com.example.kimdongun.promise.serverDB.PostDB;

import net.daum.mf.map.api.CameraPosition;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.CancelableCallback;
import net.daum.mf.map.api.MapLayout;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by KimDongun on 2016-12-30.
 */


public class FragmentContentMap extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private LinearLayout layout_route_type; //경로 종류 레이
    private ImageView imageView_car; //자동차 경로 보기
    private ImageView imageView_bus; //대중교통 경로 보기
    private ImageView imageView_walk; //도보 경로 보기

    private TextView textView_start; //출발 지점 이름
    private TextView textView_end; //도착 지점 이름

    private MapView mMapView; //맵
    private RelativeLayout layout_info; //장소 정보 레이아웃
    ArrayList<MapPOIItem> poiItemArrayList;

    private ImageView imageView_location; //현재 위치 버튼
    private ImageView imageView_plus; //맵 확대 버튼
    private ImageView imageView_minus; //맵 축소 버튼

    private RelativeLayout layout_info1; //소요시간, 거리, 비용 레이아웃
    private TextView textView_time; //쇼요 시간
    private TextView textView_distance; //거리
    private TextView textView_price; //비용
    private ImageView imageView_detail; //경로 자세히 보기

    private ListView listView; //경로 디테일 정보 리스트
    private RouteDetailListViewAdapter adapter; //경로 디테일 정보 리스트 어댑터

    private Switch switch_now; //대중교통 검색 옵션 스위치(현재시간)
    private Switch switch_arrive; //대중교통 검색 옵션 스위치(약속 시간)
    private TextView textView_switch_arrive;
    private TextView textView_switch_now;
    private RelativeLayout layout_public; //대중교통 레이아웃
    private ListView listView_public; //대중교통 정보 리스트
    private PublicTrafficListViewAdapter adapter_public; //대중교통 정보 리스트 어댑터

    private HashMap<Integer, RoutePoint> mTagItemMap = new HashMap<Integer, RoutePoint>();

    private int total_time; //소요 시간
    private int total_distance; //거리
    private int total_price; //비용

    private String routeType; //경로 타입
    private ArrayList<RoutePoint> coordiPointArrayList; //안내점 정보
    private ArrayList<CoordiPoint> coordiRouteArrayList; //구간 정보

    private ArrayList<CameraPosition> cameraPositionRoute; //경로 카메라 에니메이션
    private int cameraLV; //경로 카메라 에니메이션 맵 레벨

    public FragmentContentMap() {
        // Required empty public constructor
    }

    @Override
    public String toString() {
        return "FragmentContentMap";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_content_map, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //초기화
        layout_route_type = (LinearLayout)view.findViewById(R.id.layout_route_type); //경로 종류 레이아웃
        imageView_car = (ImageView)view.findViewById(R.id.imageView_car); //자동차 경로 보기
        imageView_bus = (ImageView)view.findViewById(R.id.imageView_bus); //버스 경로 보기
        imageView_walk = (ImageView)view.findViewById(R.id.imageView_walk); //도보 경로 보기
        textView_start = (TextView)view.findViewById(R.id.textView_start); //출발 장소 이름
        textView_end = (TextView)view.findViewById(R.id.textView_end); //도착 장소 이름
        layout_info = (RelativeLayout)view.findViewById(R.id.layout_info); //장소 정보 레이아웃
        imageView_location = (ImageView)view.findViewById(R.id.imageView_location); //현재 위치 버튼
        imageView_plus = (ImageView)view.findViewById(R.id.imageView_plus); //맵 확대 버튼
        imageView_minus = (ImageView)view.findViewById(R.id.imageView_minus); //맵 축소 버튼
        layout_info1 = (RelativeLayout)view.findViewById(R.id.layout_info1); //소요 시간, 거리 비용 레이아웃
        textView_time = (TextView)view.findViewById(R.id.textView_time); //쇼요 시간
        textView_distance = (TextView)view.findViewById(R.id.textView_distance); //거리
        textView_price = (TextView)view.findViewById(R.id.textView_price); //비용
        imageView_detail = (ImageView)view.findViewById(R.id.imageView_detail); //경로 자세히 보기
        coordiPointArrayList = new ArrayList<>();  //안내점 정보
        coordiRouteArrayList = new ArrayList<>(); //구간 정보
        routeType = "car"; //경로 타입
        choiceRouteType(routeType);

        cameraPositionRoute = new ArrayList<>();
        cameraLV = 1;
        listView = (ListView)view.findViewById(R.id.listView); //경로 디테일 정보 리스트
        adapter = new RouteDetailListViewAdapter(getActivity());
        listView.setAdapter(adapter);

        switch_now = (Switch)view.findViewById(R.id.switch_now); //대중교통 검색 옵션 스위치(현재시간)
        switch_arrive = (Switch)view.findViewById(R.id.switch_arrive); //대중교통 검색 옵션 스위치(약속시간)
        textView_switch_arrive = (TextView)view.findViewById(R.id.textView_switch_arrive);
        textView_switch_now = (TextView)view.findViewById(R.id.textView_switch_now);

        boolean isSwitchNow = switch_now.isChecked();
        switch_arrive.setChecked(!isSwitchNow);
        if(isSwitchNow){
            textView_switch_now.setText("설정");
            textView_switch_arrive.setText("해제");
        }else{
            textView_switch_now.setText("해제");
            textView_switch_arrive.setText("설정");
        }

        layout_public = (RelativeLayout)view.findViewById(R.id.layout_public); //대중교통 정보 레이아웃
        listView_public = (ListView)view.findViewById(R.id.listView_public); //대중교통 정보 리스트
        adapter_public = new PublicTrafficListViewAdapter(getActivity());
        listView_public.setAdapter(adapter_public);

        switch_now.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { //현재 시간
               if (isChecked) {
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                    alert_confirm.setMessage("대중교통 경로 탐색을 현재 시간 기준으로 검색합니다")
                            .setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    HashMap<String, String> postData = new HashMap<String, String>();
                                    postData.put("startX", String.valueOf(((ContentActivity) getActivity()).getStartPlace().longitude));
                                    postData.put("startY", String.valueOf(((ContentActivity) getActivity()).getStartPlace().latitude));
                                    postData.put("endX", String.valueOf(((ContentActivity) getActivity()).getArrivePlace().longitude));
                                    postData.put("endY", String.valueOf(((ContentActivity) getActivity()).getArrivePlace().latitude));
                                    postData.put("type", "now");

                                    PostDB postDB = new PostDB(getActivity());
                                    postDB.putData("get_route_public.php", postData, new OnFinishDBListener() {
                                        @Override
                                        public void onSuccess(String output) {
                                            Log.d("route_public", output);
                                            parsePublic(output);
                                            textView_switch_now.setText("설정");
                                            textView_switch_arrive.setText("해제");
                                            switch_arrive.setChecked(false);
                                        }
                                    });
                                }
                            });
                    AlertDialog alert = alert_confirm.create();
                    alert.show();
                }else {
                   switch_arrive.setChecked(true);
               }
            }
        });

        switch_arrive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)  {
                    String str_time = String.valueOf(((ContentActivity) getActivity()).getListViewItem().dYear) + "년 ";
                    final int dMonth = ((ContentActivity) getActivity()).getListViewItem().dMonth + 1;
                    str_time += String.valueOf(dMonth) + "월";
                    str_time += String.valueOf(((ContentActivity) getActivity()).getListViewItem().dDay) + "일 ";
                    str_time += String.valueOf(((ContentActivity) getActivity()).getListViewItem().dHour) + "시 ";
                    str_time += String.valueOf(((ContentActivity) getActivity()).getListViewItem().dMin) + "분";
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                    alert_confirm.setMessage("대중교통 경로 탐색을 약속 시간 기준으로 검색합니다\n" +
                            "약속 시간: " + str_time)
                            .setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    HashMap<String, String> postData = new HashMap<String, String>();
                                    postData.put("startX", String.valueOf(((ContentActivity) getActivity()).getStartPlace().longitude));
                                    postData.put("startY", String.valueOf(((ContentActivity) getActivity()).getStartPlace().latitude));
                                    postData.put("endX", String.valueOf(((ContentActivity) getActivity()).getArrivePlace().longitude));
                                    postData.put("endY", String.valueOf(((ContentActivity) getActivity()).getArrivePlace().latitude));
                                    postData.put("type", "arrive");
                                    postData.put("year", String.valueOf(((ContentActivity) getActivity()).getListViewItem().dYear));
                                    postData.put("month", String.valueOf(dMonth));
                                    postData.put("day", String.valueOf(((ContentActivity) getActivity()).getListViewItem().dDay));
                                    postData.put("hour", String.valueOf(((ContentActivity) getActivity()).getListViewItem().dHour));
                                    postData.put("minute", String.valueOf(((ContentActivity) getActivity()).getListViewItem().dMin));

                                    PostDB postDB = new PostDB(getActivity());
                                    postDB.putData("get_route_public.php", postData, new OnFinishDBListener() {
                                        @Override
                                        public void onSuccess(String output) {
                                            Log.d("route_public", output);
                                            parsePublic(output);
                                            textView_switch_now.setText("해제");
                                            textView_switch_arrive.setText("설정");
                                            switch_now.setChecked(false);
                                        }
                                    });
                                }
                            });
                    AlertDialog alert = alert_confirm.create();
                    alert.show();
                }else{
                    switch_now.setChecked(true);
                }
            }
        });

        MapLayout mapLayout = new MapLayout(getActivity());
        mMapView = mapLayout.getMapView();
        mMapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY); //api-key 설정

        mMapView.setMapViewEventListener(mapViewEventListener);
        mMapView.setOpenAPIKeyAuthenticationResultListener(openAPIKeyAuthenticationResultListener);
        mMapView.setPOIItemEventListener(poiItemEventListener);
        mMapView.setCurrentLocationEventListener(currentLocationEventListener);

        mMapView.setMapType(MapView.MapType.Standard);

        ViewGroup mapViewContainer = (ViewGroup)view.findViewById(R.id.map_view);
        mapViewContainer.addView(mapLayout);

        //터치 이벤트
        imageView_location.setOnClickListener(this);
        imageView_plus.setOnClickListener(this);
        imageView_minus.setOnClickListener(this);
        imageView_car.setOnClickListener(this);
        imageView_bus.setOnClickListener(this);
        imageView_walk.setOnClickListener(this);
        imageView_detail.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        listView_public.setOnItemClickListener(this);

        poiItemArrayList = new ArrayList<MapPOIItem>();

        HashMap<String, String> postData = new HashMap<String, String>();
        postData.put("startX", String.valueOf(((ContentActivity)getActivity()).getStartPlace().longitude));
        postData.put("startY", String.valueOf(((ContentActivity)getActivity()).getStartPlace().latitude));
        postData.put("endX", String.valueOf(((ContentActivity)getActivity()).getArrivePlace().longitude));
        postData.put("endY", String.valueOf(((ContentActivity)getActivity()).getArrivePlace().latitude));

        String st = ((ContentActivity)getActivity()).getStartPlace().title;
        String et = ((ContentActivity)getActivity()).getArrivePlace().title;
        postData.put("startName", st);
        postData.put("endName", et);
        postData.put("findType", routeType);
        PostDB postDB = new PostDB(getActivity());
        postDB.putData("get_route_WC.php", postData, new OnFinishDBListener() {
            @Override
            public void onSuccess(String output) {
                parse(output);
                showResult(coordiPointArrayList);
                addPolyline(coordiRouteArrayList);
                initFragment();
                hideDetailRouteList(); //기본은 디테일 경로 안보이는거로
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mMapView.setShowCurrentLocationMarker(false);
    }

    MapView.MapViewEventListener mapViewEventListener = new MapView.MapViewEventListener() {
        @Override
        public void onMapViewInitialized(MapView mapView) { //맵 초기화
            Log.e("log", "onMapViewInitialized");
        }

        @Override
        public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) { //지도 중심 좌표가 이동한 경우 호출
            Log.e("log", "onMapViewCenterPointMoved");

        }

        @Override
        public void onMapViewZoomLevelChanged(MapView mapView, int i) { //지도 확대/축소 레벨이 변경된 경우 호출
            if(isShowDetailRouteList()) {
                int zoom = mapView.getZoomLevel();
                if (zoom < 4) {
                    MapPOIItem[] poiItems = mMapView.getPOIItems();
                    for (int index = 1; index < poiItems.length - 1; index++) {
                        poiItems[index].setAlpha(255);
                    }
                } else {
                    MapPOIItem[] poiItems = mMapView.getPOIItems();
                    for (int index = 1; index < poiItems.length - 1; index++) {
                        poiItems[index].setAlpha(0);
                    }
                }
            }else {
                MapPOIItem[] poiItems = mMapView.getPOIItems();
                for (int index = 1; index < poiItems.length - 1; index++) {
                    poiItems[index].setAlpha(0);
                }
            }
            Log.e("log", "onMapViewZoomLevelChanged");
        }

        @Override
        public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) { //사용자가 지도 위를 터치한 경우 호출
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
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
            RoutePoint routePoint = mTagItemMap.get(mapPOIItem.getTag());
            int index = mapPOIItem.getTag();
            adapter.selectItem(index);
            listView.smoothScrollToPositionFromTop(index, 0);
            Log.e("log", "onPOIItemSelected");
        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
            Log.e("log", "onCalloutBalloonOfPOIItemTouched");

        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
            Log.e("log", "onCalloutBalloonOfPOIItemTouched");
            /*ItemPlace itemPlace = mTagItemMap.get(mapPOIItem.getTag());
            StringBuilder sb = new StringBuilder();
            sb.append("title=").append(itemPlace.title).append("\n");
            sb.append("address=").append(itemPlace.address).append("\n");
            sb.append("newAddress=").append(itemPlace.newAddress).append("\n");
            sb.append("longitude=").append(itemPlace.longitude).append("\n");
            sb.append("latitude=").append(itemPlace.latitude).append("\n");
            Toast.makeText(getActivity(), sb.toString(), Toast.LENGTH_SHORT).show();*/
        }

        @Override
        //단말 사용자가 길게 누른 후(long press) 끌어서(dragging) 위치 이동이 가능한 POI Item의 위치를 이동시킨 경우 호출
        public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
            Log.e("log", "onDraggablePOIItemMoved");

        }
    };

    MapView.CurrentLocationEventListener currentLocationEventListener = new MapView.CurrentLocationEventListener() {
        @Override
        public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
            Log.e("log", "onCurrentLocationUpdate");
        }

        @Override
        public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {
            Log.e("log", "onCurrentLocationDeviceHeadingUpdate");

        }

        @Override
        public void onCurrentLocationUpdateFailed(MapView mapView) {
            Log.e("log", "onCurrentLocationUpdateFailed");

        }

        @Override
        public void onCurrentLocationUpdateCancelled(MapView mapView) {
            Log.e("log", "onCurrentLocationUpdateCancelled");

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView_car: //자동차 경로 보기
                if(!routeType.equals("car")) {
                    String netState = Network.getWhatKindOfNetwork(getActivity());
                    if (netState.equals(Network.NONE_STATE)) {
                        Network.connectNetwork(getActivity());
                    } else {
                        coordiPointArrayList.clear();
                        coordiRouteArrayList.clear();
                        routeType = "car";
                        choiceRouteType(routeType);

                        HashMap<String, String> postData = new HashMap<String, String>();
                        postData.put("startX", String.valueOf(((ContentActivity) getActivity()).getStartPlace().longitude));
                        postData.put("startY", String.valueOf(((ContentActivity) getActivity()).getStartPlace().latitude));
                        postData.put("endX", String.valueOf(((ContentActivity) getActivity()).getArrivePlace().longitude));
                        postData.put("endY", String.valueOf(((ContentActivity) getActivity()).getArrivePlace().latitude));

                        postData.put("startName", ((ContentActivity) getActivity()).getStartPlace().title);
                        postData.put("endName", ((ContentActivity) getActivity()).getArrivePlace().title);
                        postData.put("findType", routeType);
                        PostDB postDB = new PostDB(getActivity());
                        postDB.putData("get_route_WC.php", postData, new OnFinishDBListener() {
                            @Override
                            public void onSuccess(String output) {
                                parse(output);
                                showResult(coordiPointArrayList);
                                addPolyline(coordiRouteArrayList);
                                initFragment();
                                textView_price.setVisibility(View.VISIBLE);
                                layout_public.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }
                break;

            case R.id.imageView_bus: //대중교통 경로 보기
                if(!routeType.equals("bus")) {
                    String netState3 = Network.getWhatKindOfNetwork(getActivity());
                    if (netState3.equals(Network.NONE_STATE)) {
                        Network.connectNetwork(getActivity());
                    } else {
                        coordiPointArrayList.clear();
                        coordiRouteArrayList.clear();
                        routeType = "bus";
                        choiceRouteType(routeType);

                        HashMap<String, String> postData = new HashMap<String, String>();
                        postData.put("startX", String.valueOf(((ContentActivity) getActivity()).getStartPlace().longitude));
                        postData.put("startY", String.valueOf(((ContentActivity) getActivity()).getStartPlace().latitude));
                        postData.put("endX", String.valueOf(((ContentActivity) getActivity()).getArrivePlace().longitude));
                        postData.put("endY", String.valueOf(((ContentActivity) getActivity()).getArrivePlace().latitude));
                        if(switch_now.isChecked()){ //현재시간 기준
                            postData.put("type", "now");
                        }else{ //약속 시간 기준
                            postData.put("type", "arrive");
                            postData.put("year", String.valueOf(((ContentActivity) getActivity()).getListViewItem().dYear));
                            int dMonth = ((ContentActivity) getActivity()).getListViewItem().dMonth + 1;
                            postData.put("month", String.valueOf(dMonth));
                            postData.put("day", String.valueOf(((ContentActivity) getActivity()).getListViewItem().dDay));
                            postData.put("hour", String.valueOf(((ContentActivity) getActivity()).getListViewItem().dHour));
                            postData.put("minute", String.valueOf(((ContentActivity) getActivity()).getListViewItem().dMonth));
                        }

                        PostDB postDB = new PostDB(getActivity());
                        postDB.putData("get_route_public.php", postData, new OnFinishDBListener() {
                            @Override
                            public void onSuccess(String output) {
                                Log.d("route_public", output);
                                parsePublic(output);
                                initFragment();
                                layout_public.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
                break;

            case R.id.imageView_walk: //도보 경로 보기
                if(!routeType.equals("walk")) {
                    String netState3 = Network.getWhatKindOfNetwork(getActivity());
                    if (netState3.equals(Network.NONE_STATE)) {
                        Network.connectNetwork(getActivity());
                    } else {
                        if (total_distance < 30000) {
                            coordiPointArrayList.clear();
                            coordiRouteArrayList.clear();
                            routeType = "walk";
                            choiceRouteType(routeType);

                            HashMap<String, String> postData2 = new HashMap<String, String>();
                            postData2.put("startX", String.valueOf(((ContentActivity) getActivity()).getStartPlace().longitude));
                            postData2.put("startY", String.valueOf(((ContentActivity) getActivity()).getStartPlace().latitude));
                            postData2.put("endX", String.valueOf(((ContentActivity) getActivity()).getArrivePlace().longitude));
                            postData2.put("endY", String.valueOf(((ContentActivity) getActivity()).getArrivePlace().latitude));

                            postData2.put("startName", ((ContentActivity) getActivity()).getStartPlace().title);
                            postData2.put("endName", ((ContentActivity) getActivity()).getArrivePlace().title);
                            postData2.put("findType", routeType);
                            PostDB postDB2 = new PostDB(getActivity());
                            postDB2.putData("get_route_WC.php", postData2, new OnFinishDBListener() {
                                @Override
                                public void onSuccess(String output) {
                                    parse(output);
                                    showResult(coordiPointArrayList);
                                    addPolyline(coordiRouteArrayList);
                                    initFragment();
                                    textView_price.setVisibility(View.INVISIBLE);
                                    layout_public.setVisibility(View.INVISIBLE);
                                }
                            });
                        } else {
                            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                            alert_confirm.setMessage("거리가 30km 이내인 경우만 도보 길찾기를 제공합니다").setCancelable(false).setPositiveButton("확인",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                            AlertDialog alert = alert_confirm.create();
                            alert.show();
                        }
                    }
                }
                break;

            case R.id.imageView_detail: //경로 자세히 보기
                showDetailRouteList();
                break;

            case R.id.imageView_location: //현재 위치 버튼
                LocationClass.chkGpsService(getActivity());
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                break;

            case R.id.imageView_plus: //맵 확대 버튼
                //layout_map.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
                mMapView.zoomIn(true);
                break;

            case R.id.imageView_minus: //맵 축소 버튼
                mMapView.zoomOut(true);
                break;
        }
    }

    private void initFragment(){
        //값 대입
        textView_start.setText(((ContentActivity)getActivity()).getStartPlace().title); //출발 장소 이름
        textView_end.setText(((ContentActivity)getActivity()).getArrivePlace().title); //도착 장소 이름
        String st_t = "약 ";
        int minite = total_time / 60; //분
        int sec = total_time % 60; //초
        if(sec >= 30){ //30초 이상이면 +1분
            minite++;
        }
        int hour = minite / 60; //시
        if(hour > 0){ //60분 이상이면 x시를 빼고 분 다시 구함
            minite = minite % 60;
            st_t += hour + "시간 ";
        }
        st_t += minite + "분";
        textView_time.setText(st_t); //쇼요 시간
        float distance = total_distance/1000f;
        textView_distance.setText(String.valueOf(distance) + "km"); //거리
        textView_price.setText("택시비 약 " + String.valueOf(total_price) + "원"); //비용
    }

    private void showResult(ArrayList<RoutePoint> pointList) {
        Log.d("showResult", "pointList size: " + pointList.size());
        mMapView.removeAllPOIItems(); // 기존 검색 결과 삭제
        cameraPositionRoute.clear(); // 기존 루트 카메라 삭제
        adapter.removeAll(); //기존 루트 디테일 정보 삭제
        MapPointBounds mapPointBounds = new MapPointBounds();

        //출발지 POI설정
        RoutePoint item_st = pointList.get(0);

        MapPOIItem poiItem_st = new MapPOIItem();
        poiItem_st.setTag(0);
        poiItem_st.setItemName(((ContentActivity)getActivity()).getStartPlace().title);
        MapPoint mapPoint_st = MapPoint.mapPointWithGeoCoord(item_st.coordiPoint.latitude, item_st.coordiPoint.longitude);
        poiItem_st.setMapPoint(mapPoint_st);
        mapPointBounds.add(mapPoint_st);
        poiItem_st.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        poiItem_st.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
        poiItem_st.setCustomImageResourceId(R.drawable.ic_map_start);
        poiItem_st.setCustomSelectedImageResourceId(R.drawable.ic_map_start_over);
        poiItem_st.setCustomImageAutoscale(false);
        poiItem_st.setCustomImageAnchor(0.5f, 1.0f);

        mMapView.addPOIItem(poiItem_st);
        mTagItemMap.put(poiItem_st.getTag(), item_st);
        adapter.addItem(new RouteDetailListViewItem(item_st.description, item_st.time, "start"));
        CameraPosition cameraPosition_st = new CameraPosition(MapPoint.mapPointWithGeoCoord(item_st.coordiPoint.latitude, item_st.coordiPoint.longitude), cameraLV);
        cameraPositionRoute.add(cameraPosition_st);

        //경로 POI설정
        for (int i = 1; i < pointList.size()-1; i++) {
            RoutePoint item = pointList.get(i);

            MapPOIItem poiItem = new MapPOIItem();
            poiItem.setTag(i);
            if(item.name.equals("")){
                poiItem.setItemName("경로");
            }else {
                poiItem.setItemName(item.name);
            }
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.coordiPoint.latitude, item.coordiPoint.longitude);
            poiItem.setMapPoint(mapPoint);
            mapPointBounds.add(mapPoint);
            poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomImageResourceId(R.drawable.ic_route_point10);
            poiItem.setCustomSelectedImageResourceId(R.drawable.ic_route_point11);
            poiItem.setCustomImageAutoscale(false);
            poiItem.setCustomImageAnchor(0.5f, 0.5f);

            mMapView.addPOIItem(poiItem);
            mTagItemMap.put(poiItem.getTag(), item);
            adapter.addItem(new RouteDetailListViewItem(item.description, item.time, routeType));
            CameraPosition cameraPosition = new CameraPosition(MapPoint.mapPointWithGeoCoord(item.coordiPoint.latitude, item.coordiPoint.longitude), cameraLV);
            cameraPositionRoute.add(cameraPosition);
        }

        //도착지 POI설정
        RoutePoint item_end = pointList.get(pointList.size()-1);

        MapPOIItem poiItem_end = new MapPOIItem();
        poiItem_end.setTag(pointList.size()-1);
        poiItem_end.setItemName(((ContentActivity)getActivity()).getArrivePlace().title);
        MapPoint mapPoint_end = MapPoint.mapPointWithGeoCoord(item_end.coordiPoint.latitude, item_end.coordiPoint.longitude);
        poiItem_end.setMapPoint(mapPoint_end);
        mapPointBounds.add(mapPoint_end);
        poiItem_end.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        poiItem_end.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
        poiItem_end.setCustomImageResourceId(R.drawable.ic_map_arrive);
        poiItem_end.setCustomSelectedImageResourceId(R.drawable.ic_map_arrive_over);
        poiItem_end.setCustomImageAutoscale(false);
        poiItem_end.setCustomImageAnchor(0.5f, 1.0f);

        mMapView.addPOIItem(poiItem_end);
        mTagItemMap.put(poiItem_end.getTag(), item_end);
        adapter.addItem(new RouteDetailListViewItem(item_end.description, item_end.time, "end"));
        CameraPosition cameraPosition_end = new CameraPosition(MapPoint.mapPointWithGeoCoord(item_end.coordiPoint.latitude, item_end.coordiPoint.longitude), cameraLV);
        cameraPositionRoute.add(cameraPosition_end);

        adapter.notifyDataSetChanged();

        //mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));

        MapPOIItem[] poiItems = mMapView.getPOIItems();
        for(int i = 1; i < poiItems.length-1; i++){
            poiItems[i].setAlpha(0);
        }
    }

    private void addPolyline(ArrayList<CoordiPoint> pointList) {
        MapPolyline existingPolyline = mMapView.findPolylineByTag(1000);
        if (existingPolyline != null) {
            mMapView.removePolyline(existingPolyline);
        }

        HandlerAddPolyline handlerAddPolyline = new HandlerAddPolyline();
        ThreadAddPolyline threadAddPolyline = new ThreadAddPolyline(handlerAddPolyline, pointList);
        threadAddPolyline.run();

        /*MapPolyline polyline = new MapPolyline();
        polyline.setTag(1000);
        polyline.setLineColor(Color.argb(255, 64, 119, 255)); // Polyline 컬러 지정.

        // Polyline 좌표 지정.
        for(int i = 0; i < pointList.size(); i++) {
            if(polyline != null) {
                CoordiPoint cp = pointList.get(i);
                MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(cp.latitude, cp.longitude);
                if(mapPoint != null)
                    polyline.addPoint(mapPoint);
            }
        }

        // Polyline 지도에 올리기.
        mMapView.addPolyline(polyline);

        // 지도뷰의 중심좌표와 줌레벨을 Polyline이 모두 나오도록 조정.
        MapPointBounds mapPointBounds = new MapPointBounds(polyline.getMapPoints());
        int padding = 100; // px
        mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));*/
    }

    private class ThreadAddPolyline extends Thread{
        ArrayList<CoordiPoint> pointList;
        public HandlerAddPolyline handler;

        ThreadAddPolyline(HandlerAddPolyline handler, ArrayList<CoordiPoint> pointList){
            this.handler = handler;
            this.pointList = new ArrayList<>();
            this.pointList.addAll(pointList);
        }

        @Override
        public void run() {
            MapPolyline polyline = new MapPolyline();
            polyline.setTag(1000);
            polyline.setLineColor(getResources().getColor(R.color.colorAccent));//Color.argb(255, 64, 119, 255)); // Polyline 컬러 지정.

            // Polyline 좌표 지정.
            for(int i = 0; i < pointList.size(); i++) {
                if(polyline != null) {
                    CoordiPoint cp = pointList.get(i);
                    polyline.addPoint(MapPoint.mapPointWithGeoCoord(cp.latitude, cp.longitude));
                }
            }
            handler.polyline = polyline;
            handler.sendEmptyMessage(0); //핸들러에게 메세지 보냄
        }
    }
    private class HandlerAddPolyline extends Handler{
        public MapPolyline polyline;

        @Override
        public void handleMessage(Message msg) {
            // Polyline 지도에 올리기.
            mMapView.addPolyline(polyline);
            // 지도뷰의 중심좌표와 줌레벨을 Polyline이 모두 나오도록 조정.
            MapPointBounds mapPointBounds = new MapPointBounds(polyline.getMapPoints());
            int padding = 100; // px
            mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));
        }
    }

    private void parse(String jsonString) {
        try {
            JSONObject reader = new JSONObject(jsonString);
            //features 배열을 추출
            JSONArray arrayFeatures = (JSONArray) reader.get("features");
            int line_time = 0; //경로 통과 시간(초)
            for(int i = 0; i < arrayFeatures.length(); i++){
                JSONObject object = arrayFeatures.getJSONObject(i);
                JSONObject object_geo = object.getJSONObject("geometry"); //좌표 정보
                JSONObject object_pro = (JSONObject) object.get("properties"); //이름, 경로 세부 정보
                Log.d("line", "Line "+ i + " type: " + object_geo.getString("type"));

                if(object_geo.getString("type").equals("LineString")) { //구간 정보
                    JSONArray coordinates = (JSONArray) object_geo.get("coordinates"); //좌표
                    line_time += object_pro.getInt("time");
                    Log.d("line", "coordinate num: " + coordinates.length());
                    for(int j = 0; j < coordinates.length(); j++){
                        JSONArray coordinate = (JSONArray) coordinates.get(j);
                        CoordiPoint cp = new CoordiPoint(coordinate.getDouble(1), coordinate.getDouble(0));
                        coordiRouteArrayList.add(cp);
                        Log.d("line", "Point(" + cp.latitude + ", " + cp.longitude + ")");
                    }
                }else if(object_geo.getString("type").equals("Point")) { //안내점 정보
                    JSONArray coordinate = (JSONArray) object_geo.get("coordinates"); //좌표
                    if(object_pro.getString("pointIndex").equals("0")){
                        total_time = Integer.valueOf(object_pro.getString("totalTime"));
                        total_distance = Integer.valueOf(object_pro.getString("totalDistance"));
                        Log.d("line", "total_distance: "+total_distance);
                        if(routeType.equals("car")) {
                            total_price = Integer.valueOf(object_pro.getString("taxiFare"));
                        }
                    }
                    RoutePoint rp = new RoutePoint(coordinate.getDouble(1), coordinate.getDouble(0), object_pro.getString("name"), object_pro.getString("description"), total_time - line_time);
                    coordiPointArrayList.add(rp);
                    Log.d("line", "Point(" + rp.coordiPoint.latitude + ", " + rp.coordiPoint.longitude + ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parsePublic(String jsonString) {
        try {
            JSONObject reader = new JSONObject(jsonString);
            //features 배열을 추출
            JSONArray arrayRoutes = (JSONArray) reader.get("routes");
            adapter_public.removeAll(); //기존 루트 디테일 정보 삭제
            for(int routes_i = 0; routes_i < arrayRoutes.length(); routes_i++){
                Log.d("Num", "routes Num: " + routes_i);
                JSONObject object_routes = arrayRoutes.getJSONObject(routes_i);
                JSONArray arrayLegs = object_routes.getJSONArray("legs"); //경로 정보
                for(int leg_i = 0; leg_i < arrayLegs.length(); leg_i++){
                    Log.d("Num", "legs Num: " + leg_i);
                    JSONObject object_legs = arrayLegs.getJSONObject(leg_i);
                    PublicTrafficListViewItem item = new PublicTrafficListViewItem();
                    item.total_time = object_legs.getJSONObject("duration").getInt("value");
                    item.total_distance = object_legs.getJSONObject("distance").getInt("value");

                    JSONArray arraySteps = object_legs.getJSONArray("steps"); //경로 정보
                    for(int step_i = 0; step_i < arraySteps.length(); step_i++){
                        Log.d("Num", "steps Num: " + step_i);
                        JSONObject object_steps = arraySteps.getJSONObject(step_i);
                        float distance_weight = (object_steps.getJSONObject("distance").getInt("value"))/1000f; //세부 경로 거리
                        item.arrayList_distance.add(distance_weight);

                        String transit_mode = object_steps.getString("travel_mode"); //교통 타입
                        JSONObject object_detail; //세부 경로 내용

                        String str_route_name;
                        String vehicle_type;
                        String line_type = "";
                        if(transit_mode.equals("TRANSIT")){ //대중교통
                            object_detail = object_steps.getJSONObject("transit_details"); //세부 경로 내용
                            String start_stop = object_detail.getJSONObject("departure_stop").getString("name");
                            String end_stop = object_detail.getJSONObject("arrival_stop").getString("name");
                            try {
                                line_type = object_detail.getJSONObject("line").getString("short_name");
                            }catch (JSONException e){
                                line_type = object_detail.getJSONObject("line").getString("name");
                            }
                            vehicle_type = object_detail.getJSONObject("line").getJSONObject("vehicle").getString("type");

                            str_route_name = line_type + " (" + start_stop + "->" + end_stop + ")";
                        }else if(transit_mode.equals("WALKING")){
                            int walk_time = object_steps.getJSONObject("duration").getInt("value");
                            item.total_walk_time += walk_time;

                            vehicle_type = "WALKING";

                            str_route_name = "도보";// (약 " + String.valueOf(walk_time) + "분)";
                        }else{
                            vehicle_type = "OTHER";
                            str_route_name = "기타";
                        }
                        if(step_i < arraySteps.length() - 1) {
                            str_route_name += " >> ";
                        }

                        item.arrayList_route_name.add(str_route_name);
                        item.arrayList_vehicle_type.add(vehicle_type);
                        item.arrayList_line_type.add(line_type);

                        //item.total_price = item.arrayList_route_name.size();
                    }
                    boolean isSameRoute = false;
                    Log.d("test", "item.total_distance: " + item.total_distance);
                    for(int i = 0; i < adapter_public.getCount(); i++){
                        PublicTrafficListViewItem compare_item = (PublicTrafficListViewItem)adapter_public.getItem(i);
                        Log.d("test", "compare_item.total_distance: " + compare_item.total_distance);
                        if(item.total_distance == compare_item.total_distance){
                            isSameRoute = true;
                            break;
                        }
                    }
                    if(!isSameRoute) {
                        adapter_public.addItem(item);
                    }
                }
            }
            adapter_public.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDetailRouteList(){
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        //경로 종류 선택창 숨기기
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)layout_route_type.getLayoutParams();
        params.height = 0;//ViewGroup.LayoutParams.WRAP_CONTENT;
        layout_route_type.setLayoutParams(params);
        //소요시간, 거리, 비용 레이아웃 숨기기
        layout_info1.setVisibility(View.INVISIBLE);
        //경로 디테일 정보 보이기
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        ViewGroup.LayoutParams params_listView = listView.getLayoutParams();
        params_listView.height = size.y / 4;
        listView.setLayoutParams(params_listView);

        MapPOIItem[] poiItems = mMapView.getPOIItems();
        mMapView.selectPOIItem(poiItems[0], false);
        adapter.selectItem(0);
        listView.smoothScrollToPositionFromTop(0, 0);
        mMapView.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositionRoute.get(0)), 500, new CancelableCallback() {
            @Override
            public void onFinish() {
            }
            @Override
            public void onCancel() {
            }
        });
    }

    public void hideDetailRouteList(){
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        //경로 종류 선택창 보이기
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)layout_route_type.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layout_route_type.setLayoutParams(params);
        //소요시간, 거리, 비용 레이아웃 보이기
        layout_info1.setVisibility(View.VISIBLE);
        //경로 디테일 정보 숨기기
        ViewGroup.LayoutParams params_listView = listView.getLayoutParams();
        params_listView.height = 0;
        listView.setLayoutParams(params_listView);

       /* MapPolyline polyline = mMapView.findPolylineByTag(1000);
        MapPointBounds mapPointBounds = new MapPointBounds(polyline.getMapPoints());
        int padding = 100; // px

        mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));*/
        MapPOIItem[] poiItems = mMapView.getPOIItems();
        for(int i = 0; i < poiItems.length; i++) {
            mMapView.deselectPOIItem(poiItems[i]);
        }
        adapter.selectItem(-1); //모든 리스트 선택 해제
        listView.smoothScrollToPositionFromTop(0, 0);
    }

    public void cameraCenter(){
        MapPolyline polyline = mMapView.findPolylineByTag(1000);
        MapPointBounds mapPointBounds = new MapPointBounds(polyline.getMapPoints());
        int padding = 100; // px

        mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));
    }

    public boolean isShowDetailRouteList(){ //경로 디테일 리스트가 보이면 리턴 true 안보이면 false
        if(listView.getHeight() != 0)
            return true;
        else
            return false;
    }

    public void choiceRouteType(String type){
        if(type.equals("car")){
            Drawable drawable_car = getActivity().getResources().getDrawable(R.drawable.ic_car1);
            imageView_car.setBackgroundDrawable(drawable_car);
            Drawable drawable_bus = getActivity().getResources().getDrawable(R.drawable.ic_bus);
            imageView_bus.setBackgroundDrawable(drawable_bus);
            Drawable drawable_walk = getActivity().getResources().getDrawable(R.drawable.ic_walk);
            imageView_walk.setBackgroundDrawable(drawable_walk);
        }else if(type.equals("bus")){
            Drawable drawable_car = getActivity().getResources().getDrawable(R.drawable.ic_car);
            imageView_car.setBackgroundDrawable(drawable_car);
            Drawable drawable_bus = getActivity().getResources().getDrawable(R.drawable.ic_bus1);
            imageView_bus.setBackgroundDrawable(drawable_bus);
            Drawable drawable_walk = getActivity().getResources().getDrawable(R.drawable.ic_walk);
            imageView_walk.setBackgroundDrawable(drawable_walk);
        }else if(type.equals("walk")){
            Drawable drawable_car = getActivity().getResources().getDrawable(R.drawable.ic_car);
            imageView_car.setBackgroundDrawable(drawable_car);
            Drawable drawable_bus = getActivity().getResources().getDrawable(R.drawable.ic_bus);
            imageView_bus.setBackgroundDrawable(drawable_bus);
            Drawable drawable_walk = getActivity().getResources().getDrawable(R.drawable.ic_walk1);
            imageView_walk.setBackgroundDrawable(drawable_walk);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int index, long id) {
        switch (parent.getId()){
            case R.id.listView:
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                MapPOIItem[] poiItems = mMapView.getPOIItems();
                mMapView.selectPOIItem(poiItems[index], false);
                adapter.selectItem(index);
                listView.smoothScrollToPositionFromTop(index, 0);
                mMapView.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositionRoute.get(index)), 500, new CancelableCallback() {
                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onCancel() {
                    }
                });
                break;

            case R.id.listView_public:
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                    String str_message = "대중교통 경로를 자세히 보기 위해서 다음지도 어플로 이동합니다\n어플이 설치 되지 않은 경우 설치 패이지로 이동합니다.";
                    alert_confirm.setMessage(str_message).setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(String.format("daummaps://route?sp=%f,%f&ep=%f,%f&by=PUBLICTRANSIT",
                                            ((ContentActivity) getActivity()).getStartPlace().latitude, ((ContentActivity) getActivity()).getStartPlace().longitude,
                                            ((ContentActivity) getActivity()).getArrivePlace().latitude, ((ContentActivity) getActivity()).getArrivePlace().longitude)));
                                    //intent.setData(Uri.parse(""));
                                    try{
                                        getActivity().startActivity(intent);
                                    }catch (ActivityNotFoundException e){
                                        intent.setData(Uri.parse(String.format("market://details?id=net.daum.android.map")));
                                        getActivity().startActivity(intent);

                                    }
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
    }
}