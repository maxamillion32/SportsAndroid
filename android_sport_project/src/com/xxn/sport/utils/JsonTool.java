package com.xxn.sport.utils;

import org.json.JSONException;
import org.json.JSONObject;

import com.xxn.sport.base.BaseApplication;
import com.xxn.sport.config.Constants;

public class JsonTool {
	public final static String STATUS = "resultCode";
	public final static String STATUS_SUCCESS = "success";
	public final static String STATUS_FAIL = "fail";
	public final static String MESSAGE = "message";
	public final static String ACCESS_TOKEN = "access_token";

	JSONObject jsonObject;
	private UserPreference userPreference;

	public JsonTool(String response) {
		try {
			jsonObject = new JSONObject(response);
			userPreference = BaseApplication.getInstance().getUserPreference();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public Boolean getStatus() {
		if (jsonObject != null) {
			try {
				if ("200".equals(jsonObject.getString(STATUS)))
					return true;
				else {
					LogTool.i(Constants.PACKAGENAME, jsonObject.getString(STATUS));
					return false;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	public String getMessage() {
		if (jsonObject != null) {
			try {
				return jsonObject.getString(MESSAGE);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}

	public String saveAccess_token() {
		if (jsonObject != null) {
			try {
				if (!jsonObject.getString(ACCESS_TOKEN).isEmpty()) {
					if (userPreference != null) {
						userPreference.setAccess_token(jsonObject.getString(ACCESS_TOKEN));
					}
					return jsonObject.getString(ACCESS_TOKEN);
				} else {
					return null;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

}
