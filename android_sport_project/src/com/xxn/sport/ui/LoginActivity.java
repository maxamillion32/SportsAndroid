package com.xxn.sport.ui;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FunctionCallback;
import com.xxn.sport.R;
import com.xxn.sport.base.BaseActivity;
import com.xxn.sport.base.BaseApplication;
import com.xxn.sport.config.Constants;
import com.xxn.sport.dbservice.IntegralGainedDbService;
import com.xxn.sport.utils.JsonTool;
import com.xxn.sport.utils.LogTool;
import com.xxn.sport.utils.ToastTool;
import com.xxn.sport.utils.UserPreference;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends BaseActivity implements OnClickListener {

	private UserPreference userPreference;
	private Button loginBtn;// 登录按钮
	private EditText userVerifyId;
	private View mProgressView;// 缓冲

	private IntegralGainedDbService integralgaineddbservice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		userPreference = BaseApplication.getInstance().getUserPreference();

		// 得到实例（创建或者直接获取数据库对象）
		integralgaineddbservice = IntegralGainedDbService.getInstance(this);
		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		loginBtn = (Button) findViewById(R.id.login_button);
		userVerifyId = (EditText) findViewById(R.id.user_verifyid);
		mProgressView = findViewById(R.id.login_status);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		loginBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.login_button:
			showProgress(true);
			String regCode = "";
			try {
				regCode = userVerifyId.getText().toString();
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			// 先检查电话号码是否为空
			if (TextUtils.isEmpty(regCode)) {
				userVerifyId.setError(getString(R.string.error_field_required));
			} else {
				// 电话号码不为空时做下一步操作
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("regCode", userVerifyId.getText().toString());
				AVCloud.callFunctionInBackground("UserLogin", parameters, new FunctionCallback<Object>() {
					public void done(Object object, AVException e) {
						if (object != null) {
							JsonTool jsonTool = new JsonTool(object.toString());
							JSONObject jsonObject = jsonTool.getJsonObject();
							Boolean status = jsonTool.getStatus(); // Boolean型变量，为true时成功
							if (status) {
								showProgress(false);
								// 登录成功做跳转操作
								LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
								try {
									// 存入当前用户ID
									userPreference.setUserId(jsonObject.getString("userId"));
									//存入用户积分
									userPreference.setIntegral(jsonObject.getString("integral"));
								} catch (JSONException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								LoginActivity.this.finish();
							} else {
								showProgress(false);
								String errorMessage;
								try {
									errorMessage = jsonObject.getString("errorMessage");
									// handleError
									LogTool.e(Constants.PACKAGENAME, "登录失败，errorMessage：" + errorMessage);
									ToastTool.showLong(LoginActivity.this, "输入的识别码有误！");
								} catch (JSONException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
									LogTool.e(Constants.PACKAGENAME, "登录失败，异常信息：" + e.toString());
								}
							}
						}
						else{
							ToastTool.showLong(LoginActivity.this, "请检查网络连接！");
						}
					}
				});
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (show) {
			// 隐藏软键盘
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
					LoginActivity.this.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}

}
