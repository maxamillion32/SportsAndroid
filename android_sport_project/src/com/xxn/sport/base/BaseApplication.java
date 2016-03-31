package com.xxn.sport.base;

import java.util.LinkedHashMap;
import java.util.Map;

import com.avos.avoscloud.AVOSCloud;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.xxn.sport.dao.DaoMaster;
import com.xxn.sport.dao.DaoMaster.OpenHelper;
import com.xxn.sport.dao.DaoSession;
import com.xxn.sport.utils.SharePreference;
import com.xxn.sport.utils.UserPreference;

import android.app.Application;
import android.content.Context;

/**
 * 类名称：BaseApplication 类描述：该类引入百度地图SDK使用位置信息，全局使用userPreference
 * 将取得DaoMaster对象的方法放到Application层这样避免多次创建生成Session对象。
 * 
 * @Prject: Sport
 * @Package: com.xxn.sport.base
 * @ClassName: BaseApplication
 * @Description: TODO
 * @author: lzjing
 * @date: 2016年3月20日 下午4:37:27
 */
public class BaseApplication extends Application {
	private static BaseApplication myApplication;
	// SQLite数据库操作
	private static DaoMaster daoMaster;
	private static DaoSession daoSession;

	private Map<String, Integer> mFaceMap = new LinkedHashMap<String, Integer>();
	private UserPreference userPreference;
	private SharePreference sharePreference;

	public synchronized static BaseApplication getInstance() {
		return myApplication;
	}

	// @Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		if (myApplication == null)
			myApplication = this;

		// leancloud如果使用美国节点，请加上这行代码 AVOSCloud.useAVCloudUS();
		 AVOSCloud.initialize(this, "WGUoySgrc7k8FVz2dvWNkJAd-gzGzoHsz",
		 "vHarYvaUm7SuGQQSoOFGVA4m");

		// AVOSCloud.initialize(this, "hypeXHruGm4T8xMYa6RQmXaU-gzGzoHsz",
		// "kYMh0g9pRz2F0xdT5DVxJYaA");
		// AVObject testObject = new AVObject("TestObject");
		// testObject.put("foo", "so easy...");
		// testObject.saveInBackground();

//		AVOSCloud.initialize(this, "hypeXHruGm4T8xMYa6RQmXaU-gzGzoHsz", "kYMh0g9pRz2F0xdT5DVxJYaA");
//		AVObject testObject = new AVObject("CityList");
//		testObject.put("city", "成都");
//		testObject.saveInBackground();

		// 登录完后去数据库获取数据表，更新到本地中
//		java.util.List<AVObject> objects = new ArrayList<AVObject>();
//		AVObject integralGainedAvo = new AVObject("CityList");
//		objects.add(integralGainedAvo);
//		AVObject.fetchAllInBackground(objects, new FindCallback<AVObject>() {
//
//			@Override
//			public void done(List<AVObject> arg0, AVException arg1) {
//				// TODO Auto-generated method stub
//				if (!arg0.isEmpty()) {
//					for (AVObject avObject : arg0) {
//						// 取出数据并存入本地的表中
//						System.out.println(avObject);
//					}
//				}
//			}
//		});

		initImageLoader(getApplicationContext());

		// initFaceMap();
		initData();

	}

	private void initData() {
		userPreference = new UserPreference(this);
		// messagePlayer = MediaPlayer.create(this, R.raw.office);
		/***
		 * 初始化定位sdk，建议在Application中创建
		 */
		// locationService = new LocationService(getApplicationContext());
		// WriteLog.getInstance().init(); // 初始化日志
		// SDKInitializer.initialize(getApplicationContext());
	}

	/**
	 * 返回消息提示声音
	 * 
	 * @return
	 */
	// public synchronized MediaPlayer getMessagePlayer() {
	// if (messagePlayer == null)
	// messagePlayer = MediaPlayer.create(this, R.raw.office);
	// return messagePlayer;
	// }

	// 同一时间只有一个线程访问
	public synchronized UserPreference getUserPreference() {
		if (userPreference == null)
			userPreference = new UserPreference(this);
		return userPreference;
	}

	// 同一时间只有一个线程访问
	public synchronized SharePreference getSharePreference() {
		if (sharePreference == null)
			sharePreference = new SharePreference(this);
		return sharePreference;
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	public Map<String, Integer> getFaceMap() {
		if (!mFaceMap.isEmpty())
			return mFaceMap;
		return null;
	}

	/**
	 * SQLite数据库操作之取得DaoMaster
	 * 
	 * @param context
	 * @return
	 */
	public static DaoMaster getDaoMaster(Context context) {
		if (daoMaster == null) {
			OpenHelper openHelper = new DaoMaster.DevOpenHelper(context, "sport.db", null);
			daoMaster = new DaoMaster(openHelper.getWritableDatabase());
		}
		return daoMaster;
	}

	/**
	 * SQLite数据库操作之取得DaoSession
	 * 
	 * @param context
	 * @return
	 */
	public static DaoSession getDaoSession(Context context) {
		if (daoSession == null) {
			if (daoMaster == null) {
				daoMaster = getDaoMaster(context);
			}
			daoSession = daoMaster.newSession();
		}
		return daoSession;
	}

}
