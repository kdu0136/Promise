package com.example.kimdongun.promise.search;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Searcher {
    // http://dna.daum.net/apis/local
	public static final String DAUM_MAPS_LOCAL_ADDRESS_SEARCH_API_FORMAT = "https://apis.daum.net/local/geo/addr2coord?output=json&q=%s&apikey=%s";
	public static final String DAUM_MAPS_LOCAL_KEYWORD_SEARCH_API_FORMAT = "https://apis.daum.net/local/v1/search/keyword.json?query=%s&location=%f,%f&page=%d&apikey=%s";
	
	private static final String HEADER_NAME_X_APPID = "x-appid";
	private static final String HEADER_NAME_X_PLATFORM = "x-platform";
	private static final String HEADER_VALUE_X_PLATFORM_ANDROID = "android";
	private static final int KEYWORD = 0;
	private static final int ADDRESS = 1;

	private Context mContext;
	
	OnFinishSearchListener onFinishSearchListener;
	SearchTask searchTask;
	String appId;
	
	private class SearchTask extends AsyncTask<String, List<ItemPlace>, Void> {
		public int search_type = KEYWORD; //0:키워드 1:주소
		@Override
		protected Void doInBackground(String... urls) {
			String url = urls[0];
			Map<String, String> header = new HashMap<String, String>();
			header.put(HEADER_NAME_X_APPID, appId);
			header.put(HEADER_NAME_X_PLATFORM, HEADER_VALUE_X_PLATFORM_ANDROID);
			String json = fetchData(url, header);
			List<ItemPlace> itemList = parse(json, search_type);
			publishProgress(itemList);
			return null;
		}

		@Override
		protected void onProgressUpdate(List<ItemPlace>... values) {
			if (onFinishSearchListener != null) {
				if (values[0] == null) {
					onFinishSearchListener.onFail();
				} else {
					onFinishSearchListener.onSuccess(values[0]);
				}
				super.onProgressUpdate(values);
			}
		}
	}

	public void searchKeyword(Context applicationContext, String query, double latitude, double longitude, int radius, int page, String apikey, OnFinishSearchListener onFinishSearchListener) {
    	this.onFinishSearchListener = onFinishSearchListener;
		this.mContext = applicationContext;
    	
		if (searchTask != null) {
			searchTask.cancel(true);
			searchTask = null;
		}
		
		if (applicationContext != null) {
			appId = applicationContext.getPackageName();
		}
		String url = buildKeywordSearchApiUrlString(query, latitude, longitude, radius, page, apikey);
		searchTask = new SearchTask();
		searchTask.search_type = KEYWORD;
		searchTask.execute(url);
    }

	public void searchAddress(Context applicationContext, String query, String apikey, OnFinishSearchListener onFinishSearchListener) {
		this.onFinishSearchListener = onFinishSearchListener;
		this.mContext = applicationContext;

		if (searchTask != null) {
			searchTask.cancel(true);
			searchTask = null;
		}

		if (applicationContext != null) {
			appId = applicationContext.getPackageName();
		}
		String url = buildAddressSearchApiUrlString(query, apikey);
		searchTask = new SearchTask();
		searchTask.search_type = ADDRESS;
		searchTask.execute(url);
	}

	private String buildKeywordSearchApiUrlString(String query, double latitude, double longitude, int radius, int page, String apikey) {
    	String encodedQuery = "";
		try {
			encodedQuery = URLEncoder.encode(query, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return String.format(DAUM_MAPS_LOCAL_KEYWORD_SEARCH_API_FORMAT, encodedQuery, latitude, longitude, page, apikey);
    }

	private String buildAddressSearchApiUrlString(String query, String apikey) {
		String encodedQuery = "";
		try {
			encodedQuery = URLEncoder.encode(query, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//return String.format(DAUM_MAPS_LOCAL_KEYWORD_SEARCH_API_FORMAT, encodedQuery, latitude, longitude, radius, page, apikey);
		return String.format(DAUM_MAPS_LOCAL_ADDRESS_SEARCH_API_FORMAT, encodedQuery, apikey);
	}

	private String fetchData(String urlString, Map<String, String> header) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(4000 /* milliseconds */);
			conn.setConnectTimeout(7000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			if (header != null) {
				for (String key : header.keySet()) {
					conn.addRequestProperty(key, header.get(key));
				}
			}
			conn.connect();
			InputStream is = conn.getInputStream();
			@SuppressWarnings("resource")
			Scanner s = new Scanner(is);
			s.useDelimiter("\\A");
			String data = s.hasNext() ? s.next() : "";
			is.close();
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
    
	private List<ItemPlace> parse(String jsonString, int search_type) {
		List<ItemPlace> itemList = new ArrayList<ItemPlace>();
		try {
			JSONObject reader = new JSONObject(jsonString);
			JSONObject channel = reader.getJSONObject("channel");
			JSONArray objects = channel.getJSONArray("item");
			for (int i = 0; i < objects.length(); i++) {
				JSONObject object = objects.getJSONObject(i);
				ItemPlace itemPlace = new ItemPlace();
				if(search_type == KEYWORD) {
					itemPlace.title = object.getString("title");
					//itemPlace.address = object.getString("address");
					itemPlace.latitude = object.getDouble("latitude");
					itemPlace.longitude = object.getDouble("longitude");
					//itemPlace.address = LocationClass.findAddress(mContext, itemPlace.latitude, itemPlace.longitude);
					itemPlace.id = object.getString("id");
				}else if(search_type == ADDRESS){
					itemPlace.title = object.getString("title");
					//itemPlace.address = object.getString("title");
					itemPlace.latitude = object.getDouble("lat");
					itemPlace.longitude = object.getDouble("lng");
					itemPlace.id = object.getString("id");
				}
				itemList.add(itemPlace);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return itemList;
	}
	
	public void cancel() {
		if (searchTask != null) {
			searchTask.cancel(true);
			searchTask = null;
		}
	}
}
