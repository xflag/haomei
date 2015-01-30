package com.frank.haomei.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 
 * @author XuYongqi
 *
 */
public class DateUtil {

	public static String getDate(Date date) {
		date = date == null ? new Date() : date;
		SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日",Locale.getDefault());
		return formatter.format(date);
	}

	public static String getDayOfWeek(Date date) {
		date = date == null ? new Date() : date;
		SimpleDateFormat dateFm = new SimpleDateFormat("EEEE",Locale.getDefault());
		return dateFm.format(date);
	}

	public static String getTimeDiff(String t1,String format) {
		DateFormat df = new SimpleDateFormat(format,Locale.getDefault());
		try {
			Date d1 = new Date();
			Date d2 = df.parse(t1);			
			long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别

			long days = diff / (1000 * 60 * 60 * 24);
			long hours = (diff - days * (1000 * 60 * 60 * 24))
					/ (1000 * 60 * 60);
			long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours
					* (1000 * 60 * 60))
					/ (1000 * 60);
			if(days>0)
				return days+"天前";
			if (hours>0) 
				return hours+"小时前";
			if(minutes>0)
				return minutes+"分钟前";			
		} catch (Exception e) {
			LogUtil.e(DateUtil.class.getSimpleName(), e.getMessage());
		}
		return "";
	}
}