package com.haomei.app.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.GridView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.haomei.app.R;
import com.haomei.app.adapter.ForecastGridAdapter;
import com.haomei.app.base.BaseActivity;
import com.haomei.app.bean.ForecastItem;
import com.haomei.app.util.DateUtil;
import com.haomei.app.util.HttpCallbackListener;
import com.haomei.app.util.HttpUtil;
import com.haomei.app.util.WeatherPhenomenon;
import com.haomei.app.util.WeatherUtil;
import com.haomei.app.util.WindDirection;
import com.haomei.app.util.WindScale;

public class W2Activity extends BaseActivity {
	private TextView textViewLoc,textViewToday;
	private TextView textViewRelease,textViewSunraise,textViewSunset,textViewPhenomenon,textViewTemperature,textViewWindDirection,textViewWindScale;
	private GridView forecastGridView;
	private PullToRefreshScrollView mPullRefreshScrollView;
	private ScrollView mScrollView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_w2);
		this.findViews();
		this.loadWeather();
		this.initData();
	}
	
	private void findViews(){
		this.textViewLoc=(TextView)this.findViewById(R.id.textViewLoc);
		this.textViewToday=(TextView)this.findViewById(R.id.textViewToday);
		this.textViewRelease=(TextView)this.findViewById(R.id.textViewRelease);
		this.textViewSunraise=(TextView)this.findViewById(R.id.textViewSunraise);
		this.textViewSunset=(TextView)this.findViewById(R.id.textViewSunset);
		this.textViewPhenomenon=(TextView)this.findViewById(R.id.textViewPhenomenon);
		this.textViewTemperature=(TextView)this.findViewById(R.id.textViewTemperature);
		this.textViewWindDirection=(TextView)this.findViewById(R.id.textViewWindDirection);
		this.textViewWindScale=(TextView)this.findViewById(R.id.textViewWindScale);
		this.forecastGridView=(GridView)this.findViewById(R.id.gridViewForecast);
		mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				loadWeather();				
			}
		});
		mScrollView = mPullRefreshScrollView.getRefreshableView();
	}
	
	private void initData(){		
		Date date=new Date();
		this.textViewToday.setText(DateUtil.getDate(date)+" "+DateUtil.getDayOfWeek(date));
	}	
	
	private void loadWeather(){
		HttpUtil.executeGet(WeatherUtil.getUrl("101230201",WeatherUtil.FORECAST_F), new HttpCallbackListener() {
			
			@Override
			public void onFinish(final String response) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							JSONObject jsonObject=new JSONObject(response);							
							JSONObject cJsonObject=jsonObject.getJSONObject("c");
							textViewLoc.setText(cJsonObject.getString("c3"));		
							List<JSONObject> list=new ArrayList<JSONObject>();
							list.add(jsonObject);							
							JSONObject fJsonObject=jsonObject.getJSONObject("f");
							textViewRelease.setText(DateUtil.getTimeDiff(fJsonObject.getString("f0"),"yyyyMMddHHmm")+"发布");
							JSONArray f1JsonArray=fJsonObject.getJSONArray("f1");
							String pString="fa",tString="fc",wdString="fe",wsString="fg";
							if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)>18) {
								pString="fb";
								tString="fd";
								wdString="ff";
								wsString="fh";
							}
							JSONObject day1=f1JsonArray.getJSONObject(0);
							textViewPhenomenon.setText(WeatherPhenomenon.getInstance().getPhenomenon(day1.getString(pString)));
							textViewTemperature.setText(day1.getString(tString)+WeatherUtil.DEGREE);
							textViewWindDirection.setText(WindDirection.getInstance().getDirection(day1.getString(wdString)));
							textViewWindScale.setText(WindScale.getInstance().getScale(day1.getString(wsString)));
							String[] suns=day1.getString("fi").split("\\|");
							textViewSunraise.setText(suns[0]+"日出");
							textViewSunset.setText(suns[1]+"日落");
							loadForecastGrid(f1JsonArray);
							mPullRefreshScrollView.onRefreshComplete();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(W2Activity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
						}						
					}
				});
			}
			
			@Override
			public void onError(Exception exception) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void loadForecastGrid(JSONArray f1JsonArray) throws JSONException{
		List<ForecastItem> list=new ArrayList<ForecastItem>();
		for (int i = 0; i < f1JsonArray.length(); ++i) {
			ForecastItem item=new ForecastItem();
			if (i==0) 
				item.setDay("今天");
			else if(i==1)
				item.setDay("明天");
			else if(i==2)
				item.setDay("后天");
			JSONObject object=f1JsonArray.getJSONObject(i);
			String fa=object.getString("fa");
			String fb=object.getString("fb");
			if(fa.equals(fb))
				fa=WeatherPhenomenon.getInstance().getPhenomenon(fa);
			else if (TextUtils.isEmpty(fa)) //晚上时，不显示白天天气
				fa=WeatherPhenomenon.getInstance().getPhenomenon(fb);
			else 
				fa=WeatherPhenomenon.getInstance().getPhenomenon(fa)+"转"+WeatherPhenomenon.getInstance().getPhenomenon(fb);
			item.setWeatherPhenomenon(fa);
			String fc=object.getString("fc");
			String fd=object.getString("fd");
			if(TextUtils.isEmpty(fc))
				item.setTemeprature(fd+WeatherUtil.DEGREE);
			else
				item.setTemeprature(fc+"/"+fd+WeatherUtil.DEGREE);
			list.add(item);
		}
		ForecastGridAdapter forecastGridAdapter=new ForecastGridAdapter(this, R.layout.forecast_item, list);
		forecastGridView.setAdapter(forecastGridAdapter);
	}
}
