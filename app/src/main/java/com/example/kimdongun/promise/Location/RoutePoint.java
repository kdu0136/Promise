package com.example.kimdongun.promise.Location;

/**
 * Created by KimDongun on 2017-01-17.
 */

public class RoutePoint {
    public CoordiPoint coordiPoint; //위도,경도
    public String name; //장소 이름
    public String description; //경로 디테일 정보
    public int time; //목적지까지 남은 시간

    public RoutePoint(double latitude, double longitude, String name, String description, int time){
        coordiPoint = new CoordiPoint(latitude, longitude);
        this.name = name;
        this.description = description;
        this.time = time;
    }
}
