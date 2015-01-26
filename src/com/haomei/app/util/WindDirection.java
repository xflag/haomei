package com.haomei.app.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author XuYongqi
 *
 */
public class WindDirection {
	private static WindDirection instance;
	private Map<String, String> map=new HashMap<String, String>();
	
	private WindDirection(){
		this.map.put("0", "无持续风向");
		this.map.put("1", "东北风");
		this.map.put("2", "东风");
		this.map.put("3", "东南风");
		this.map.put("4", "南风");
		this.map.put("5", "西南风");
		this.map.put("6", "西风");
		this.map.put("7", "西北风");
		this.map.put("8", "北风");
		this.map.put("9", "旋转风");		
	}
	
	public static WindDirection getInstance(){
		if(instance==null)
			return new WindDirection();
		return instance;
	}
	
	public String getDirection(String key){
		try {
			return this.map.get(key);
		} catch (Exception e) {
			LogUtil.e(WindDirection.class.getSimpleName(), e.getMessage());
		}
		return "";
	}
}
