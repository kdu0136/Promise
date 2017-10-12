package com.example.kimdongun.promise.search;

import java.io.Serializable;

public class ItemPlace implements Serializable {
	public String title=""; //검색 키워드 or 주소
	public String address=""; //주소
	public double longitude=0; //경도
	public double latitude=0; //위도
	public String id=""; //id

	public void setItem(ItemPlace itemPlace){
		this.title = itemPlace.title;
		//this.address = itemPlace.address;
		this.longitude = itemPlace.longitude;
		this.latitude = itemPlace.latitude;
		this.id = itemPlace.id;
	}
}
