package com.example.kimdongun.promise;

import java.io.Serializable;

/**
 * Created by KimDongun on 2016-10-03.
 */

//친구 추가 리스트 아이템
public class AddFriendListViewItem implements Serializable {
    //아이템 추가시 입력되는 값
    public String mail; //메일 주소
    public String name; //이름
    public String type; //계정 타입
    public int no; //계정 고유 번호
    public String state; //friend - 친구상태, none - 남남, request - 요청 함, accept - 요청 옴

    public AddFriendListViewItem(){
        this.mail = "";
        this.name = "";
        this.type = "";
        no = 0;
        this.state = "none";
    }

    public AddFriendListViewItem(String mail, String name, String type, int no, String state){
        this.mail = mail;
        this.name = name;
        this.type = type;
        this.no = no;
        this.state = state;
    }
}
