package com.haomei.app.ui.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.haomei.app.R;
import com.haomei.app.adapter.ForecastGridAdapter;
import com.haomei.app.adapter.IndexListAdapter;
import com.haomei.app.bean.City;
import com.haomei.app.bean.ForecastItem;
import com.haomei.app.bean.IndexItem;
import com.haomei.app.db.HaomeiDB;
import com.haomei.app.ui.activity.WeatherActivity;
import com.haomei.app.util.DateUtil;
import com.haomei.app.util.HttpCallbackListener;
import com.haomei.app.util.HttpUtil;
import com.haomei.app.util.LogUtil;
import com.haomei.app.util.UIUtil;
import com.haomei.app.util.UmengEvent;
import com.haomei.app.util.WeatherPhenomenon;
import com.haomei.app.util.WeatherUtil;
import com.haomei.app.util.WindDirection;
import com.haomei.app.util.WindScale;

/**
 * @author XuYongqi
 *
 */
public class WeatherFragment extends Fragment implements OnItemClickListener {
	private ImageView imgSunraise, imgSunset;
	private TextView textViewRelease, textViewSunraise, textViewSunset,
			textViewPhenomenon, textViewTemperature, textViewWindDirection,
			textViewWindScale;
	private GridView forecastGridView;
	private ListView indexListView;

	private City curCity;
	private JSONObject jsonObject;
	private boolean isLocate;

	private PullToRefreshScrollView mPullRefreshScrollView;

	public boolean isLocate() {
		return isLocate;
	}

	// private static final String
	// BAIDU_MAP_URL="http://api.map.baidu.com/geocoder/v2/?ak=UosvSDYpTOIaaMpDZ42G4RYW&location=%s&output=json&mcode=5F:03:11:B5:EA:1D:A4:61:30:11:FD:7C:B1:4B:DE:26:DB:B3:23:25;com.haomei.app";
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new HaomeiLocationListener();

	public static WeatherFragment newInstance(City city) {
		WeatherFragment newFragment = new WeatherFragment();
		newFragment.curCity = city;
		if (city.getAreaId().equals(HaomeiDB.LOCATE))
			newFragment.isLocate = true;
		return newFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.weather_fragment, container,
				false);
		this.findViews(view);
		((WeatherActivity) this.getActivity()).initData(0);
		try {
			if (this.curCity.getAreaId().equals(HaomeiDB.LOCATE)) {
				// this.curCity.setAreaId("101010100");
				this.isLocate = true;
				this.locateBaidu();
			} else {
				this.loadWeather();
			}
		} catch (Exception e) {
			Toast.makeText(getActivity(), "加载天气失败", Toast.LENGTH_SHORT).show();
			LogUtil.e(WeatherActivity.class.getSimpleName(), e.getMessage());
		}

		return view;
	}

	private void findViews(View view) {
		this.imgSunraise = (ImageView) view
				.findViewById(R.id.imageViewSunraise);
		this.imgSunset = (ImageView) view.findViewById(R.id.imageViewSunset);
		this.textViewRelease = (TextView) view
				.findViewById(R.id.textViewRelease);
		this.textViewSunraise = (TextView) view
				.findViewById(R.id.textViewSunraise);
		this.textViewSunset = (TextView) view.findViewById(R.id.textViewSunset);
		this.textViewPhenomenon = (TextView) view
				.findViewById(R.id.textViewPhenomenon);
		this.textViewTemperature = (TextView) view
				.findViewById(R.id.textViewTemperature);
		this.textViewWindDirection = (TextView) view
				.findViewById(R.id.textViewWindDirection);
		this.textViewWindScale = (TextView) view
				.findViewById(R.id.textViewWindScale);
		this.forecastGridView = (GridView) view
				.findViewById(R.id.gridViewForecast);
		this.forecastGridView.setOnItemClickListener(this);
		this.indexListView = (ListView) view.findViewById(R.id.listViewIndex);
		this.mPullRefreshScrollView = (PullToRefreshScrollView) view
				.findViewById(R.id.pull_refresh_scrollview);
		this.mPullRefreshScrollView
				.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						//友盟统计，刷新天气
						UmengEvent.invokeByCity(getActivity(), UmengEvent.REFRESH, curCity);
						new GetDataTask().execute();
					}
				});
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			refresh();
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			// Do some stuff here

			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshScrollView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}

	public void refresh() {
		try {
			if (this.isLocate) {
				this.locateBaidu();
			} else {
				this.loadWeather();
			}
		} catch (Exception e) {
			Toast.makeText(getActivity(), "刷新天气失败", Toast.LENGTH_SHORT).show();
			LogUtil.e(WeatherActivity.class.getSimpleName(), e.getMessage());
		}

	}

	private void locateBaidu() {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				textViewRelease.setText("定位中...");
				mLocationClient = new LocationClient(getActivity()
						.getApplicationContext()); // 声明LocationClient类
				mLocationClient.registerLocationListener(myListener); // 注册监听函数
				LocationClientOption option = new LocationClientOption();
				option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
				option.setProdName("com.haomei.app");
				option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
				option.setScanSpan(3000);// 设置发起定位请求的间隔时间为3000ms
				option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
				option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
				mLocationClient.setLocOption(option);
				mLocationClient.start();
				if (mLocationClient != null && mLocationClient.isStarted())
					mLocationClient.requestLocation();
				else
					LogUtil.d(WeatherActivity.class.getSimpleName(),
							"baiduLocClient is null or not started");
			}
		});

	}

	public void loadWeather() {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				textViewRelease.setText("同步中...");
			}
		});

		HttpUtil.executeGet(
				WeatherUtil.getUrl(curCity.getAreaId(), WeatherUtil.FORECAST_V),
				new HttpCallbackListener() {

					@Override
					public void onFinish(final String response) {
						// TODO Auto-generated method stub
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									// LogUtil.i(WeatherActivity.class.getSimpleName(),
									// response);
									// CacheUtil.addCache(getActivity(),curCity);
									jsonObject = new JSONObject(response);
									// JSONObject
									// cJsonObject=jsonObject.getJSONObject("c");
									// curCity.setNameCn(cJsonObject.getString("c3"));
									List<JSONObject> list = new ArrayList<JSONObject>();
									list.add(jsonObject);
									JSONObject fJsonObject = jsonObject
											.getJSONObject("f");
									textViewRelease.setText(DateUtil.getTimeDiff(
											fJsonObject.getString("f0"),
											"yyyyMMddHHmm")
											+ "发布");
									JSONArray f1JsonArray = fJsonObject
											.getJSONArray("f1");
									String pString = "fa", tString = "fc", wdString = "fe", wsString = "fg";
									if (Calendar.getInstance().get(
											Calendar.HOUR_OF_DAY) > 18) {
										pString = "fb";
										tString = "fd";
										wdString = "ff";
										wsString = "fh";
									}
									JSONObject day1 = f1JsonArray
											.getJSONObject(0);
									textViewPhenomenon.setText(WeatherPhenomenon
											.getInstance().getPhenomenon(
													day1.getString(pString)));
									textViewTemperature.setText(day1
											.getString(tString)
											+ WeatherUtil.DEGREE);
									textViewWindDirection.setText(WindDirection
											.getInstance().getDirection(
													day1.getString(wdString)));
									textViewWindScale.setText(WindScale
											.getInstance().getScale(
													day1.getString(wsString)));
									String[] suns = day1.getString("fi").split(
											"\\|");
									textViewSunraise.setText(suns[0] + "日出");
									textViewSunset.setText(suns[1] + "日落");
									if (imgSunraise.getVisibility() == View.GONE)
										imgSunraise.setVisibility(View.VISIBLE);
									if (imgSunset.getVisibility() == View.GONE)
										imgSunset.setVisibility(View.VISIBLE);
									loadForecastGrid(f1JsonArray);
									// if(isLocate){
									// curCity.setAreaId(HaomeiDB.LOCATE);
									// ((WeatherActivity)getActivity()).setLocateCity(curCity.getNameCn());
									// }
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									Toast.makeText(getActivity(), "获取天气失败",
											Toast.LENGTH_SHORT).show();
									LogUtil.e(WeatherActivity.class
											.getSimpleName(), e.getMessage());
								}
							}
						});
					}

					@Override
					public void onError(Exception exception) {
						// TODO Auto-generated method stub
						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								textViewRelease.setText("获取天气失败");
								Toast.makeText(getActivity(), "获取天气失败",
										Toast.LENGTH_SHORT).show();
							}
						});
						LogUtil.e(WeatherActivity.class.getSimpleName(),
								exception.getMessage());
					}
				});

		HttpUtil.executeGet(
				WeatherUtil.getUrl(curCity.getAreaId(), WeatherUtil.INDEX_V),
				new HttpCallbackListener() {

					@Override
					public void onFinish(final String response) {
						// TODO Auto-generated method stub
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									JSONObject indexJsonObject = new JSONObject(
											response);
									JSONArray iJsonArray = indexJsonObject
											.getJSONArray("i");
									loadIndexList(iJsonArray);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									Toast.makeText(getActivity(), "获取天气指数失败",
											Toast.LENGTH_SHORT).show();
									LogUtil.e(WeatherActivity.class
											.getSimpleName(), e.getMessage());
								}
							}
						});
					}

					@Override
					public void onError(Exception exception) {
						// TODO Auto-generated method stub
						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								textViewRelease.setText("获取天气指数失败");
								Toast.makeText(getActivity(), "获取天气指数失败",
										Toast.LENGTH_SHORT).show();
							}
						});
						LogUtil.e(WeatherActivity.class.getSimpleName(),
								exception.getMessage());
					}
				});
	}
	
	private String getDayByNumber(int i){
		if (i == 0)
			return "今天";
		else if (i == 1)
			return "明天";
		else if (i == 2)
			return "后天";
		return "";
	}

	private void loadForecastGrid(JSONArray f1JsonArray) throws JSONException {
		List<ForecastItem> list = new ArrayList<ForecastItem>();
		for (int i = 0; i < f1JsonArray.length(); ++i) {
			ForecastItem item = new ForecastItem();
			item.setDay(this.getDayByNumber(i));
			JSONObject object = f1JsonArray.getJSONObject(i);
			String fa = object.getString("fa");
			String fb = object.getString("fb");
			int faInt;
			String faString;
//			if (fa.equals(fb)) {
//				faInt = Integer.parseInt(fa);
//				faString = WeatherPhenomenon.getInstance().getPhenomenon(fa);
//			} else if (TextUtils.isEmpty(fa)) { // 晚上时，不显示白天天气
//				faInt = Integer.parseInt(fb) + 36;
//				faString = WeatherPhenomenon.getInstance().getPhenomenon(fb);
//			}
//			else {
//				faInt = Integer.parseInt(fa);
//				faString = WeatherPhenomenon.getInstance().getPhenomenon(fa)
//						+ "转"
//						+ WeatherPhenomenon.getInstance().getPhenomenon(fb);
//			}
			if (TextUtils.isEmpty(fa)) { // 晚上时，不显示白天天气
				faInt = Integer.parseInt(fb) + 36;
				faString = WeatherPhenomenon.getInstance().getPhenomenon(fb);
			}
			else {
				faInt = Integer.parseInt(fa);
				faString = WeatherPhenomenon.getInstance().getPhenomenon(fa);
			} 			
			if (faInt == 53 || faInt == (53 + 36))
				faInt -= 19;// 53-34=19,霾
			else if (faInt == 99 || faInt == (99 + 36))
				faInt -= 64;// 99-35=64，无
			++faInt;// 取下来的编码与res里图片的编码差1
			item.setImgPhenomenon(faInt);
			item.setWeatherPhenomenon(faString);
			String fc = object.getString("fc");
			String fd = object.getString("fd");
			if (TextUtils.isEmpty(fc))
				item.setTemeprature(fd + WeatherUtil.DEGREE);
			else
				item.setTemeprature(fc + "/" + fd + WeatherUtil.DEGREE);
			list.add(item);
		}
		ForecastGridAdapter forecastGridAdapter = new ForecastGridAdapter(
				getActivity(), R.layout.forecast_item, list);
		forecastGridView.setAdapter(forecastGridAdapter);
//		UIUtil.fixGridViewHeight(forecastGridView);
	}

	private void loadIndexList(JSONArray iJsonArray) throws JSONException {
		List<IndexItem> list = new ArrayList<IndexItem>();
		for (int i = 0; i < iJsonArray.length(); ++i) {
			IndexItem item = new IndexItem();
			item.setTypeStr(iJsonArray.getJSONObject(i).getString("i2"));
			item.setIndexStr(iJsonArray.getJSONObject(i).getString("i4"));
			item.setDescStr(iJsonArray.getJSONObject(i).getString("i5"));
			list.add(item);
		}
		IndexListAdapter listAdapter = new IndexListAdapter(getActivity(),
				R.layout.index_list_item, list);
		this.indexListView.setAdapter(listAdapter);
		UIUtil.fixListViewHeight(this.indexListView);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (arg0.getId() == R.id.gridViewForecast) {
			try {
				//友盟统计自定义事件，点击日期查看天气
				UmengEvent.invokeByDay(getActivity(), UmengEvent.CLICK_DAY, this.getDayByNumber(arg2));
				
				((WeatherActivity) this.getActivity()).initData(arg2);
				JSONObject fJsonObject = jsonObject.getJSONObject("f");
				JSONArray f1JsonArray = fJsonObject.getJSONArray("f1");
				String pString = "fa", tString = "fc", wdString = "fe", wsString = "fg";
				if (arg2 == 0
						&& Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 18) {
					pString = "fb";
					tString = "fd";
					wdString = "ff";
					wsString = "fh";
				}
				JSONObject day1 = f1JsonArray.getJSONObject(arg2);
				textViewPhenomenon.setText(WeatherPhenomenon.getInstance()
						.getPhenomenon(day1.getString(pString)));
				textViewTemperature.setText(day1.getString(tString)
						+ WeatherUtil.DEGREE);
				textViewWindDirection.setText(WindDirection.getInstance()
						.getDirection(day1.getString(wdString)));
				textViewWindScale.setText(WindScale.getInstance().getScale(
						day1.getString(wsString)));
				String[] suns = day1.getString("fi").split("\\|");
				textViewSunraise.setText(suns[0] + "日出");
				textViewSunset.setText(suns[1] + "日落");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Toast.makeText(getActivity(), "解析天气出错", Toast.LENGTH_SHORT)
						.show();
				LogUtil.e(WeatherActivity.class.getSimpleName(), e.getMessage());
			}
		}
	}

	public class HaomeiLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			String district = location.getDistrict();
			if (TextUtils.isEmpty(district))
				return;
			City city = HaomeiDB.getInstance(getActivity()).getCityByName(
					district.substring(0, district.length() - 1));
			String areaId = city.getAreaId();
			if (!TextUtils.isEmpty(areaId)) {
				curCity = city;
			} else {
				String cityStr = location.getCity();
				city = HaomeiDB.getInstance(getActivity()).getCityByName(
						cityStr.substring(0, cityStr.length() - 1));
				if (!TextUtils.isEmpty(city.getAreaId())) {
					curCity = city;
				} else {
					Toast.makeText(getActivity(), "无法找到目前位置编码",
							Toast.LENGTH_SHORT).show();
				}
			}
			// LogUtil.i(WeatherActivity.class.getSimpleName(),
			// location.getLocType()+"  "+ BDLocation.TypeGpsLocation);
			((WeatherActivity) getActivity()).setTextViewLocation(curCity
					.getNameCn());
			HaomeiDB.getInstance(getActivity()).updateSelCityLocate(
					curCity.getNameCn());
			loadWeather();
			mLocationClient.stop();
			mLocationClient.unRegisterLocationListener(myListener);
		}
	}

	// private void locateWeather(){
	// String baiduUrl=String.format(BAIDU_MAP_URL, "39.983424,116.322987");
	// HttpUtil.executeGet(baiduUrl, new HttpCallbackListener() {
	//
	// @Override
	// public void onFinish(String response) {
	// // TODO Auto-generated method stub
	// try {
	// JSONObject jsonObject=new JSONObject(response);
	// JSONObject
	// addrJsonObject=jsonObject.getJSONObject("result").getJSONObject("addressComponent");
	// String district=addrJsonObject.getString("district");
	// City
	// city=HaomeiDB.getInstance(WeatherActivity.this).getCityByName(district.substring(0,district.length()-1));
	// String areaId=city.getAreaId();
	// if (!TextUtils.isEmpty(areaId)) {
	// curCity.setAreaId(areaId);
	// }
	// else {
	// String cityStr=addrJsonObject.getString("city");
	// city=HaomeiDB.getInstance(WeatherActivity.this).getCityByName(cityStr.substring(0,cityStr.length()-1));
	// areaId=city.getAreaId();
	// if (!TextUtils.isEmpty(areaId)) {
	// curCity.setAreaId(areaId);
	// }
	// else {
	// Toast.makeText(WeatherActivity.this, "无法找到目前位置编码",
	// Toast.LENGTH_SHORT).show();
	// }
	// }
	// loadWeather(curCity.getAreaId());
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// Toast.makeText(WeatherActivity.this, "解析位置失败",
	// Toast.LENGTH_SHORT).show();
	// LogUtil.e(WeatherActivity.class.getSimpleName(), e.getMessage());
	// }
	// }
	//
	// @Override
	// public void onError(Exception exception) {
	// // TODO Auto-generated method stub
	// Toast.makeText(WeatherActivity.this, "获取位置失败",
	// Toast.LENGTH_SHORT).show();
	// LogUtil.e(WeatherActivity.class.getSimpleName(), exception.getMessage());
	// }
	// });
	// }
}
