/**
 * 
 */
package com.haomei.app.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.haomei.app.bean.City;

/**
 * @author XuYongqi
 *
 */
public class CacheUtil {
	public static final String LOCATE="locate";
	public static final String LOCATE_NAME="定位";
	
	public static List<City> loadCache(Context ctx){
		List<City> cityList=new ArrayList<City>();
		SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(ctx);		
		String cities=sharedPreferences.getString("cities", "");
		if(!TextUtils.isEmpty(cities)){
			String[] cityStrings=cities.split(",");
			for (int i = 0; i < cityStrings.length; ++i) {
				String[] singleCity=cityStrings[i].split(":");
				City city=new City();
				city.setAreaId(singleCity[0]);
				city.setNameCn(singleCity[1]);
			    cityList.add(city);
			}
		}	
		else {
			City city=new City();
			city.setAreaId(CacheUtil.LOCATE);
			city.setNameCn(CacheUtil.LOCATE_NAME);
		    cityList.add(city);
		}
		return cityList;
	}

	public static void addCache(Context ctx,City city){
		SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(ctx);
		String cities=sharedPreferences.getString("cities", "");
		StringBuilder sBuilder=new StringBuilder();
		if(!TextUtils.isEmpty(cities)){
			if(cities.contains(city.getAreaId()))
				return;
			else {				
				sBuilder.append(cities).append(",");
			}
		}		
		sBuilder.append(city.getAreaId());
		sBuilder.append(":").append(city.getNameCn());	
		SharedPreferences.Editor editor=sharedPreferences.edit();
		editor.putString("cities", sBuilder.toString());
		editor.commit();		
	}
}
