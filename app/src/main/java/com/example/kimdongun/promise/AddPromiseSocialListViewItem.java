package com.example.kimdongun.promise;

import java.io.Serializable;

/**
 * Created by KimDongun on 2016-10-03.
 */

public class AddPromiseSocialListViewItem implements Serializable {
    //아이템 추가시 입력되는 값
    public String mail; //메일 주소
    public String name; //이름
    public String type; //계정 타입
    public boolean isCheck;
    public int no; //계정 고유 번호

    public AddPromiseSocialListViewItem(){
        this.mail = "";
        this.name = "";
        this.type = "";
        this.isCheck = false;
        no = 0;
    }

    public AddPromiseSocialListViewItem(String mail, String name, String type, int no, boolean isCheck){
        this.mail = mail;
        this.name = name;
        this.type = type;
        this.no = no;
        this.isCheck = isCheck;
    }
}
