package com.xxn.sport.utils;

import java.util.Date;

import com.xxn.sport.table.UserTable;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
*
*/

public class UserPreference {
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	public static final String USER_SHAREPREFERENCE = "userSharePreference";// 用户SharePreference
	private Context context;

	public UserPreference(Context context) {
		this.context = context;
		sp = context.getSharedPreferences(USER_SHAREPREFERENCE, Context.MODE_PRIVATE);
		editor = sp.edit();
	}

	// 打印用户信息
	public void printUserInfo() {
		// LogTool.i("是否登录: " + getUserLogin());
		// LogTool.i("手机验证码: " + getU_VERIFY_CODE());
		// LogTool.i("登录手机号: " + getU_tel());
		// LogTool.i("该账户是否完成支付: " + getU_IS_PAY());
		// LogTool.i("该账户订单是否完成: " + getU_IS_FINISH());
		// LogTool.i("该账户是否冻结: " + getU_IS_FROZEN());
		// LogTool.i("access_token: " + getAccess_token());
		LogTool.i("userVerifyId: " + getUserId());
	}

	/**
	 * 清空数据
	 */
	public void clear() {
		String tel = getU_tel();
		editor.clear();
		setU_tel(tel);
		editor.commit();
	}

	/**
	 * 不保存电话号码
	 */
	public void clearthird() {
		editor.clear();
		editor.commit();
	}

	public void setResetState() {
		editor.putString("runDate", "");
		editor.putString("startTime", "");
		editor.putString("usingTime", "");
		editor.commit();
	}

	// 记录用户运动的点集
	public String getUserSportPoints() {
		return sp.getString("points", "");
	}

	public void setUserSportPoints(String points) {
		editor.putString("points", points);
		editor.commit();
	}

	// 记录用户是否登录
	public boolean getUserLogin() {
		return sp.getBoolean("login", false);
	}

	public void setUserLogin(boolean login) {
		editor.putBoolean("login", login);
		editor.commit();
	}

	// 记录用户是否跑完了
	public boolean getUserFinsh() {
		return sp.getBoolean("isFinish", false);
	}

	public void setUserFinish(boolean isFinish) {
		editor.putBoolean("isFinish", isFinish);
		editor.commit();
	}

	// 记录用户运动状态
	public String getUserRunState() {
		return sp.getString("sport_state", "");
	}

	public void setUserRunState(String sportState) {
		editor.putString("sport_state", sportState);
		editor.commit();
	}

	// 记录用户跑步的日期
	public String getUserRunDate() {
		return sp.getString("runDate", "00:00:00");
	}

	public void setUserRunDate(String runDate) {
		editor.putString("runDate", runDate);
		editor.commit();
	}

	// 记录用户未完成时的开始时间
	public String getUserStartTime() {
		return sp.getString("startTime", "00:00:00");
	}

	public void setUserStartTime(String startTime) {
		editor.putString("startTime", startTime);
		editor.commit();
	}

	// 记录用户未完成时的花销时间
	public String getUserUsingTime() {
		return sp.getString("usingTime", "0");
	}

	public void setUserUsingTime(String usingTime) {
		editor.putString("usingTime", usingTime);
		editor.commit();
	}

	// 记录用户未完成时的距离
	public String getUserFinishDis() {
		return sp.getString("finishDis", "0");
	}

	public void setUserFinishDis(String finishDis) {
		editor.putString("finishDis", finishDis);
		editor.commit();
	}

	// 手机号
	public String getU_tel() {
		return sp.getString(UserTable.U_TEL, "");
	}

	public void setU_tel(String u_tel) {
		editor.putString(UserTable.U_TEL, u_tel);
		editor.commit();
	}

	// 用户userId
	public String getUserId() {
		return sp.getString("userId", "");
	}

	public void setUserId(String userId) {
		editor.putString("userId", userId);
		editor.commit();
	}

	// 用户积分
	public String getIntegral() {
		return sp.getString("integral", "");
	}

	public void setIntegral(String integral) {
		editor.putString("integral", integral);
		editor.commit();
	}

	// 验证码
	public String getU_VERIFY_CODE() {
		return sp.getString(UserTable.U_VERIFY_CODE, "");
	}

	public void setU_VERIFY_CODE(String code) {
		editor.putString(UserTable.U_VERIFY_CODE, code);
		editor.commit();
	}

	// 记录用户是否冻结
	public String getU_IS_FROZEN() {
		return sp.getString(UserTable.U_IS_FROZEN, "");
	}

	public void setU_IS_FROZEN(String isfrozen) {
		editor.putString(UserTable.U_IS_FROZEN, isfrozen);
		editor.commit();
	}

	// 记录用户订单是否完成
	public String getU_IS_FINISH() {
		return sp.getString(UserTable.U_IS_FINISH, "");
	}

	public void setU_IS_FINISH(String isfinish) {
		editor.putString(UserTable.U_IS_FINISH, isfinish);
		editor.commit();
	}

	// 记录用户是否完成支付
	public String getU_IS_PAY() {
		return sp.getString(UserTable.U_IS_PAY, "");
	}

	public void setU_IS_PAY(String ispay) {
		editor.putString(UserTable.U_IS_PAY, ispay);
		editor.commit();
	}

	// access_token
	public String getAccess_token() {
		return sp.getString(UserTable.U_ACCESS_TOKEN, "");
	}

	public void setAccess_token(String access_token) {
		if (!TextUtils.isEmpty(access_token)) {
			editor.putString(UserTable.U_ACCESS_TOKEN, access_token);
			editor.commit();
		}
	}

	// 创建时间
	public Date getU_CreatTime() {
		Long time = sp.getLong(UserTable.U_CREATE_TIME, 0);
		if (time != 0) {
			return new Date(time);
		} else {
			return null;
		}
	}

	public void setU_CreatTime(Date creatTime) {
		if (creatTime != null) {
			editor.putLong(UserTable.U_CREATE_TIME, creatTime.getTime());
			editor.commit();
		}
	}

	// 身份证URL
	public String getU_ID_CARD() {
		// return sp.getString(UserTable.ID_CARD_FRONT, "drawable://" +
		// R.drawable.idcard_bg);
		return sp.getString(UserTable.ID_CARD_FRONT, "");
	}

	public void setU_ID_CARD(String u_id_card) {
		editor.putString(UserTable.ID_CARD_FRONT, u_id_card);
		editor.commit();
	}

	// 学生证URL
	public String getU_STU_CARD() {
		return sp.getString(UserTable.STU_CARD_FRONT, "");
	}

	public void setU_STU_CARD(String u_stu_card) {
		editor.putString(UserTable.STU_CARD_FRONT, u_stu_card);
		editor.commit();
	}

}
