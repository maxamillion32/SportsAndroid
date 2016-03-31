package com.xxn.sport.ui;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.xxn.sport.R;
import com.xxn.sport.base.BaseActivity;
import com.xxn.sport.base.BaseApplication;
import com.xxn.sport.dbservice.IntegralGainedDbService;
import com.xxn.sport.dbservice.SportRecordDbService;
import com.xxn.sport.utils.UserPreference;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RecordListActivity extends BaseActivity implements OnClickListener {

	private UserPreference userPreference;
	private ImageView leftBtnBack;
	private ListView listView;
	private List<Map<String, String>> sportitems; // 获取运动记录信息
	private List<Map<String, String>> hisitems; // 获取历史积分的数据
	private TextView timeRecord;
	private TextView scoreRecord;
	private TextView sportRecord;
	private RelativeLayout pastRecordlayout;
	private RelativeLayout sportRecordList;
	private ImageView dotView_1;
	private ImageView dotView_2;
	private TextView pastRecordListText;
	private TextView sportRecordListText;
	private TextView totalIntegralgainedText;
	String userid = ""; // 用户识别码
	IntegralGainedDbService integralGainedDbService = null;
	SportRecordDbService sportRecordDbService = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_recordlist);
		userPreference = BaseApplication.getInstance().getUserPreference();
		userid = userPreference.getUserId(); // 用户ID

		// 为历史积分提供数据源支持
		integralGainedDbService = IntegralGainedDbService.getInstance(RecordListActivity.this);
		hisitems = integralGainedDbService.getIntegralGainedHis(userid);

		// 为运动历史提供数据源支持
		sportRecordDbService = SportRecordDbService.getInstance(RecordListActivity.this);
		sportitems = sportRecordDbService.getSportRecordhis(userid);

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		listView = (ListView) findViewById(R.id.list_view);
		leftBtnBack = (ImageView) findViewById(R.id.left_btn_back);
		pastRecordlayout = (RelativeLayout) findViewById(R.id.past_record_list);
		sportRecordList = (RelativeLayout) findViewById(R.id.sport_record_list);
		dotView_1 = (ImageView) findViewById(R.id.selected_dot_1);
		dotView_2 = (ImageView) findViewById(R.id.selected_dot_2);
		pastRecordListText = (TextView) findViewById(R.id.past_record_list_text);
		sportRecordListText = (TextView) findViewById(R.id.sport_record_list_text);
		totalIntegralgainedText = (TextView) findViewById(R.id.total_integralgained_text);

	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		listView.setAdapter(new CustomListAdapter(this));
		leftBtnBack.setOnClickListener(this);
		pastRecordlayout.setOnClickListener(this);
		sportRecordList.setOnClickListener(this);

		// 初始化用户总的积分数和记录数据
//		String integralGainedTotal = integralGainedDbService.getIntegralGainedTotal(userid);
//		if (!"".equals(integralGainedTotal))
//			totalIntegralgainedText.setText(integralGainedTotal);
		totalIntegralgainedText.setText("  当前积分" + userPreference.getIntegral() + "分");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_btn_back:
			this.finish();
			break;
		case R.id.past_record_list:
			dotView_1.setVisibility(View.VISIBLE);
			pastRecordListText.setTextColor(Color.RED);
			dotView_2.setVisibility(View.INVISIBLE);
			sportRecordListText.setTextColor(Color.BLACK);
			listView.setAdapter(new CustomListAdapter(this, 0));
			break;
		case R.id.sport_record_list:
			dotView_1.setVisibility(View.INVISIBLE);
			pastRecordListText.setTextColor(Color.BLACK);
			dotView_2.setVisibility(View.VISIBLE);
			sportRecordListText.setTextColor(Color.RED);
			listView.setAdapter(new CustomListAdapter(this, 1));
			break;
		default:
			break;
		}
	}

	class CustomListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Context mContext = null;
		private int whichType = 0;

		public CustomListAdapter(Context context) {
			mContext = context;
			mInflater = LayoutInflater.from(mContext);
		}

		public CustomListAdapter(Context context, int type) {
			mContext = context;
			whichType = type;
			mInflater = LayoutInflater.from(mContext);
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			if (whichType == 0) {
				return hisitems.get(arg0);
			} else {
				return sportitems.get(arg0);
			}
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			if (whichType == 0) {
				return hisitems.size();
			} else {
				return sportitems.size();
			}
		}

		public View getView(int position, View convertView, android.view.ViewGroup parent) {
			switch (whichType) {
			case 0:
				// 和item_custom.xml脚本关联
				if (convertView == null)
					convertView = mInflater.inflate(R.layout.past_record_item_custom, null);
				timeRecord = (TextView) convertView.findViewById(R.id.time_record);
				scoreRecord = (TextView) convertView.findViewById(R.id.score_record);

				// 设置获取历史积分的信息
				Map<String, String> mapValue = hisitems.get(position);
				Iterator iter = mapValue.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = (String) entry.getKey();
					String val = (String) entry.getValue();

					timeRecord.setText("" + key);
					scoreRecord.setText("" + val);
				}
				break;
			case 1:
				if (convertView == null)
					convertView = mInflater.inflate(R.layout.sport_record_item_custom, null);
				timeRecord = (TextView) convertView.findViewById(R.id.time_record);
				sportRecord = (TextView) convertView.findViewById(R.id.sport_record);
				
				// 设置运动历史的信息
				Map<String, String> mapValueother = sportitems.get(position);
				Iterator iterother = mapValueother.entrySet().iterator();
				while (iterother.hasNext()) {
					Map.Entry entry = (Map.Entry) iterother.next();
					String key = (String) entry.getKey();
					String val = (String) entry.getValue();

					timeRecord.setText("" + key);
					sportRecord.setText("" + val);
				}
				break;
			default:
				break;
			}

			return convertView;
		}
	}

}
