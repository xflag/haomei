/**
 * 
 */
package com.frank.haomei.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author XuYongqi
 *
 */
public class CacheUtil {
	
	public static int loadCache(Context ctx){
		SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(ctx);		
		int idx=sharedPreferences.getInt("curIdx", 0);	
		return idx;
	}

	public static void addCache(Context ctx,int idx){
		SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(ctx);			
		SharedPreferences.Editor editor=sharedPreferences.edit();
		editor.putInt("curIdx", idx);
		editor.commit();		
	}
}
