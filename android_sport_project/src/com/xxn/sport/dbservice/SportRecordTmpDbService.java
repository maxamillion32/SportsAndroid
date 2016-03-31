package com.xxn.sport.dbservice;

import com.xxn.sport.base.BaseApplication;
import com.xxn.sport.dao.DaoSession;
import com.xxn.sport.dao.SportRecordTmpDao;
import com.xxn.sport.entities.SportRecord;
import com.xxn.sport.entities.SportRecordTmp;

import android.content.Context;

public class SportRecordTmpDbService {
	private static final String TAG = SportRecordTmpDbService.class.getSimpleName();
	private static SportRecordTmpDbService instance;
	private static Context appContext;
	private DaoSession mDaoSession;
	public SportRecordTmpDao sportrecordtmpdao;

	public SportRecordTmpDbService() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 得到实例
	 * @param context
	 * @return
	 */
	public static SportRecordTmpDbService getInstance(Context context) {
		if (instance == null) {
			instance = new SportRecordTmpDbService();
			if (appContext == null) {
				appContext = context.getApplicationContext();
			}
			instance.mDaoSession = BaseApplication.getDaoSession(context);
			instance.sportrecordtmpdao = instance.mDaoSession.getSportRecordTmpDao();
		}
		return instance;
	}

	/**
	 * 存放临时运动记录信息
	 * @Title: addSportRecordTmp 
	 * @Description: TODO
	 * @param sportrecordtmp
	 * @return: void
	 */
	public void addSportRecordTmp(SportRecordTmp sportrecordtmp) {
		sportrecordtmpdao.insertOrReplace(sportrecordtmp);
	}
	
	/**
	 * 删除数据表中的所有实体
	 * @Title: deleteAllSportRecordTmp 
	 * @Description: TODO
	 * @return: void
	 */
	public void deleteAllSportRecordTmp() {
		sportrecordtmpdao.deleteAll();
	}
}
