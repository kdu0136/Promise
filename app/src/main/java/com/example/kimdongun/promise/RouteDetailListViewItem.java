package com.example.kimdongun.promise;

import java.io.Serializable;

/**
 * Created by KimDongun on 2016-10-03.
 */

//약속 리스트 아이템
public class RouteDetailListViewItem implements Serializable {
    //아이템 추가시 입력되는 값
    public String detail; //약속 내용
    public int time; //약속 시간
    public String type; //경로 정보
    public boolean isSelect; //선택 된 거?

    public RouteDetailListViewItem(String detail, int time, String type){
        this.detail = detail;
        this.time = time;
        this.type = type;
        isSelect = false;
    }
}
