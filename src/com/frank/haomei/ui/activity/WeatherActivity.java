package com.frank.haomei.ui.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.frank.haomei.R;
import com.frank.haomei.adapter.WeatherFragmentAdapter;
import com.frank.haomei.base.BaseFragmentActivity;
import com.frank.haomei.bean.City;
import com.frank.haomei.db.HaomeiDB;
import com.frank.haomei.ui.fragment.WeatherFragment;
import com.frank.haomei.util.ActivityRequestCode;
import com.frank.haomei.util.CacheUtil;
import com.frank.haomei.util.DateUtil;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.update.UmengUpdateAgent;

public class WeatherActivity extends BaseFragmentActivity implements
		OnClickListener {
	private TextView textViewLoc, textViewToday;
	private LinearLayout layoutSplash;
	private ImageView imgLoc, imgHome, imgAbout;
	private ViewPager vPager;

	private int curIdx;
	private static List<City> cityList = new ArrayList<City>();
	public static int getCityCount(){
		return cityList.size();
	}
	private List<ImageView> imgSplashList = new ArrayList<ImageView>();
	private WeatherFragmentAdapter weatherFragmentAdapter;

	private long exitTime;

	/*public static final String REFRESH_BROADCAST_ACTION = "action.refreshViewPager";
	private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(WeatherActivity.REFRESH_BROADCAST_ACTION)) {
				int delIdx = intent.getIntExtra("delIdx", -1);
				// if (delIdx != -1) {
				// if (curIdx >= delIdx){
				// curIdx = delIdx-1;
				// vPager.setCurrentItem(curIdx);
				// }
				// cityList.remove(delIdx);
				// imgSplashList.remove(delIdx);
				// layoutSplash.removeViewAt(delIdx);
				// refreshSplash();
				// weatherFragmentAdapter.removeItem(delIdx);
				// weatherFragmentAdapter.notifyDataSetChanged();
				// vPager.setCurrentItem(curIdx);
				// }
				if (delIdx != -1) {
					cityList.remove(delIdx);
					imgSplashList.remove(delIdx);					
					if (curIdx >= delIdx) {
						if(curIdx==0&&delIdx==0)
							curIdx=0;
						else
							curIdx = delIdx - 1;
					}
					layoutSplash.removeViewAt(delIdx);
					refreshViewPager();
					refreshSplash();
				}
			}
		}
	};*/

	public static void startActivity(Activity activity, City city) {
		Intent intent = new Intent(activity, WeatherActivity.class);
		intent.putExtra("city", city);
		activity.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		
		//友盟自动更新
		UmengUpdateAgent.update(this);
		AnalyticsConfig.enableEncrypt(true);//umeng统计数据传输加密
		
		this.curIdx=CacheUtil.loadCache(this);
		cityList = HaomeiDB.getInstance(this).loadSelCities();
		this.findViews();
		this.initLayoutSplash();
		this.initViewPager();
		this.refreshSplash();
		// 注册接收刷新viewPager的广播接收器，用于接收其它activity发出的刷新广播
//		IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(WeatherActivity.REFRESH_BROADCAST_ACTION);
//		LocalBroadcastManager.getInstance(this).registerReceiver(
//				this.mRefreshBroadcastReceiver, intentFilter);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		this.setIntent(intent);
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Intent intent = this.getIntent();
		City city = intent.getParcelableExtra("city");
		if (city != null) {
			int idx = findCity(city);
			if (idx != -1) {
				this.curIdx = idx;
				this.vPager.setCurrentItem(idx);
			} else {
				HaomeiDB.getInstance(this).addSelCity(city);
				cityList.add(city);
				ImageView imageView = new ImageView(this);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						9, 9);
				params.setMargins(15, 0, 0, 0);
				imageView.setLayoutParams(params);
				imageView.setScaleType(ScaleType.CENTER_INSIDE);
				imageView.setBackgroundResource(R.drawable.splash_indicator_focused);
				this.imgSplashList.add(imageView);
				this.layoutSplash.addView(imageView);
				this.weatherFragmentAdapter.addItem(WeatherFragment
						.newInstance(city));
				this.curIdx = cityList.size() - 1;
				this.weatherFragmentAdapter.notifyDataSetChanged();
				this.vPager.setCurrentItem(this.curIdx);
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		CacheUtil.addCache(this, this.curIdx);
//		LocalBroadcastManager.getInstance(this).unregisterReceiver(
//				this.mRefreshBroadcastReceiver);
		HaomeiDB.getInstance(this).close();
	}

	@Override
	public void onBackPressed() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
			return;
		}
		this.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
//		case ActivityRequestCode.CITY_ACTIVITY:
//			if (resultCode == RESULT_OK) {
//				City city = data.getParcelableExtra("city");
//				int idx = this.findCity(city);
//				if (idx != -1) {
//					this.curIdx = idx;
//					this.vPager.setCurrentItem(idx);
//				} else {
//					HaomeiDB.getInstance(this).addSelCity(city);
//					cityList.add(city);
//					ImageView imageView = new ImageView(this);
//					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//							9, 9);
//					params.setMargins(15, 0, 0, 0);
//					imageView.setLayoutParams(params);
//					imageView.setScaleType(ScaleType.CENTER_INSIDE);
//					imageView
//							.setBackgroundResource(R.drawable.splash_indicator);
//					this.imgSplashList.add(imageView);
//					this.layoutSplash.addView(imageView);
//					this.weatherFragmentAdapter.addItem(WeatherFragment
//							.newInstance(city));
//					this.curIdx = cityList.size() - 1;
//					this.weatherFragmentAdapter.notifyDataSetChanged();
//					this.vPager.setCurrentItem(this.cityList.size() - 1);
//				}
//			}
//			break;
		case ActivityRequestCode.SELECTED_CITY_ACTIVITY:
			if (resultCode == RESULT_OK) {
				boolean hasChanged=data.getBooleanExtra("hasChanged", false);
				if(hasChanged){
					this.vPager.removeAllViews();
					this.layoutSplash.removeAllViews();
					this.imgSplashList.clear();
					cityList = HaomeiDB.getInstance(this).loadSelCities();
					if(curIdx>=cityList.size())
						curIdx=cityList.size()-1;					
					this.refreshViewPager();					
					this.initLayoutSplash();
					this.refreshSplash();
				}
			}
			break;
		default:
			break;
		}
	}

	public static int findCity(City city) {
		int idx = -1;
		for (int i = 0; i < cityList.size(); ++i) {
			if (cityList.get(i).getAreaId().equals(city.getAreaId())) {
				return i;
			}
		}
		return idx;
	}

	private void findViews() {
		this.imgLoc = (ImageView) this.findViewById(R.id.imageViewLoc);
		this.textViewLoc = (TextView) this.findViewById(R.id.textViewLoc);
		this.textViewToday = (TextView) this.findViewById(R.id.textViewToday);
		this.layoutSplash = (LinearLayout) this.findViewById(R.id.layoutSplash);
		this.imgHome = (ImageView) this.findViewById(R.id.imageViewHome);
		this.imgHome.setOnClickListener(this);
		this.imgAbout = (ImageView) this.findViewById(R.id.imageViewAbout);
		this.imgAbout.setOnClickListener(this);
	}

	private void initLayoutSplash() {
		for (int i = 0; i < cityList.size(); ++i) {
			ImageView imageView = new ImageView(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(9,
					9);
			if (i > 0)
				params.setMargins(15, 0, 0, 0);
			imageView.setLayoutParams(params);
			imageView.setScaleType(ScaleType.CENTER_INSIDE);
			this.imgSplashList.add(imageView);
			if (i == 0) { // 默认选中第一张图片
				imageView
						.setBackgroundResource(R.drawable.splash_indicator_focused);
			} else {
				imageView.setBackgroundResource(R.drawable.splash_indicator);
			}
			this.layoutSplash.addView(imageView);
		}
	}

	private void initViewPager() {
		this.vPager = (ViewPager) findViewById(R.id.vPager);
		List<Fragment> fragments = new ArrayList<Fragment>();
		for (int i = 0; i < cityList.size(); ++i) {
			fragments.add(WeatherFragment.newInstance(cityList.get(i)));
		}
		this.weatherFragmentAdapter = new WeatherFragmentAdapter(
				this.getSupportFragmentManager(), fragments);
		this.vPager.setAdapter(this.weatherFragmentAdapter);
		this.vPager.setCurrentItem(this.curIdx);
		this.vPager.setOnPageChangeListener(new MyOnPageChangeListener());
		this.textViewLoc.setText(cityList.get(curIdx).getNameCn());
	}

	private void refreshViewPager() {
		List<Fragment> fragments = new ArrayList<Fragment>();
		for (int i = 0; i < cityList.size(); ++i) {
			fragments.add(WeatherFragment.newInstance(cityList.get(i)));
		}
		this.weatherFragmentAdapter = new WeatherFragmentAdapter(
				this.getSupportFragmentManager(), fragments);
		this.vPager.setAdapter(this.weatherFragmentAdapter);
		this.vPager.setCurrentItem(curIdx);
	}

	private void refreshSplash() {
		for (int i = 0; i < imgSplashList.size(); ++i) {
			if (i == curIdx) { // 选中
				imgSplashList.get(i).setBackgroundResource(
						R.drawable.splash_indicator_focused);
			} else {
				imgSplashList.get(i).setBackgroundResource(
						R.drawable.splash_indicator);
			}
		}
		if (((WeatherFragment) this.weatherFragmentAdapter.getItem(this.curIdx))
				.isLocate()) {
			imgLoc.setVisibility(View.VISIBLE);
		} else {
			imgLoc.setVisibility(View.GONE);
		}
	}

	public void initData(int diff) {
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, diff);
		date = c.getTime();
		this.textViewToday.setText(DateUtil.getDate(date) + " "
				+ DateUtil.getDayOfWeek(date));
	}

	public void setTextViewLocation(final String str) {
		for (int i = 0; i < cityList.size(); ++i) {
			if (cityList.get(i).getAreaId().equals(HaomeiDB.LOCATE)) {
				cityList.get(i).setNameCn(str);
				if(this.curIdx!=i)
					return;
				break;
			}
		}		
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				textViewLoc.setText(str);
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageViewHome:
			SelectedCityActivity.startActivity(this);
			break;
		case R.id.imageViewAbout:
//			try {
//				((WeatherFragment) this.weatherFragmentAdapter.getItem(this.curIdx))
//				.refresh();
//			} catch (Exception e) {
//				LogUtil.e(WeatherActivity.class.getSimpleName(),e.getMessage());
//				Toast.makeText(this, "刷新出错", Toast.LENGTH_SHORT).show();				
//			}
			AboutActivity.startActivity(this);
			break;

		default:
			break;
		}
	}

	private class MyOnPageChangeListener implements OnPageChangeListener {
		// private int one = offset *2 +bmpW;//两个相邻页面的偏移量

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			// Animation animation = new
			// TranslateAnimation(currIndex*one,arg0*one,0,0);//平移动画
			// animation.setFillAfter(true);//动画终止时停留在最后一帧，不然会回到没有执行前的状态
			// animation.setDuration(200);//动画持续时间0.2秒
			// image.startAnimation(animation);//是用ImageView来显示动画的
			curIdx = arg0;
			refreshSplash();
			textViewLoc.setText(cityList.get(curIdx).getNameCn());
			initData(0);
		}
	}
}
