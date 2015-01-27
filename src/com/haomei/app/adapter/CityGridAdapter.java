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
import android.widget.ImageView;
import android.widget.TextView;

import com.haomei.app.R;
import com.haomei.app.bean.City;
import com.haomei.app.db.HaomeiDB;

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
			viewHolder.imgLoc=(ImageView)view.findViewById(R.id.imageViewLoc);
			viewHolder.cityTextView=(TextView)view.findViewById(com.haomei.app.R.id.textViewCity);
			viewHolder.imageViewSelectedCity=(ImageView)view.findViewById(R.id.imageViewSelectedCity);
			view.setTag(viewHolder);
		}
		else {
			view=convertView;
			viewHolder=(ViewHolder)view.getTag();
		}		 
		City city=this.getItem(position);		
		if(viewHolder.imgLoc!=null&&city.getAreaId().equals(HaomeiDB.LOCATE))
			viewHolder.imgLoc.setVisibility(View.VISIBLE);
		viewHolder.cityTextView.setText(city.getNameCn());	
		if(viewHolder.imageViewSelectedCity!=null&&(city.isSelected()||city.getAreaId().equals(HaomeiDB.LOCATE)))
			viewHolder.imageViewSelectedCity.setVisibility(View.VISIBLE);
		viewHolder.cityTextView.setTag(city);		
		return view;
	}
	
	private class ViewHolder {
		public ImageView imgLoc,imageViewSelectedCity;
		public TextView cityTextView;
    }
}