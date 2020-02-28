package com.frank.haomei.util;

import java.util.HashMap;
import java.util.Map;


/**
 * @author XuYongqi
 *
 */
public class WeatherPhenomenon {
	private static WeatherPhenomenon instance;
	private Map<String, String> map=new HashMap<String, String>();
	
	private WeatherPhenomenon(){
		this.map.put("00", "晴");
		this.map.put("01", "多云");
		this.map.put("02", "阴");
		this.map.put("03", "阵雨");
		this.map.put("04", "雷阵雨");
		this.map.put("05", "雷阵雨伴有冰雹");
		this.map.put("06", "雨夹雪");
		this.map.put("07", "小雨");
		this.map.put("08", "中雨");
		this.map.put("09", "大雨");
		this.map.put("10", "暴雨");
		this.map.put("11", "大暴雨");
		this.map.put("12", "特大暴雨");
		this.map.put("13", "阵雪");
		this.map.put("14", "小雪");
		this.map.put("15", "中雪");
		this.map.put("16", "大雪");
		this.map.put("17", "暴雪");
		this.map.put("18", "雾");
		this.map.put("19", "冻雨");
		this.map.put("20", "沙尘暴");
		this.map.put("21", "小到中雨");
		this.map.put("22", "中到大雨");
		this.map.put("23", "大到暴雨");
		this.map.put("24", "暴雨到大暴雨");
		this.map.put("25", "大暴雨到特大暴雨");
		this.map.put("26", "小到中雪");
		this.map.put("27", "中到大雪");
		this.map.put("28", "大到暴雪");
		this.map.put("29", "浮尘");
		this.map.put("30", "扬沙");
		this.map.put("31", "强沙尘暴");
		this.map.put("53", "霾");
		this.map.put("99", "无");
	}
	
	public static WeatherPhenomenon getInstance(){
		if(instance==null)
			return new WeatherPhenomenon();
		return instance;
	}
	
	public String getPhenomenon(String key){
		try {
			return this.map.get(key);
		} catch (Exception e) {
			LogUtil.e(WeatherPhenomenon.class.getSimpleName(), e.getMessage());
		}
		return "";
	}
}
