package com.xxn.sport.ui;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;

public class LocationService extends Service implements BDLocationListener{
	private LocationClient mLocClient;// 定位相关
	@Override
	public void onCreate() {
		super.onCreate();
		// 定位初始化
		mLocClient = new LocationClient(getApplicationContext());
		//为LocationClient设置监听器，一旦获取了结果，会回调listener中的onReceiveLocation方法
		mLocClient.registerLocationListener(this);
		//设置定位的参数，包括坐标编码方式，定位方式等等
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式为高精度
		option.setCoorType("bd09ll"); // 设置返回的定位结果坐标类型
		option.setScanSpan(1000);//设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setOpenGps(true); // 打开gps
		option.setLocationNotify(true);//当gps有效时按照1S1次频率输出GPS结果
		mLocClient.setLocOption(option);
		//启动定位
		mLocClient.start();
	}	

	/**
	 * 定位SDK监听函数
	 */
	@Override
	public void onReceiveLocation(BDLocation location) {
		if (location == null) {
			return;
		}
		MyLocationData locData = new MyLocationData.Builder()
		.accuracy(location.getRadius())// 此处设置开发者获取到的方向信息，顺时针0-360
		.direction(100)
		.latitude(location.getLatitude())
		.longitude(location.getLongitude())
		.build();
		Message message = new Message();
		message.obj = locData;
		message.what = 0x001;
		MapRouteActivity.handler.sendMessage(message);
	}

	@Override                          
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mLocClient.isStarted()){
			mLocClient.stop();
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

}
