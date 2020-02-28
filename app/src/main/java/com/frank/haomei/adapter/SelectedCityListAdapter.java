/**
 * 
 */
package com.frank.haomei.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.frank.haomei.R;
import com.frank.haomei.bean.City;
import com.frank.haomei.db.HaomeiDB;

/**
 * 选中城市listview的adapter
 * @author XuYongqi
 *
 */
public class SelectedCityListAdapter extends ArrayAdapter<City> {
	private int resourceId;
	
	public SelectedCityListAdapter(Context context, int resource,
			List<City> objects) {
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
			viewHolder.imgLoc=(ImageView)view.findViewById(R.id.imageViewLoc);
			viewHolder.cityTextView=(TextView)view.findViewById(R.id.textViewCity);
			viewHolder.textViewTemperature=(TextView)view.findViewById(R.id.textViewTemperature);
			view.setTag(viewHolder);
		}
		else {
			view=convertView;
			viewHolder=(ViewHolder)view.getTag();
		}		 
		City city=this.getItem(position);		
		if(city.getAreaId().equals(HaomeiDB.LOCATE))
			viewHolder.imgLoc.setVisibility(View.VISIBLE);
		else
			viewHolder.imgLoc.setVisibility(View.INVISIBLE);
		viewHolder.cityTextView.setText(city.getNameCn());	
		
		viewHolder.cityTextView.setTag(city);		
		return view;
	}
	
	private class ViewHolder {
		public ImageView imgLoc;
		public TextView cityTextView,textViewTemperature;
    }
}
