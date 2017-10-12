package com.example.kimdongun.promise;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KimDongun on 2016-10-03.
 */

//대중교통 리스트 아이템
public class PublicTrafficListViewItem implements Serializable {
    //아이템 추가시 입력되는 값
    public int total_time;
    public int total_distance;
    public int total_walk_time;
    public int total_price;

    public ArrayList<Float> arrayList_distance; //각 경로 거리
    public ArrayList<String> arrayList_route_name; //경로 이름
    public ArrayList<String> arrayList_vehicle_type; //경로 타입 ex)버스, 지하철, 도보 등
    public ArrayList<String> arrayList_line_type; //라인 번호 ex) 7호선, 501번 버스

    public boolean isSelect; //선택 된 거?

    public PublicTrafficListViewItem(){
        this.total_time = 0;
        this.total_distance = 0;
        this.total_walk_time = 0;
        this.total_price = 0;

        this.arrayList_distance = new ArrayList<>();
        this.arrayList_route_name = new ArrayList<>();
        this.arrayList_vehicle_type = new ArrayList<>();
        this.arrayList_line_type = new ArrayList<>();

        isSelect = false;
    }

    public PublicTrafficListViewItem(int total_time, int total_distance, int total_walk_time, int total_price,
                                     ArrayList<Float> arrayList_distance, ArrayList<String> arrayList_route_name, ArrayList<String> arrayList_vehicle_type, ArrayList<String> arrayList_line_type){
        this.total_time = total_time;
        this.total_distance = total_distance;
        this.total_walk_time = total_walk_time;
        this.total_price = total_price;

        this.arrayList_distance = new ArrayList<>();
        this.arrayList_route_name = new ArrayList<>();
        this.arrayList_vehicle_type = new ArrayList<>();
        this.arrayList_line_type = new ArrayList<>();

        this.arrayList_distance.addAll(arrayList_distance);
        this.arrayList_route_name.addAll(arrayList_route_name);
        this.arrayList_vehicle_type.addAll(arrayList_vehicle_type);
        this.arrayList_line_type.addAll(arrayList_line_type);

        isSelect = false;
    }
}
