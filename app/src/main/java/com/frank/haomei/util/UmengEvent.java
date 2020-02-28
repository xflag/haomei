/**
 * 
 */
package com.frank.haomei.util;

import java.util.HashMap;

import android.content.Context;

import com.frank.haomei.bean.City;
import com.umeng.analytics.MobclickAgent;

/**
 * 友盟自定义事件
 * 
 * @author XuYongqi
 *
 */
public class UmengEvent {
	// 点击日期查看天气
	public static final String CLICK_DAY = "clickDay";

	// 通过搜索城市查看天气
	public static final String SEARCH_BY_CITY = "searchByCity";

	// 通过已选城市查看天气
	public static final String VIEW_SEL_CITY = "viewSelCity";

	// 通过常用城市查看天气
	public static final String VIEW_FREQ_CITY = "viewFreqCity";

	// 通过热门城市查看天气
	public static final String VIEW_HOT_CITY = "viewHotCity";
	
	// 刷新天气
	public static final String REFRESH = "refresh";

	public static void invokeByCity(Context context,String eventName,City city) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("city", city.getNameCn());
		MobclickAgent.onEvent(context, eventName, map);
	}
	
	public static void invokeByDay(Context context,String eventName,String dayName) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("day", dayName);
		MobclickAgent.onEvent(context, eventName, map);
	}
}
