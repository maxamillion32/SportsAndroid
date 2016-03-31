package com.xxn.sport.ui;

import com.xxn.sport.R;
import com.xxn.sport.base.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

public class TopRightActivity extends BaseActivity implements OnClickListener {

	private View dayLayout;
	private View weekLayout;
	private View monthLayout;
	private View yearLayout;
	private ImageView iv_1;
	private ImageView iv_2;
	private ImageView iv_3;
	private ImageView iv_4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_record_top_right);

		findViewById();
		initView();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		dayLayout = (View) findViewById(R.id.day_ranklist);
		weekLayout = (View) findViewById(R.id.week_ranklist);
		monthLayout = (View) findViewById(R.id.month_ranklist);
		yearLayout = (View) findViewById(R.id.year_ranklist);
		iv_1 = (ImageView) findViewById(R.id.sel_dot_1);
		iv_2 = (ImageView) findViewById(R.id.sel_dot_2);
		iv_3 = (ImageView) findViewById(R.id.sel_dot_3);
		iv_4 = (ImageView) findViewById(R.id.sel_dot_4);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		dayLayout.setOnClickListener(this);
		weekLayout.setOnClickListener(this);
		monthLayout.setOnClickListener(this);
		yearLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.day_ranklist:
			iv_1.setBackgroundResource(R.drawable.title_btn_dot_sel);
			iv_2.setBackgroundResource(R.drawable.title_btn_dot_normal);
			iv_3.setBackgroundResource(R.drawable.title_btn_dot_normal);
			iv_4.setBackgroundResource(R.drawable.title_btn_dot_normal);
			Intent intent = new Intent(this, TopRightActivity.class);
			intent.putExtra("back", "A");
            setResult(RESULT_OK, intent);
            finish();
			break;
		case R.id.week_ranklist:
			iv_1.setBackgroundResource(R.drawable.title_btn_dot_normal);
			iv_2.setBackgroundResource(R.drawable.title_btn_dot_sel);
			iv_3.setBackgroundResource(R.drawable.title_btn_dot_normal);
			iv_4.setBackgroundResource(R.drawable.title_btn_dot_normal);
			break;
		case R.id.month_ranklist:
			iv_1.setBackgroundResource(R.drawable.title_btn_dot_normal);
			iv_2.setBackgroundResource(R.drawable.title_btn_dot_normal);
			iv_3.setBackgroundResource(R.drawable.title_btn_dot_sel);
			iv_4.setBackgroundResource(R.drawable.title_btn_dot_normal);
			break;
		case R.id.year_ranklist:
			iv_1.setBackgroundResource(R.drawable.title_btn_dot_normal);
			iv_2.setBackgroundResource(R.drawable.title_btn_dot_normal);
			iv_3.setBackgroundResource(R.drawable.title_btn_dot_normal);
			iv_4.setBackgroundResource(R.drawable.title_btn_dot_sel);
			break;
		case R.id.login_button:
			this.startActivity(new Intent(this, MainActivity.class));
			break;
		default:
			break;
		}
	}

}
