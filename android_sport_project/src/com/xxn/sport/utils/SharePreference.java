package com.xxn.sport.utils;

import com.xxn.sport.table.ShareTable;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 
 */
public class SharePreference {
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	public static final String SHARE_PREFERENCE = "SharePreference";// 用户SharePreference
	private Context context;

	public SharePreference(Context context, String file) {
		sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
		editor = sp.edit();
	}

	public SharePreference(Context context) {
		this.context = context;
		sp = context.getSharedPreferences(SHARE_PREFERENCE, Context.MODE_PRIVATE);
		editor = sp.edit();
	}
	
	// 获得活动状态
	public String getActivity_status() {
		return sp.getString(ShareTable.ACTIVITY_STATUS, "");
	}

	public void setActivity_status(String status) {
		editor.putString(ShareTable.ACTIVITY_STATUS, status);
		editor.commit();
	}

	// 获得图片URL
	public String getImage_url() {
		return sp.getString(ShareTable.IMAGE_URL, "");
	}

	public void setImage_url(String imageurl) {
		editor.putString(ShareTable.IMAGE_URL, imageurl);
		editor.commit();
	}

	// 获得链接URL
	public String getLink_url() {
		return sp.getString(ShareTable.LINK_URL, "");
	}

	public void setLink_url(String linkurl) {
		editor.putString(ShareTable.LINK_URL, linkurl);
		editor.commit();
	}
	
	//打印出该preference中的信息
	public void printUserInfo() {
		
	}
	

}
