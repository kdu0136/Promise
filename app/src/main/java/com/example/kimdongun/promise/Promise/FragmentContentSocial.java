package com.example.kimdongun.promise.Promise;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kimdongun.promise.DaumMapAPI.MapApiConst;
import com.example.kimdongun.promise.Location.LocationClass;
import com.example.kimdongun.promise.Main.MainActivity;
import com.example.kimdongun.promise.PromiseSocialListViewAdapter;
import com.example.kimdongun.promise.PromiseSocialListViewItem;
import com.example.kimdongun.promise.R;
import com.example.kimdongun.promise.search.ItemPlace;
import com.example.kimdongun.promise.serverDB.OnFinishDBListener;
import com.example.kimdongun.promise.serverDB.PostDB;

import net.daum.mf.map.api.CameraPosition;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.CancelableCallback;
import net.daum.mf.map.api.MapLayout;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.kimdongun.promise.Account.myAccount;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by KimDongun on 2016-12-30.
 */

public class FragmentContentSocial extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener{

    private int index;
    private ItemPlace arrivePlace; //도착 장소

    private MapView mMapView; //맵
    ArrayList<MapPOIItem> poiItemArrayList;

    private ImageView imageView_location; //현재 위치 버튼
    private ImageView imageView_plus; //맵 확대 버튼
    private ImageView imageView_minus; //맵 축소 버튼
    private ImageView imageView_arrive; //도착 장소 버튼

    private ListView listView; //친구 리스트
    public TextView textView_no_friend; //친구 없음 텍스트
    private PromiseSocialListViewAdapter adapter; //친구 어뎁터

    private HashMap<Integer, PromiseSocialListViewItem> mTagItemMap;
    private HashMap<Integer, CameraPosition> cameraPositionRoute;//카메라 에니메이션

    private int cameraLV; //카메라 에니메이션 맵 레벨

    private SocialThread thread;
    private SocialHandler handler;

    public FragmentContentSocial() {
        // Required empty public constructor
    }
    @Override
    public String toString() {
        return "FragmentContentSocial";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_content_social, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //초기화
        index = ((ContentActivity) getActivity()).index;
        arrivePlace = ((ContentActivity)getActivity()).getArrivePlace();
        adapter = new PromiseSocialListViewAdapter(getActivity()); //친구 어뎁터
        listView = (ListView)view.findViewById(R.id.listView); //친구 리스트뷰
        listView.setAdapter(adapter);
        textView_no_friend = (TextView)view.findViewById(R.id.textView_no_friend); //친구없음 텍스트
        imageView_location = (ImageView)view.findViewById(R.id.imageView_location); //현재 위치 버튼
        imageView_plus = (ImageView)view.findViewById(R.id.imageView_plus); //맵 확대 버튼
        imageView_minus = (ImageView)view.findViewById(R.id.imageView_minus); //맵 축소 버튼
        imageView_arrive = (ImageView)view.findViewById(R.id.imageView_arrive);//도착 버튼

        mTagItemMap = new HashMap<Integer, PromiseSocialListViewItem>();
        cameraPositionRoute = new HashMap<Integer, CameraPosition>();
        cameraLV = 1;

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
        listView.setOnItemClickListener(this);
        imageView_location.setOnClickListener(this);
        imageView_plus.setOnClickListener(this);
        imageView_minus.setOnClickListener(this);
        imageView_arrive.setOnClickListener(this);

        handler = new SocialHandler(getActivity());
        thread = new SocialThread(handler);
        thread.start();
    }

    @Override
    public void onDestroy() {
        thread.stopForever();
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        super.onDestroy();
    }

    MapView.MapViewEventListener mapViewEventListener = new MapView.MapViewEventListener() {
        @Override
        public void onMapViewInitialized(MapView mapView) { //맵 초기화
            Log.e("log", "onMapViewInitialized");
            LocationClass.chkGpsService(getActivity());
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
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
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            adapter.selectItem(-1);
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
            PromiseSocialListViewItem routePoint = mTagItemMap.get(mapPOIItem.getTag());
            int index = mapPOIItem.getTag();
            adapter.selectItem(index-1);
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

    private void parse(String jsonString) {
        try {
            JSONObject reader = new JSONObject(jsonString);
            JSONArray objects = reader.getJSONArray("social");
            int select_index = -1;
            for(int i = 0; i < adapter.getCount(); i++){
                PromiseSocialListViewItem item = (PromiseSocialListViewItem)adapter.getItem(i);
                if(item.isSelect){
                    select_index = i;
                    break;
                }
            }
            Log.d("Select Inded: ", ""+select_index);
            adapter.removeAll();
            for (int i = 0; i < objects.length(); i++) {
                JSONObject object = objects.getJSONObject(i);
                PromiseSocialListViewItem item = new PromiseSocialListViewItem(); //약속 시간등 정보
                if(i == select_index){
                    item.isSelect = true;
                }
                item.no = Integer.valueOf(object.getString("no"));
                item.mail = object.getString("mail");
                item.name = object.getString("name");
                item.type = object.getString("type");
                double lat = Double.valueOf(object.getString("lat"));
                double lon = Double.valueOf(object.getString("lon"));
                item.locationON = Integer.valueOf(object.getString("locationON"));
                if(item.locationON == 0){ //실시간 위치 공유 꺼진 계정? -> 출발지를 장소로
                    lat = Double.valueOf(object.getString("lat_s"));
                    lon = Double.valueOf(object.getString("lon_s"));
                }
                item.lat = lat;
                item.lon = lon;
                int isStart = Integer.valueOf(object.getString("isStart"));
                int isAccept = Integer.valueOf(object.getString("isAccept"));
                if(isAccept == 0) { //수락 대기
                    item.state = "수락 대기";
                }else{
                    if(isStart == 0){ //미 출발
                        item.state = "미 출발";
                    }else{ //출발
                        item.state = "출발";
                    }
                }
                adapter.addItem(item);
            }
            adapter.notifyDataSetChanged();
            if (adapter.getCount() == 0) {
                textView_no_friend.setVisibility(View.VISIBLE);
            } else {
                textView_no_friend.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showResult() {
        mMapView.removeAllPOIItems(); // 기존 검색 결과 삭제
        cameraPositionRoute.clear(); // 기존 루트 카메라 삭제
        MapPointBounds mapPointBounds = new MapPointBounds();

        //도착지 POI설정
        PromiseSocialListViewItem item_end = new PromiseSocialListViewItem();
        item_end.name = arrivePlace.title;
        item_end.lat = arrivePlace.latitude;
        item_end.lon = arrivePlace.longitude;

        MapPOIItem poiItem_end = new MapPOIItem();
        poiItem_end.setTag(0);
        poiItem_end.setItemName("도착: "+ item_end.name);
        MapPoint mapPoint_end = MapPoint.mapPointWithGeoCoord(item_end.lat, item_end.lon);
        poiItem_end.setMapPoint(mapPoint_end);
        mapPointBounds.add(mapPoint_end);
        poiItem_end.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        poiItem_end.setCustomImageResourceId(R.drawable.ic_map_arrive);
        poiItem_end.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
        poiItem_end.setCustomSelectedImageResourceId(R.drawable.ic_map_arrive_over);
        poiItem_end.setCustomImageAutoscale(false);
        poiItem_end.setCustomImageAnchor(0.5f, 1.0f);

        mMapView.addPOIItem(poiItem_end);
        mTagItemMap.put(poiItem_end.getTag(), item_end);

        CameraPosition cameraPosition_st = new CameraPosition(MapPoint.mapPointWithGeoCoord(item_end.lat, item_end.lon), cameraLV);
        cameraPositionRoute.put(poiItem_end.getTag(), cameraPosition_st);

        //친구들 POI설정
        for (int i = 0; i < adapter.getCount(); i++) {
            PromiseSocialListViewItem item = (PromiseSocialListViewItem)adapter.getItem(i);
            if(item.state.equals("출발")) {
                MapPOIItem poiItem = new MapPOIItem();
                poiItem.setTag(i + 1);
                poiItem.setItemName(item.name);
                MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.lat, item.lon);
                poiItem.setMapPoint(mapPoint);
                mapPointBounds.add(mapPoint);

                MapPOIItem.MarkerType markerType;
                if (i % 3 == 0) {
                    markerType = MapPOIItem.MarkerType.BluePin;
                } else if (i % 3 == 1) {
                    markerType = MapPOIItem.MarkerType.RedPin;
                } else {
                    markerType = MapPOIItem.MarkerType.YellowPin;
                }
                poiItem.setMarkerType(markerType);
                poiItem.setSelectedMarkerType(markerType);
                //poiItem.setCustomImageResourceId(R.drawable.ic_route_point);
                //poiItem.setCustomSelectedImageResourceId(R.drawable.ic_route_point1);
                poiItem.setCustomImageAutoscale(false);
                poiItem.setCustomImageAnchor(0.5f, 0.5f);

                mMapView.addPOIItem(poiItem);
                mTagItemMap.put(poiItem.getTag(), item);
                CameraPosition cameraPosition = new CameraPosition(MapPoint.mapPointWithGeoCoord(item.lat, item.lon), cameraLV);
                cameraPositionRoute.put(poiItem.getTag(), cameraPosition);
            }
        }
        MapPOIItem[] poiItems = mMapView.getPOIItems();
        for(int i = 0; i < adapter.getCount(); i++) {
            PromiseSocialListViewItem item = (PromiseSocialListViewItem)adapter.getItem(i);
            if(item.isSelect) {
                mMapView.selectPOIItem(poiItems[i + 1], false);
                listView.smoothScrollToPositionFromTop(i, 0);
                mMapView.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositionRoute.get(i+1)), 500, new CancelableCallback() {
                    @Override
                    public void onFinish() {
                    }
                    @Override
                    public void onCancel() {
                    }
                });
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
        if(parent.equals(listView)) {
            final PromiseSocialListViewItem item = ((PromiseSocialListViewItem) adapter.getItem(index));
            if(item.state.equals("출발")){
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                MapPOIItem[] poiItems = mMapView.getPOIItems();
                mMapView.selectPOIItem(poiItems[index+1], false);
                adapter.selectItem(index);
                listView.smoothScrollToPositionFromTop(index, 0);
                mMapView.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositionRoute.get(index+1)), 500, new CancelableCallback() {
                    @Override
                    public void onFinish() {
                    }
                    @Override
                    public void onCancel() {
                    }
                });
            }else { //미출발 또는 미수락
                /*HashMap<String, String> postData = new HashMap<>();
                postData.put("no", String.valueOf(myAccount.getIndex()));
                postData.put("f_no", String.valueOf(item.no));

                PostDB postDB = new PostDB(getActivity());
                postDB.prograssBar = false;
                postDB.putData("select_friend_state.php", postData, new OnFinishDBListener() {
                    @Override
                    public void onSuccess(String output) {
                        AddFriendListViewItem item2 = new AddFriendListViewItem(item.mail, item.name, item.type, item.no, output);
                        ContentFriendDialog contentItemDialog = new ContentFriendDialog(getActivity(), item2);
                        contentItemDialog.show();
                    }
                });*/
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView_location: //현재 위치 버튼
                LocationClass.chkGpsService(getActivity());
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                adapter.selectItem(-1);
                break;

            case R.id.imageView_plus: //맵 확대 버튼
                //layout_map.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
                mMapView.zoomIn(true);
                break;

            case R.id.imageView_minus: //맵 축소 버튼
                mMapView.zoomOut(true);
                break;

            case R.id.imageView_arrive: //도착 장소 버튼
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                MapPOIItem[] poiItems = mMapView.getPOIItems();
                mMapView.selectPOIItem(poiItems[0], false);
                adapter.selectItem(-1);
                listView.smoothScrollToPositionFromTop(0, 0);
                mMapView.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositionRoute.get(0)), 500, new CancelableCallback() {
                    @Override
                    public void onFinish() {
                    }
                    @Override
                    public void onCancel() {
                    }
                });
                break;
        }
    }


    class SocialThread extends Thread {
        public Handler handler;
        boolean isRun = true;

        public SocialThread(Handler handler){
            this.handler = handler;
        }

        public void stopForever(){
            synchronized (this){
                this.isRun = false;
            }
        }

        public void run(){
            //반복으로 수행할 작업
            Log.d("SocialThread", "시작");
            while (isRun){
                handler.sendEmptyMessage(0);
                try {
                    Thread.sleep(1000); //1분마다
                }catch (Exception e){}
            }
            Log.d("SocialThread", "종료");
        }
    }

    class SocialHandler extends Handler {
        Context context;
        public SocialHandler(Context context){
            this.context = context;
        }
        @Override
        public void handleMessage(Message msg) {
            if(thread.isRun) {
                HashMap<String, String> postData = new HashMap<>();
                postData.put("no", String.valueOf(index));
                postData.put("m_no", String.valueOf(myAccount.getIndex()));

                PostDB postDB = new PostDB();
                postDB.putData("load_promise_social.php", postData, new OnFinishDBListener() {
                    @Override
                    public void onSuccess(String output) {
                        if(output.equals("null")){
                            Toast.makeText(getApplicationContext(),"잘못된 접근입니다.",Toast.LENGTH_LONG).show();
                            myAccount.load_Data();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            getActivity().startActivity(intent);
                            getActivity().finish();
                        }else {
                            parse(output);
                            showResult();
                            Log.d("SocialHandler", "갱신");
                        }
                    }
                });
            }
        }
    }
}