package com.xxn.sport.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FunctionCallback;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.xxn.sport.R;
import com.xxn.sport.base.BaseActivity;
import com.xxn.sport.base.BaseApplication;
import com.xxn.sport.config.Constants;
import com.xxn.sport.utils.FastJsonTool;
import com.xxn.sport.utils.LogTool;
import com.xxn.sport.utils.ToastTool;
import com.xxn.sport.utils.UserPreference;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ScoreBoardActivity extends BaseActivity implements OnClickListener {

	private UserPreference userPreference;
	private ListView listView;
	private List<String> items = new ArrayList<String>();;
	private List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>(); // 用于存储list数据
	private TextView orderNum;
	private TextView rankUserName;
	private TextView userScore;
	private ImageView sportHead;
	private ImageView leftBtnBack;
	private TextView scoreBoardOrderText;
	private ImageView headHat;
	private View recordTopRight;
	private View dayLayout;
	private View weekLayout;
	private View monthLayout;
	private View yearLayout;
	private ImageView iv_1;
	private ImageView iv_2;
	private ImageView iv_3;
	private ImageView iv_4;

	private int SCORE_BOARD_RESULT = -1;
	private int flag = 1; // 全局变量用来进行显示的设定

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_scoreboard);
		userPreference = BaseApplication.getInstance().getUserPreference();

		// 首先填充好数组
		// fillArray();
		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		listView = (ListView) findViewById(R.id.list_view);
		leftBtnBack = (ImageView) findViewById(R.id.left_btn_back);
		scoreBoardOrderText = (TextView) findViewById(R.id.score_board_order_text);
		recordTopRight = (View) findViewById(R.id.record_top_right);

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
		leftBtnBack.setOnClickListener(this);
		scoreBoardOrderText.setOnClickListener(this);

		dayLayout.setOnClickListener(this);
		weekLayout.setOnClickListener(this);
		monthLayout.setOnClickListener(this);
		yearLayout.setOnClickListener(this);

		// SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd
		// hh:mm:ss");
		// String date = sDateFormat.format(new java.util.Date());
		Map<String, Object> parameters = new HashMap<String, Object>();// 初始化积分榜
		parameters.put("type", "1"); // 积分榜 - 1 （1-积分，2-里程，3-集赞）
		parameters.put("unit", "3"); // 1-日，2-周，3-月，4-年度
		parameters.put("timestamp", ""); // float参考时间
		// 初始时先去获取积分信息，按月来获取,填充数据
		getScoreBoardInfo(parameters);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Map<String, Object> parameters = new HashMap<String, Object>();// 初始化积分榜
		switch (v.getId()) {
		case R.id.left_btn_back:
			this.finish();
			break;
		case R.id.score_board_order_text:
			// 做来回切换操作
			if (flag == 1) {
				recordTopRight.setVisibility(View.VISIBLE);
				flag = 0;
			} else if (flag == 0) {
				recordTopRight.setVisibility(View.INVISIBLE);
				flag = 1;
			}
			break;
		case R.id.day_ranklist:
			iv_1.setBackgroundResource(R.drawable.title_btn_dot_sel);
			iv_2.setBackgroundResource(R.drawable.title_btn_dot_normal);
			iv_3.setBackgroundResource(R.drawable.title_btn_dot_normal);
			iv_4.setBackgroundResource(R.drawable.title_btn_dot_normal);
			recordTopRight.setVisibility(View.INVISIBLE);
			flag = 1;// 设置为下次可见标注
			scoreBoardOrderText.setText("日排行");
			// 传参去请求积分排行榜的信息
			parameters.put("type", "1"); // 积分榜 - 1 （1-积分，2-里程，3-集赞）
			parameters.put("unit", "1"); // 1-日，2-周，3-月，4-年度
			parameters.put("timestamp", ""); // float参考时间
			getScoreBoardInfo(parameters);
			break;
		case R.id.week_ranklist:
			iv_1.setBackgroundResource(R.drawable.title_btn_dot_normal);
			iv_2.setBackgroundResource(R.drawable.title_btn_dot_sel);
			iv_3.setBackgroundResource(R.drawable.title_btn_dot_normal);
			iv_4.setBackgroundResource(R.drawable.title_btn_dot_normal);
			recordTopRight.setVisibility(View.INVISIBLE);
			flag = 1;// 设置为下次可见标注
			scoreBoardOrderText.setText("周排行");
			// 传参去请求积分排行榜的信息
			parameters.put("type", "1"); // 积分榜 - 1 （1-积分，2-里程，3-集赞）
			parameters.put("unit", "2"); // 1-日，2-周，3-月，4-年度
			parameters.put("timestamp", ""); // float参考时间
			getScoreBoardInfo(parameters);
			break;
		case R.id.month_ranklist:
			iv_1.setBackgroundResource(R.drawable.title_btn_dot_normal);
			iv_2.setBackgroundResource(R.drawable.title_btn_dot_normal);
			iv_3.setBackgroundResource(R.drawable.title_btn_dot_sel);
			iv_4.setBackgroundResource(R.drawable.title_btn_dot_normal);
			recordTopRight.setVisibility(View.INVISIBLE);
			flag = 1;// 设置为下次可见标注
			scoreBoardOrderText.setText("月排行");
			// 传参去请求积分排行榜的信息
			parameters.put("type", "1"); // 积分榜 - 1 （1-积分，2-里程，3-集赞）
			parameters.put("unit", "3"); // 1-日，2-周，3-月，4-年度
			parameters.put("timestamp", ""); // float参考时间
			getScoreBoardInfo(parameters);
			break;
		case R.id.year_ranklist:
			iv_1.setBackgroundResource(R.drawable.title_btn_dot_normal);
			iv_2.setBackgroundResource(R.drawable.title_btn_dot_normal);
			iv_3.setBackgroundResource(R.drawable.title_btn_dot_normal);
			iv_4.setBackgroundResource(R.drawable.title_btn_dot_sel);
			recordTopRight.setVisibility(View.INVISIBLE);
			flag = 1;// 设置为下次可见标注
			scoreBoardOrderText.setText("年排行");
			// 传参去请求积分排行榜的信息
			parameters.put("type", "1"); // 积分榜 - 1 （1-积分，2-里程，3-集赞）
			parameters.put("unit", "4"); // 1-日，2-周，3-月，4-年度
			parameters.put("timestamp", ""); // float参考时间
			getScoreBoardInfo(parameters);
			break;
		case R.id.login_button:
			this.startActivity(new Intent(this, MainActivity.class));
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			// case SCORE_BOARD_RESULT:
			// Bundle bundle = data.getExtras();
			// String back = bundle.getString("back");
			//// Toast.makeText(getApplicationContext(), "A界面传回来的数据为:::::::" +
			// back, 0).show();
			//// content.setText("A界面传回来的数据" + back);
			// break;
			default:
				break;
			}
		}

	}

	private void fillArray() {
		items = new ArrayList<String>();
		items.add("二十丁");
		items.add("Waiting");
		items.add("丁磊");
		items.add("A-NZ");
		items.add("吴大郎");
		items.add("张一");
		items.add("刘江儿");
		items.add("江流儿");
	}

	/**
	 * 
	 * 根据type( 1-积分，2-里程，3-集赞)和unit(1-日，2-周，3-月，4-年度)获取个人K榜信息
	 * 
	 * @Title: getScoreBoardInfo
	 * @Description: TODO
	 * @param parameters
	 *            (type/unit/timestamp)
	 * @return: void
	 */
	public void getScoreBoardInfo(Map<String, Object> parameters) {
		AVCloud.callFunctionInBackground("GetUserTopList", parameters, new FunctionCallback<Object>() {
			public void done(Object object, AVException e) {
				// List<Map<String, Object>> listMap = null;
				if(e == null){
					String jsonString = JSON.toJSONString(object);
					System.out.println(jsonString);
				}
				Boolean status = false;
				JSONObject jsonObject = FastJsonTool.hashMapToJson((HashMap) object);
				String resultCode = "";
				try {
					resultCode = jsonObject.getString("resultCode");
					if ("200".equals(resultCode))
						status = true;
					listMap = (List<Map<String, Object>>) ((HashMap) object).get("info");
				} catch (JSONException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				if (status) {
					listView.setAdapter(new CustomListAdapter(getContext()));
				} else {
					 String errorMessage = (String) ((HashMap) object).get("errorMessage");
					 LogTool.e(Constants.PACKAGENAME,
					 "获取积分榜出错失败，errorMessage：" + errorMessage);
					ToastTool.showLong(ScoreBoardActivity.this, "获取积分榜出错！");
				}
			}
		});
	}

	/**
	 * 获得当前context域
	 * 
	 * @Title: getContext
	 * @Description: TODO
	 * @return
	 * @return: Context
	 */
	public Context getContext() {
		return this;
	}

	class CustomListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Context mContext = null;

		public CustomListAdapter(Context context) {
			mContext = context;
			mInflater = LayoutInflater.from(mContext);
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return listMap.get(arg0);
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub111
			return position;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return listMap.size();
		}

		public View getView(int position, View convertView, android.view.ViewGroup parent) {
			if (convertView == null) {
				// 和item_custom.xml脚本关联
				convertView = mInflater.inflate(R.layout.score_item_custom, null);
			}
			// 排序数字
			orderNum = (TextView) convertView.findViewById(R.id.order_num);
			// 必须放在这里加载
			rankUserName = (TextView) convertView.findViewById(R.id.rank_user_name);
			userScore = (TextView) convertView.findViewById(R.id.rank_user_score);
			sportHead = (ImageView) convertView.findViewById(R.id.sport_head);
			// 帽子
			headHat = (ImageView) convertView.findViewById(R.id.sport_head_hat);
			switch (position) {
			case 0:
				headHat.setBackgroundResource(R.drawable.hat_red);
				break;
			case 1:
				headHat.setBackgroundResource(R.drawable.hat_blue);
				break;
			case 2:
				headHat.setBackgroundResource(R.drawable.hat_yellow);
				break;
			default:
				break;
			}
			// 排列序号
			orderNum.setText("" + (position + 1));
			// 对字符串进行切分
			String name = (String) listMap.get(position).get("userName") + "";
			String integral = listMap.get(position).get("integral") + "";
			String portrait = listMap.get(position).get("portrait") + "";
			if (!"".equals(name) && !"".equals(integral)) {
				// 积分榜中的姓名
				rankUserName.setText(name);
				userScore.setText(integral);
				// 下载图片作为头像
				imageLoader.displayImage(portrait, sportHead, ScoreBoardActivity.getImageOptions());
			}
			// 设置item中ImageView的图片
			// indexImage.setBackgroundResource(R.drawable.icon);
			return convertView;
		}
	}

	// 处理图片下载
	public static DisplayImageOptions getImageOptions() {
		DisplayImageOptions options = null;
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.k_sport_head)// 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.k_sport_head) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.k_sport_head) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
//				.displayer(new CircleBitmapDisplayer()) // 设置成圆角图片
				.build(); // 创建配置过得DisplayImageOption对象
		return options;
	}

}
