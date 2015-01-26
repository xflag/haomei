package com.haomei.app.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.haomei.app.R;
import com.haomei.app.adapter.ForecastGridAdapter;
import com.haomei.app.base.BaseActivity;
import com.haomei.app.bean.City;
import com.haomei.app.bean.ForecastItem;
import com.haomei.app.db.HaomeiDB;
import com.haomei.app.util.ActivityRequestCode;
import com.haomei.app.util.DateUtil;
import com.haomei.app.util.HttpCallbackListener;
import com.haomei.app.util.HttpUtil;
import com.haomei.app.util.LogUtil;
import com.haomei.app.util.WeatherPhenomenon;
import com.haomei.app.util.WeatherUtil;
import com.haomei.app.util.WindDirection;
import com.haomei.app.util.WindScale;

public class WeatherActivity extends BaseActivity implements OnClickListener,OnItemClickListener{
	private TextView textViewLoc,textViewToday;
	private TextView textViewRelease,textViewSunraise,textViewSunset,textViewPhenomenon,textViewTemperature,textViewWindDirection,textViewWindScale;
	private GridView forecastGridView;	
	private ImageView imgHome,imgRefresh;
	
	private City curCity;
	private JSONObject jsonObject;
	
//	private static final String BAIDU_MAP_URL="http://api.map.baidu.com/geocoder/v2/?ak=UosvSDYpTOIaaMpDZ42G4RYW&location=%s&output=json&mcode=5F:03:11:B5:EA:1D:A4:61:30:11:FD:7C:B1:4B:DE:26:DB:B3:23:25;com.haomei.app";
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new HaomeiLocationListener();	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);	
		this.findViews();
		this.loadCache();		
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
			if(resultCode==RESULT_OK)
			{
				Bundle bundle = data.getExtras(); 
				String areaId=bundle.getString("area_id");
				this.curCity.setAreaId(areaId);
				if(this.curCity.getAreaId().equals("locate"))
					this.locateBaidu();
				else
					this.loadWeather(areaId);
			}
			break;

		default:
			break;
		}
	}
	
	private void loadCache(){
		SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
		String areaId=sharedPreferences.getString("areaId", "");
		this.curCity=new City();
		if(TextUtils.isEmpty(areaId)){
//			this.curCity.setAreaId("101010100");
			this.locateBaidu();
		}			
		else {
			this.curCity.setAreaId(areaId);
			this.loadWeather(this.curCity.getAreaId());			
		}
		this.initData(0);
	}
	
	private void locateBaidu(){
		this.mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
	    this.mLocationClient.registerLocationListener( myListener );    //注册监听函数
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		option.setProdName("com.haomei.app");
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(3000);//设置发起定位请求的间隔时间为3000ms
		option.setIsNeedAddress(true);//返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
		this.mLocationClient.setLocOption(option);
		this.mLocationClient.start();
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.requestLocation();
		else 
			LogUtil.d(WeatherActivity.class.getSimpleName(), "baiduLocClient is null or not started");
	}	
	
	
//	private void locateWeather(){
//		String baiduUrl=String.format(BAIDU_MAP_URL, "39.983424,116.322987");
//		HttpUtil.executeGet(baiduUrl, new HttpCallbackListener() {
//			
//			@Override
//			public void onFinish(String response) {
//				// TODO Auto-generated method stub
//				try {						
//					JSONObject jsonObject=new JSONObject(response);							
//					JSONObject addrJsonObject=jsonObject.getJSONObject("result").getJSONObject("addressComponent");						
//					String district=addrJsonObject.getString("district");
//					City city=HaomeiDB.getInstance(WeatherActivity.this).getCityByName(district.substring(0,district.length()-1));
//					String areaId=city.getAreaId();
//					if (!TextUtils.isEmpty(areaId)) {
//						curCity.setAreaId(areaId);
//					}
//					else {
//						String cityStr=addrJsonObject.getString("city");
//						city=HaomeiDB.getInstance(WeatherActivity.this).getCityByName(cityStr.substring(0,cityStr.length()-1));
//						areaId=city.getAreaId();
//						if (!TextUtils.isEmpty(areaId)) {
//							curCity.setAreaId(areaId);
//						}
//						else {
//							Toast.makeText(WeatherActivity.this, "无法找到目前位置编码", Toast.LENGTH_SHORT).show();
//						}
//					}
//					loadWeather(curCity.getAreaId());
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block						
//					Toast.makeText(WeatherActivity.this, "解析位置失败", Toast.LENGTH_SHORT).show();
//					LogUtil.e(WeatherActivity.class.getSimpleName(), e.getMessage());
//				}			
//			}
//			
//			@Override
//			public void onError(Exception exception) {
//				// TODO Auto-generated method stub
//				Toast.makeText(WeatherActivity.this, "获取位置失败", Toast.LENGTH_SHORT).show();
//				LogUtil.e(WeatherActivity.class.getSimpleName(), exception.getMessage());
//			}
//		});
//	}	
	
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
		this.forecastGridView.setOnItemClickListener(this);
		this.imgHome=(ImageView)this.findViewById(R.id.imageViewHome);
		this.imgHome.setOnClickListener(this);
		this.imgRefresh=(ImageView)this.findViewById(R.id.imageViewRefresh);
		this.imgRefresh.setOnClickListener(this);
	}
	
	private void initData(int diff){		
		Date date=new Date();
		Calendar c = Calendar.getInstance();  
		c.add(Calendar.DATE, diff);
		date=c.getTime();
		this.textViewToday.setText(DateUtil.getDate(date)+" "+DateUtil.getDayOfWeek(date));
	}	
	
	private void loadWeather(final String areaId){
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				textViewRelease.setText("同步中...");				
			}
		});
		
		HttpUtil.executeGet(WeatherUtil.getUrl(areaId,WeatherUtil.FORECAST_V), new HttpCallbackListener() {
			
			@Override
			public void onFinish(final String response) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
//							LogUtil.i(WeatherActivity.class.getSimpleName(), response);
							SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
							SharedPreferences.Editor editor=sharedPreferences.edit();
							editor.putString("areaId", areaId);
							editor.commit();
							jsonObject=new JSONObject(response);							
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
						} catch (JSONException e) {
							// TODO Auto-generated catch block							
							Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
							LogUtil.e(WeatherActivity.class.getSimpleName(), e.getMessage());
						}						
					}
				});
			}
			
			@Override
			public void onError(Exception exception) {
				// TODO Auto-generated method stub
				textViewRelease.setText("获取天气失败");
				Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
				LogUtil.e(WeatherActivity.class.getSimpleName(), exception.getMessage());
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
			int faInt;
			String faString;
			if(fa.equals(fb)){
				faInt=Integer.parseInt(fa);
				faString=WeatherPhenomenon.getInstance().getPhenomenon(fa);
			}
			else if (TextUtils.isEmpty(fa)){ //晚上时，不显示白天天气
				faInt=Integer.parseInt(fb)+36;
				faString=WeatherPhenomenon.getInstance().getPhenomenon(fb);
			}
			else {
				faInt=Integer.parseInt(fa);
				faString=WeatherPhenomenon.getInstance().getPhenomenon(fa)+"转"+WeatherPhenomenon.getInstance().getPhenomenon(fb);
			}
			if(faInt==53||faInt==(53+36))
				faInt-=19;//53-34=19,霾
			else if(faInt==99||faInt==(99+36))
				faInt-=64;//99-35=64，无
			++faInt;//取下来的编码与res里图片的编码差1
			item.setImgPhenomenon(faInt);
			item.setWeatherPhenomenon(faString);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageViewHome:
			CityActivity.startActivity(this);
			break;
		case R.id.imageViewRefresh:
			this.loadWeather(this.curCity.getAreaId());
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub		
		if(arg0.getId()==R.id.gridViewForecast)
		{
			try {
				this.initData(arg2);
				JSONObject fJsonObject = jsonObject.getJSONObject("f");
				JSONArray f1JsonArray=fJsonObject.getJSONArray("f1");
				String pString="fa",tString="fc",wdString="fe",wsString="fg";			
				JSONObject day1=f1JsonArray.getJSONObject(arg2);
				textViewPhenomenon.setText(WeatherPhenomenon.getInstance().getPhenomenon(day1.getString(pString)));
				textViewTemperature.setText(day1.getString(tString)+WeatherUtil.DEGREE);
				textViewWindDirection.setText(WindDirection.getInstance().getDirection(day1.getString(wdString)));
				textViewWindScale.setText(WindScale.getInstance().getScale(day1.getString(wsString)));
				String[] suns=day1.getString("fi").split("\\|");
				textViewSunraise.setText(suns[0]+"日出");
				textViewSunset.setText(suns[1]+"日落");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Toast.makeText(WeatherActivity.this, "解析天气出错", Toast.LENGTH_SHORT).show();
				LogUtil.e(WeatherActivity.class.getSimpleName(), e.getMessage());
			}	
		}
	}
	
	public class HaomeiLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
		            return ;			
			String district=location.getDistrict();
			City city=HaomeiDB.getInstance(WeatherActivity.this).getCityByName(district.substring(0,district.length()-1));
			String areaId=city.getAreaId();
			if (!TextUtils.isEmpty(areaId)) {
				curCity.setAreaId(areaId);
			}
			else {
				String cityStr=location.getCity();
				city=HaomeiDB.getInstance(WeatherActivity.this).getCityByName(cityStr.substring(0,cityStr.length()-1));
				areaId=city.getAreaId();
				if (!TextUtils.isEmpty(areaId)) {
					curCity.setAreaId(areaId);
				}
				else {
					Toast.makeText(WeatherActivity.this, "无法找到目前位置编码", Toast.LENGTH_SHORT).show();
				}
			}
//			LogUtil.i(WeatherActivity.class.getSimpleName(), location.getLocType()+"  "+ BDLocation.TypeGpsLocation);
			loadWeather(curCity.getAreaId());
			mLocationClient.stop();
			mLocationClient.unRegisterLocationListener(myListener);
		}		
	}
}
