package com.example.kimdongun.promise.search;

import java.util.List;

public interface OnFinishSearchListener {
	public void onSuccess(List<ItemPlace> itemList);
	public void onFail();
}
