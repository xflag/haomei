/**
 * 
 */
package com.frank.haomei.bean;

/**
 * @author XuYongqi
 *
 */
public class ForecastItem {
	private String day;
	private int imgPhenomenon;	
	private String weatherPhenomenon;
	private String temeprature;
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public int getImgPhenomenon() {
		return imgPhenomenon;
	}
	public void setImgPhenomenon(int imgPhenomenon) {
		this.imgPhenomenon = imgPhenomenon;
	}
	public String getWeatherPhenomenon() {
		return weatherPhenomenon;
	}
	public void setWeatherPhenomenon(String weatherPhenomenon) {
		this.weatherPhenomenon = weatherPhenomenon;
	}
	public String getTemeprature() {
		return temeprature;
	}
	public void setTemeprature(String temeprature) {
		this.temeprature = temeprature;
	}
}
