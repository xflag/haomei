package com.haomei.app.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haomei.app.R;
import com.haomei.app.adapter.WeatherAdapter;
import com.haomei.app.base.BaseActivity;
import com.haomei.app.util.DateUtil;
import com.haomei.app.util.HttpCallbackListener;
import com.haomei.app.util.HttpUtil;
import com.haomei.app.util.WeatherUtil;

/**
 * 
 * @author XuYongqi
 *
 */
public class Weather2Activity extends BaseActivity {
	private TextView textViewLoc,textViewToday;
	private PullToRefreshListView pullToRefreshListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather2);
		this.findViews();
		this.loadWeather();
		this.initData();
	}
	
	private void findViews(){
		this.textViewLoc=(TextView)this.findViewById(R.id.textViewLoc);
		this.textViewToday=(TextView)this.findViewById(R.id.textViewToday);
		this.pullToRefreshListView=(PullToRefreshListView)this.findViewById(R.id.pull_to_refresh_listview);
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
		    @Override
		    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		        // Do work to refresh the list here.
		        loadWeather();	        
		    }
		});
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
							WeatherAdapter weatherAdapter=new WeatherAdapter(Weather2Activity.this, R.layout.weather_item, list);
							pullToRefreshListView.setAdapter(weatherAdapter);
							pullToRefreshListView.onRefreshComplete();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(Weather2Activity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
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
}
