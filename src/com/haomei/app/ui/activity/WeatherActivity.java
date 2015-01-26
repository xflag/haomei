package com.haomei.app.ui.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haomei.app.R;
import com.haomei.app.adapter.WeatherFragmentAdapter;
import com.haomei.app.base.BaseFragmentActivity;
import com.haomei.app.bean.City;
import com.haomei.app.db.HaomeiDB;
import com.haomei.app.ui.fragment.WeatherFragment;
import com.haomei.app.util.ActivityRequestCode;
import com.haomei.app.util.DateUtil;

public class WeatherActivity extends BaseFragmentActivity implements
		OnClickListener {
	private TextView textViewLoc, textViewToday;
	private LinearLayout layoutSplash;
	private ImageView imgLoc, imgHome, imgRefresh;
	private ViewPager vPager;	

	private int curIdx;
	private List<City> cityList = new ArrayList<City>();
	private List<ImageView> imgSplashList=new ArrayList<ImageView>();
	private WeatherFragmentAdapter weatherFragmentAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);

		this.cityList = HaomeiDB.getInstance(this).loadSelCities();		
		this.findViews();
		this.initLayoutSplash();
		this.initViewPager();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		HaomeiDB.getInstance(this).close();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case ActivityRequestCode.CITY_ACTIVITY:
			if (resultCode == RESULT_OK) {
				City city = data.getParcelableExtra("city");
				int idx = this.findCity(city);
				if (idx != -1) {
					this.curIdx = idx;
					this.vPager.setCurrentItem(idx);
				} else {
					HaomeiDB.getInstance(this).addSelCity(city);
					this.cityList.add(city);
					ImageView imageView = new ImageView(this);  
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(9, 9);					
					params.setMargins(15, 0, 0, 0); 
		            imageView.setLayoutParams(params);
		            imageView.setScaleType(ScaleType.CENTER_INSIDE);
		            imageView.setBackgroundResource(R.drawable.splash_indicator);  
		            this.imgSplashList.add(imageView);  
		            this.layoutSplash.addView(imageView);
					this.weatherFragmentAdapter.addItem(WeatherFragment
							.newInstance(city));
					this.curIdx = this.cityList.size() - 1;
					this.weatherFragmentAdapter.notifyDataSetChanged();
					this.vPager.setCurrentItem(this.cityList.size() - 1);
				}
			}
			break;

		default:
			break;
		}
	}

	private int findCity(City city) {
		int idx = -1;
		for (int i = 0; i < this.cityList.size(); ++i) {
			if (this.cityList.get(i).getAreaId().equals(city.getAreaId())) {
				return i;
			}
		}
		return idx;
	}

	private void findViews() {
		this.imgLoc = (ImageView) this.findViewById(R.id.imageViewLoc);
		this.textViewLoc = (TextView) this.findViewById(R.id.textViewLoc);
		this.textViewToday = (TextView) this.findViewById(R.id.textViewToday);
		this.layoutSplash=(LinearLayout)this.findViewById(R.id.layoutSplash);
		this.imgHome = (ImageView) this.findViewById(R.id.imageViewHome);
		this.imgHome.setOnClickListener(this);
		this.imgRefresh = (ImageView) this.findViewById(R.id.imageViewRefresh);
		this.imgRefresh.setOnClickListener(this);
	}
	
	private void initLayoutSplash(){
		for (int i = 0; i < this.cityList.size();++i){
			ImageView imageView = new ImageView(this);  
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(9, 9);
			if(i>0)
				params.setMargins(15, 0, 0, 0); 
            imageView.setLayoutParams(params);
            imageView.setScaleType(ScaleType.CENTER_INSIDE);
            this.imgSplashList.add(imageView);  
            if (i == 0) {  // 默认选中第一张图片                  
                imageView.setBackgroundResource(R.drawable.splash_indicator_focused);  
            } else {  
            	imageView.setBackgroundResource(R.drawable.splash_indicator);  
            }    
            this.layoutSplash.addView(imageView);
		}
	}

	private void initViewPager() {
		this.vPager = (ViewPager) findViewById(R.id.vPager);
		List<Fragment> fragments = new ArrayList<Fragment>();
		for (int i = 0; i < this.cityList.size(); ++i) {
			fragments.add(WeatherFragment.newInstance(this.cityList.get(i)));
		}
		this.weatherFragmentAdapter = new WeatherFragmentAdapter(
				this.getSupportFragmentManager(), fragments);
		this.vPager.setAdapter(this.weatherFragmentAdapter);
		this.vPager.setCurrentItem(0);
		this.vPager.setOnPageChangeListener(new MyOnPageChangeListener());
		this.textViewLoc.setText(cityList.get(curIdx).getNameCn());
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
		this.cityList.get(0).setNameCn(str);
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
			CityActivity.startActivity(this);
			break;
		case R.id.imageViewRefresh:
			((WeatherFragment) this.weatherFragmentAdapter.getItem(this.curIdx))
					.refresh();
			break;

		default:
			break;
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
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
			for (int i = 0; i < imgSplashList.size();++i){				
	            if (i == curIdx) {  // 选中               
	                imgSplashList.get(i).setBackgroundResource(R.drawable.splash_indicator_focused);  
	            } else {  
	            	imgSplashList.get(i).setBackgroundResource(R.drawable.splash_indicator);  
	            }    
			}
			if (arg0 == 0) {
				imgLoc.setVisibility(View.VISIBLE);
			} else {
				imgLoc.setVisibility(View.GONE);
			}
			textViewLoc.setText(cityList.get(curIdx).getNameCn());
			initData(0);
		}
	}
}
