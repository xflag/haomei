package com.haomei.app.ui.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.haomei.app.R;
import com.haomei.app.adapter.SelectedCityListAdapter;
import com.haomei.app.base.BaseActivity;
import com.haomei.app.bean.City;
import com.haomei.app.db.HaomeiDB;
import com.haomei.app.util.ActivityRequestCode;
import com.haomei.app.util.UmengEvent;

/**
 * 
 * @author XuYongqi
 *
 */
public class SelectedCityActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener, OnItemLongClickListener {
	private ImageView imageViewBack, imageViewAdd;
	private ListView listViewSelCity;

	private List<City> list;
	private SelectedCityListAdapter cityListAdapter;

	private boolean hasChanged;

	public static void startActivity(Activity activity) {
		Intent intent = new Intent(activity, SelectedCityActivity.class);
		activity.startActivityForResult(intent,
				ActivityRequestCode.SELECTED_CITY_ACTIVITY);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selected_city);

		this.findView();
	}

	private void findView() {
		this.imageViewBack = (ImageView) this.findViewById(R.id.imageViewBack);
		this.imageViewBack.setOnClickListener(this);
		this.imageViewAdd = (ImageView) this.findViewById(R.id.imageViewAdd);
		this.imageViewAdd.setOnClickListener(this);
		this.listViewSelCity = (ListView) this
				.findViewById(R.id.listViewSelCity);
		this.list = HaomeiDB.getInstance(this).loadSelCities();
		this.cityListAdapter = new SelectedCityListAdapter(this,
				R.layout.selected_city_list_item, list);
		this.listViewSelCity.setAdapter(cityListAdapter);
		this.listViewSelCity.setOnItemClickListener(this);
		this.listViewSelCity.setOnItemLongClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageViewBack:
			Intent intent = new Intent();
			intent.putExtra("hasChanged", this.hasChanged);
			this.setResult(RESULT_OK, intent);
			this.finish();
			break;

		case R.id.imageViewAdd:
			CityActivity.startActivity(this);
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
		// String nameCnString=city.getNameCn();
		// if(nameCnString.contains(","))//当是搜索浮出的listview时，显示城市,省份格式(厦门,福建)，需要只提取出厦门传参
		// city.setNameCn(nameCnString.split(",")[0]);
		// Intent intent = new Intent();
		// intent.putExtra("city", city);
		// this.setResult(RESULT_OK, intent);

		// 友盟统计自定义事件，通过已选城市查看天气
		UmengEvent.invokeByCity(this, UmengEvent.VIEW_SEL_CITY, city);

		if (city.getId() != 0)
			HaomeiDB.getInstance(this).updateCitySelTimes(city.getAreaId());
		// this.finish();
		WeatherActivity.startActivity(this, city);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			final int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (list.size() == 1) {// 只剩一个城市，不允许再删除了
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("亲,别删我了,只剩一个城市了");
			builder.setTitle("呜呜");
			builder.setPositiveButton("好吧",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.create().show();
		} else {// 多于一个城市，允许删除
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("真的要删除这个城市吗?");
			builder.setTitle("提示");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							hasChanged = true;
							// 删除sqlite里的记录
							HaomeiDB.getInstance(SelectedCityActivity.this)
									.deleteSelCity(list.get(arg2).getAreaId());
							// 发出本地广播
							// Intent intent = new Intent();
							// intent.setAction(WeatherActivity.REFRESH_BROADCAST_ACTION);
							// intent.putExtra("delIdx", arg2);
							// LocalBroadcastManager.getInstance(SelectedCityActivity.this).sendBroadcast(intent);
							list.remove(arg2);
							cityListAdapter.notifyDataSetChanged();
						}
					});
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.create().show();
		}
		return true;
	}
}
