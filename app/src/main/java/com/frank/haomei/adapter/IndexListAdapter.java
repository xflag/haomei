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
import android.widget.TextView;

import com.frank.haomei.R;
import com.frank.haomei.bean.IndexItem;

/**
 * 天气指数listview的adapter
 * @author XuYongqi
 *
 */
public class IndexListAdapter extends ArrayAdapter<IndexItem> {
	private int resourceId;
	
	public IndexListAdapter(Context context, int resource,
			List<IndexItem> objects) {
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
			viewHolder.textViewType=(TextView)view.findViewById(R.id.textViewType);
			viewHolder.textViewIndex=(TextView)view.findViewById(R.id.textViewIndex);
			viewHolder.textViewDesc=(TextView)view.findViewById(R.id.textViewDesc);
			view.setTag(viewHolder);
		}
		else {
			view=convertView;
			viewHolder=(ViewHolder)view.getTag();
		}		 
		IndexItem item=this.getItem(position);
		viewHolder.textViewType.setText(item.getTypeStr());	
		viewHolder.textViewIndex.setText(item.getIndexStr());
		viewHolder.textViewDesc.setText(item.getDescStr());
		return view;
	}
	
	private class ViewHolder {
		public TextView textViewType,textViewIndex,textViewDesc;
    }
}
