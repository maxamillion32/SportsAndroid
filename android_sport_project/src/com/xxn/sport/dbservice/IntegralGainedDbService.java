package com.xxn.sport.dbservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xxn.sport.base.BaseApplication;
import com.xxn.sport.dao.DaoSession;
import com.xxn.sport.dao.IntegralGainedDao;
import com.xxn.sport.dao.IntegralGainedDao.Properties;
import com.xxn.sport.entities.IntegralGained;

import android.content.Context;

public class IntegralGainedDbService {
	private static final String TAG = IntegralGainedDbService.class.getSimpleName();
	private static IntegralGainedDbService instance;
	private static Context appContext;
	private DaoSession mDaoSession;
	public IntegralGainedDao integralgaineddao;

	public IntegralGainedDbService() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 得到实例
	 * 
	 * @param context
	 * @return
	 */
	public static IntegralGainedDbService getInstance(Context context) {
		if (instance == null) {
			instance = new IntegralGainedDbService();
			if (appContext == null) {
				appContext = context.getApplicationContext();
			}
			instance.mDaoSession = BaseApplication.getDaoSession(context);
			instance.integralgaineddao = instance.mDaoSession.getIntegralGainedDao();
		}
		return instance;
	}

	public List<IntegralGained> getSoundSourceType01() {
		return integralgaineddao.queryBuilder().where(Properties.Uid.eq("01")).list();
	}

	public List<IntegralGained> getSoundSourceType02() {
		return integralgaineddao.queryBuilder().where(Properties.Uid.eq("02")).list();
	}

	public List<IntegralGained> getSoundSourceType03() {
		return integralgaineddao.queryBuilder().where(Properties.Uid.eq("03")).list();
	}

	/**
	 * 获取用户总的积分数显示在顶端
	 * 
	 * @Title: getIntegralGainedTotal
	 * @Description: TODO
	 * @param userId
	 * @return
	 * @return: String
	 */
	public String getIntegralGainedTotal(String userId) {
		int integralTotal = 0; // 获取的总的积分数
		List<IntegralGained> lists = integralgaineddao.queryBuilder().where(Properties.UseId.eq(userId)).list();
		for (IntegralGained integralgained : lists) {
			int integral = integralgained.getIntegral();
			integralTotal += integral;
		}
		return "  当前积分" + integralTotal + "分";
	}

	/**
	 * 得到获取积分的历史
	 * @Title: getIntegralGainedHis 
	 * @Description: TODO
	 * @param userId
	 * @return
	 * @return: List<Map<String,String>>
	 */
	public List<Map<String,String>> getIntegralGainedHis(String userId) {
		List<Map<String,String>> hisLists = new ArrayList<Map<String,String>>();
		List<IntegralGained> lists = integralgaineddao.queryBuilder().where(Properties.UseId.eq(userId)).list();
		for (IntegralGained integralgained : lists) {
			Map<String,String> mapValue = new HashMap<String, String>();
			//获得积分的时间
			String gainedTime = integralgained.getGainTime();
			String newGainedTime = "";
			if(!"".equals(gainedTime))
			newGainedTime = gainedTime.substring(0, 10);
			//获取的积分
			int integral = integralgained.getIntegral();
			mapValue.put(newGainedTime, integral+"");
			//将积分和时间存入到list<map>中
			hisLists.add(mapValue);
		}
		return hisLists;
	}

	/**
	 * 添加实体数据
	 * 
	 * @Title: addIntegralGained
	 * @Description: TODO
	 * @param integralgained
	 * @return: void
	 */
	public void addIntegralGained(IntegralGained integralgained) {
		integralgaineddao.insertOrReplace(integralgained);
	}

	/**
	 * 删除数据表中的所有实体
	 * 
	 * @Title: deleteAllIntegralGained
	 * @Description: TODO
	 * @return: void
	 */
	public void deleteAllIntegralGained() {
		integralgaineddao.deleteAll();
	}
}
