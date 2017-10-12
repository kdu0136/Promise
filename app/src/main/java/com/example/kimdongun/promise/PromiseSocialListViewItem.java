package com.example.kimdongun.promise;

import java.io.Serializable;

/**
 * Created by KimDongun on 2016-10-03.
 */

public class PromiseSocialListViewItem implements Serializable {
    //아이템 추가시 입력되는 값
    public String mail; //메일 주소
    public String name; //이름
    public String type; //계정 타입
    public String state; //출발 유무, 수락 상태
    public int no; //계정 고유 번호
    public double lat; //현재 위도
    public double lon; //현재 경도
    public int locationON; //실시간 위치 상태
    public boolean isSelect; //선택 된 거?

    public PromiseSocialListViewItem(){
        this.mail = "";
        this.name = "";
        this.type = "";
        this.state = "";
        this.lat = 0;
        this.lon = 0;
        no = 0;
        locationON = 1;
        isSelect = false;
    }

    public PromiseSocialListViewItem(String mail, String name, String type, int no, String state, double lat, double lon, int locationON, boolean isSelect){
        this.mail = mail;
        this.name = name;
        this.type = type;
        this.no = no;
        this.state = state;
        this.lat = lat;
        this.lon = lon;
        this.locationON = locationON;
        this.isSelect = isSelect;
    }
}
