/**
 * 
 */
package com.haomei.app.adapter;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haomei.app.R;
import com.haomei.app.bean.ForecastItem;

/**
 * @author XuYongqi
 *
 */
public class ForecastGridAdapter extends ArrayAdapter<ForecastItem> {
	private int resourceId;
	private static final String IMG_URL="blue80_%02d";
	
	public ForecastGridAdapter(Context context, int resource,
			List<ForecastItem> objects) {
		super(context, resource, objects);
		this.resourceId=resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view=null;
		ViewHolder viewHolder=null;		
		if(convertView==null){
			view=LayoutInflater.from(getContext()).inflate(this.resourceId, null);
			viewHolder=new ViewHolder();
			viewHolder.tvDay=(TextView)view.findViewById(com.haomei.app.R.id.tvDay);
			viewHolder.imageViewPhenomenon=(ImageView)view.findViewById(R.id.imageViewPhenomenon);
			viewHolder.tvTemeprature=(TextView)view.findViewById(com.haomei.app.R.id.tvTemperature);
			viewHolder.tvWeatherPhenomenon=(TextView)view.findViewById(com.haomei.app.R.id.tvWeatherPhenomenon);
			view.setTag(viewHolder);
		}
		else {
			view=convertView;
			viewHolder=(ViewHolder)view.getTag();
		}		 
		ForecastItem item=this.getItem(position);
		viewHolder.tvDay.setText(item.getDay());
		int imgId=this.getResource(String.format(Locale.getDefault(),IMG_URL, item.getImgPhenomenon()));
		viewHolder.imageViewPhenomenon.setImageResource(imgId);
		viewHolder.tvWeatherPhenomenon.setText(item.getWeatherPhenomenon());
		viewHolder.tvTemeprature.setText(item.getTemeprature());
		return view;
	}	
	
	private int  getResource(String imageName){  
		Context ctx=this.getContext();  		
		int resId = ctx.getResources().getIdentifier(imageName, "drawable" , ctx.getPackageName());  
		return resId;  
	}
	
	private class ViewHolder {
		public TextView tvDay,tvTemeprature,tvWeatherPhenomenon;
		public ImageView imageViewPhenomenon;
    }

}