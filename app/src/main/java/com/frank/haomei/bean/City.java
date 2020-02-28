/**
 * 
 */
package com.frank.haomei.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author XuYongqi
 *
 */
public class City implements Parcelable {
	private int id;
	private String areaId;
	private String nameEn;
	private String nameCn;
	private String districtEn;
	private String districtCn;
	private String provEn;
	private String ProvCn;
	private String nationEn;
	private String nationCn;
	private int selTimes;
	private boolean isSelected;	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public String getNameCn() {
		return nameCn;
	}

	public void setNameCn(String nameCn) {
		this.nameCn = nameCn;
	}

	public String getDistrictEn() {
		return districtEn;
	}

	public void setDistrictEn(String districtEn) {
		this.districtEn = districtEn;
	}

	public String getDistrictCn() {
		return districtCn;
	}

	public void setDistrictCn(String districtCn) {
		this.districtCn = districtCn;
	}

	public String getProvEn() {
		return provEn;
	}

	public void setProvEn(String provEn) {
		this.provEn = provEn;
	}

	public String getProvCn() {
		return ProvCn;
	}

	public void setProvCn(String provCn) {
		ProvCn = provCn;
	}

	public String getNationEn() {
		return nationEn;
	}

	public void setNationEn(String nationEn) {
		this.nationEn = nationEn;
	}

	public String getNationCn() {
		return nationCn;
	}

	public void setNationCn(String nationCn) {
		this.nationCn = nationCn;
	}

	public int getSelTimes() {
		return selTimes;
	}

	public void setSelTimes(int selTimes) {
		this.selTimes = selTimes;
	}
	
	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(areaId);
		dest.writeString(nameCn);
	}

	public static final Parcelable.Creator<City> CREATOR = new Parcelable.Creator<City>() {

		@Override
		public City createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			City city=new City();
			city.setAreaId(source.readString());
			city.setNameCn(source.readString());
			return city;
		}

		@Override
		public City[] newArray(int size) {
			// TODO Auto-generated method stub
			return new City[size];
		}

	};

}
