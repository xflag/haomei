/**
 * 
 */
package com.haomei.app.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.haomei.app.bean.City;

/**
 * @author XuYongqi
 *
 */
public class HaomeiDB {
	/**
	 * 数据库名
	 */
	public static final String DB_NAME = "haomei";
	
	public static final String LOCATE="locate";
	public static final String LOCATE_NAME="定位";
	
	/**
	 * 数据库版本
	 */
	public static final int VERSION = 1;

	private static HaomeiDB haomeiDB;

	private SQLiteDatabase db;
	
	/**
	 * 热门城市列表
	 */
	public static final String[] HOT_CITIES={"101010100","101020100","101280101","101280601","101030100","101040100","101210101","101230101","101270101","101200101","101300101","101140101","101290101","101130101","101110101","101070101","101250101","101340101","101320101","101330101"};

	/**
	 * 将构造方法私有化
	 */
	private HaomeiDB(Context context) {
		HaomeiOpenHelper dbHelper = new HaomeiOpenHelper(context,DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}

	/**
	 * 获取HaomeiDB的实例。
	 */
	public synchronized static HaomeiDB getInstance(Context context) {
		if (haomeiDB == null) {
			haomeiDB = new HaomeiDB(context.getApplicationContext());
		}
		return haomeiDB;
	}	
	
	/**
	 * 关闭数据库
	 */
	public void close(){
		if(db.isOpen())
		{
			db.close();
			haomeiDB=null;
//			LogUtil.i(HaomeiDB.class.getSimpleName(), "close db");
		}
	}
	
	/**
	 * 将City实例存储到数据库。
	 * @param city
	 */
	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("area_id", city.getAreaId());
			values.put("name_en", city.getNameEn());
			values.put("name_cn", city.getNameCn());
			values.put("district_en", city.getDistrictEn());
			values.put("district_cn", city.getDistrictCn());
			values.put("prov_en", city.getProvEn());
			values.put("prov_cn", city.getProvCn());
			values.put("nation_en", city.getNationEn());
			values.put("nation_cn", city.getNationCn());
			db.insert("city", null, values);
		}
	}
	
	public City getCityByName(String nameCn){
		City city=new City();
		Cursor cursor=db.rawQuery("select id,area_id,name_cn from city where name_cn=?", new String[]{nameCn});
		if (cursor.moveToFirst()) {
			city.setAreaId(cursor.getString(cursor.getColumnIndex("area_id")));
			city.setNameCn(cursor.getString(cursor.getColumnIndex("name_cn")));
		}
		return city;
	}
	
	/**
	 * 根据城市名称过滤出城市列表
	 * @param inputCityName
	 * @return
	 */
	public List<City> filterCities(String inputCityName) {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("city", new String[]{"id","area_id","name_cn","prov_cn","sel_times"}, "name_cn like ?",new String[]{"%"+inputCityName+"%"}, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setAreaId(cursor.getString(cursor.getColumnIndex("area_id")));
				city.setNameCn(cursor.getString(cursor.getColumnIndex("name_cn"))+","+cursor.getString(cursor.getColumnIndex("prov_cn")));
				city.setSelTimes(cursor.getInt(cursor.getColumnIndex("sel_times")));
				list.add(city);
			} while (cursor.moveToNext());
		}
		if(!cursor.isClosed())
			cursor.close();
		return list;
	}	
	
	/**
	 * 更新选中城市的sel_time,sel_times
	 * @param areaId
	 */
	public void updateCitySelTimes(String areaId){
		db.execSQL("update city set sel_time=datetime('now', 'localtime'),sel_times=sel_times+1 where area_id=?",new String[]{ areaId});
	}
	
	/**
	 * 查询出用户最常选择的前6个城市
	 * @return
	 */
	public List<City> loadFreqCities() {
		List<City> list = new ArrayList<City>();
//		Cursor cursor =db.rawQuery("select a.id,a.area_id,a.name_cn,a.sel_times from city a where a.sel_times>0 order by a.sel_times desc,a.sel_time desc limit 0,6", null);
		Cursor cursor =db.rawQuery("select a.id,a.area_id,a.name_cn,a.sel_times,b.area_id b_area_id from city a left join sel_city b on a.area_id=b.area_id where a.sel_times>0 order by a.sel_times desc,a.sel_time desc limit 0,6", null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setAreaId(cursor.getString(cursor.getColumnIndex("area_id")));
				city.setNameCn(cursor.getString(cursor.getColumnIndex("name_cn")));
				city.setSelTimes(cursor.getInt(cursor.getColumnIndex("sel_times")));
				String bAreaId=cursor.getString(cursor.getColumnIndex("b_area_id"));
				if(TextUtils.isEmpty(bAreaId))
					city.setSelected(false);
				else 
					city.setSelected(true);
				list.add(city);
			} while (cursor.moveToNext());
		}
		if(!cursor.isClosed())
			cursor.close();
		return list;
	}	
	
	/**
	 * 查询热门城市
	 * @param HOT_CITIES
	 * @return
	 */
	public List<City> loadHotCities() {
		List<City> list = new ArrayList<City>();
		City city = new City();		
		city.setAreaId(HaomeiDB.LOCATE);
		city.setNameCn(HaomeiDB.LOCATE_NAME);
		Cursor cursor=db.rawQuery("select area_id from sel_city where area_id=?", new String[]{city.getAreaId()});
		if (cursor.moveToFirst()) {
			city.setSelected(true);
		}
		if(!cursor.isClosed())
			cursor.close();
		list.add(city);
		cursor=db.rawQuery("select a.id,a.area_id,a.name_cn,a.sel_times,b.area_id b_area_id from city a left join sel_city b on a.area_id=b.area_id where a.area_id in (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", HOT_CITIES);
		if (cursor.moveToFirst()) {
			do {
			    city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setAreaId(cursor.getString(cursor.getColumnIndex("area_id")));
				city.setNameCn(cursor.getString(cursor.getColumnIndex("name_cn")));
				city.setSelTimes(cursor.getInt(cursor.getColumnIndex("sel_times")));
				String bAreaId=cursor.getString(cursor.getColumnIndex("b_area_id"));
				if(TextUtils.isEmpty(bAreaId))
					city.setSelected(false);
				else 
					city.setSelected(true);
				list.add(city);
			} while (cursor.moveToNext());
		}
		if(!cursor.isClosed())
			cursor.close();
		return list;
	}	
	
	/**
	 * 获取选中的城市
	 * @return
	 */
	public List<City> loadSelCities() {
		List<City> list = new ArrayList<City>();
		Cursor cursor =db.rawQuery("select id,area_id,name_cn from sel_city order by id", null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setAreaId(cursor.getString(cursor.getColumnIndex("area_id")));	
				city.setNameCn(cursor.getString(cursor.getColumnIndex("name_cn")));	
				list.add(city);
			} while (cursor.moveToNext());
		}
		if(!cursor.isClosed())
			cursor.close();
		return list;
	}	
	
	/**
	 * 添加选中的城市
	 * @param city
	 */
	public void addSelCity(City city) {
		if (city != null) {
//			if(city.getAreaId().equals(HaomeiDB.LOCATE))			
//				db.execSQL("update sel_city set name_cn=? where area_id=?",new String[]{city.getNameCn(),city.getAreaId()});
//			else {
				ContentValues values = new ContentValues();
				values.put("area_id", city.getAreaId());
				values.put("name_cn", city.getNameCn());			
				db.insert("sel_city", null, values);
//			}			
		}
	}	
	
	/**
	 * 更新locate记录的城市名称
	 * @param nameCn
	 */
	public void updateSelCityLocate(String nameCn){
		db.execSQL("update sel_city set name_cn=? where area_id=?",new String[]{nameCn,HaomeiDB.LOCATE});
	}
	
	/**
	 * 删除某个选中的城市
	 * @param areaId
	 */
	public void deleteSelCity(String areaId){
		db.execSQL("delete from sel_city where area_id=?",new String[]{areaId});
	}
}