package com.frank.haomei.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author XuYongqi
 *
 */
public class WindScale {
	private static WindScale instance;
	private Map<String, String> map=new HashMap<String, String>();
	
	private WindScale(){
		this.map.put("0", "微风");
		this.map.put("1", "3-4级");
		this.map.put("2", "4-5级");
		this.map.put("3", "5-6级");
		this.map.put("4", "6-7级");
		this.map.put("5", "7-8级");
		this.map.put("6", "8-9级");
		this.map.put("7", "9-10级");
		this.map.put("8", "10-11级");
		this.map.put("9", "11-12级");		
	}
	
	public static WindScale getInstance(){
		if(instance==null)
			return new WindScale();
		return instance;
	}
	
	public String getScale(String key){
		try {
			return this.map.get(key);
		} catch (Exception e) {
			LogUtil.e(WindScale.class.getSimpleName(), e.getMessage());
		}
		return "";
	}
}
