package com.xxn.sport.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.xxn.sport.R;
import com.xxn.sport.base.BaseActivity;
import com.xxn.sport.base.BaseApplication;
import com.xxn.sport.dbservice.IntegralGainedDbService;
import com.xxn.sport.dbservice.SportRecordDbService;
import com.xxn.sport.dbservice.SportRecordTmpDbService;
import com.xxn.sport.entities.IntegralGained;
import com.xxn.sport.entities.SportRecord;
import com.xxn.sport.entities.SportRecordTmp;
import com.xxn.sport.utils.DateTimeTools;
import com.xxn.sport.utils.LogTool;
import com.xxn.sport.utils.UserPreference;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends BaseActivity implements OnClickListener {

	private UserPreference userPreference;
	private Button logoutBtn;// 登出按钮
	private ImageView kSport;// K运动sport
	private ImageView recordList;// 记录列表
	private ImageView scoreBoard;// 积分面板
	private ImageView kUnrun;// 跑步
	private ImageView kUnWalk;// 走
	private ImageView kUnRide;// 骑车
	private ImageView turnOpen;// 打开
	private TextView totalSportTime;
	private TextView totalSportLegend;
	View bg;// 面板View
	UUID uuid;
	String s, userid;
	Intent intent;
	private static int runingFlag = 0;
	private static final int RUN = 1;
	private static final int WALK = 2;
	private static final int RIDE = 3;
	final Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		userPreference = BaseApplication.getInstance().getUserPreference();

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		logoutBtn = (Button) findViewById(R.id.logout_button);
		kSport = (ImageView) findViewById(R.id.k_sport);
		recordList = (ImageView) findViewById(R.id.record_list);
		scoreBoard = (ImageView) findViewById(R.id.score_board);
		bg = findViewById(R.id.bg);
		kUnrun = (ImageView) findViewById(R.id.k_unrun);
		kUnWalk = (ImageView) findViewById(R.id.k_unwalk);
		kUnRide = (ImageView) findViewById(R.id.k_unride);
		turnOpen = (ImageView) findViewById(R.id.turn_open);

		totalSportTime = (TextView) findViewById(R.id.total_sport_time);
		totalSportLegend = (TextView) findViewById(R.id.total_sport_legend);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		logoutBtn.setOnClickListener(this);
		kSport.setOnClickListener(this);
		recordList.setOnClickListener(this);
		scoreBoard.setOnClickListener(this);
		bg.setOnClickListener(this);
		kUnrun.setOnClickListener(this);
		kUnWalk.setOnClickListener(this);
		kUnRide.setOnClickListener(this);

		userid = userPreference.getUserId(); // 用户识别码

		// 登录完后去数据库获取数据表，更新到本地中
		java.util.List<AVObject> objects = new ArrayList<AVObject>();
		AVObject integralGainedAvo = new AVObject("IntegralGained");
		AVObject sportRecordAavo = new AVObject("SportRecord");
		objects.add(sportRecordAavo);
		objects.add(integralGainedAvo);
		final IntegralGainedDbService integralGainedDbService = IntegralGainedDbService.getInstance(MainActivity.this);
		integralGainedDbService.deleteAllIntegralGained();
		final SportRecordDbService sportRecordDbService = SportRecordDbService.getInstance(MainActivity.this);
		sportRecordDbService.deleteAllSportRecord();
		final SportRecordTmpDbService sportRecordTmpDbService = SportRecordTmpDbService.getInstance(MainActivity.this);
		sportRecordTmpDbService.deleteAllSportRecordTmp();

		// 去云端数据库中取对象 SportRecord
		AVQuery<AVObject> integralGainedQuery = new AVQuery<AVObject>("IntegralGained");
		// integralGainedQuery.whereEqualTo("userID", userid);
		integralGainedQuery.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					for (AVObject avObject : arg0) {
						// 获取值
						String uid = avObject.getString("uid");
						String useId = avObject.getString("useId");
						Date gainTime = avObject.getDate("gainTime");
						int integral = avObject.getInt("integral");
						int gainReason = avObject.getInt("gainReason");
						IntegralGained integralgained = new IntegralGained(uid, useId,
								DateTimeTools.DateToDateString(gainTime), integral, gainReason);
						integralGainedDbService.addIntegralGained(integralgained);
					}
				}
			}

		});

		// 去云端数据库中取对象 SportRecord
		AVQuery<AVObject> sportRecordQuery = new AVQuery<AVObject>("SportRecord");
		sportRecordQuery.whereEqualTo("userID", userid);
		sportRecordQuery.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					for (AVObject avObject : arg0) {
						// 获取值
						String uid = avObject.getString("uid");
						String userID = avObject.getString("userID");
						int sportType = avObject.getInt("sportType");
						Date startTime = avObject.getDate("startTime");
						Date endTime = avObject.getDate("endTime");
						int pauseTime = avObject.getInt("pauseTime");
						String motionTrack = avObject.getString("motionTrack");
						Float distance = (float) avObject.getDouble("distance");
						SportRecord sportrecord = new SportRecord(uid, userID, sportType,
								DateTimeTools.DateToDateString(startTime), DateTimeTools.DateToDateString(endTime),
								pauseTime, motionTrack, distance);
						sportRecordDbService.addSportRecord(sportrecord);
//						LogTool.i(sportrecord.getUid());
					}

					// 初始化总的时间和总的里程数
					String recordTime = sportRecordDbService.getSportRecordTime(userid);
					String recordDistance = sportRecordDbService.getSportRecordDistance(userid);
					if (!"".equals(recordTime))
						totalSportTime.setText(recordTime);
					if (!"".equals(recordDistance))
						totalSportLegend.setText(recordDistance);
				}
			}
		});

		// 去云端数据库中取对象 SportRecordTmp
		AVQuery<AVObject> SportRecordTmpQuery = new AVQuery<AVObject>("SportRecordTmp");
		SportRecordTmpQuery.whereEqualTo("userID", userid);
		SportRecordTmpQuery.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					for (AVObject avObject : arg0) {
						// 获取值
						String uid = avObject.getString("uid");
						String userID = avObject.getString("userID");
						int sportType = avObject.getInt("sportType");
						Date startTime = avObject.getDate("startTime");
						Date endTime = avObject.getDate("endTime");
						int pauseTime = avObject.getInt("pauseTime");
						String motionTrack = avObject.getString("motionTrack");
						Float distance = (float) avObject.getDouble("distance");
						SportRecordTmp sportRecordTmp = new SportRecordTmp(uid, userID, sportType,
								DateTimeTools.DateToDateString(startTime), DateTimeTools.DateToDateString(endTime),
								pauseTime, motionTrack, distance);
						sportRecordTmpDbService.addSportRecordTmp(sportRecordTmp);
					}
				}
			}
		});

		// AVObject.fetchAllInBackground(objects, new FindCallback<AVObject>() {
		//
		// @Override
		// public void done(List<AVObject> arg0, AVException arg1) {
		// // TODO Auto-generated method stub
		// if (!arg0.isEmpty()) {
		// for (AVObject avObject : arg0) {
		// // 取出数据并存入本地的表中
		// System.out.println(avObject);
		// }
		// } else {
		// // 获取当前时间
		// SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd
		// HH:mm:ss");
		// String dateTime = format.format(new Date());
		// // final Calendar c = Calendar.getInstance();
		// // int mCurrentDay = c.get(Calendar.DAY_OF_MONTH);
		// // c.set(Calendar.DAY_OF_MONTH, mCurrentDay + 3);
		//
		// uuid = UUID.randomUUID();
		// String uuidother = UUID.randomUUID().toString();
		// s = uuid.toString(); // 唯一码
		// // 为空时将假数据填入数据库中
		// IntegralGained integralgained = new IntegralGained(s, userid,
		// dateTime, 1, 1);
		// IntegralGained integralgainedother = new IntegralGained(uuidother,
		// userid, "2016-03-29 23:57:00", 1,
		// 1);
		// integralGainedDbService.addIntegralGained(integralgained);
		// integralGainedDbService.addIntegralGained(integralgainedother);
		//
		// SportRecord sportrecord = new SportRecord(s, userid, 1, dateTime,
		// "2016-03-29 23:59:00", 20, "1",
		// (float) 32.50);
		// SportRecord sportrecordother = new SportRecord(uuidother, userid, 1,
		// "2016-03-29 23:51:00",
		// "2016-03-29 23:59:00", 20, "1", (float) 3.211);
		// sportRecordDbService.addSportRecord(sportrecord);
		// sportRecordDbService.addSportRecord(sportrecordother);
		//
		// // 临时表不做同步操作
		// // SportRecordTmp sportrecordtmp = new SportRecordTmp(s,
		// // userid, 1, dateTime, "2016-03-29 23:59:00",
		// // 20, "1", (float) 32.50);
		// // SportRecordTmp sportrecordtmpother = new
		// // SportRecordTmp(uuidother, userid, 1, "2016-03-29
		// // 23:51:00",
		// // "2016-03-29 23:59:00", 20, "1", (float) 3.211);
		// // sportRecordTmpDbService.addSportRecordTmp(sportrecordtmp);
		// // sportRecordTmpDbService.addSportRecordTmp(sportrecordtmpother);
		// }
		// }
		//
		// });

		// 找到最近的一条运动记录
		// SportRecord sr = sportRecordDbService.getLatestSportRecord(userid);
		// 判断这条运动记录
		// String startTime = sr.getStartTime();
		// Date startDate = DateTimeTools.StringToDate(startTime);
		// Boolean isToday = DateTimeTools.getIntervalDif(startDate);
		// if (isToday) {// 如果是今天，则为运动中
		// kSport.setBackgroundResource(R.drawable.sporting);
		// }
		// 从userPreference取得是否完成，如果没有完成对比下当时日期和今天的日期
		if (!userPreference.getUserFinsh() && userPreference.getUserRunDate().equals(DateTimeTools.getCurDateDay())) {
			kSport.setBackgroundResource(R.drawable.sporting);
			runingFlag = 1;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.k_unrun:
			kUnrun.setBackgroundResource(R.drawable.k_run_img);
			kUnWalk.setBackgroundResource(R.drawable.k_unwalk_img);
			kUnRide.setBackgroundResource(R.drawable.k_unride_img);
			intent = new Intent(this, MapRouteActivity.class);
			intent.putExtra("fromReason", "0"); // 这里用来传值
			intent.putExtra("sport_state", RUN);
			startActivity(intent);
			// this.startActivity(new Intent(this, MapRouteActivity.class));
			bg.setVisibility(View.INVISIBLE);
			break;
		case R.id.k_unwalk:
			kUnrun.setBackgroundResource(R.drawable.k_unrun_img);
			kUnWalk.setBackgroundResource(R.drawable.k_walk_img);
			kUnRide.setBackgroundResource(R.drawable.k_unride_img);
			intent = new Intent(this, MapRouteActivity.class);
			intent.putExtra("fromReason", "0"); // 这里用来传值
			intent.putExtra("sport_state", WALK);
			startActivity(intent);
			bg.setVisibility(View.INVISIBLE);
			break;
		case R.id.k_unride:
			kUnrun.setBackgroundResource(R.drawable.k_unrun_img);
			kUnWalk.setBackgroundResource(R.drawable.k_unwalk_img);
			kUnRide.setBackgroundResource(R.drawable.k_ride_img);
			intent = new Intent(this, MapRouteActivity.class);
			intent.putExtra("fromReason", "0"); // 这里用来传值
			intent.putExtra("sport_state", RIDE);
			startActivity(intent);
			bg.setVisibility(View.INVISIBLE);
			break;
		case R.id.k_sport:
			if (runingFlag == 0) {
				// this.startActivity(
				// new Intent(this, KPopUpActivity.class));
				// bg.getBackground().setAlpha(120);
				bg.setVisibility(View.VISIBLE);
				/**
				 * 设置缩放动画 AlphaAnimation 透明度动画效果 ScaleAnimation 缩放动画效果
				 * TranslateAnimation 位移动画效果 RotateAnimation 旋转动画效果
				 */
				final ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF,
						0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				animation.setDuration(800);// 设置动画持续时间
				/** 常用方法 */
				// animation.setRepeatCount(int repeatCount);//设置重复次数
				animation.setFillAfter(true);// 动画执行完后是否停留在执行完的状态
				// animation.setStartOffset(long startOffset);//执行前的等待时间
				kUnrun.setAnimation(animation);
				kUnWalk.setAnimation(animation);
				kUnRide.setAnimation(animation);

				/** 设置位移动画 向右位移150 */
				// final TranslateAnimation tAnimation = new
				// TranslateAnimation(0,
				// 150, 0,
				// 0);
				// tAnimation.setDuration(800);// 设置动画持续时间
				turnOpen.setAnimation(animation);
				// animation.setRepeatCount(2);// 设置重复次数
				// animation.setRepeatMode(Animation.REVERSE);// 设置反方向执行

				/** 开始动画 */
				// animation.startNow();
				// this.overridePendingTransition(R.anim.fade_out,
				// R.anim.pop_in);
			} else {
				Intent intent = new Intent();
				intent.putExtra("fromReason", "1"); // 这里用来传值
				intent.putExtra("sport_state", userPreference.getUserRunState()); // 这里用来传值
				intent.setClass(MainActivity.this, MapRouteActivity.class);
				this.startActivity(intent);
			}
			break;
		case R.id.record_list:
			this.startActivity(new Intent(this, RecordListActivity.class));
			break;
		case R.id.score_board:
			this.startActivity(new Intent(this, ScoreBoardActivity.class));
			break;
		case R.id.logout_button:
			// 做登出操作,同时将用户识别码清空
			this.startActivity(new Intent(this, LoginActivity.class));
			this.finish();
			userPreference.setUserId("");
			break;
		default:
			bg.setVisibility(View.INVISIBLE);
			break;
		}
	}

}
