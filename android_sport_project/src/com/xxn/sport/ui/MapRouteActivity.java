package com.xxn.sport.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FunctionCallback;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.SnapshotReadyCallback;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.xxn.sport.R;
import com.xxn.sport.base.BaseActivity;
import com.xxn.sport.base.BaseApplication;
import com.xxn.sport.config.Constants;
import com.xxn.sport.dbservice.SportRecordDbService;
import com.xxn.sport.dbservice.SportRecordTmpDbService;
import com.xxn.sport.entities.SportRecord;
import com.xxn.sport.entities.SportRecordTmp;
import com.xxn.sport.utils.DateTimeTools;
import com.xxn.sport.utils.JsonTool;
import com.xxn.sport.utils.LogTool;
import com.xxn.sport.utils.ToastTool;
import com.xxn.sport.utils.UserPreference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MapRouteActivity extends BaseActivity implements OnClickListener {

	private ImageButton k_state;// 运动状态控件
	private TextView finish_sport, start_time_tv, sport_time_tv, sport_distance_tv, completed;
	private ImageView goOn, ok, turnShow, loctionMyself, back, k_sport_head;
	private LinearLayout turnMune;
	boolean isTurn = true;// 判断运动详情是否下拉
	boolean isPause = true;// 判断是否是暂停状态,默认暂停
	private RelativeLayout finish_state;// 结束状态控件
	private MapView mMapView;// 地图控件
	private BaiduMap mBaiduMap;
	Polyline mPolyline;
	LatLng mLocation;// 定位位置的经纬度
	LatLng pre = null;// 之前位置坐标
	LatLng now;// 当前位置坐标
	boolean isFirstLoc = true; // 是否首次定位,默认是
	float level = 19;// 当前地图的缩放级别(2秒刷新一次)
	boolean isContinue = true;// 判断是否为暂停之后继续的状态，默认是
	String point;// 运动轨迹点经纬度数据
	List<String> list = new ArrayList<String>();
	boolean isStopMark = false;// 是否要标记"StopMark"
	int sport_state;// 运动状态
	float distance = 0;// 运动距离
	float dist = 0;
	boolean isupdate = true;
	List<LatLng> points = null;// 运动轨迹点的集合
	static Handler handler, handler3Mius, timeHandler;
	private static final int RUN = 1;
	private static final int WALK = 2;
	private static final int RIDE = 3;
	Time t = new Time();
	String curDate = DateTimeTools.getCurDateTime();
	boolean isFirstStart = true;
	int sport_time_hour, sport_time_minute, sport_time_second;
	Thread thread;
	DecimalFormat decimalFormat = new DecimalFormat("0.00");// 距离取两位小数点
	// 语音合成对象
	private SpeechSynthesizer mTts;
	Runnable runnableInsertAndUpdate;
	// 引擎类型
	private String mEngineType = SpeechConstant.TYPE_CLOUD;
	// 默认发音人
	private String voicer = "xiaoyan";
	private SharedPreferences mSharedPreferences;
	boolean isXunFei = true;
	public volatile boolean exit = false;
	int sport_time; // 运动时间
	String fromReason;// 是由运动中过来的还是由正常情况过来的

	private UserPreference userPreference;
	SportRecordTmpDbService sportRecordTmpDbService; // 数据库操作接口
	SportRecordDbService sportRecordDbService;
	String uuidString = UUID.randomUUID().toString();

	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			// if
			// (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR))
			// {
			// Toast.makeText(context, "key 验证出错! 请在 AndroidManifest.xml 文件中检查
			// key 设置"
			// , Toast.LENGTH_LONG).show();
			// } else if (s
			// .equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK))
			// {
			// Toast.makeText(context, "key 验证成功! 功能可以正常使用",
			// Toast.LENGTH_LONG).show();
			// }
			// else
			if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Toast.makeText(context, "网络出错", Toast.LENGTH_LONG).show();
			}
		}
	}

	private SDKReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext,该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_maproute);
		userPreference = BaseApplication.getInstance().getUserPreference();
		findViewById();
		initView();

		Intent intent = getIntent();
		fromReason = intent.getStringExtra("fromReason");
		sport_state = intent.getIntExtra("sport_state", 0);
		if ("1".equals(fromReason)) {
			// 直接过来的
			String sportTime = userPreference.getUserUsingTime();
			start_time_tv.setText(userPreference.getUserStartTime());
			sport_time_tv.setText(sportTime);
			if (sportTime.length() == 8) {
				sport_time_hour = Integer.valueOf(sportTime.substring(0, 2));
				sport_time_minute = Integer.valueOf(sportTime.substring(3, 5));
				sport_time_second = Integer.valueOf(sportTime.substring(6, 8));
				// sport_time_second = sportTimeHour * 60 * 60 * 60 +
				// sportTimeMius * 60 * 60 + sportTimeScd * 60;
			}
			sport_state = Integer.valueOf(userPreference.getUserRunState());
			distance = Float.valueOf(userPreference.getUserFinishDis());
			sport_distance_tv.setText(decimalFormat.format(distance / 1000) + "KM");// 当前运动的公里数
			isPause = false;
			k_state.setBackgroundResource(R.drawable.k_pause_img);// 点击切换成暂停图标
			isFirstStart = false;
			// 计时线程
			timeCountThread();
		}

		switch (sport_state) {
		case RUN:
			k_sport_head.setImageResource(R.drawable.k_run_img);
			break;
		case WALK:
			k_sport_head.setImageResource(R.drawable.k_walk_img);
			break;
		case RIDE:
			k_sport_head.setImageResource(R.drawable.k_ride_img);
			break;
		default:
			break;
		}
		// 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		registerReceiver(mReceiver, iFilter);
		intent = new Intent(getApplicationContext(), LocationService.class);
		startService(intent);
		location();
		SpeechUtility.createUtility(getApplication(), SpeechConstant.APPID + "=56e62d06");
		// 初始化合成对象
		mTts = SpeechSynthesizer.createSynthesizer(MapRouteActivity.this, mTtsInitListener);
		mSharedPreferences = getSharedPreferences("xfConfig", MODE_PRIVATE);

		sportRecordTmpDbService = SportRecordTmpDbService.getInstance(MapRouteActivity.this);
		sportRecordDbService = SportRecordDbService.getInstance(MapRouteActivity.this);

		handler3Mius = new Handler();
		runnableInsertAndUpdate = new Runnable() {

			@Override
			public void run() {
				if (isupdate) {
					// TODO Auto-generated method stub
					// 没三分钟执行一次保存或者更新操作
					String motionTrack = list.toString();
					storeSportRecordTmp(motionTrack, distance);
					LogTool.i("*******执行了一次临时表和云端数据表的更新操作！*****");
					handler3Mius.postDelayed(this, 3 * 60 * 1000);// 3 * 60 *
																	// 1000是延时时长
				}
			}
		};
		handler3Mius.postDelayed(runnableInsertAndUpdate, 3 * 60 * 1000);// 打开定时器，执行操作
	}

	/**
	 * 开始定位
	 */
	protected void location() {
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0x001:
					MyLocationData locData = (MyLocationData) msg.obj;
					Log.i("123", locData.toString());
					mBaiduMap.setMyLocationData(locData);// 设置定位数据
					if (isFirstLoc) {// 判断是否首次定位
						isFirstLoc = false;
						// 获取经纬度
						LatLng ll = new LatLng(locData.latitude, locData.longitude);
						// 设置地图中心点以及缩放级别(50米)
						MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(ll, 19);
						mBaiduMap.animateMapStatus(mapStatusUpdate);
					}
					// level = mBaiduMap.getMapStatus().zoom;
					mLocation = new LatLng(locData.latitude, locData.longitude);
					routeLine(mLocation);
					break;
				case 0x002:
					Log.i("111", msg.obj.toString());
					sport_time_tv.setText(msg.obj + "");
					break;
				default:
					break;
				}
			}
		};
	}

	@Override
	protected void findViewById() {
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		k_state = (ImageButton) findViewById(R.id.k_state);
		finish_sport = (TextView) findViewById(R.id.finish_sport_choice);
		finish_state = (RelativeLayout) findViewById(R.id.finish_state);
		goOn = (ImageView) findViewById(R.id.goOn);
		ok = (ImageView) findViewById(R.id.ok);
		turnShow = (ImageView) findViewById(R.id.turn_show);
		turnMune = (LinearLayout) findViewById(R.id.turn_menu);
		loctionMyself = (ImageView) findViewById(R.id.location_myself);
		back = (ImageView) findViewById(R.id.left_btn_back);
		start_time_tv = (TextView) findViewById(R.id.time_record);
		sport_time_tv = (TextView) findViewById(R.id.time_compute);
		sport_distance_tv = (TextView) findViewById(R.id.route_distance);
		k_sport_head = (ImageView) findViewById(R.id.k_sport_head);
		completed = (TextView) findViewById(R.id.completed);
	}

	@Override
	protected void initView() {
		k_state.setOnClickListener(this);
		finish_sport.setOnClickListener(this);
		goOn.setOnClickListener(this);
		ok.setOnClickListener(this);
		turnShow.setOnClickListener(this);
		loctionMyself.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.k_state:// 开始暂停切换按钮,默认是暂停状态
			if (isPause) {
				// 判断是否为暂停状态
				isPause = false;
				k_state.setBackgroundResource(R.drawable.k_pause_img);// 点击切换成暂停图标
				if (isFirstStart) {
					isFirstStart = false;
					t.setToNow();
					// String time = t.hour + ":" + t.minute + ":" + t.second;
					String sth = t.hour + "";
					String stm = t.minute + "";
					String sts = t.second + "";
					if (t.hour < 10)
						sth = "0" + sth;
					if (t.minute < 10)
						stm = "0" + stm;
					if (t.second < 10)
						sts = "0" + sts;
					String time = sth + ":" + stm + ":" + sts;
					start_time_tv.setText(time);
					// 把开始时间和用户的运动状态保存下来
					userPreference.setUserStartTime(time);
					userPreference.setUserRunState(sport_state + "");

					// 计时线程
					timeCountThread();
				}

				// 点击开始时存一次临时记录表
				String motionTrack = list.toString();
				storeSportRecordTmp(motionTrack, distance);
				// 点击开始时，该用户开始跑
				userPreference.setUserFinish(false);
				userPreference.setUserRunDate(DateTimeTools.getCurDateDay());

			} else {// 点击之后
				isPause = true;
				k_state.setBackgroundResource(R.drawable.k_start_img);
				finish_state.setVisibility(View.VISIBLE);// 暂停之后 完成界面可见
				k_state.setClickable(false);// 按钮设置不可点击
				completed.setText(decimalFormat.format(distance / 1000));
			}
			break;
		case R.id.finish_sport_choice:// 完成按钮

			isupdate = false; // 结束更新临时运动表

			// 先往云端和本地数据表中写入此次运动的记录
			String motionTrack = list.toString();
			String curTime = DateTimeTools.getCurrentTimeForString(); // 取得当前时间的时分秒
			// 取此时此刻开始运动时间上的文本和运动时间上的文本
			String startTime = start_time_tv.getText().toString();
			String sportTime = sport_time_tv.getText().toString();
			int pauseTime = getPauseTime(curTime, sportTime, startTime);
			storeSportRecord(motionTrack, distance, pauseTime);

			// 完成运动后表示用户已经跑步完成
			userPreference.setUserFinish(true);
			handler3Mius.removeCallbacks(runnableInsertAndUpdate);// 关闭定时器处理
			// 完成后将数据置为初始状态
			userPreference.setResetState();

			mBaiduMap.snapshot(new SnapshotReadyCallback() {
				@Override
				public void onSnapshotReady(Bitmap bitmap) {
					// 图片存储路径
					String SavePath = getSDCardPath() + "/SportRecord/ScreenImages";
					// 保存Bitmap
					try {
						File path = new File(SavePath);
						// 文件
						String filepath = SavePath + "/Screen_1.png";
						File file = new File(filepath);
						if (!path.exists()) {
							path.mkdirs();
						}
						if (!file.exists()) {
							file.createNewFile();
						}
						FileOutputStream fos = null;
						fos = new FileOutputStream(file);
						if (null != fos) {
							bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
							fos.flush();
							fos.close();
							Toast.makeText(getApplicationContext(), "截屏文件已保存至SDCard/ScreenImages/目录下",
									Toast.LENGTH_LONG).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finish();
			this.startActivity(new Intent(MapRouteActivity.this, MainActivity.class));
			break;
		case R.id.goOn:// 暂停按钮之后的继续
			k_state.setBackgroundResource(R.drawable.k_pause_img);// 点击切换成暂停图标
			k_state.setClickable(true);// 暂停按钮设置可点击
			finish_state.setVisibility(View.INVISIBLE);// 点击继续按钮画面不可见
			isPause = false;
			break;
		case R.id.ok:// 暂停按钮之后的完成
			// finish();
			break;
		case R.id.turn_show:// 下拉按钮
			if (isTurn) {
				isTurn = false;
				turnMune.setVisibility(View.INVISIBLE);
				turnShow.setImageResource(R.drawable.turn_show_win);
			} else {
				isTurn = true;
				turnMune.setVisibility(View.VISIBLE);
				turnShow.setImageResource(R.drawable.up_show);
			}
			break;
		case R.id.location_myself:// 快速定位按钮
			// 地图中心点显示到定位点
			if (mLocation != null) {
				// 设置地图中心点以及缩放级别(50米)
				MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(mLocation, level);
				mBaiduMap.animateMapStatus(mapStatusUpdate);
			}
			break;
		case R.id.left_btn_back:// 返回按钮
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 计时线程启动调用过程
	 * 
	 * @Title: timeCountThread
	 * @Description: TODO
	 * @return: void
	 */
	public void timeCountThread() {
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!exit) {
					if (!isPause) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						sport_time_second++;
						if (sport_time_second == 60) {
							sport_time_second = 0;
							sport_time_minute++;
						}
						if (sport_time_minute == 60) {
							sport_time_minute = 0;
							sport_time_hour++;
						}
						sport_time = sport_time_hour * 3600 + sport_time_minute * 60 + sport_time_second;
						String sth = sport_time_hour + "";
						String stm = sport_time_minute + "";
						String sts = sport_time_second + "";
						if (sport_time_hour < 10)
							sth = "0" + sth;
						if (sport_time_minute < 10)
							stm = "0" + stm;
						if (sport_time_second < 10)
							sts = "0" + sts;
						String sportTime = sth + ":" + stm + ":" + sts;
						Message message = new Message();
						message.obj = sportTime;
						message.what = 0x002;
						handler.sendMessage(message);
					}
				}
			}
		});
		thread.start();
	}

	/**
	 * 画运动轨迹线,并且获取轨迹点
	 * 
	 * @param latlon
	 *            定位位置的经纬度
	 */
	public void routeLine(LatLng latlon) {
		if ("1".equals(fromReason)) {
			// 开始绘制上次未结束时的地图
			String userSportPts = userPreference.getUserSportPoints();
			// System.out.println(userSportPts + "******");
			userSportPts = userSportPts.substring(1, userSportPts.length() - 1);
			userSportPts = userSportPts.replace("latitude:", "");
			userSportPts = userSportPts.replace("longitude:", "");
			String[] arr = userSportPts.split(",");
			Double[] arrdouble = new Double[arr.length];
			for (int i = 0; i < arr.length; i++) {
				arrdouble[i] = Double.valueOf(arr[i]);
			}
			for (int i = 0; i < arrdouble.length - 3; i += 4) {
				LatLng pLatLng = new LatLng(arrdouble[i], arrdouble[i + 1]);
				LatLng nLatLng = new LatLng(arrdouble[i + 2], arrdouble[i + 3]);
				points = new ArrayList<LatLng>();
				points.add(pLatLng);
				points.add(nLatLng);
				// 画地图轨迹
				OverlayOptions ooPolyline = new PolylineOptions().width(10).color(Color.BLACK).points(points);
				mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
			}
		}

		if (!isPause) {// 判断是否暂停
			// 获取当前位置
			now = latlon;
			if (pre == null) {
				pre = now;
			}
			// 当位置发生变化开始画线
			if (pre != now) {
				// 实时保存用户运动的轨迹线路
				if (points != null && !points.isEmpty())
					userPreference.setUserSportPoints(points.toString());
				// 每次位置变化时，去记录当前跑步的时间和完成的距离
				String usingTime = sport_time_tv.getText().toString();
				userPreference.setUserUsingTime(usingTime);
				String finishDis = distance + "";
				userPreference.setUserFinishDis(finishDis);

				distance += DistanceUtil.getDistance(pre, now);// 获取两点间的距离(米),并叠加
				dist += DistanceUtil.getDistance(pre, now);
				if (dist / 1000 >= 1 || sport_time == 9000) {
					setParam();
					dist = 0;
					int speed = (int) (distance / sport_time / 60);
					int code = mTts.startSpeaking("您已经跑了" + distance / 1000 + "公里,平均速度为每分钟" + speed + "米",
							mTtsListener);
					if (code != ErrorCode.SUCCESS) {
						if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
							// 未安装则跳转到提示安装页面
							// mInstaller.install();
						} else {
							// showTip("语音合成失败,错误码: " + code);
						}
					}
				}
				sport_distance_tv.setText(decimalFormat.format(distance / 1000) + "KM");// 当前运动的公里数
				points = new ArrayList<LatLng>();
				points.add(pre);
				points.add(now);
				OverlayOptions ooPolyline = new PolylineOptions().width(10).color(Color.BLACK).points(points);
				mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);

				if (isContinue) {// 判断当前点是否为暂停之后继续定位的点
					point = "mark" + pre.longitude + "Lat" + pre.latitude;
					list.add(point);
					isContinue = false;
				}
				point = "Lon" + now.longitude + "Lat" + now.latitude;
				list.add(point);
				pre = now;
				if (!isStopMark) {
					isStopMark = true;
				}
			}
		} else {
			if (isStopMark) {
				String lastPoint = "stopMark" + list.get(list.size() - 1);
				list.remove(list.size() - 1);
				list.add(lastPoint);
				isStopMark = false;
				pre = null;// 暂停之后重置上一个点的定位坐标
				isContinue = true;
			}
		}
	}

	/**
	 * 初始化监听
	 */
	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d(TAG, "InitListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				// showTip("初始化失败,错误码："+code);
			} else {
				// 初始化成功，之后可以调用startSpeaking方法
				// 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
				// 正确的做法是将onCreate中的startSpeaking调用移至这里
			}
		}
	};

	/**
	 * 合成回调监听。
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener() {

		@Override
		public void onSpeakBegin() {
		}

		@Override
		public void onSpeakPaused() {
		}

		@Override
		public void onSpeakResumed() {
		}

		@Override
		public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
		}

		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
		}

		@Override
		public void onCompleted(SpeechError error) {
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
		}
	};

	/**
	 * 参数设置
	 * 
	 * @param param
	 * @return
	 */
	private void setParam() {
		// 清空参数
		mTts.setParameter(SpeechConstant.PARAMS, null);
		// 根据合成引擎设置相应参数
		if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
			mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
			// 设置在线合成发音人
			mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
			// 设置合成语速
			mTts.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "50"));
			// 设置合成音调
			mTts.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "50"));
			// 设置合成音量
			mTts.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "50"));
		} else {
			mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
			// 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
			mTts.setParameter(SpeechConstant.VOICE_NAME, "");
			/**
			 * 本地合成不设置语速、音调、音量，默认使用语记设置 开发者如需自定义参数，请参考在线合成参数设置
			 */
		}
		// 设置播放器音频流类型
		mTts.setParameter(SpeechConstant.STREAM_TYPE, mSharedPreferences.getString("stream_preference", "3"));
		// 设置播放合成音频打断音乐播放，默认为true
		mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
		mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
	}

	/**
	 * 获取SDCard的目录路径功能
	 * 
	 * @return
	 */
	private String getSDCardPath() {
		File sdcardDir = null;
		// 判断SDCard是否存在
		boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if (sdcardExist) {
			sdcardDir = Environment.getExternalStorageDirectory();
		}
		return sdcardDir.toString();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		mBaiduMap.setMyLocationEnabled(false);// 关闭定位图层
		mMapView.onDestroy();
		stopService(getIntent());
		mMapView = null;
		mTts.stopSpeaking();
		// 退出时释放连接
		mTts.destroy();
		if (thread != null) {
			exit = true;
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	/**
	 * 存入服务器临时记录表
	 * 
	 * @Title: storeSportRecordTmp
	 * @Description: TODO
	 * @param motionTrackList
	 * @param distance
	 * @return
	 * @return: Boolean
	 */
	public Boolean storeSportRecordTmp(final String motionTrack, final float distance) {
		final int sportType = sport_state; // 运动状态
		final String startTime = curDate;
		final String endTime = "";// 结束时间暂时不存
		final int pauseTime = 0;// 暂停时间暂时不存
		// String motionTrack = list.toString();
		// Float dstc = distance;
		// 点击完开始后
		SportRecordTmp srt = new SportRecordTmp(uuidString, userPreference.getUserId(), sportType, startTime, endTime,
				pauseTime, motionTrack, distance);
		// 存储信息，如果有的话就更新，否则做插入操作
		sportRecordTmpDbService.addSportRecordTmp(srt);

		// 去云端数据库中取对象 SportRecord
		AVQuery<AVObject> sportRecordQuery = new AVQuery<AVObject>("SportRecordTemp");
		sportRecordQuery.whereEqualTo("userID", userPreference.getUserId());
		sportRecordQuery.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					if (arg0.size() == 1) {
						AVObject post = arg0.get(0);
						post.put("uid", uuidString);
						post.put("userID", userPreference.getUserId());
						post.put("sportType", sportType);
						post.put("startTime", DateTimeTools.StringToDate(startTime));
						post.put("endTime", DateTimeTools.StringToDate(endTime));
						post.put("pauseTime", pauseTime);
						post.put("motionTrack", motionTrack);
						post.put("distance", distance);
						post.saveInBackground();
					}
				} else {// 如果云端不存在这样一条数据则插入
						// 同时将数据存到云端
					AVObject post = new AVObject("SportRecordTemp");
					post.put("uid", uuidString);
					post.put("userID", userPreference.getUserId());
					post.put("sportType", sportType);
					post.put("startTime", DateTimeTools.StringToDate(startTime));
					post.put("endTime", DateTimeTools.StringToDate(endTime));
					post.put("pauseTime", pauseTime);
					post.put("motionTrack", motionTrack);
					post.put("distance", distance);
					post.saveInBackground();
				}
			}
		});

		return true;
	}

	/**
	 * 存入服务器正式记录表
	 * 
	 * @Title: storeSportRecord
	 * @Description: TODO
	 * @param motionTrack
	 * @param distance
	 * @param endTime
	 * @param pauseTime
	 * @return
	 * @return: Boolean
	 */
	public Boolean storeSportRecord(final String motionTrack, final float distance, final int pauseTime) {
		final int sportType = sport_state; // 运动状态
		final String startTime = curDate;
		// String motionTrack = list.toString();
		// Float dstc = distance;
		final String endTime = DateTimeTools.getCurDateTime();
		// 点击完完成后
		SportRecord srt = new SportRecord(uuidString, userPreference.getUserId(), sportType, startTime, endTime,
				pauseTime, motionTrack, distance);
		// 存储信息，如果有的话就更新，否则做插入操作
		sportRecordDbService.addSportRecord(srt);

		// 去云端数据库中取对象 SportRecord
		AVQuery<AVObject> sportRecordQuery = new AVQuery<AVObject>("SportRecord");
		sportRecordQuery.whereEqualTo("userID", userPreference.getUserId());
		sportRecordQuery.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					if (arg0.size() == 1) {
						AVObject post = arg0.get(0);
						post.put("uid", uuidString);
						post.put("userID", userPreference.getUserId());
						post.put("sportType", sportType);
						post.put("startTime", DateTimeTools.StringToDate(startTime));
						post.put("endTime", DateTimeTools.StringToDate(endTime));
						post.put("pauseTime", pauseTime);
						post.put("motionTrack", motionTrack);
						post.put("distance", distance);
						post.saveInBackground();
					}
				} else {// 如果云端不存在这样一条数据则插入
					AVObject post = new AVObject("SportRecord");
					post.put("uid", uuidString);
					post.put("userID", userPreference.getUserId());
					post.put("sportType", sportType);
					post.put("startTime", DateTimeTools.StringToDate(startTime));
					post.put("endTime", DateTimeTools.StringToDate(endTime));
					post.put("pauseTime", pauseTime);
					post.put("motionTrack", motionTrack);
					post.put("distance", distance);
					post.saveInBackground();
				}
			}
		});

		// 做下积分获取操作

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userId", userPreference.getUserId());
		parameters.put("sportType", sportType + "");
		parameters.put("distance", distance + "");
		parameters.put("duration", sport_time * 1000 + "");
		long endTimelong = 0;
		try {
			endTimelong = DateTimeTools.getCurDateTimeLong();
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		parameters.put("endTimestamp", endTimelong + "");

		// parameters.put("userId", "B3026954-DDDF-4A8C-94A2-8DAA0CBC7466");
		// parameters.put("sportType", "2");
		// parameters.put("distance", "1230.6");
		// parameters.put("duration", "180");
		// parameters.put("endTimestamp", "1458113776990");
		AVCloud.callFunctionInBackground("GainIntegralByPersonalSport", parameters, new FunctionCallback<Object>() {
			public void done(Object object, AVException e) {
				if (object != null) {
					JsonTool jsonTool = new JsonTool(object.toString());
					JSONObject jsonObject = jsonTool.getJsonObject();
					Boolean status = jsonTool.getStatus(); // Boolean型变量，为true时成功
					if (status) {
						try {
							ToastTool.showShort(MapRouteActivity.this, jsonObject.getInt("integralGained") +"");
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						String errorMessage;
						errorMessage = (String) ((HashMap) object).get("errorMessage");
						// handleError
						LogTool.e(Constants.PACKAGENAME, "失败，errorMessage：" + errorMessage);
						ToastTool.showLong(MapRouteActivity.this, "获取积分失败！");
					}
				}
			}
		});

		return true;
	}

	/**
	 * 获取暂停时间
	 * 
	 * @Title: getPauseTime
	 * @Description: TODO
	 * @param curTime
	 * @param sportTime
	 * @param startTime
	 * @return: int
	 */
	public int getPauseTime(String curTime, String sportTime, String startTime) {
		int puaseTime = 0;
		if (curTime.length() == 8 && sportTime.length() == 8 && startTime.length() == 8) {
			// 现在分别取得各个时间点上的时分秒
			int curTimeHour = Integer.valueOf(curTime.substring(0, 2));
			int curTimeMius = Integer.valueOf(curTime.substring(3, 5));
			int curTimeScd = Integer.valueOf(curTime.substring(6, 8));

			int sportTimeHour = Integer.valueOf(sportTime.substring(0, 2));
			int sportTimeMius = Integer.valueOf(sportTime.substring(3, 5));
			int sportTimeScd = Integer.valueOf(sportTime.substring(6, 8));

			int startTimeHour = Integer.valueOf(startTime.substring(0, 2));
			int startTimeMius = Integer.valueOf(startTime.substring(3, 5));
			int startTimeScd = Integer.valueOf(startTime.substring(6, 8));

			int hour = 0, mius = 0, scd = 0;
			// 不可能超过24个小时
			if (curTimeHour > startTimeHour)
				hour = curTimeHour - startTimeHour - sportTimeHour;
			if (curTimeMius > startTimeMius)
				mius = curTimeMius - startTimeMius - sportTimeMius;
			// 时间上转了一个轮回
			if (curTimeMius < startTimeMius) {
				mius = curTimeMius - startTimeMius + 60 - sportTimeMius;
				if (hour >= 1)
					hour -= 1;
			}
			if (curTimeScd > startTimeScd)
				scd = curTimeHour - startTimeHour - sportTimeScd;
			// 时间上转了一个轮回
			if (curTimeScd < startTimeScd) {
				scd = curTimeHour - startTimeHour + 60 - sportTimeScd;
				if (mius >= 1)
					mius -= 1;
			}
			puaseTime = hour * 60 + mius; // 分钟数
		}
		return puaseTime;
	}
}
