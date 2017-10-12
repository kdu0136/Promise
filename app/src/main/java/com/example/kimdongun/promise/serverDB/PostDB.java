package com.example.kimdongun.promise.serverDB;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class PostDB {
    // http://dna.daum.net/apis/local
	public static final String SERVER_ROOT_URL = "http://promise.woobi.co.kr/%s";
	public boolean prograssBar; //프로그래스바 실행 여부
	public boolean full_url; //주소를 다 적을 것인가
	private int delay; //시작 딜레이
	private Context mContext;
	private String loadingMessage ; //로딩 메세지
	private HashMap<String, String> postData = new HashMap<String, String>(); //보낼 데이타
	OnFinishDBListener onFinishDBListener;
	PostDBTask postDBTask;

	public PostDB(){
		this.loadingMessage = "Loading...";
		this.delay = 0;
		prograssBar = false;
		full_url = false;
	}

	public PostDB(Context mContext){
		this.mContext = mContext;
		this.loadingMessage = "Loading...";
		this.delay = 0;
		prograssBar = true;
		full_url = false;
	}

	public PostDB(Context mContext, String loadingMessage){
		this.mContext = mContext;
		this.loadingMessage = loadingMessage;
		this.delay = 0;
		prograssBar = true;
		full_url = false;
	}

	public PostDB(Context mContext, String loadingMessage, int delay){
		this.mContext = mContext;
		this.loadingMessage = loadingMessage;
		this.delay = delay;
		prograssBar = true;
		full_url = false;
	}
	
	private class PostDBTask extends AsyncTask<String, String, Void> {

		private ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
			if(prograssBar) {
				progressDialog = new ProgressDialog(mContext);
				progressDialog.setMessage(loadingMessage);
				progressDialog.show();
			}
			super.onPreExecute();
		}//onPreExecute

		@Override
		protected void onPostExecute(Void aVoid) {
			if(prograssBar) {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}
			super.onPostExecute(aVoid);
		}

		@Override
		protected Void doInBackground(String... urls) {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String url = urls[0];
			String result = invokePost(url, postData);
			publishProgress(result);
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			if (onFinishDBListener != null) {
				String result = values[0].trim();
				onFinishDBListener.onSuccess(result);
			}
		}
	}

	public void putData(String php, HashMap<String, String> postData, OnFinishDBListener onFinishDBListener) {
    	this.onFinishDBListener = onFinishDBListener;
    	
		if (postDBTask != null) {
			postDBTask.cancel(true);
			postDBTask = null;
		}
		String url;
		if(full_url) { //url 주소 다 적는 경우
			url = php;
		}else {
			url = buildPostDBUrlString(php);
		}
		this.postData = postData;
		postDBTask = new PostDBTask();
		postDBTask.execute(url);
    }

	private String buildPostDBUrlString(String php) {
		String phpName = "";
		try {
			phpName = URLEncoder.encode(php, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return String.format(SERVER_ROOT_URL, phpName);
	}

	private String invokePost(String requestURL, HashMap<String,
			String> postDataParams) {
		URL url;
		String response = "";
		try {
			url = new URL(requestURL);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(15000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);

			OutputStream os = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(os, "UTF-8"));
			writer.write(getPostDataString(postDataParams));

			writer.flush();
			writer.close();
			os.close();
			int responseCode = conn.getResponseCode();

			if (responseCode == HttpsURLConnection.HTTP_OK) {
				String line;
				BufferedReader br = new BufferedReader(new
						InputStreamReader(conn.getInputStream()));
				while ((line = br.readLine()) != null) {
					response+=line;
				}
			}
			else {
				response="";
				Log.i("PostResponseAsyncTask", responseCode + "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}//invokePost

	private String getPostDataString(HashMap<String, String> params)
			throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;

		for(Map.Entry<String, String> entry : params.entrySet()){
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
		}

		return result.toString();
	}//getPostDataString
	
	public void cancel() {
		if (postDBTask != null) {
			postDBTask.cancel(true);
			postDBTask = null;
		}
	}
}
