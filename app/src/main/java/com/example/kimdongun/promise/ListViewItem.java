package com.example.kimdongun.promise;

import java.io.Serializable;

/**
 * Created by KimDongun on 2016-10-03.
 */

//약속 리스트 아이템
public class ListViewItem implements Serializable {
    //아이템 추가시 입력되는 값
    public String content; //약속 내용
    public String place; //약속 장소
    public int dYear, dMonth, dDay, dHour, dMin; //디데이 날짜와 시간
    public int spon_no; //주최자 번호
    public String spon_name; //주최자 이름
    public int isStart; //1은 출발
    public int alram_time; //알람 몇분 전
    public int isAlarm; //알람 울렸나?

    public int D_day; //디데이

    public int index; //DB에서 약속 식별자

    public ListViewItem(){
        this.content = "";
        this.place = "";
        this.dYear = -1;
        this.dMonth = -1;
        this.dDay = -1;
        this.dHour = -1;
        this.dMin = -1;
        this.index = 0;
        this.spon_name = "";
        this.spon_no = 0;
        this.isStart = 0;
        this.alram_time = 60;
        this.isAlarm = 0;
    }

    public ListViewItem(ListViewItem listViewItem){
        this.content = listViewItem.content;
        this.place = listViewItem.place;
        this.dYear = listViewItem.dYear;
        this.dMonth = listViewItem.dMonth;
        this.dDay = listViewItem.dDay;
        this.dHour = listViewItem.dHour;
        this.dMin = listViewItem.dMin;
        this.index = listViewItem.index;
        this.spon_name = listViewItem.spon_name;
        this.spon_no = listViewItem.spon_no;
        this.isStart = listViewItem.isStart;
        this.alram_time = listViewItem.alram_time;
        this.isAlarm = listViewItem.isAlarm;
    }
}
