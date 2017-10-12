package com.example.kimdongun.promise.Promise;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kimdongun.promise.DaumMapAPI.MapApiConst;
import com.example.kimdongun.promise.Location.LocationClass;
import com.example.kimdongun.promise.Network.Network;
import com.example.kimdongun.promise.R;
import com.example.kimdongun.promise.search.ItemPlace;
import com.example.kimdongun.promise.search.OnFinishSearchListener;
import com.example.kimdongun.promise.search.Searcher;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapLayout;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by KimDongun on 2016-12-30.
 */


public class FragmentEditMap extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    public static final int ARRIVE = 0;
    public static final int START = 1;

    public int TYPE_OF_MAP = ARRIVE; //약속 장소 선택인지 약속 출발 선택인지

    private ItemPlace selectPlace; //선택 장소

    private FrameLayout frame_list; //서치 리스트 레이아웃(프래임)
    private LinearLayout frame_map; //맵 레이아웃(프래임)

    private MapView mMapView; //맵
    private RelativeLayout layout_info; //장소 정보 레이아웃

    private ImageView imageView_location; //현재 위치 버튼
    private ImageView imageView_plus; //맵 확대 버튼
    private ImageView imageView_minus; //맵 축소 버튼

    private SearchView searchView; //검색 창
    private ImageView buttonSearch; //검색 버튼
    private String searchText; //검색 텍스트

    private ListView listView; //서치 리스트
    private ArrayAdapter<String> adapter; //서치 리스트 어댑터
    private ArrayList<String> searchStrList; //검색 결과 이름 리스트
    private ArrayList<ItemPlace> searchList; //검색 결과 정보 리스트

    private TextView textView_place_name; //장소 이름
    private TextView textView_place_address; //장소 세부 주소
    private Button button_select; //약속 장소 선택 버튼
    private Button button_select2; //출발 장소 선택 버튼

    private HashMap<Integer, ItemPlace> mTagItemMap = new HashMap<Integer, ItemPlace>();

    public FragmentEditMap() {
        // Required empty public constructor
    }

    @Override
    public String toString() {
        return "FragmentEditMap";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_add_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        //초기화
        selectPlace = new ItemPlace();
        frame_list = (FrameLayout)view.findViewById(R.id.frame_list); //서치 리스트 레이아웃(프래임)
        frame_map = (LinearLayout)view.findViewById(R.id.frame_map);//맵 레이아웃(프래임)
        frame_list.setVisibility(View.INVISIBLE);
        frame_map.setVisibility(View.VISIBLE);

        layout_info = (RelativeLayout)view.findViewById(R.id.layout_info); //장소 정도도 레이아웃
        imageView_location = (ImageView)view.findViewById(R.id.imageView_location); //현재 위치 버튼
        imageView_plus = (ImageView)view.findViewById(R.id.imageView_plus); //맵 확대 버튼
        imageView_minus = (ImageView)view.findViewById(R.id.imageView_minus); //맵 축소 버튼
        searchView=(SearchView)view.findViewById(R.id.searchView); //검색 창
        searchView.setQueryHint("장소 검색");
        searchText = "";  //검색 텍스트
        listView = (ListView)view.findViewById(R.id.listView); //서치 리스트
        searchStrList = new ArrayList<String>(); //검색 결과 이름 리스트
        searchList = new ArrayList<ItemPlace>(); //검색 결과 정보리스트
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, searchStrList){
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the current item from ListView
                View view = super.getView(position,convertView,parent);

                // Get the Layout Parameters for ListView Current Item View
                ViewGroup.LayoutParams params = view.getLayoutParams();

                // Set the height of the Item View
                params.height = 150;
                view.setLayoutParams(params);
                return view;
            }
        }; //서치 리스트 어댑터
        listView.setAdapter(adapter);

        buttonSearch = (ImageView)view.findViewById(R.id.buttonSearch); //검색 버튼
        textView_place_name = (TextView)view.findViewById(R.id.textView_place_name); //장소 이름
        textView_place_address = (TextView) view.findViewById(R.id.textView_place_address); //장소 세부 주소

        button_select = (Button)view.findViewById(R.id.button_select); //약속 장소 선택 버튼
        button_select2 = (Button)view.findViewById(R.id.button_select2); //출발 장소 선택 버튼
        if(TYPE_OF_MAP == ARRIVE){
            button_select.setVisibility(View.VISIBLE);
            button_select2.setVisibility(View.INVISIBLE);
            selectPlace.setItem(((EditActivity)getActivity()).getArrivePlace());
        }else if(TYPE_OF_MAP == START){
            button_select.setVisibility(View.INVISIBLE);
            button_select2.setVisibility(View.VISIBLE);
            selectPlace.setItem(((EditActivity)getActivity()).getStartPlace());
        }

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
        buttonSearch.setOnClickListener(this);
        button_select.setOnClickListener(this);
        button_select2.setOnClickListener(this);
        listView.setOnItemClickListener(this);

        //검색 이벤트
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchText = query;
                searchPlace();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                return false;
            }
        });

        poiItemArrayList = new ArrayList<MapPOIItem>();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mMapView.setShowCurrentLocationMarker(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.menu_add_place, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

        }
        return super.onOptionsItemSelected(item);
    }

    MapView.MapViewEventListener mapViewEventListener = new MapView.MapViewEventListener() {
        @Override
        public void onMapViewInitialized(MapView mapView) { //맵 초기화
            Log.e("log", "onMapViewInitialized");
            if(selectPlace.title == "") {
                LocationClass.chkGpsService(getActivity());
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            }
            else{
                MapPointBounds mapPointBounds = new MapPointBounds();

                MapPOIItem poiItem = new MapPOIItem();
                poiItem.setItemName(selectPlace.title);
                poiItem.setTag(0);
                MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(selectPlace.latitude, selectPlace.longitude);
                poiItem.setMapPoint(mapPoint);
                mapPointBounds.add(mapPoint);
                poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomImageResourceId(R.drawable.map_pin_blue);
                poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomSelectedImageResourceId(R.drawable.map_pin_red);
                poiItem.setCustomImageAutoscale(false);
                poiItem.setCustomImageAnchor(0.5f, 1.0f);

                mMapView.addPOIItem(poiItem);
                mTagItemMap.put(poiItem.getTag(), selectPlace);

                mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));
                mMapView.selectPOIItem(poiItem, false);

                textView_place_name.setText(selectPlace.title);;
                textView_place_address.setText(selectPlace.address);
            }
            // 중심점 변경
            /*mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.484793511299706, 126.97091020065321), true);

            MapPOIItem marker = new MapPOIItem();
            marker.setItemName("Default Marker");
            marker.setTag(0);
            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633));
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

            mMapView.addPOIItem(marker);*/
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
            hideSoftKeyboard();
            if(layout_info.getHeight() != 0) {
                //layout_info.setMinimumHeight(0);
                //layout_info.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)layout_info.getLayoutParams();
                params.height = 0;
                layout_info.setLayoutParams(params);
            }
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
            hideSoftKeyboard();
            ItemPlace itemPlace = mTagItemMap.get(mapPOIItem.getTag());
            textView_place_name.setText(itemPlace.title);
            if(itemPlace.address == "") //상세 주소가 비어있을 경우 상세 주소 구함
                itemPlace.address = LocationClass.findAddress(getActivity(), itemPlace.latitude, itemPlace.longitude);
            textView_place_address.setText(itemPlace.address);
            if(layout_info.getHeight() == 0) {
                //layout_info.setMinimumHeight(INFO_HEIGHT);
                //layout_info.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, INFO_HEIGHT));
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)layout_info.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                layout_info.setLayoutParams(params);
            }
            Log.e("log", "onPOIItemSelected");

            selectPlace.setItem(itemPlace);
            Log.e("place", "title: "+selectPlace.title);
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

            case R.id.buttonSearch: //검색 버튼
                searchPlace();
                break;

            case R.id.button_select: //장소 선택 버튼
                ((EditActivity)getActivity()).setArrivePlace(selectPlace);
                ((EditActivity)getActivity()).switchFragment("FragmentEditMain");
                break;

            case R.id.button_select2: //장소 선택 버튼
                ((EditActivity)getActivity()).setStartPlace(selectPlace);
                ((EditActivity)getActivity()).switchFragment("FragmentEditMain");
                break;
        }
    }

    public boolean isShowSearchList(){ //서치 리스트가 보이면 리턴 true 안보이면 false
        if(frame_list.getVisibility() == View.VISIBLE)
            return true;
        else
            return false;
    }

    public void hideSearchList(){
        frame_list.setVisibility(View.INVISIBLE);
        frame_map.setVisibility(View.VISIBLE);
    }

    private void searchPlace(){
        String netState = Network.getWhatKindOfNetwork(getActivity());
        if(netState.equals(Network.NONE_STATE)){
            Network.connectNetwork(getActivity());
        }else {
            if (searchText == null || searchText.length() == 0) {
                Toast.makeText(getActivity(), "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            hideSoftKeyboard(); // 키보드 숨김
            MapPoint.GeoCoordinate geoCoordinate = mMapView.getMapCenterPoint().getMapPointGeoCoord();
            double latitude = geoCoordinate.latitude; // 위도
            double longitude = geoCoordinate.longitude; // 경도
            int radius = 10000; // 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
            int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개
            final String apikey = MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY;

            final Searcher searcher = new Searcher(); // net.daum.android.map.openapi.search.Searcher
            searcher.searchKeyword(getActivity(), searchText, latitude, longitude, radius, page, apikey, new OnFinishSearchListener() {
                @Override
                public void onSuccess(List<ItemPlace> itemList) {
                    //mMapView.removeAllPOIItems(); // 기존 검색 결과 삭제
                    if (itemList.size() == 0) {
                        searcher.searchAddress(getActivity(), searchText, apikey, new OnFinishSearchListener() {
                            @Override
                            public void onSuccess(List<ItemPlace> itemList) {
                                //mMapView.removeAllPOIItems(); // 기존 검색 결과 삭제
                                if (itemList.size() == 0)
                                    Toast.makeText(getActivity(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                                else
                                    showResult(itemList); // 검색 결과 보여줌
                            }

                            @Override
                            public void onFail() {
                                Toast.makeText(getActivity(), "검색에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        showResult(itemList); // 검색 결과 보여줌
                    }
                }

                @Override
                public void onFail() {
                    Toast.makeText(getActivity(), "검색에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    ArrayList<MapPOIItem> poiItemArrayList;
    private void showResult(List<ItemPlace> itemList) {
        MapPointBounds mapPointBounds = new MapPointBounds();
        poiItemArrayList.clear();
        searchList.clear();
        searchStrList.clear();

        for (int i = 0; i < itemList.size(); i++) {
            ItemPlace itemPlace = itemList.get(i);
            searchList.add(itemPlace);
            searchStrList.add(itemPlace.title);

            MapPOIItem poiItem = new MapPOIItem();
            poiItem.setItemName(itemPlace.title);
            poiItem.setTag(i);
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(itemPlace.latitude, itemPlace.longitude);
            poiItem.setMapPoint(mapPoint);
            mapPointBounds.add(mapPoint);
            poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomImageResourceId(R.drawable.map_pin_blue);
            poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomSelectedImageResourceId(R.drawable.map_pin_red);
            poiItem.setCustomImageAutoscale(false);
            poiItem.setCustomImageAnchor(0.5f, 1.0f);

            poiItemArrayList.add(poiItem);
            //mMapView.addPOIItem(poiItem);
            //mTagItemMap.put(poiItem.getTag(), item);
        }

        if (frame_list.getVisibility() == View.INVISIBLE) {
            frame_list.setVisibility(View.VISIBLE);
            frame_map.setVisibility(View.INVISIBLE);
        }
        adapter.notifyDataSetChanged();
        /*mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));

        MapPOIItem[] poiItems = mMapView.getPOIItems();
        if (poiItems.length > 0) {
            mMapView.selectPOIItem(poiItems[0], false);
        }*/
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
        if (frame_list.getVisibility() == View.VISIBLE) {
            frame_list.setVisibility(View.INVISIBLE);
            frame_map.setVisibility(View.VISIBLE);
        }
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mMapView.removeAllPOIItems(); // 기존 검색 결과 삭제
        for(int i = 0; i < searchList.size(); i++){
            MapPOIItem poiItem = poiItemArrayList.get(i);
            ItemPlace itemPlace = searchList.get(i);

            mMapView.addPOIItem(poiItem);
            mTagItemMap.put(poiItem.getTag(), itemPlace);
        }
        MapPointBounds mapPointBounds = new MapPointBounds();
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(searchList.get(index).latitude, searchList.get(index).longitude);
        mapPointBounds.add(mapPoint);

        mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));

        MapPOIItem[] poiItems = mMapView.getPOIItems();
        if (poiItems.length > 0) {
            mMapView.selectPOIItem(poiItems[index], false);
        }

        searchView.setQuery(searchList.get(index).title,false);
        textView_place_name.setText(searchList.get(index).title);
        if(searchList.get(index).address == "") //상세 주소가 비어있을 경우 상세 주소 구함
            searchList.get(index).address = LocationClass.findAddress(getActivity(), searchList.get(index).latitude, searchList.get(index).longitude);
        textView_place_address.setText(searchList.get(index).address);
        if(layout_info.getHeight() == 0) {
            //layout_info.setMinimumHeight(INFO_HEIGHT);
            //layout_info.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, INFO_HEIGHT));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)layout_info.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layout_info.setLayoutParams(params);
        }

        selectPlace.setItem(searchList.get(index));
    }
}