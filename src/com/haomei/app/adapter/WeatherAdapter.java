package com.haomei.app.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.haomei.app.R;
import com.haomei.app.bean.ForecastItem;
import com.haomei.app.util.DateUtil;
import com.haomei.app.util.LogUtil;
import com.haomei.app.util.WeatherPhenomenon;
import com.haomei.app.util.WeatherUtil;
import com.haomei.app.util.WindDirection;
import com.haomei.app.util.WindScale;

/**
 * @author XuYongqi
 *
 */
public class WeatherAdapter extends ArrayAdapter<JSONObject> {
	private int resourceId;
	
	public WeatherAdapter(Context context, int resource, List<JSONObject> objects) {
		super(context, resource, objects);
		this.resourceId=resource;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view=null;
		ViewHolder viewHolder=null;		
		if(convertView==null){
			view=LayoutInflater.from(getContext()).inflate(this.resourceId, null);
//			RelativeLayout layout=(RelativeLayout) view.findViewById(R.id.r1);
//			LayoutParams params=(LayoutParams) layout.getLayoutParams();
//			params.height=500;
//			layout.setLayoutParams(params);
			viewHolder=new ViewHolder();
			viewHolder.textViewRelease=(TextView)view.findViewById(R.id.textViewRelease);
			viewHolder.textViewSunraise=(TextView)view.findViewById(R.id.textViewSunraise);
			viewHolder.textViewSunset=(TextView)view.findViewById(R.id.textViewSunset);
			viewHolder.textViewPhenomenon=(TextView)view.findViewById(R.id.textViewPhenomenon);
			viewHolder.textViewTemperature=(TextView)view.findViewById(R.id.textViewTemperature);
			viewHolder.textViewWindDirection=(TextView)view.findViewById(R.id.textViewWindDirection);
			viewHolder.textViewWindScale=(TextView)view.findViewById(R.id.textViewWindScale);
			viewHolder.forecastGridView=(GridView)view.findViewById(R.id.gridViewForecast);
			view.setTag(viewHolder);
		}
		else {
			view=convertView;
			viewHolder=(ViewHolder)view.getTag();
		}		 
		try {
			JSONObject jsonObject=getItem(position);
			JSONObject fJsonObject=jsonObject.getJSONObject("f");
			viewHolder.textViewRelease.setText(DateUtil.getTimeDiff(fJsonObject.getString("f0"),"yyyyMMddHHmm")+"发布");
			JSONArray f1JsonArray=fJsonObject.getJSONArray("f1");
			String pString="fa",tString="fc",wdString="fe",wsString="fg";
			if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)>18) {
				pString="fb";
				tString="fd";
				wdString="ff";
				wsString="fh";
			}
			JSONObject day1=f1JsonArray.getJSONObject(0);
			viewHolder.textViewPhenomenon.setText(WeatherPhenomenon.getInstance().getPhenomenon(day1.getString(pString)));
			viewHolder.textViewTemperature.setText(day1.getString(tString)+WeatherUtil.DEGREE);
			viewHolder.textViewWindDirection.setText(WindDirection.getInstance().getDirection(day1.getString(wdString)));
			viewHolder.textViewWindScale.setText(WindScale.getInstance().getScale(day1.getString(wsString)));
			String[] suns=day1.getString("fi").split("\\|");
			viewHolder.textViewSunraise.setText(suns[0]+"日出");
			viewHolder.textViewSunset.setText(suns[1]+"日落");
			loadForecastGrid(viewHolder,f1JsonArray);
		} catch (Exception e) {
			LogUtil.e(WeatherAdapter.class.getSimpleName(), e.getMessage());
		}		
		return view;
	}
	
	private void loadForecastGrid(ViewHolder viewHolder,JSONArray f1JsonArray) throws JSONException{
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
		ForecastGridAdapter forecastGridAdapter=new ForecastGridAdapter(getContext(), R.layout.forecast_item, list);
		viewHolder.forecastGridView.setAdapter(forecastGridAdapter);
	}
	
	private class ViewHolder {
		public TextView textViewRelease,textViewSunraise,textViewSunset,textViewPhenomenon,textViewTemperature,textViewWindDirection,textViewWindScale;
		public GridView forecastGridView;
    }

}
