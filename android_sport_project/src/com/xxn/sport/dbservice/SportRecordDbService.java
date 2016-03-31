package com.xxn.sport.dbservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xxn.sport.base.BaseApplication;
import com.xxn.sport.dao.DaoSession;
import com.xxn.sport.dao.SportRecordDao;
import com.xxn.sport.dao.SportRecordDao.Properties;
import com.xxn.sport.entities.SportRecord;
import com.xxn.sport.utils.DateTimeTools;

import android.content.Context;

public class SportRecordDbService {
	private static final String TAG = SportRecordDbService.class.getSimpleName();
	private static SportRecordDbService instance;
	private static Context appContext;
	private DaoSession mDaoSession;
	public SportRecordDao sportrecorddao;

	public SportRecordDbService() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 得到实例
	 * 
	 * @param context
	 * @return
	 */
	public static SportRecordDbService getInstance(Context context) {
		if (instance == null) {
			instance = new SportRecordDbService();
			if (appContext == null) {
				appContext = context.getApplicationContext();
			}
			instance.mDaoSession = BaseApplication.getDaoSession(context);
			instance.sportrecorddao = instance.mDaoSession.getSportRecordDao();
		}
		return instance;
	}

	public void addSportRecord(SportRecord sportrecord) {
		sportrecorddao.insertOrReplace(sportrecord);
	}

	/**
	 * 删除数据表中的所有实体
	 * 
	 * @Title: deleteAllSportRecord
	 * @Description: TODO
	 * @return: void
	 */
	public void deleteAllSportRecord() {
		sportrecorddao.deleteAll();
	}

	/**
	 * 获取运动积分历史(时间和公里数)
	 * 
	 * @Title: getSportRecordhis
	 * @Description: TODO
	 * @param userId
	 * @return
	 * @return: List<Map<String,String>>
	 */
	public List<Map<String, String>> getSportRecordhis(String userId) {
		List<Map<String, String>> hisLists = new ArrayList<Map<String, String>>();
		List<SportRecord> lists = sportrecorddao.queryBuilder().where(Properties.UserID.eq(userId)).list();
		for (SportRecord sportrecord : lists) {
			Map<String, String> mapValue = new HashMap<String, String>();
			// 获得历史运动记录的时间
			String gainedTime = sportrecord.getStartTime();
			String newGainedTime = "";
			int timeDif = 0;
			if (!"".equals(gainedTime))
				newGainedTime = gainedTime.substring(0, 10);
			// 获取的运动的公里数
			Float dis = sportrecord.getDistance();
			if (!"".equals(sportrecord.getEndTime()) && !"".equals(sportrecord.getEndTime())) {
				Date endDate = DateTimeTools.StringToDate(sportrecord.getEndTime());
				Date startDate = DateTimeTools.StringToDate(sportrecord.getEndTime());
				// 取得时间差（以分钟形式存在）
				timeDif = DateTimeTools.getTimeInterval(endDate, startDate);
			}
			
			mapValue.put(newGainedTime, dis + "公里" + "-" + timeDif + "小时");
			// 将积分和时间存入到list<map>中
			hisLists.add(mapValue);
		}
		return hisLists;
	}

	/**
	 * 获取总运动时间
	 * 
	 * @Title: getSportRecordTime
	 * @Description: TODO
	 * @param userId
	 * @return
	 * @return: String
	 */
	public String getSportRecordTime(String userId) {
		int timeDif = 0;
		List<SportRecord> lists = sportrecorddao.queryBuilder().where(Properties.UserID.eq(userId)).list();
		for (SportRecord sportRecord : lists) {
			if (!"".equals(sportRecord.getEndTime()) && !"".equals(sportRecord.getEndTime())) {
				Date endDate = DateTimeTools.StringToDate(sportRecord.getEndTime());
				Date startDate = DateTimeTools.StringToDate(sportRecord.getStartTime());
				// 取得时间差（以分钟形式存在）
				timeDif += DateTimeTools.getTimeInterval(endDate, startDate);
			}
		}
		int hours = timeDif / 60;
		int mus = timeDif % 60;
		return hours + "小时" + mus + "分钟";
	}

	/**
	 * 找到最近的一个运动记录
	 * 
	 * @Title: getLatestSportRecord
	 * @Description: TODO
	 * @param userId
	 * @return
	 * @return: SportRecord
	 */
	public SportRecord getLatestSportRecord(String userId) {
		List<SportRecord> lists = sportrecorddao.queryBuilder().where(Properties.UserID.eq(userId)).list();
		SportRecord latestsportRecord = new SportRecord();
		for (SportRecord sportRecord : lists) {
			latestsportRecord = sportRecord;
		}
		return latestsportRecord;
	}

	/**
	 * 获取总的运动距离
	 * 
	 * @Title: getSportRecordDistance
	 * @Description: TODO
	 * @param userId
	 * @return
	 * @return: String
	 */
	public String getSportRecordDistance(String userId) {
		Float distance = 0f; // 公里数
		List<SportRecord> lists = sportrecorddao.queryBuilder().where(Properties.UserID.eq(userId)).list();
		for (SportRecord sportRecord : lists) {
			Float dis = sportRecord.getDistance();
			distance += dis;
		}
		return distance + "公里";
	}
}
