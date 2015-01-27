package com.haomei.app.ui.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.haomei.app.R;
import com.haomei.app.adapter.CityGridAdapter;
import com.haomei.app.base.BaseActivity;
import com.haomei.app.bean.City;
import com.haomei.app.db.HaomeiDB;

public class CityActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {

	private ImageView imgBack;
	private EditText editTextCity;
	private LinearLayout layoutFreq, layoutHot;
	private ListView listViewResult;
	private GridView gridViewFreqCity, gridViewHotCity;
	private ImageButton btnSearch;
	private int freqInt = View.VISIBLE;

	public static void startActivity(Activity activity) {
		Intent intent = new Intent(activity, CityActivity.class);
		activity.startActivity(intent);
	}

	private void findViews() {
		this.imgBack = (ImageView) this.findViewById(R.id.imageViewBack);
		this.imgBack.setOnClickListener(this);
		this.listViewResult = (ListView) this.findViewById(R.id.listViewResult);
		this.listViewResult.setOnItemClickListener(this);
		this.layoutFreq = (LinearLayout) this.findViewById(R.id.layoutFreq);
		this.layoutHot = (LinearLayout) this.findViewById(R.id.layoutHot);
		this.gridViewFreqCity = (GridView) this
				.findViewById(R.id.gridViewFreqCity);
		this.gridViewHotCity = (GridView) this
				.findViewById(R.id.gridViewHotCity);
		this.btnSearch = (ImageButton) this
				.findViewById(R.id.imageButtonSearch);
		this.btnSearch.setOnClickListener(this);
		this.editTextCity = (EditText) this.findViewById(R.id.editTextCity);
		this.editTextCity.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				searchCity(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});

	}

	private void initData() {
		List<City> freqCityList = HaomeiDB.getInstance(this).loadFreqCities();
		if (freqCityList.size() == 0) {
			freqInt = View.GONE;
			this.layoutFreq.setVisibility(freqInt);
		} else {
			CityGridAdapter freqCityListAdapter = new CityGridAdapter(this,
					R.layout.city_grid_item, freqCityList);
			this.gridViewFreqCity.setAdapter(freqCityListAdapter);
			this.gridViewFreqCity.setOnItemClickListener(this);
		}
		List<City> hotCityList = HaomeiDB.getInstance(this).loadHotCities();
		CityGridAdapter hotCityListAdapter = new CityGridAdapter(this,
				R.layout.city_grid_item, hotCityList);
		this.gridViewHotCity.setAdapter(hotCityListAdapter);
		this.gridViewHotCity.setOnItemClickListener(this);
	}

	private void searchCity(CharSequence s) {
		if (s.length() > 0) {
			List<City> list = HaomeiDB.getInstance(CityActivity.this)
					.filterCities(s.toString());
			CityGridAdapter listAdapter = new CityGridAdapter(
					CityActivity.this, R.layout.city_list_item, list);
			listViewResult.setAdapter(listAdapter);
			listViewResult.setVisibility(View.VISIBLE);
			layoutFreq.setVisibility(View.GONE);
			layoutHot.setVisibility(View.GONE);
			gridViewFreqCity.setVisibility(View.GONE);
			gridViewHotCity.setVisibility(View.GONE);
		} else {
			listViewResult.setVisibility(View.GONE);
			layoutFreq.setVisibility(freqInt);
			layoutHot.setVisibility(View.VISIBLE);
			gridViewFreqCity.setVisibility(freqInt);
			gridViewHotCity.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city);
		this.findViews();
		this.initData();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageViewBack:
			this.finish();
			break;
		case R.id.imageButtonSearch:
			searchCity(this.editTextCity.getText());
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		TextView textView = (TextView) arg1.findViewById(R.id.textViewCity);
		City city = (City) textView.getTag();
		String nameCnString = city.getNameCn();
		if (nameCnString.contains(","))// 当是搜索浮出的listview时，显示城市,省份格式(厦门,福建)，需要只提取出厦门传参
			city.setNameCn(nameCnString.split(",")[0]);
		// Intent intent = new Intent();
		// intent.putExtra("city", city);
		// this.setResult(RESULT_OK, intent);
		int fIdx = WeatherActivity.findCity(city);
		if (fIdx == -1 && WeatherActivity.getCityCount() > 7) {
			Toast.makeText(this, "最多添加8个城市哦~", Toast.LENGTH_SHORT).show();
			return;
		}
		if (city.getId() != 0)
			HaomeiDB.getInstance(this).updateCitySelTimes(city.getAreaId());
		// this.finish();
		WeatherActivity.startActivity(this, city);
	}
}