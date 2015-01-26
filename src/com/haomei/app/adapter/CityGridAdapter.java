/**
 * 
 */
package com.haomei.app.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.haomei.app.bean.City;

/**
 * @author XuYongqi
 *
 */
public class CityGridAdapter extends ArrayAdapter<City> {
	private int resourceId;
	
	public CityGridAdapter(Context context, int resource,
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
			viewHolder.cityTextView=(TextView)view.findViewById(com.haomei.app.R.id.textViewCity);			
			view.setTag(viewHolder);
		}
		else {
			view=convertView;
			viewHolder=(ViewHolder)view.getTag();
		}		 
		City city=this.getItem(position);
		viewHolder.cityTextView.setText(city.getNameCn());		
		viewHolder.cityTextView.setTag(city);		
		return view;
	}
	
	private class ViewHolder {
		public TextView cityTextView;
    }
}